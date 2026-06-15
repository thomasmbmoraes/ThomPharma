package thompharma.telas;

import thompharma.App;
import thompharma.Conexao;
import thompharma.Mascara;
import thompharma.UiUtil;
import thompharma.modelo.Fornecedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class FornecedoresController {

    @FXML private TableView<Fornecedor> tabelaFornecedores;
    @FXML private TableColumn<Fornecedor, String> colNome;
    @FXML private TextField campoFiltro;
    @FXML private TextField campoNome;
    @FXML private TextField campoCnpj;
    @FXML private TextField campoContato;
    @FXML private TextField campoTelefone;
    @FXML private TextField campoEmail;
    @FXML private TextField campoCidade;
    @FXML private TextField campoUf;
    @FXML private Label mensagem;
    @FXML private RadioButton radioCpf;
    @FXML private RadioButton radioCnpj;

    private ObservableList<Fornecedor> listaCompleta = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        Mascara.cnpj(campoCnpj);
        Mascara.telefone(campoTelefone);

        radioCpf.setOnAction(e -> { campoCnpj.setText(""); Mascara.cpf(campoCnpj); });
        radioCnpj.setOnAction(e -> { campoCnpj.setText(""); Mascara.cnpj(campoCnpj); });

        carregarFornecedores();

        tabelaFornecedores.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> { if (novo != null) preencherFormulario(novo); }
        );
        campoFiltro.textProperty().addListener((obs, a, novo) -> filtrar(novo));
        tabelaFornecedores.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabelaFornecedores.setPlaceholder(new Label("Nenhum fornecedor cadastrado."));
    }

    private void carregarFornecedores() {
        listaCompleta.clear();
        try (Connection conn = Conexao.conectar();
             ResultSet rs = conn.createStatement().executeQuery(
                 "SELECT * FROM tb_fornecedores ORDER BY nome")) {
            while (rs.next()) {
                Fornecedor f = new Fornecedor();
                f.setId(rs.getInt("id"));
                f.setNome(rs.getString("nome"));
                f.setCnpjCpf(rs.getString("cnpj_cpf"));
                f.setContato(rs.getString("contato"));
                f.setTelefone(rs.getString("telefone"));
                f.setEmail(rs.getString("email"));
                f.setCidade(rs.getString("cidade"));
                f.setUf(rs.getString("uf"));
                listaCompleta.add(f);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar fornecedores: " + e.getMessage());
        }
        tabelaFornecedores.setItems(listaCompleta);
    }

    private void filtrar(String texto) {
        if (texto == null || texto.isEmpty()) {
            tabelaFornecedores.setItems(listaCompleta);
            return;
        }
        ObservableList<Fornecedor> filtrada = FXCollections.observableArrayList();
        for (Fornecedor f : listaCompleta) {
            if (f.getNome().toLowerCase().contains(texto.toLowerCase())) filtrada.add(f);
        }
        tabelaFornecedores.setItems(filtrada);
    }

    private void preencherFormulario(Fornecedor f) {
        campoNome.setText(f.getNome());
        campoCnpj.setText(f.getCnpjCpf() != null ? f.getCnpjCpf() : "");
        campoContato.setText(f.getContato() != null ? f.getContato() : "");
        campoTelefone.setText(f.getTelefone() != null ? f.getTelefone() : "");
        campoEmail.setText(f.getEmail() != null ? f.getEmail() : "");
        campoCidade.setText(f.getCidade() != null ? f.getCidade() : "");
        campoUf.setText(f.getUf() != null ? f.getUf() : "");
        mensagem.setText("");
    }

    @FXML
    private void novo() {
        campoNome.setText(""); campoCnpj.setText(""); campoContato.setText("");
        campoTelefone.setText(""); campoEmail.setText(""); campoCidade.setText("");
        campoUf.setText(""); mensagem.setText("");
        tabelaFornecedores.getSelectionModel().clearSelection();
    }

    @FXML
    private void salvar() {
        if (campoNome.getText().isEmpty()) {
            UiUtil.erro(mensagem, "Preencha o nome!");
            return;
        }
        try (Connection conn = Conexao.conectar()) {
            Fornecedor sel = tabelaFornecedores.getSelectionModel().getSelectedItem();
            if (sel == null) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO tb_fornecedores (nome, cnpj_cpf, contato, telefone, email, cidade, uf) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    stmt.setString(1, campoNome.getText()); stmt.setString(2, campoCnpj.getText());
                    stmt.setString(3, campoContato.getText()); stmt.setString(4, campoTelefone.getText());
                    stmt.setString(5, campoEmail.getText()); stmt.setString(6, campoCidade.getText());
                    stmt.setString(7, campoUf.getText());
                    stmt.executeUpdate();
                }
                UiUtil.sucesso(mensagem, "Fornecedor cadastrado com sucesso!");
            } else {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE tb_fornecedores SET nome=?, cnpj_cpf=?, contato=?, telefone=?, email=?, cidade=?, uf=? WHERE id=?")) {
                    stmt.setString(1, campoNome.getText()); stmt.setString(2, campoCnpj.getText());
                    stmt.setString(3, campoContato.getText()); stmt.setString(4, campoTelefone.getText());
                    stmt.setString(5, campoEmail.getText()); stmt.setString(6, campoCidade.getText());
                    stmt.setString(7, campoUf.getText()); stmt.setInt(8, sel.getId());
                    stmt.executeUpdate();
                }
                UiUtil.sucesso(mensagem, "Fornecedor atualizado com sucesso!");
            }
            carregarFornecedores();
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void excluir() {
        Fornecedor sel = tabelaFornecedores.getSelectionModel().getSelectedItem();
        if (sel == null) { mensagem.setText("Selecione um fornecedor!"); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja excluir o fornecedor \"" + sel.getNome() + "\"?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_fornecedores WHERE id=?")) {
            stmt.setInt(1, sel.getId());
            stmt.executeUpdate();
            carregarFornecedores();
            novo();
            UiUtil.sucesso(mensagem, "Fornecedor excluído com sucesso!");
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao excluir: " + e.getMessage());
        }
    }

    @FXML
    private void fechar() { App.trocarTela("principal"); }
}
