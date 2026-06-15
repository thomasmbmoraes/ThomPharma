package thompharma.telas;

import thompharma.App;
import thompharma.service.UsuarioService;
import thompharma.modelo.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller de login.
 * A proteção brute-force é persistida em tb_tentativas_login via UsuarioService,
 * sobrevivendo a reinicializações da aplicação.
 */
public class LoginController {

    @FXML private TextField campoUsuario;
    @FXML private PasswordField campoSenha;
    @FXML private Label mensagemErro;

    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    private void fazerLogin() {
        String usuario = campoUsuario.getText().trim();
        String senha = campoSenha.getText();

        if (usuario.isEmpty() || senha.isEmpty()) {
            mensagemErro.setText("Preencha usuário e senha!");
            return;
        }

        try {
            UsuarioService.ResultadoLogin resultado = usuarioService.autenticar(usuario, senha);

            if (!resultado.sucesso()) {
                mensagemErro.setText(resultado.mensagem());
                return;
            }

            Usuario u = resultado.usuario();
            App.setAdmin(u.isAdmin());
            App.setNomeUsuarioLogado(u.getNomeCompleto());

            FXMLLoader loader = App.getLoader("principal");
            App.getStage().getScene().setRoot(loader.load());
            PrincipalController controller = loader.getController();
            controller.setUsuario(App.getNomeUsuarioLogado());
            App.getStage().setTitle("ThomPharma");
            App.getStage().setMaximized(true);

        } catch (Exception e) {
            mensagemErro.setText("Erro ao conectar com banco!");
            System.err.println(e.getMessage());
        }
    }
}
