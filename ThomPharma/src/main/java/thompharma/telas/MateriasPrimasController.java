package thompharma.telas;

import thompharma.App;
import thompharma.Conexao;
import thompharma.UiUtil;
import thompharma.modelo.Lote;
import thompharma.modelo.MateriaPrima;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

/**
 * controller do cadastro de materias primas
 * gerencia materias primas, seus lotes e sinonimos
 * o saldo da materia prima e calculado automaticamente como a soma dos saldos dos lotes
 * paineis de campos extras aparecem dinamicamente conforme o tipo selecionado
 */
public class MateriasPrimasController {

    @FXML private TableView<MateriaPrima> tabelaMateriasPrimas;
    @FXML private TableColumn<MateriaPrima, String> colNome;
    @FXML private TableColumn<MateriaPrima, String> colUnidade;
    @FXML private TableColumn<MateriaPrima, String> colTipo;
    @FXML private TableColumn<MateriaPrima, Double> colSaldo;

    @FXML private TableView<Lote> tabelaLotes;
    @FXML private TableColumn<Lote, String> colLoteNome;
    @FXML private TableColumn<Lote, Double> colLoteCusto;
    @FXML private TableColumn<Lote, Double> colLoteQuantidade;
    @FXML private TableColumn<Lote, Double> colLoteSaldo;
    @FXML private TableColumn<Lote, String> colLoteValidade;
    @FXML private TableColumn<Lote, String> colLoteFornecedor;

    @FXML private TextField campoFiltro;
    @FXML private TextField campoCodigo;
    @FXML private TextField campoNome;
    @FXML private ComboBox<String> comboUnidade;
    @FXML private ComboBox<String> comboTipo;
    @FXML private ComboBox<String> comboControlada;
    @FXML private ComboBox<String> comboClasseAnvisa;
    @FXML private TextField campoEstoqueMinimo;
    @FXML private TextField campoEstoqueCritico;
    @FXML private CheckBox checkRotulo;
    @FXML private CheckBox checkGeladeira;
    @FXML private CheckBox checkControlado;
    @FXML private TextArea campoObservacoes;
    @FXML private Label mensagem;

    @FXML private VBox painelSolidoLiquido;
    @FXML private VBox painelCapsula;
    @FXML private VBox painelEmbalagem;

    @FXML private TextField campoDoseMinima;
    @FXML private TextField campoDoseMaxima;
    @FXML private TextField campoVolume;
    @FXML private TextField campoVolumeCaps;
    @FXML private TextField campoPesoCaps;

    @FXML private ListView<String> listaSinonimos;
    @FXML private TextField campoSinonimo;

    private ObservableList<Integer> idsSinonimos = FXCollections.observableArrayList();
    private ObservableList<MateriaPrima> listaCompleta = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colUnidade.setCellValueFactory(new PropertyValueFactory<>("unidade"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colSaldo.setCellValueFactory(new PropertyValueFactory<>("saldo"));
        tabelaMateriasPrimas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colLoteNome.setCellValueFactory(new PropertyValueFactory<>("nomeLote"));
        colLoteCusto.setCellValueFactory(new PropertyValueFactory<>("custo"));
        colLoteQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colLoteSaldo.setCellValueFactory(new PropertyValueFactory<>("saldo"));
        colLoteValidade.setCellValueFactory(new PropertyValueFactory<>("validadeFormatada"));
        colLoteFornecedor.setCellValueFactory(new PropertyValueFactory<>("nomeFornecedor"));
        tabelaLotes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        comboUnidade.getItems().addAll("mg", "g", "kg", "ml", "l", "un");
        comboUnidade.getSelectionModel().selectFirst();

        comboTipo.getItems().addAll(
            "Sólido", "Líquido", "Cápsula", "Embalagem",
            "Homeopatia", "Floral", "Balcão", "Excipiente", "Outro"
        );
        comboTipo.getSelectionModel().selectFirst();

        comboControlada.getItems().addAll("Nenhuma", "ANVISA", "Polícia Federal");
        comboControlada.getSelectionModel().selectFirst();

        comboClasseAnvisa.getItems().addAll("C1", "C2", "C3", "C4", "C5");
        comboClasseAnvisa.getSelectionModel().selectFirst();

        carregarMateriasPrimas();

        tabelaMateriasPrimas.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> {
                if (novo != null) {
                    preencherFormulario(novo);
                    carregarLotes(novo.getId());
                    carregarSinonimos(novo.getId());
                }
            }
        );

        campoFiltro.textProperty().addListener((obs, antigo, novo) -> filtrar(novo));
        comboTipo.setOnAction(e -> atualizarPainelTipo(comboTipo.getValue()));

        campoCodigo.setText(gerarCodigo());

        // colore linhas conforme nivel de estoque: vermelho=critico, amarelo=minimo
        tabelaMateriasPrimas.setRowFactory(tv -> new TableRow<MateriaPrima>() {
            @Override
            protected void updateItem(MateriaPrima mp, boolean empty) {
                super.updateItem(mp, empty);
                if (mp == null || empty) {
                    setStyle("");
                } else if (mp.getEstoqueCritico() > 0 && mp.getSaldo() <= mp.getEstoqueCritico()) {
                    setStyle("-fx-background-color: #2d1020; -fx-text-fill: #f7768e;");
                } else if (mp.getEstoqueMinimo() > 0 && mp.getSaldo() <= mp.getEstoqueMinimo()) {
                    setStyle("-fx-background-color: #2a2510; -fx-text-fill: #e0af68;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void atualizarPainelTipo(String tipo) {
        boolean isSolidoLiquido = "Sólido".equals(tipo) || "Líquido".equals(tipo);
        boolean isCapsula = "Cápsula".equals(tipo);
        boolean isEmbalagem = "Embalagem".equals(tipo);

        painelSolidoLiquido.setVisible(isSolidoLiquido);
        painelSolidoLiquido.setManaged(isSolidoLiquido);
        painelCapsula.setVisible(isCapsula);
        painelCapsula.setManaged(isCapsula);
        painelEmbalagem.setVisible(isEmbalagem);
        painelEmbalagem.setManaged(isEmbalagem);
    }

    private void carregarMateriasPrimas() {
        listaCompleta.clear();
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM tb_materias_primas ORDER BY nome")) {
            while (rs.next()) {
                MateriaPrima mp = new MateriaPrima();
                mp.setId(rs.getInt("id"));
                mp.setCodigo(rs.getString("codigo"));
                mp.setNome(rs.getString("nome"));
                mp.setUnidade(rs.getString("unidade"));
                mp.setTipo(rs.getString("tipo"));
                mp.setDoseMinima(rs.getDouble("dose_minima"));
                mp.setDoseMaxima(rs.getDouble("dose_maxima"));
                mp.setVolume(rs.getDouble("volume"));
                mp.setVolumeCaps(rs.getDouble("volume_caps"));
                mp.setPesoCaps(rs.getDouble("peso_caps"));
                mp.setSaldo(rs.getDouble("saldo"));
                mp.setRotulo(rs.getBoolean("rotulo"));
                mp.setGeladeira(rs.getBoolean("geladeira"));
                mp.setControlado(rs.getBoolean("controlado"));
                mp.setControladaTipo(rs.getString("controlada_tipo"));
                mp.setClasseAnvisa(rs.getString("classe_anvisa"));
                mp.setObservacoes(rs.getString("observacoes"));
                mp.setEstoqueMinimo(rs.getDouble("estoque_minimo"));
                mp.setEstoqueCritico(rs.getDouble("estoque_critico"));
                listaCompleta.add(mp);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar materias primas: " + e.getMessage());
        }
        tabelaMateriasPrimas.setItems(listaCompleta);
    }

    private void carregarLotes(int idMateriaPrima) {
        ObservableList<Lote> listaLotes = FXCollections.observableArrayList();
        String sql = "SELECT l.*, f.nome as nome_fornecedor FROM tb_lotes l " +
                     "LEFT JOIN tb_fornecedores f ON l.id_fornecedor = f.id " +
                     "WHERE l.id_materia_prima = ? ORDER BY l.data_cadastro DESC";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMateriaPrima);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Lote l = new Lote();
                    l.setId(rs.getInt("id"));
                    l.setIdMateriaPrima(rs.getInt("id_materia_prima"));
                    l.setNomeLote(rs.getString("nome_lote"));
                    l.setCusto(rs.getDouble("custo"));
                    l.setFator(rs.getDouble("fator"));
                    l.setFator2(rs.getDouble("fator2"));
                    l.setQuantidade(rs.getDouble("quantidade"));
                    l.setSaldo(rs.getDouble("saldo"));
                    l.setDensidade(rs.getDouble("densidade"));
                    java.sql.Date vd = rs.getDate("validade");
                    if (vd != null) l.setValidade(vd.toLocalDate());
                    l.setEnderecoUso(rs.getString("endereco_uso"));
                    l.setEnderecoEstoque(rs.getString("endereco_estoque"));
                    l.setIdFornecedor(rs.getInt("id_fornecedor"));
                    l.setNomeFornecedor(rs.getString("nome_fornecedor"));
                    listaLotes.add(l);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar lotes: " + e.getMessage());
        }
        tabelaLotes.setItems(listaLotes);
    }

    private void carregarSinonimos(int idMateriaPrima) {
        listaSinonimos.getItems().clear();
        idsSinonimos.clear();
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT id, sinonimo FROM tb_sinonimos WHERE id_materia_prima = ? ORDER BY sinonimo")) {
            stmt.setInt(1, idMateriaPrima);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    idsSinonimos.add(rs.getInt("id"));
                    listaSinonimos.getItems().add(rs.getString("sinonimo"));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar sinonimos: " + e.getMessage());
        }
    }

    @FXML
    private void adicionarSinonimo() {
        MateriaPrima selecionada = tabelaMateriasPrimas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            UiUtil.erro(mensagem, "Selecione uma matéria-prima primeiro!");
            return;
        }
        String sinonimo = campoSinonimo.getText().trim();
        if (sinonimo.isEmpty()) return;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO tb_sinonimos (id_materia_prima, sinonimo) VALUES (?, ?)")) {
            stmt.setInt(1, selecionada.getId());
            stmt.setString(2, sinonimo);
            stmt.executeUpdate();
            campoSinonimo.setText("");
            carregarSinonimos(selecionada.getId());
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao adicionar sinônimo: " + e.getMessage());
        }
    }

    @FXML
    private void removerSinonimo() {
        int idx = listaSinonimos.getSelectionModel().getSelectedIndex();
        if (idx < 0) return;
        int idSinonimo = idsSinonimos.get(idx);
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_sinonimos WHERE id = ?")) {
            stmt.setInt(1, idSinonimo);
            stmt.executeUpdate();
            MateriaPrima selecionada = tabelaMateriasPrimas.getSelectionModel().getSelectedItem();
            if (selecionada != null) carregarSinonimos(selecionada.getId());
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao remover sinônimo: " + e.getMessage());
        }
    }

    private void filtrar(String texto) {
        if (texto == null || texto.isEmpty()) {
            tabelaMateriasPrimas.setItems(listaCompleta);
            return;
        }
        ObservableList<MateriaPrima> filtrada = FXCollections.observableArrayList();
        for (MateriaPrima mp : listaCompleta) {
            if (mp.getNome().toLowerCase().contains(texto.toLowerCase())) {
                filtrada.add(mp);
            }
        }
        tabelaMateriasPrimas.setItems(filtrada);
    }

    private void preencherFormulario(MateriaPrima mp) {
        campoCodigo.setText(mp.getCodigo() != null ? mp.getCodigo() : "");
        campoNome.setText(mp.getNome());
        comboUnidade.getSelectionModel().select(mp.getUnidade());
        comboTipo.getSelectionModel().select(mp.getTipo());
        comboControlada.getSelectionModel().select(
            mp.getControladaTipo() != null ? mp.getControladaTipo() : "Nenhuma"
        );
        comboClasseAnvisa.getSelectionModel().select(
            mp.getClasseAnvisa() != null ? mp.getClasseAnvisa() : "C1"
        );
        campoDoseMinima.setText(String.valueOf(mp.getDoseMinima()));
        campoDoseMaxima.setText(String.valueOf(mp.getDoseMaxima()));
        campoVolume.setText(String.valueOf(mp.getVolume()));
        campoVolumeCaps.setText(String.valueOf(mp.getVolumeCaps()));
        campoPesoCaps.setText(String.valueOf(mp.getPesoCaps()));
        campoEstoqueMinimo.setText(String.valueOf(mp.getEstoqueMinimo()));
        campoEstoqueCritico.setText(String.valueOf(mp.getEstoqueCritico()));
        checkRotulo.setSelected(mp.isRotulo());
        checkGeladeira.setSelected(mp.isGeladeira());
        checkControlado.setSelected(mp.isControlado());
        campoObservacoes.setText(mp.getObservacoes() != null ? mp.getObservacoes() : "");
        atualizarPainelTipo(mp.getTipo());
        UiUtil.limpar(mensagem);
    }

    @FXML
    private void novo() {
        campoCodigo.setText(gerarCodigo());
        campoNome.setText("");
        comboUnidade.getSelectionModel().selectFirst();
        comboTipo.getSelectionModel().selectFirst();
        comboControlada.getSelectionModel().selectFirst();
        comboClasseAnvisa.getSelectionModel().selectFirst();
        campoDoseMinima.setText("0");
        campoDoseMaxima.setText("0");
        campoVolume.setText("0");
        campoVolumeCaps.setText("0");
        campoPesoCaps.setText("0");
        campoEstoqueMinimo.setText("0");
        campoEstoqueCritico.setText("0");
        checkRotulo.setSelected(false);
        checkGeladeira.setSelected(false);
        checkControlado.setSelected(false);
        campoObservacoes.setText("");
        campoSinonimo.setText("");
        listaSinonimos.getItems().clear();
        idsSinonimos.clear();
        atualizarPainelTipo(comboTipo.getValue());
        UiUtil.limpar(mensagem);
        tabelaMateriasPrimas.getSelectionModel().clearSelection();
        tabelaLotes.getItems().clear();
    }

    @FXML
    private void salvar() {
        if (campoNome.getText().isEmpty()) {
            UiUtil.erro(mensagem, "Preencha o nome!");
            return;
        }
        MateriaPrima selecionada = tabelaMateriasPrimas.getSelectionModel().getSelectedItem();
        try (Connection conn = Conexao.conectar()) {
            if (selecionada == null) {
                String sql = "INSERT INTO tb_materias_primas (codigo, nome, unidade, tipo, " +
                             "dose_minima, dose_maxima, volume, volume_caps, peso_caps, rotulo, " +
                             "geladeira, controlado, controlada_tipo, classe_anvisa, observacoes, " +
                             "estoque_minimo, estoque_critico) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    preencherParams(stmt, selecionada);
                    stmt.executeUpdate();
                }
                UiUtil.sucesso(mensagem, "Matéria-prima cadastrada com sucesso!");
            } else {
                String sql = "UPDATE tb_materias_primas SET codigo=?, nome=?, unidade=?, tipo=?, " +
                             "dose_minima=?, dose_maxima=?, volume=?, volume_caps=?, peso_caps=?, " +
                             "rotulo=?, geladeira=?, controlado=?, controlada_tipo=?, classe_anvisa=?, " +
                             "observacoes=?, estoque_minimo=?, estoque_critico=? WHERE id=?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    preencherParams(stmt, selecionada);
                    stmt.setInt(18, selecionada.getId());
                    stmt.executeUpdate();
                }
                UiUtil.sucesso(mensagem, "Matéria-prima atualizada com sucesso!");
            }
            carregarMateriasPrimas();
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao salvar: " + e.getMessage());
        }
    }

    private void preencherParams(PreparedStatement stmt, MateriaPrima ignorado) throws Exception {
        stmt.setString(1, campoCodigo.getText());
        stmt.setString(2, campoNome.getText());
        stmt.setString(3, comboUnidade.getValue());
        stmt.setString(4, comboTipo.getValue());
        stmt.setDouble(5, parseDouble(campoDoseMinima.getText(), 0));
        stmt.setDouble(6, parseDouble(campoDoseMaxima.getText(), 0));
        stmt.setDouble(7, parseDouble(campoVolume.getText(), 0));
        stmt.setDouble(8, parseDouble(campoVolumeCaps.getText(), 0));
        stmt.setDouble(9, parseDouble(campoPesoCaps.getText(), 0));
        stmt.setBoolean(10, checkRotulo.isSelected());
        stmt.setBoolean(11, checkGeladeira.isSelected());
        stmt.setBoolean(12, checkControlado.isSelected());
        stmt.setString(13, comboControlada.getValue());
        stmt.setString(14, comboClasseAnvisa.getValue());
        stmt.setString(15, campoObservacoes.getText());
        stmt.setDouble(16, parseDouble(campoEstoqueMinimo.getText(), 0));
        stmt.setDouble(17, parseDouble(campoEstoqueCritico.getText(), 0));
    }

    private double parseDouble(String texto, double padrao) {
        try { return Double.parseDouble(texto.isEmpty() ? String.valueOf(padrao) : texto); }
        catch (NumberFormatException e) { return padrao; }
    }

    @FXML
    private void excluir() {
        MateriaPrima selecionada = tabelaMateriasPrimas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            UiUtil.erro(mensagem, "Selecione uma matéria-prima!");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja excluir a matéria-prima \"" + selecionada.getNome() + "\"?\nTodos os lotes e sinônimos associados também serão excluídos.");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_materias_primas WHERE id=?")) {
            stmt.setInt(1, selecionada.getId());
            stmt.executeUpdate();
            carregarMateriasPrimas();
            novo();
            UiUtil.sucesso(mensagem, "Matéria-prima excluída com sucesso!");
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao excluir: " + e.getMessage());
        }
    }

    @FXML
    private void novoLote() {
        tabelaLotes.getSelectionModel().clearSelection();
        UiUtil.info(mensagem, "Selecione uma matéria-prima e clique em Salvar Lote para adicionar.");
    }

    @FXML
    private void salvarLote() {
        MateriaPrima selecionada = tabelaMateriasPrimas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            UiUtil.erro(mensagem, "Selecione uma matéria-prima primeiro!");
            return;
        }
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/thompharma/lote_dialog.fxml")
            );
            javafx.scene.Parent root = loader.load();
            LoteDialogController controller = loader.getController();
            controller.setDados(selecionada.getId(), this);

            javafx.stage.Stage dialog = new javafx.stage.Stage();
            dialog.setTitle("Novo Lote - " + selecionada.getNome());
            dialog.setScene(new javafx.scene.Scene(root));
            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialog.showAndWait();
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao abrir dialogo: " + e.getMessage());
        }
    }

    public void atualizarLotes(int idMateriaPrima) {
        carregarLotes(idMateriaPrima);
        carregarMateriasPrimas();
    }

    @FXML
    private void excluirLote() {
        Lote selecionado = tabelaLotes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            UiUtil.erro(mensagem, "Selecione um lote!");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja excluir o lote \"" + selecionado.getNomeLote() + "\"?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try (Connection conn = Conexao.conectar()) {
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_lotes WHERE id=?")) {
                stmt.setInt(1, selecionado.getId());
                stmt.executeUpdate();
            }
            int idMP = selecionado.getIdMateriaPrima();
            try (PreparedStatement upd = conn.prepareStatement(
                    "UPDATE tb_materias_primas SET saldo = " +
                    "(SELECT COALESCE(SUM(saldo), 0) FROM tb_lotes WHERE id_materia_prima = ?) " +
                    "WHERE id = ?")) {
                upd.setInt(1, idMP);
                upd.setInt(2, idMP);
                upd.executeUpdate();
            }
            MateriaPrima mp = tabelaMateriasPrimas.getSelectionModel().getSelectedItem();
            if (mp != null) carregarLotes(mp.getId());
            carregarMateriasPrimas();
            UiUtil.sucesso(mensagem, "Lote excluído com sucesso!");
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao excluir lote: " + e.getMessage());
        }
    }

    @FXML
    private void fechar() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/thompharma/principal.fxml")
            );
            App.getStage().getScene().setRoot(loader.load());
            App.getStage().setMaximized(true);
        } catch (Exception e) {
            System.err.println("Erro ao fechar: " + e.getMessage());
        }
    }

    private String gerarCodigo() {
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT codigo FROM tb_materias_primas ORDER BY id DESC LIMIT 1")) {
            int num = 1;
            if (rs.next() && rs.getString("codigo") != null) {
                String ultimo = rs.getString("codigo");
                if (ultimo.contains("-")) {
                    num = Integer.parseInt(ultimo.split("-")[1]) + 1;
                }
            }
            return String.format("MP-%04d", num);
        } catch (Exception e) {
            return "MP-0001";
        }
    }
}
