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

/**
 * controller do cadastro de usuarios do sistema
 * permite criar, editar e excluir usuarios
 * controla permissoes de administrador e status ativo/inativo
 */
public class UsuariosController {

    // componentes da tabela de listagem
    @FXML private TableView<farmap.modelo.Usuario> tabelaUsuarios;
    @FXML private TableColumn<farmap.modelo.Usuario, String> colUsuario;
    @FXML private TableColumn<farmap.modelo.Usuario, String> colNome;

    // campos do formulario
    @FXML private TextField campoUsuario;
    @FXML private TextField campoNome;
    @FXML private PasswordField campoSenha;
    @FXML private CheckBox checkAdmin;
    @FXML private CheckBox checkAtivo;
    @FXML private Label mensagem;

    /**
     * executado automaticamente ao carregar a tela
     * configura as colunas da tabela e carrega os usuarios do banco
     */
    @FXML
    public void initialize() {
        // configura quais atributos do modelo aparecem em cada coluna
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nomeCompleto"));
        carregarUsuarios();

        // ao selecionar um usuario na tabela, preenche o formulario automaticamente
        tabelaUsuarios.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> {
                if (novo != null) preencherFormulario(novo);
            }
        );
    }

    /**
     * busca todos os usuarios do banco e exibe na tabela
     */
    private void carregarUsuarios() {
        ObservableList<farmap.modelo.Usuario> lista = FXCollections.observableArrayList();
        try {
            Connection conn = Conexao.conectar();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM usuarios ORDER BY usuario"
            );
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

    /**
     * preenche o formulario com os dados do usuario selecionado na tabela
     * @param u usuario selecionado
     */
    private void preencherFormulario(farmap.modelo.Usuario u) {
        campoUsuario.setText(u.getUsuario());
        campoNome.setText(u.getNomeCompleto());
        campoSenha.setText("");
        checkAdmin.setSelected(u.isAdmin());
        checkAtivo.setSelected(u.isAtivo());
        mensagem.setText("");
    }

    /**
     * limpa o formulario para cadastrar um novo usuario
     */
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

    /**
     * salva o usuario no banco
     * se nenhum usuario estiver selecionado na tabela, insere um novo
     * se um usuario estiver selecionado, atualiza os dados dele
     */
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
                // insere novo usuario
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
                // atualiza usuario existente
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

    /**
     * exclui o usuario selecionado na tabela
     */
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

    /**
     * fecha a tela de usuarios e volta para a tela principal
     */
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