package thompharma.telas;

import thompharma.App;
import thompharma.Conexao;
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

/**
 * controller do cadastro de usuarios do sistema
 * permite criar, editar e excluir usuarios
 * controla permissoes de administrador e status ativo/inativo
 */
public class UsuariosController {

    // componentes da tabela de listagem
    @FXML private TableView<thompharma.modelo.Usuario> tabelaUsuarios;
    @FXML private TableColumn<thompharma.modelo.Usuario, String> colUsuario;
    @FXML private TableColumn<thompharma.modelo.Usuario, String> colNome;

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
        
        tabelaUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * busca todos os usuarios do banco e exibe na tabela
     */
    private void carregarUsuarios() {
        ObservableList<thompharma.modelo.Usuario> lista = FXCollections.observableArrayList();
        try {
            Connection conn = Conexao.conectar();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM tb_usuarios ORDER BY usuario"
            );
            while (rs.next()) {
                thompharma.modelo.Usuario u = new thompharma.modelo.Usuario();
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
    private void preencherFormulario(thompharma.modelo.Usuario u) {
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
        thompharma.modelo.Usuario selecionado = tabelaUsuarios.getSelectionModel().getSelectedItem();
        boolean inserindo = (selecionado == null);

        if (campoUsuario.getText().isEmpty()) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Preencha o nome de usuario!");
            return;
        }
        if (inserindo && campoSenha.getText().isEmpty()) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Preencha a senha para novo usuario!");
            return;
        }

        try (Connection conn = Conexao.conectar()) {
            if (inserindo) {
                String hash = BCrypt.hashpw(campoSenha.getText(), BCrypt.gensalt());
                PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO tb_usuarios (usuario, nome_completo, senha, admin, ativo) VALUES (?, ?, ?, ?, ?)");
                stmt.setString(1, campoUsuario.getText());
                stmt.setString(2, campoNome.getText());
                stmt.setString(3, hash);
                stmt.setBoolean(4, checkAdmin.isSelected());
                stmt.setBoolean(5, checkAtivo.isSelected());
                stmt.executeUpdate();
                mensagem.setStyle("-fx-text-fill: #9ece6a;");
                mensagem.setText("Usuario cadastrado com sucesso!");
            } else {
                if (!campoSenha.getText().isEmpty()) {
                    // atualiza incluindo nova senha
                    String hash = BCrypt.hashpw(campoSenha.getText(), BCrypt.gensalt());
                    PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE tb_usuarios SET usuario=?, nome_completo=?, senha=?, admin=?, ativo=? WHERE id=?");
                    stmt.setString(1, campoUsuario.getText());
                    stmt.setString(2, campoNome.getText());
                    stmt.setString(3, hash);
                    stmt.setBoolean(4, checkAdmin.isSelected());
                    stmt.setBoolean(5, checkAtivo.isSelected());
                    stmt.setInt(6, selecionado.getId());
                    stmt.executeUpdate();
                } else {
                    // atualiza sem alterar a senha
                    PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE tb_usuarios SET usuario=?, nome_completo=?, admin=?, ativo=? WHERE id=?");
                    stmt.setString(1, campoUsuario.getText());
                    stmt.setString(2, campoNome.getText());
                    stmt.setBoolean(3, checkAdmin.isSelected());
                    stmt.setBoolean(4, checkAtivo.isSelected());
                    stmt.setInt(5, selecionado.getId());
                    stmt.executeUpdate();
                }
                mensagem.setStyle("-fx-text-fill: #9ece6a;");
                mensagem.setText("Usuario atualizado com sucesso!");
            }
            carregarUsuarios();
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: #f7768e;");
            mensagem.setText("Erro ao salvar: " + e.getMessage());
        }
    }

    /**
     * exclui o usuario selecionado na tabela
     * exibe dialogo de confirmacao antes de executar a exclusao
     */
    @FXML
    private void excluir() {
        thompharma.modelo.Usuario selecionado = tabelaUsuarios.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mensagem.setText("Selecione um usuario!");
            return;
        }
        // pede confirmacao antes de excluir para evitar exclusoes acidentais
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja excluir o usuário \"" + selecionado.getUsuario() + "\"?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_usuarios WHERE id=?");
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
        App.trocarTela("principal");
    }
}