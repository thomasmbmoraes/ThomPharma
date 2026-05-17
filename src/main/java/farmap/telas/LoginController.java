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

        // valida se os campos foram preenchidos
        if (usuario.isEmpty() || senha.isEmpty()) {
            mensagemErro.setText("Preencha usuario e senha!");
            return;
        }

        try {
            Connection conn = Conexao.conectar();

            // busca o usuario no banco verificando usuario, senha e se esta ativo
            String sql = "SELECT * FROM usuarios WHERE usuario = ? AND senha = ? AND ativo = true";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // login bem sucedido, abre a tela principal passando o nome do usuario
                String nomeCompleto = rs.getString("nome_completo");
                conn.close();

                FXMLLoader loader = App.getLoader("principal");
                Scene scene = new Scene(loader.load(), 600, 400);
                PrincipalController controller = loader.getController();
                controller.setUsuario(nomeCompleto);
                App.getStage().setTitle("Farmap");
                App.getStage().setScene(scene);
            } else {
                // usuario ou senha incorretos
                mensagemErro.setText("Usuario ou senha incorretos!");
                conn.close();
            }

        } catch (Exception e) {
            mensagemErro.setText("Erro ao conectar com banco!");
            System.out.println(e.getMessage());
        }
    }
}