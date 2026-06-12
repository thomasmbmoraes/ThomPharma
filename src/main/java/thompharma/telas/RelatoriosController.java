package thompharma.telas;

import thompharma.App;
import thompharma.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class RelatoriosController {

    // aba 1 - pedidos por periodo
    @FXML private DatePicker dataInicio;
    @FXML private DatePicker dataFim;
    @FXML private ComboBox<String> comboPedidoStatus;
    @FXML private TableView<ObservableList<String>> tabelaPedidos;
    @FXML private Label labelTotalPedidos;

    // aba 2 - estoque
    @FXML private ComboBox<String> comboEstoqueFiltro;
    @FXML private TableView<ObservableList<String>> tabelaEstoque;
    @FXML private Label labelResumoEstoque;

    // aba 3 - rotulos emitidos
    @FXML private DatePicker dataInicioRotulo;
    @FXML private DatePicker dataFimRotulo;
    @FXML private TextField campoBuscaRotulo;
    @FXML private TableView<ObservableList<String>> tabelaRotulos;
    @FXML private Label labelTotalRotulos;

    // aba 4 - clientes mais atendidos
    @FXML private ComboBox<String> comboPeriodoClientes;
    @FXML private TableView<ObservableList<String>> tabelaClientes;

    private static final DateTimeFormatter FMT_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        comboPedidoStatus.getItems().addAll("Todos", "Aguardando", "Em Produção", "Pronto", "Entregue", "Cancelado");
        comboPedidoStatus.getSelectionModel().selectFirst();

        comboEstoqueFiltro.getItems().addAll("Todos", "Crítico (zerado)", "Mínimo (abaixo)", "Normal");
        comboEstoqueFiltro.getSelectionModel().selectFirst();

        comboPeriodoClientes.getItems().addAll("Últimos 30 dias", "Últimos 90 dias", "Últimos 12 meses", "Todo o período");
        comboPeriodoClientes.getSelectionModel().selectFirst();

        dataInicio.setValue(LocalDate.now().withDayOfMonth(1));
        dataFim.setValue(LocalDate.now());
        dataInicioRotulo.setValue(LocalDate.now().withDayOfMonth(1));
        dataFimRotulo.setValue(LocalDate.now());

        configurarTabelaPedidos();
        configurarTabelaEstoque();
        configurarTabelaRotulos();
        configurarTabelaClientes();

        gerarPedidos();
        gerarEstoque();
        gerarRotulos();
        gerarClientes();
    }

    // ---------- ABA 1: PEDIDOS POR PERÍODO ----------

    private void configurarTabelaPedidos() {
        tabelaPedidos.getColumns().clear();
        String[] nomes = {"Código", "Data Pedido", "Cliente", "Prescritor", "Fórmula", "Status", "Retirada"};
        int[] larguras = {70, 90, 150, 130, 150, 110, 90};
        for (int i = 0; i < nomes.length; i++) {
            final int col = i;
            TableColumn<ObservableList<String>, String> c = new TableColumn<>(nomes[i]);
            c.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().size() > col ? data.getValue().get(col) : ""
            ));
            c.setPrefWidth(larguras[i]);
            tabelaPedidos.getColumns().add(c);
        }
        tabelaPedidos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void gerarPedidos() {
        if (dataInicio.getValue() == null || dataFim.getValue() == null) return;
        ObservableList<ObservableList<String>> dados = FXCollections.observableArrayList();
        String status = comboPedidoStatus.getValue();

        StringBuilder sql = new StringBuilder(
            "SELECT p.id, p.data_pedido, c.nome AS cliente, pr.nome AS prescritor, " +
            "COALESCE(r.nome, p.observacoes, '') AS formula, p.status, p.data_retirada " +
            "FROM tb_pedidos p " +
            "LEFT JOIN tb_clientes c ON p.id_cliente = c.id " +
            "LEFT JOIN tb_prescritores pr ON p.id_prescritor = pr.id " +
            "LEFT JOIN tb_receitas r ON p.id_receita = r.id " +
            "WHERE p.data_pedido BETWEEN ? AND ?"
        );
        if (!"Todos".equals(status)) sql.append(" AND p.status = ?");
        sql.append(" ORDER BY p.data_pedido DESC");

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            stmt.setDate(1, java.sql.Date.valueOf(dataInicio.getValue()));
            stmt.setDate(2, java.sql.Date.valueOf(dataFim.getValue()));
            if (!"Todos".equals(status)) stmt.setString(3, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.format("%05d", rs.getInt("id")));
                java.sql.Date dp = rs.getDate("data_pedido");
                row.add(dp != null ? dp.toLocalDate().format(FMT_BR) : "");
                row.add(nullStr(rs.getString("cliente")));
                row.add(nullStr(rs.getString("prescritor")));
                row.add(nullStr(rs.getString("formula")));
                row.add(nullStr(rs.getString("status")));
                java.sql.Date dr = rs.getDate("data_retirada");
                row.add(dr != null ? dr.toLocalDate().format(FMT_BR) : "");
                dados.add(row);
            }
        } catch (Exception e) {
            mostrarErro("Erro ao gerar relatório de pedidos: " + e.getMessage());
        }
        tabelaPedidos.setItems(dados);
        colorirLinhasPedidos();
        labelTotalPedidos.setText("Total: " + dados.size() + " pedido(s)");
    }

    private void colorirLinhasPedidos() {
        tabelaPedidos.setRowFactory(tv -> new javafx.scene.control.TableRow<ObservableList<String>>() {
            @Override
            protected void updateItem(ObservableList<String> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.size() < 6) {
                    setStyle("");
                    return;
                }
                String s = item.get(5);
                switch (s) {
                    case "Pronto":    setStyle("-fx-background-color: #1a3028; -fx-text-fill: #9ece6a;"); break;
                    case "Entregue": setStyle("-fx-background-color: #1a2a1a; -fx-text-fill: #73daca;"); break;
                    case "Cancelado":setStyle("-fx-background-color: #2d1020; -fx-text-fill: #f7768e;"); break;
                    case "Em Produção": setStyle("-fx-background-color: #2a2510; -fx-text-fill: #e0af68;"); break;
                    default:         setStyle("");
                }
            }
        });
    }

    @FXML
    private void imprimirPedidos() {
        imprimirTabela("Relatório de Pedidos por Período",
            "Período: " + dataInicio.getValue().format(FMT_BR) + " a " + dataFim.getValue().format(FMT_BR),
            tabelaPedidos, labelTotalPedidos.getText());
    }

    // ---------- ABA 2: ESTOQUE ----------

    private void configurarTabelaEstoque() {
        tabelaEstoque.getColumns().clear();
        String[] nomes = {"Matéria-Prima", "Unidade", "Estoque Atual", "Est. Mínimo", "Situação"};
        int[] larguras = {220, 80, 110, 110, 100};
        for (int i = 0; i < nomes.length; i++) {
            final int col = i;
            TableColumn<ObservableList<String>, String> c = new TableColumn<>(nomes[i]);
            c.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().size() > col ? data.getValue().get(col) : ""
            ));
            c.setPrefWidth(larguras[i]);
            tabelaEstoque.getColumns().add(c);
        }
        tabelaEstoque.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void gerarEstoque() {
        ObservableList<ObservableList<String>> dados = FXCollections.observableArrayList();
        String filtro = comboEstoqueFiltro.getValue();

        String sql = "SELECT nome, unidade, estoque_atual, estoque_minimo FROM tb_materias_primas ORDER BY nome";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            int criticos = 0, minimos = 0, normais = 0;
            while (rs.next()) {
                double atual = rs.getDouble("estoque_atual");
                double minimo = rs.getDouble("estoque_minimo");
                String situacao;
                if (atual <= 0) { situacao = "Crítico"; criticos++; }
                else if (atual <= minimo) { situacao = "Mínimo"; minimos++; }
                else { situacao = "Normal"; normais++; }

                if ("Todos".equals(filtro)
                    || ("Crítico (zerado)".equals(filtro) && "Crítico".equals(situacao))
                    || ("Mínimo (abaixo)".equals(filtro) && "Mínimo".equals(situacao))
                    || ("Normal".equals(filtro) && "Normal".equals(situacao))) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(nullStr(rs.getString("nome")));
                    row.add(nullStr(rs.getString("unidade")));
                    row.add(String.format("%.2f", atual));
                    row.add(String.format("%.2f", minimo));
                    row.add(situacao);
                    dados.add(row);
                }
            }
            labelResumoEstoque.setText(
                String.format("Total: %d item(s)  |  Crítico: %d  |  Mínimo: %d  |  Normal: %d",
                    dados.size(), criticos, minimos, normais)
            );
        } catch (Exception e) {
            mostrarErro("Erro ao gerar estoque: " + e.getMessage());
        }
        tabelaEstoque.setItems(dados);
        colorirLinhasEstoque();
    }

    private void colorirLinhasEstoque() {
        tabelaEstoque.setRowFactory(tv -> new javafx.scene.control.TableRow<ObservableList<String>>() {
            @Override
            protected void updateItem(ObservableList<String> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.size() < 5) { setStyle(""); return; }
                switch (item.get(4)) {
                    case "Crítico": setStyle("-fx-background-color: #2d1020; -fx-text-fill: #f7768e;"); break;
                    case "Mínimo":  setStyle("-fx-background-color: #2a2510; -fx-text-fill: #e0af68;"); break;
                    default:        setStyle("");
                }
            }
        });
    }

    @FXML
    private void imprimirEstoque() {
        imprimirTabela("Relatório de Estoque de Matérias-Primas",
            "Gerado em: " + LocalDate.now().format(FMT_BR),
            tabelaEstoque, labelResumoEstoque.getText());
    }

    // ---------- ABA 3: RÓTULOS EMITIDOS ----------

    private void configurarTabelaRotulos() {
        tabelaRotulos.getColumns().clear();
        String[] nomes = {"Código", "Data", "Cliente", "Fórmula", "Prescritor", "Validade"};
        int[] larguras = {100, 90, 160, 160, 130, 90};
        for (int i = 0; i < nomes.length; i++) {
            final int col = i;
            TableColumn<ObservableList<String>, String> c = new TableColumn<>(nomes[i]);
            c.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().size() > col ? data.getValue().get(col) : ""
            ));
            c.setPrefWidth(larguras[i]);
            tabelaRotulos.getColumns().add(c);
        }
        tabelaRotulos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void gerarRotulos() {
        if (dataInicioRotulo.getValue() == null || dataFimRotulo.getValue() == null) return;
        ObservableList<ObservableList<String>> dados = FXCollections.observableArrayList();
        String busca = campoBuscaRotulo.getText().trim().toLowerCase();

        String sql = "SELECT codigo, data_rotulo, nome_cliente, nome_formula, nome_prescritor, validade " +
                     "FROM tb_rotulos WHERE data_rotulo BETWEEN ? AND ? ORDER BY data_rotulo DESC, codigo";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(dataInicioRotulo.getValue()));
            stmt.setDate(2, java.sql.Date.valueOf(dataFimRotulo.getValue()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String cliente = nullStr(rs.getString("nome_cliente"));
                String formula = nullStr(rs.getString("nome_formula"));
                if (!busca.isEmpty()
                    && !cliente.toLowerCase().contains(busca)
                    && !formula.toLowerCase().contains(busca)) continue;

                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(nullStr(rs.getString("codigo")));
                java.sql.Date dr = rs.getDate("data_rotulo");
                row.add(dr != null ? dr.toLocalDate().format(FMT_BR) : "");
                row.add(cliente);
                row.add(formula);
                row.add(nullStr(rs.getString("nome_prescritor")));
                row.add(nullStr(rs.getString("validade")));
                dados.add(row);
            }
        } catch (Exception e) {
            mostrarErro("Erro ao gerar rótulos: " + e.getMessage());
        }
        tabelaRotulos.setItems(dados);
        labelTotalRotulos.setText("Total: " + dados.size() + " rótulo(s)");
    }

    @FXML
    private void imprimirRotulos() {
        imprimirTabela("Relatório de Rótulos Emitidos",
            "Período: " + dataInicioRotulo.getValue().format(FMT_BR) + " a " + dataFimRotulo.getValue().format(FMT_BR),
            tabelaRotulos, labelTotalRotulos.getText());
    }

    // ---------- ABA 4: CLIENTES MAIS ATENDIDOS ----------

    private void configurarTabelaClientes() {
        tabelaClientes.getColumns().clear();
        String[] nomes = {"#", "Cliente", "Pedidos", "Último Pedido", "Status mais comum"};
        int[] larguras = {40, 220, 80, 110, 150};
        for (int i = 0; i < nomes.length; i++) {
            final int col = i;
            TableColumn<ObservableList<String>, String> c = new TableColumn<>(nomes[i]);
            c.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().size() > col ? data.getValue().get(col) : ""
            ));
            c.setPrefWidth(larguras[i]);
            tabelaClientes.getColumns().add(c);
        }
        tabelaClientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void gerarClientes() {
        ObservableList<ObservableList<String>> dados = FXCollections.observableArrayList();
        String periodo = comboPeriodoClientes.getValue();

        String filtroData = "";
        switch (periodo) {
            case "Últimos 30 dias":   filtroData = "AND p.data_pedido >= CURRENT_DATE - INTERVAL '30 days'"; break;
            case "Últimos 90 dias":   filtroData = "AND p.data_pedido >= CURRENT_DATE - INTERVAL '90 days'"; break;
            case "Últimos 12 meses":  filtroData = "AND p.data_pedido >= CURRENT_DATE - INTERVAL '12 months'"; break;
            default: break;
        }

        String sql =
            "SELECT c.nome, COUNT(p.id) AS total, MAX(p.data_pedido) AS ultimo, " +
            "       (SELECT p2.status FROM tb_pedidos p2 WHERE p2.id_cliente = c.id " + filtroData +
            "        GROUP BY p2.status ORDER BY COUNT(*) DESC LIMIT 1) AS status_comum " +
            "FROM tb_clientes c " +
            "JOIN tb_pedidos p ON p.id_cliente = c.id " +
            (filtroData.isEmpty() ? "" : "WHERE " + filtroData.replace("AND ", "")) +
            "GROUP BY c.id, c.nome ORDER BY total DESC LIMIT 20";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            int pos = 1;
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(pos++));
                row.add(nullStr(rs.getString("nome")));
                row.add(String.valueOf(rs.getInt("total")));
                java.sql.Date du = rs.getDate("ultimo");
                row.add(du != null ? du.toLocalDate().format(FMT_BR) : "");
                row.add(nullStr(rs.getString("status_comum")));
                dados.add(row);
            }
        } catch (Exception e) {
            mostrarErro("Erro ao gerar ranking: " + e.getMessage());
        }
        tabelaClientes.setItems(dados);
    }

    @FXML
    private void imprimirClientes() {
        imprimirTabela("Relatório — Clientes Mais Atendidos",
            "Período: " + comboPeriodoClientes.getValue(),
            tabelaClientes, "Top 20 clientes por número de pedidos");
    }

    // ---------- IMPRESSÃO GENÉRICA ----------

    private void imprimirTabela(String titulo, String subtitulo,
                                 TableView<ObservableList<String>> tabela, String rodape) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) { mostrarErro("Nenhuma impressora disponível."); return; }
        if (!job.showPrintDialog(App.getStage())) return;

        VBox pagina = new VBox(8);
        pagina.setPadding(new Insets(20));
        pagina.setStyle("-fx-background-color: white;");

        Text tTitulo = new Text(titulo);
        tTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        tTitulo.setStyle("-fx-fill: black;");

        Text tSub = new Text(subtitulo);
        tSub.setFont(Font.font("Arial", 11));
        tSub.setStyle("-fx-fill: #444;");

        Text tRodape = new Text(rodape + "   |   Impresso em: " + LocalDate.now().format(FMT_BR));
        tRodape.setFont(Font.font("Arial", 10));
        tRodape.setStyle("-fx-fill: #666;");

        // cabeçalho das colunas
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #333; -fx-padding: 4 6;");
        for (TableColumn<?, ?> col : tabela.getColumns()) {
            Label l = new Label(col.getText());
            l.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px;");
            l.setPrefWidth(col.getWidth());
            HBox.setHgrow(l, Priority.ALWAYS);
            header.getChildren().add(l);
        }

        // linhas
        VBox linhas = new VBox(1);
        int rowNum = 0;
        for (ObservableList<String> item : tabela.getItems()) {
            HBox row = new HBox();
            String bg = (rowNum++ % 2 == 0) ? "#f5f5f5" : "white";
            row.setStyle("-fx-background-color: " + bg + "; -fx-padding: 3 6;");
            for (int i = 0; i < tabela.getColumns().size(); i++) {
                String val = item.size() > i ? item.get(i) : "";
                Label l = new Label(val);
                l.setStyle("-fx-font-size: 10px; -fx-text-fill: #111;");
                l.setPrefWidth(tabela.getColumns().get(i).getWidth());
                HBox.setHgrow(l, Priority.ALWAYS);
                row.getChildren().add(l);
            }
            linhas.getChildren().add(row);
        }

        pagina.getChildren().addAll(tTitulo, tSub, header, linhas, tRodape);

        javafx.print.PageLayout layout = job.getJobSettings().getPageLayout();
        job.printPage(layout, pagina);
        job.endJob();
    }

    // ---------- UTILITÁRIOS ----------

    private String nullStr(String s) { return s != null ? s : ""; }

    private void mostrarErro(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Erro");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    @FXML
    private void fechar() {
        App.trocarTela("principal");
    }
}
