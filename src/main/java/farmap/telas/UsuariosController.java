package farmap.telas;

import farmap.App;
import farmap.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class UsuariosController {

    @FXML private TableView<farmap.modelo.Usuario> tabelaUsuarios;
    @FXML private TableColumn<farmap.modelo.Usuario, String> colUsuario;
    @FXML private TableColumn<farmap.modelo.Usuario, String> colNome;
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
        carregarUsuarios();

        tabelaUsuarios.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> {
                if (novo != null) preencherFormulario(novo);
            }
        );
    }

    private void carregarUsuarios() {
        ObservableList<farmap.modelo.Usuario> lista = FXCollections.observableArrayList();
        try {
            Connection conn = Conexao.conectar();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM usuarios ORDER BY usuario");
            while (rs.next()) {
                farmap.modelo.Usuario u = new farmap.modelo.Usuario();
                u.setId(rs.getInt("id"));
                u.setUsuario(rs.getString("usuario"));
                u.setNomeCompleto(rs.getString("nome_completo"));
                u.setAdmin(rs.getBoolean("admin"));
                u.setAtivo(rs.getBoolean("ativo"));
                lista.add(u);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Erro ao carregar usuarios: " + e.getMessage());
        }
        tabelaUsuarios.setItems(lista);
    }

    private void preencherFormulario(farmap.modelo.Usuario u) {
        campoUsuario.setText(u.getUsuario());
        campoNome.setText(u.getNomeCompleto());
        campoSenha.setText("");
        checkAdmin.setSelected(u.isAdmin());
        checkAtivo.setSelected(u.isAtivo());
        mensagem.setText("");
    }

    @FXML
    private void novo() {
        campoUsuario.setText("");
        campoNome.setText("");
        campoSenha.setText("");
        checkAdmin.setSelected(false);
        checkAtivo.setSelected(true);
        mensagem.setText("");
        tabelaUsuarios.getSelectionModel().clearSelection();
    }

    @FXML
    private void salvar() {
        if (campoUsuario.getText().isEmpty() || campoSenha.getText().isEmpty()) {
            mensagem.setText("Preencha usuario e senha!");
            return;
        }
        try {
            Connection conn = Conexao.conectar();
            farmap.modelo.Usuario selecionado = tabelaUsuarios.getSelectionModel().getSelectedItem();

            if (selecionado == null) {
                String sql = "INSERT INTO usuarios (usuario, nome_completo, senha, admin, ativo) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, campoUsuario.getText());
                stmt.setString(2, campoNome.getText());
                stmt.setString(3, campoSenha.getText());
                stmt.setBoolean(4, checkAdmin.isSelected());
                stmt.setBoolean(5, checkAtivo.isSelected());
                stmt.executeUpdate();
                mensagem.setStyle("-fx-text-fill: green;");
                mensagem.setText("Usuario cadastrado com sucesso!");
            } else {
                String sql = "UPDATE usuarios SET usuario=?, nome_completo=?, admin=?, ativo=? WHERE id=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, campoUsuario.getText());
                stmt.setString(2, campoNome.getText());
                stmt.setBoolean(3, checkAdmin.isSelected());
                stmt.setBoolean(4, checkAtivo.isSelected());
                stmt.setInt(5, selecionado.getId());
                stmt.executeUpdate();
                mensagem.setStyle("-fx-text-fill: green;");
                mensagem.setText("Usuario atualizado com sucesso!");
            }
            conn.close();
            carregarUsuarios();
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void excluir() {
        farmap.modelo.Usuario selecionado = tabelaUsuarios.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mensagem.setText("Selecione um usuario!");
            return;
        }
        try {
            Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM usuarios WHERE id=?");
            stmt.setInt(1, selecionado.getId());
            stmt.executeUpdate();
            conn.close();
            carregarUsuarios();
            novo();
            mensagem.setStyle("-fx-text-fill: green;");
            mensagem.setText("Usuario excluido com sucesso!");
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao excluir: " + e.getMessage());
        }
    }

    @FXML
    private void fechar() {
        try {
            App.getStage().getScene().setRoot(
                new javafx.fxml.FXMLLoader(App.class.getResource("principal.fxml")).load()
            );
        } catch (Exception e) {
            System.out.println("Erro ao fechar: " + e.getMessage());
        }
    }
}