package farmap.telas;

import farmap.App;
import farmap.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * controller da tela de login
 * responsavel por autenticar o usuario no banco de dados
 * e redirecionar para a tela principal em caso de sucesso
 */
public class LoginController {

    // campos da tela de login
    @FXML private TextField campoUsuario;
    @FXML private PasswordField campoSenha;
    @FXML private Label mensagemErro;

    /**
     * executado ao clicar no botao entrar
     * valida os campos, consulta o banco e abre a tela principal
     */
    @FXML
    private void fazerLogin() {
        String usuario = campoUsuario.getText();
        String senha = campoSenha.getText();

        if (usuario.isEmpty() || senha.isEmpty()) {
            mensagemErro.setText("Preencha usuario e senha!");
            return;
        }

        try {
            Connection conn = Conexao.conectar();
            String sql = "SELECT * FROM tb_usuarios WHERE usuario = ? AND senha = ? AND ativo = true";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nomeCompleto = rs.getString("nome_completo");
                conn.close();

                FXMLLoader loader = App.getLoader("principal");
                App.getStage().getScene().setRoot(loader.load());
                PrincipalController controller = loader.getController();
                controller.setUsuario(nomeCompleto);
                App.getStage().setTitle("ThomPharma");
                App.getStage().setMaximized(true);
            } else {
                mensagemErro.setText("Usuario ou senha incorretos!");
                conn.close();
            }

        } catch (Exception e) {
            mensagemErro.setText("Erro ao conectar com banco!");
            System.out.println(e.getMessage());
        }
    }
}