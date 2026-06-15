package thompharma.telas;

import thompharma.App;
import thompharma.Conexao;
import thompharma.Mascara;
import thompharma.UiUtil;
import thompharma.modelo.Prescritor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class PrescritoresController {

    @FXML private TableView<Prescritor> tabelaPrescritores;
    @FXML private TableColumn<Prescritor, String> colNome;
    @FXML private TableColumn<Prescritor, String> colTipo;
    @FXML private TableColumn<Prescritor, String> colRegistro;
    @FXML private TextField campoFiltro;
    @FXML private ComboBox<String> comboFiltroTipo;
    @FXML private TextField campoNome;
    @FXML private ComboBox<String> comboTipoRegistro;
    @FXML private TextField campoNumeroRegistro;
    @FXML private TextField campoTelefone;
    @FXML private TextField campoEmail;
    @FXML private TextArea campoObservacoes;
    @FXML private Label mensagem;

    private ObservableList<Prescritor> listaCompleta = FXCollections.observableArrayList();

    private static final String[] TIPOS = {
        "CRM", "CRO", "CRV", "CRP", "CRN", "CREFITO", "Terapeuta", "Outro"
    };

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoRegistro"));
        colRegistro.setCellValueFactory(new PropertyValueFactory<>("numeroRegistro"));
        tabelaPrescritores.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        comboTipoRegistro.getItems().addAll(TIPOS);
        comboTipoRegistro.getSelectionModel().selectFirst();

        comboFiltroTipo.getItems().add("Todos");
        comboFiltroTipo.getItems().addAll(TIPOS);
        comboFiltroTipo.getSelectionModel().selectFirst();

        Mascara.telefone(campoTelefone);
        tabelaPrescritores.setPlaceholder(new Label("Nenhum prescritor cadastrado."));

        carregarPrescritores();

        tabelaPrescritores.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> { if (novo != null) preencherFormulario(novo); }
        );
        campoFiltro.textProperty().addListener((obs, antigo, novo) -> filtrar());
        comboFiltroTipo.setOnAction(e -> filtrar());
    }

    private void carregarPrescritores() {
        listaCompleta.clear();
        try (Connection conn = Conexao.conectar();
             ResultSet rs = conn.createStatement().executeQuery(
                 "SELECT * FROM tb_prescritores ORDER BY nome")) {
            while (rs.next()) {
                Prescritor p = new Prescritor();
                p.setId(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setTipoRegistro(rs.getString("tipo_registro"));
                p.setNumeroRegistro(rs.getString("numero_registro"));
                p.setTelefone(rs.getString("telefone"));
                p.setEmail(rs.getString("email"));
                p.setObservacoes(rs.getString("observacoes"));
                listaCompleta.add(p);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar prescritores: " + e.getMessage());
        }
        tabelaPrescritores.setItems(listaCompleta);
    }

    private void filtrar() {
        String texto = campoFiltro.getText();
        String tipo = comboFiltroTipo.getValue();
        if ((texto == null || texto.isEmpty()) && "Todos".equals(tipo)) {
            tabelaPrescritores.setItems(listaCompleta);
            return;
        }
        ObservableList<Prescritor> filtrada = FXCollections.observableArrayList();
        for (Prescritor p : listaCompleta) {
            boolean passaNome = texto == null || texto.isEmpty()
                || p.getNome().toLowerCase().contains(texto.toLowerCase());
            boolean passaTipo = "Todos".equals(tipo) || tipo.equals(p.getTipoRegistro());
            if (passaNome && passaTipo) filtrada.add(p);
        }
        tabelaPrescritores.setItems(filtrada);
    }

    private void preencherFormulario(Prescritor p) {
        campoNome.setText(p.getNome());
        comboTipoRegistro.getSelectionModel().select(p.getTipoRegistro());
        campoNumeroRegistro.setText(p.getNumeroRegistro() != null ? p.getNumeroRegistro() : "");
        campoTelefone.setText(p.getTelefone() != null ? p.getTelefone() : "");
        campoEmail.setText(p.getEmail() != null ? p.getEmail() : "");
        campoObservacoes.setText(p.getObservacoes() != null ? p.getObservacoes() : "");
        UiUtil.limpar(mensagem);
    }

    @FXML
    private void novo() {
        campoNome.setText("");
        comboTipoRegistro.getSelectionModel().selectFirst();
        campoNumeroRegistro.setText(""); campoTelefone.setText("");
        campoEmail.setText(""); campoObservacoes.setText("");
        UiUtil.limpar(mensagem);
        tabelaPrescritores.getSelectionModel().clearSelection();
    }

    @FXML
    private void salvar() {
        if (campoNome.getText().isEmpty()) {
            UiUtil.erro(mensagem, "Preencha o nome!");
            return;
        }
        String nome = Mascara.padronizarNome(campoNome.getText());
        Prescritor selecionado = tabelaPrescritores.getSelectionModel().getSelectedItem();

        try (Connection conn = Conexao.conectar()) {
            if (!campoNumeroRegistro.getText().isEmpty()) {
                try (PreparedStatement check = conn.prepareStatement(
                        "SELECT id FROM tb_prescritores WHERE numero_registro = ? AND tipo_registro = ?")) {
                    check.setString(1, campoNumeroRegistro.getText());
                    check.setString(2, comboTipoRegistro.getValue());
                    try (ResultSet rs = check.executeQuery()) {
                        if (rs.next()) {
                            int idEncontrado = rs.getInt("id");
                            if (selecionado == null || idEncontrado != selecionado.getId()) {
                                UiUtil.erro(mensagem, "Já existe um prescritor com este número de registro!");
                                return;
                            }
                        }
                    }
                }
            }

            if (selecionado == null) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO tb_prescritores (nome, tipo_registro, numero_registro, telefone, email, observacoes) " +
                        "VALUES (?, ?, ?, ?, ?, ?)")) {
                    stmt.setString(1, nome); stmt.setString(2, comboTipoRegistro.getValue());
                    stmt.setString(3, campoNumeroRegistro.getText()); stmt.setString(4, campoTelefone.getText());
                    stmt.setString(5, campoEmail.getText()); stmt.setString(6, campoObservacoes.getText());
                    stmt.executeUpdate();
                }
                UiUtil.sucesso(mensagem, "Prescritor cadastrado com sucesso!");
            } else {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE tb_prescritores SET nome=?, tipo_registro=?, numero_registro=?, " +
                        "telefone=?, email=?, observacoes=? WHERE id=?")) {
                    stmt.setString(1, nome); stmt.setString(2, comboTipoRegistro.getValue());
                    stmt.setString(3, campoNumeroRegistro.getText()); stmt.setString(4, campoTelefone.getText());
                    stmt.setString(5, campoEmail.getText()); stmt.setString(6, campoObservacoes.getText());
                    stmt.setInt(7, selecionado.getId());
                    stmt.executeUpdate();
                }
                UiUtil.sucesso(mensagem, "Prescritor atualizado com sucesso!");
            }
            carregarPrescritores();
            campoNome.setText(nome);
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void excluir() {
        Prescritor selecionado = tabelaPrescritores.getSelectionModel().getSelectedItem();
        if (selecionado == null) { UiUtil.erro(mensagem, "Selecione um prescritor!"); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja excluir o prescritor \"" + selecionado.getNome() + "\"?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_prescritores WHERE id=?")) {
            stmt.setInt(1, selecionado.getId());
            stmt.executeUpdate();
            carregarPrescritores();
            novo();
            UiUtil.sucesso(mensagem, "Prescritor excluído com sucesso!");
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao excluir: " + e.getMessage());
        }
    }

    @FXML
    private void fechar() { App.trocarTela("principal"); }
}
