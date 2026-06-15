package thompharma.telas;

import thompharma.App;
import thompharma.Conexao;
import thompharma.UiUtil;
import thompharma.modelo.Rotulo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * controller da tela de rotulos de embalagem
 * rotulos sao gerados automaticamente ao mover pedido para Em Producao
 * cada rotulo pode ser editado individualmente antes de imprimir
 */
public class RotulosController {

    // --- lista de rotulos ---
    @FXML private TableView<Rotulo> tabelaRotulos;
    @FXML private TableColumn<Rotulo, String> colCodigo;
    @FXML private TableColumn<Rotulo, String> colCliente;
    @FXML private TableColumn<Rotulo, String> colFormula;
    @FXML private TableColumn<Rotulo, LocalDate> colData;
    @FXML private TextField campoFiltro;
    @FXML private DatePicker dateFiltro;

    // --- formulario de edicao ---
    @FXML private Label labelCodigo;
    @FXML private TextField campoNomeFormula;
    @FXML private TextField campoNomeCliente;
    @FXML private TextField campoNomePrescritor;
    @FXML private TextArea campoPosologia;
    @FXML private DatePicker dateValidade;
    @FXML private TextArea campoObservacoes;

    // --- configuracao de impressao ---
    @FXML private ComboBox<String> comboTamanho;
    @FXML private TextField campoLargura;
    @FXML private TextField campoAltura;

    @FXML private Label mensagem;

    private ObservableList<Rotulo> listaRotulos = FXCollections.observableArrayList();
    private int idRotuloAtual = -1;

    // converte mm para pixels a 96dpi: 1mm = 96/25.4 px
    private static final double MM_TO_PX = 96.0 / 25.4;

    @FXML
    public void initialize() {
        configurarColunas();

        comboTamanho.getItems().addAll(
            "100 × 50 mm",
            "100 × 150 mm",
            "80 × 40 mm",
            "62 × 29 mm",
            "Personalizado"
        );
        comboTamanho.getSelectionModel().selectFirst();
        comboTamanho.setOnAction(e -> atualizarCamposTamanho());
        atualizarCamposTamanho();

        tabelaRotulos.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> { if (novo != null) selecionarRotulo(novo); }
        );

        carregarRotulos();
    }

    private void configurarColunas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nomeCliente"));
        colFormula.setCellValueFactory(new PropertyValueFactory<>("nomeFormula"));
        colData.setCellValueFactory(new PropertyValueFactory<>("dataRotulo"));
        tabelaRotulos.setItems(listaRotulos);
        tabelaRotulos.setPlaceholder(new Label("Nenhum rótulo encontrado."));
    }

    /**
     * carrega rotulos do banco aplicando filtros de texto e data
     */
    private void carregarRotulos() {
        listaRotulos.clear();
        String texto = campoFiltro.getText().trim().toLowerCase();
        LocalDate dataFiltro = dateFiltro.getValue();

        String sql = "SELECT * FROM tb_rotulos WHERE 1=1 ";
        if (!texto.isEmpty()) sql += "AND (LOWER(nome_cliente) LIKE ? OR LOWER(codigo) LIKE ? OR LOWER(nome_formula) LIKE ?) ";
        if (dataFiltro != null) sql += "AND data_rotulo = ? ";
        sql += "ORDER BY data_rotulo DESC, sequencia DESC";

        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;
            if (!texto.isEmpty()) {
                String like = "%" + texto + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            if (dataFiltro != null) ps.setDate(idx++, Date.valueOf(dataFiltro));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Rotulo r = new Rotulo();
                r.setId(rs.getInt("id"));
                r.setIdPedido(rs.getInt("id_pedido"));
                r.setCodigo(rs.getString("codigo"));
                Date d = rs.getDate("data_rotulo");
                if (d != null) r.setDataRotulo(d.toLocalDate());
                r.setSequencia(rs.getInt("sequencia"));
                r.setNomeFormula(rs.getString("nome_formula"));
                r.setNomeCliente(rs.getString("nome_cliente"));
                r.setNomePrescritor(rs.getString("nome_prescritor"));
                r.setPosologia(rs.getString("posologia"));
                Date v = rs.getDate("validade");
                if (v != null) r.setValidade(v.toLocalDate());
                r.setObservacoes(rs.getString("observacoes"));
                r.setLarguraMm(rs.getInt("largura_mm"));
                r.setAlturaMm(rs.getInt("altura_mm"));
                listaRotulos.add(r);
            }

        } catch (Exception e) {
            mensagem.setText("Erro ao carregar rótulos: " + e.getMessage());
        }
    }

    /**
     * preenche o formulario com os dados do rotulo selecionado
     */
    private void selecionarRotulo(Rotulo r) {
        idRotuloAtual = r.getId();
        labelCodigo.setText(r.getCodigo());
        campoNomeFormula.setText(r.getNomeFormula() != null ? r.getNomeFormula() : "");
        campoNomeCliente.setText(r.getNomeCliente() != null ? r.getNomeCliente() : "");
        campoNomePrescritor.setText(r.getNomePrescritor() != null ? r.getNomePrescritor() : "");
        campoPosologia.setText(r.getPosologia() != null ? r.getPosologia() : "");
        dateValidade.setValue(r.getValidade());
        campoObservacoes.setText(r.getObservacoes() != null ? r.getObservacoes() : "");

        // ajusta o seletor de tamanho ao valor salvo
        String tamanhoSalvo = r.getLarguraMm() + " × " + r.getAlturaMm() + " mm";
        if (comboTamanho.getItems().contains(tamanhoSalvo)) {
            comboTamanho.setValue(tamanhoSalvo);
        } else {
            comboTamanho.setValue("Personalizado");
            campoLargura.setText(String.valueOf(r.getLarguraMm()));
            campoAltura.setText(String.valueOf(r.getAlturaMm()));
        }

        mensagem.setText("");
    }

    /**
     * salva as edicoes do rotulo selecionado
     */
    @FXML
    private void salvar() {
        if (idRotuloAtual == -1) {
            mensagem.setText("Selecione um rótulo para editar.");
            return;
        }

        int largura = parseTamanho(campoLargura.getText(), 100);
        int altura  = parseTamanho(campoAltura.getText(), 50);

        String sql = "UPDATE tb_rotulos SET nome_formula=?, nome_cliente=?, nome_prescritor=?, " +
                     "posologia=?, validade=?, observacoes=?, largura_mm=?, altura_mm=? WHERE id=?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, campoNomeFormula.getText().trim());
            ps.setString(2, campoNomeCliente.getText().trim());
            ps.setString(3, campoNomePrescritor.getText().trim());
            ps.setString(4, campoPosologia.getText().trim());
            LocalDate val = dateValidade.getValue();
            if (val != null) ps.setDate(5, Date.valueOf(val)); else ps.setNull(5, Types.DATE);
            ps.setString(6, campoObservacoes.getText().trim());
            ps.setInt(7, largura);
            ps.setInt(8, altura);
            ps.setInt(9, idRotuloAtual);
            ps.executeUpdate();

            UiUtil.sucesso(mensagem, "Rótulo salvo com sucesso.");
            carregarRotulos();

        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao salvar: " + e.getMessage());
        }
    }

    /**
     * exclui o rotulo selecionado com confirmacao
     */
    @FXML
    private void excluir() {
        if (idRotuloAtual == -1) {
            mensagem.setText("Selecione um rótulo para excluir.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Excluir rótulo " + labelCodigo.getText() + "?");
        alert.setContentText("Essa ação não pode ser desfeita.");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_rotulos WHERE id = ?")) {
            ps.setInt(1, idRotuloAtual);
            ps.executeUpdate();
            idRotuloAtual = -1;
            labelCodigo.setText("—");
            carregarRotulos();
            UiUtil.sucesso(mensagem, "Rótulo excluído.");
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao excluir: " + e.getMessage());
        }
    }

    /**
     * aplica o filtro de texto e data na lista
     */
    @FXML
    private void filtrar() {
        carregarRotulos();
    }

    // =================================================================
    // IMPRESSAO
    // Constrói um painel com o layout do rótulo nas dimensões escolhidas
    // e envia para a impressora selecionada pelo usuário via PrinterJob
    // =================================================================

    /**
     * abre o dialogo de impressao e imprime o rotulo selecionado
     * o painel gerado tem as dimensoes exatas da etiqueta em pixels (96dpi)
     */
    @FXML
    private void imprimir() {
        if (idRotuloAtual == -1) {
            mensagem.setText("Selecione um rótulo para imprimir.");
            return;
        }

        int larguraMm = parseTamanho(campoLargura.getText(), 100);
        int alturaMm  = parseTamanho(campoAltura.getText(), 50);
        double larguraPx = larguraMm * MM_TO_PX;
        double alturaPx  = alturaMm  * MM_TO_PX;

        // monta o painel visual do rotulo
        Pane painelRotulo = montarPainelRotulo(larguraPx, alturaPx);

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Nenhuma impressora encontrada.");
            return;
        }

        // abre dialogo para o usuario escolher a impressora e configurar o papel
        // o tamanho do papel e configurado no driver da impressora de etiquetas
        boolean confirmado = job.showPrintDialog(App.getStage());
        if (!confirmado) return;

        // usa o layout definido pelo driver da impressora (paper size configurado no Windows)
        PageLayout layout = job.getJobSettings().getPageLayout();

        boolean sucesso = job.printPage(layout, painelRotulo);
        if (sucesso) {
            job.endJob();
            UiUtil.sucesso(mensagem, "Rótulo enviado para impressão.");
        } else {
            UiUtil.erro(mensagem, "Erro ao imprimir.");
        }
    }

    /**
     * constrói o painel visual do rótulo com as dimensoes informadas
     * usado tanto para preview quanto para impressao
     */
    private Pane montarPainelRotulo(double larguraPx, double alturaPx) {
        VBox painel = new VBox(4);
        painel.setPrefSize(larguraPx, alturaPx);
        painel.setMaxSize(larguraPx, alturaPx);
        painel.setPadding(new Insets(6));
        painel.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1;");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // codigo e data de fabricacao no topo
        HBox topo = new HBox();
        topo.setAlignment(Pos.CENTER_LEFT);
        Text txtCodigo = new Text(labelCodigo.getText());
        txtCodigo.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        String dataFab = LocalDate.now().format(fmt);
        Text txtDataFab = new Text("  Fab.: " + dataFab);
        txtDataFab.setFont(Font.font("Arial", 8));
        topo.getChildren().addAll(txtCodigo, txtDataFab);

        // nome da formula em destaque
        Text txtFormula = new Text(nvl(campoNomeFormula.getText(), "—"));
        txtFormula.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        txtFormula.setWrappingWidth(larguraPx - 12);

        // cliente e prescritor
        Text txtCliente = new Text("Paciente: " + nvl(campoNomeCliente.getText(), "—"));
        txtCliente.setFont(Font.font("Arial", 8));
        txtCliente.setWrappingWidth(larguraPx - 12);

        String prescritor = campoNomePrescritor.getText().trim();
        Text txtPrescritor = new Text(prescritor.isEmpty() ? "" : "Prescritor: " + prescritor);
        txtPrescritor.setFont(Font.font("Arial", 8));
        txtPrescritor.setWrappingWidth(larguraPx - 12);

        // posologia
        String posologia = campoPosologia.getText().trim();
        Text txtPosologia = new Text(posologia.isEmpty() ? "" : posologia);
        txtPosologia.setFont(Font.font("Arial", 8));
        txtPosologia.setWrappingWidth(larguraPx - 12);

        // validade na base
        String validadeStr = dateValidade.getValue() != null
            ? "Val.: " + dateValidade.getValue().format(fmt) : "";
        Text txtValidade = new Text(validadeStr);
        txtValidade.setFont(Font.font("Arial", FontWeight.BOLD, 8));

        painel.getChildren().addAll(topo, txtFormula, txtCliente);
        if (!prescritor.isEmpty()) painel.getChildren().add(txtPrescritor);
        if (!posologia.isEmpty()) painel.getChildren().add(txtPosologia);
        if (!validadeStr.isEmpty()) painel.getChildren().add(txtValidade);

        // observacoes se houver espaco
        String obs = campoObservacoes.getText().trim();
        if (!obs.isEmpty()) {
            Text txtObs = new Text(obs);
            txtObs.setFont(Font.font("Arial", 7));
            txtObs.setWrappingWidth(larguraPx - 12);
            painel.getChildren().add(txtObs);
        }

        return painel;
    }

    /**
     * atualiza os campos de largura e altura conforme o tamanho selecionado na combo
     * "Personalizado" libera os campos para edicao manual
     */
    private void atualizarCamposTamanho() {
        String sel = comboTamanho.getValue();
        if (sel == null || sel.equals("Personalizado")) {
            campoLargura.setEditable(true);
            campoAltura.setEditable(true);
            return;
        }
        // formato "LLL × HHH mm"
        try {
            String[] partes = sel.replace(" mm", "").split("×");
            campoLargura.setText(partes[0].trim());
            campoAltura.setText(partes[1].trim());
        } catch (Exception ignored) {}
        campoLargura.setEditable(false);
        campoAltura.setEditable(false);
    }

    // =================================================================
    // METODO ESTATICO — chamado pelo PedidosController ao salvar
    // Gera o rotulo automaticamente quando o status muda para Em Producao
    // =================================================================

    /**
     * gera um rotulo para o pedido informado se ainda nao existir
     * deve ser chamado pelo PedidosController ao mudar status para Em Producao
     *
     * @param idPedido     id do pedido
     * @param nomeFormula  nome da receita ou formula avulsa
     * @param nomeCliente  nome do cliente
     * @param nomePrescritor nome do prescritor (pode ser null)
     */
    public static void gerarSeNaoExistir(int idPedido, String nomeFormula,
                                          String nomeCliente, String nomePrescritor) {
        try (Connection conn = Conexao.conectar()) {
            conn.setAutoCommit(false);
            try {
                // verifica se ja existe rotulo para este pedido
                try (PreparedStatement check = conn.prepareStatement(
                        "SELECT id FROM tb_rotulos WHERE id_pedido = ?")) {
                    check.setInt(1, idPedido);
                    try (ResultSet rsCheck = check.executeQuery()) {
                        if (rsCheck.next()) {
                            conn.rollback();
                            return;
                        }
                    }
                }

                // calcula a proxima sequencia do dia (dentro da mesma transacao)
                int sequencia;
                try (PreparedStatement seq = conn.prepareStatement(
                        "SELECT COALESCE(MAX(sequencia), 0) + 1 FROM tb_rotulos WHERE data_rotulo = CURRENT_DATE")) {
                    try (ResultSet rsSeq = seq.executeQuery()) {
                        sequencia = rsSeq.next() ? rsSeq.getInt(1) : 1;
                    }
                }

                // monta o codigo no formato DDMMAA/NN
                LocalDate hoje = LocalDate.now();
                String codigo = String.format("%02d%02d%02d/%02d",
                    hoje.getDayOfMonth(), hoje.getMonthValue(), hoje.getYear() % 100, sequencia);

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO tb_rotulos (id_pedido, codigo, data_rotulo, sequencia, " +
                        "nome_formula, nome_cliente, nome_prescritor, largura_mm, altura_mm) " +
                        "VALUES (?, ?, CURRENT_DATE, ?, ?, ?, ?, 100, 50)")) {
                    ps.setInt(1, idPedido);
                    ps.setString(2, codigo);
                    ps.setInt(3, sequencia);
                    ps.setString(4, nomeFormula);
                    ps.setString(5, nomeCliente);
                    ps.setString(6, nomePrescritor);
                    ps.executeUpdate();
                }

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            System.err.println("Aviso: nao foi possivel gerar rotulo automatico: " + e.getMessage());
        }
    }

    /** retorna o valor ou o padrao se nulo ou vazio */
    private String nvl(String valor, String padrao) {
        return (valor == null || valor.isBlank()) ? padrao : valor;
    }

    private int parseTamanho(String texto, int padrao) {
        try { return Integer.parseInt(texto.trim()); }
        catch (Exception e) { return padrao; }
    }

    @FXML
    private void fechar() {
        App.trocarTela("principal");
    }
}
