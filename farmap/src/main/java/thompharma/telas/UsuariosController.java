package thompharma.telas;

import thompharma.App;
import thompharma.Conexao;
import thompharma.UiUtil;
import thompharma.modelo.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class UsuariosController {

    @FXML private TableView<Usuario> tabelaUsuarios;
    @FXML private TableColumn<Usuario, String> colUsuario;
    @FXML private TableColumn<Usuario, String> colNome;
    @FXML private TextField campoUsuario;
    @FXML private TextField campoNome;
    @FXML private PasswordField campoSenha;
    @FXML private CheckBox checkAdmin;
    @FXML private CheckBox checkAtivo;
    @FXML private Label mensagem;

    @FXML
    public void initialize() {
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nomeCompleto"));
        tabelaUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabelaUsuarios.setPlaceholder(new Label("Nenhum usuário cadastrado."));
        carregarUsuarios();
        tabelaUsuarios.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> { if (novo != null) preencherFormulario(novo); }
        );
    }

    private void carregarUsuarios() {
        ObservableList<Usuario> lista = FXCollections.observableArrayList();
        try (Connection conn = Conexao.conectar();
             ResultSet rs = conn.createStatement().executeQuery(
                 "SELECT id, usuario, nome_completo, admin, ativo FROM tb_usuarios ORDER BY usuario")) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setUsuario(rs.getString("usuario"));
                u.setNomeCompleto(rs.getString("nome_completo"));
                u.setAdmin(rs.getBoolean("admin"));
                u.setAtivo(rs.getBoolean("ativo"));
                lista.add(u);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar usuarios: " + e.getMessage());
        }
        tabelaUsuarios.setItems(lista);
    }

    private void preencherFormulario(Usuario u) {
        campoUsuario.setText(u.getUsuario());
        campoNome.setText(u.getNomeCompleto());
        campoSenha.setText("");
        checkAdmin.setSelected(u.isAdmin());
        checkAtivo.setSelected(u.isAtivo());
        UiUtil.limpar(mensagem);
    }

    @FXML
    private void novo() {
        campoUsuario.setText(""); campoNome.setText(""); campoSenha.setText("");
        checkAdmin.setSelected(false); checkAtivo.setSelected(true);
        UiUtil.limpar(mensagem);
        tabelaUsuarios.getSelectionModel().clearSelection();
    }

    @FXML
    private void salvar() {
        Usuario selecionado = tabelaUsuarios.getSelectionModel().getSelectedItem();
        boolean inserindo = (selecionado == null);

        if (campoUsuario.getText().isEmpty()) {
            UiUtil.erro(mensagem, "Preencha o nome de usuario!");
            return;
        }
        if (inserindo && campoSenha.getText().isEmpty()) {
            UiUtil.erro(mensagem, "Preencha a senha para novo usuario!");
            return;
        }

        try (Connection conn = Conexao.conectar()) {
            if (inserindo) {
                String hash = BCrypt.hashpw(campoSenha.getText(), BCrypt.gensalt());
                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO tb_usuarios (usuario, nome_completo, senha, admin, ativo) VALUES (?, ?, ?, ?, ?)")) {
                    stmt.setString(1, campoUsuario.getText()); stmt.setString(2, campoNome.getText());
                    stmt.setString(3, hash); stmt.setBoolean(4, checkAdmin.isSelected());
                    stmt.setBoolean(5, checkAtivo.isSelected());
                    stmt.executeUpdate();
                }
                UiUtil.sucesso(mensagem, "Usuario cadastrado com sucesso!");
            } else {
                if (!campoSenha.getText().isEmpty()) {
                    String hash = BCrypt.hashpw(campoSenha.getText(), BCrypt.gensalt());
                    try (PreparedStatement stmt = conn.prepareStatement(
                            "UPDATE tb_usuarios SET usuario=?, nome_completo=?, senha=?, admin=?, ativo=? WHERE id=?")) {
                        stmt.setString(1, campoUsuario.getText()); stmt.setString(2, campoNome.getText());
                        stmt.setString(3, hash); stmt.setBoolean(4, checkAdmin.isSelected());
                        stmt.setBoolean(5, checkAtivo.isSelected()); stmt.setInt(6, selecionado.getId());
                        stmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement stmt = conn.prepareStatement(
                            "UPDATE tb_usuarios SET usuario=?, nome_completo=?, admin=?, ativo=? WHERE id=?")) {
                        stmt.setString(1, campoUsuario.getText()); stmt.setString(2, campoNome.getText());
                        stmt.setBoolean(3, checkAdmin.isSelected()); stmt.setBoolean(4, checkAtivo.isSelected());
                        stmt.setInt(5, selecionado.getId());
                        stmt.executeUpdate();
                    }
                }
                UiUtil.sucesso(mensagem, "Usuario atualizado com sucesso!");
            }
            carregarUsuarios();
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void excluir() {
        Usuario selecionado = tabelaUsuarios.getSelectionModel().getSelectedItem();
        if (selecionado == null) { UiUtil.erro(mensagem, "Selecione um usuario!"); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja excluir o usuário \"" + selecionado.getUsuario() + "\"?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_usuarios WHERE id=?")) {
            stmt.setInt(1, selecionado.getId());
            stmt.executeUpdate();
            carregarUsuarios();
            novo();
            UiUtil.sucesso(mensagem, "Usuario excluido com sucesso!");
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao excluir: " + e.getMessage());
        }
    }

    @FXML
    private void fechar() { App.trocarTela("principal"); }
}
