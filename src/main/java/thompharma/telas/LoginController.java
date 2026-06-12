package thompharma.telas;

import thompharma.App;
import thompharma.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController {

    @FXML private TextField campoUsuario;
    @FXML private PasswordField campoSenha;
    @FXML private Label mensagemErro;

    @FXML
    private void fazerLogin() {
        String usuario = campoUsuario.getText();
        String senha = campoSenha.getText();

        if (usuario.isEmpty() || senha.isEmpty()) {
            mensagemErro.setText("Preencha usuario e senha!");
            return;
        }

        try (Connection conn = Conexao.conectar()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, nome_completo, senha, admin FROM tb_usuarios WHERE usuario = ? AND ativo = true"
            );
            stmt.setString(1, usuario);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                mensagemErro.setText("Usuario ou senha incorretos!");
                return;
            }

            String hashArmazenado = rs.getString("senha");
            boolean senhaCorreta;

            if (hashArmazenado != null && hashArmazenado.startsWith("$2a$")) {
                // senha já em bcrypt
                senhaCorreta = BCrypt.checkpw(senha, hashArmazenado);
            } else {
                // senha em texto puro (contas antigas) — migra automaticamente ao logar
                senhaCorreta = senha.equals(hashArmazenado);
                if (senhaCorreta) {
                    String novoHash = BCrypt.hashpw(senha, BCrypt.gensalt());
                    try (PreparedStatement upd = conn.prepareStatement(
                            "UPDATE tb_usuarios SET senha = ? WHERE id = ?")) {
                        upd.setString(1, novoHash);
                        upd.setInt(2, rs.getInt("id"));
                        upd.executeUpdate();
                    }
                }
            }

            if (!senhaCorreta) {
                mensagemErro.setText("Usuario ou senha incorretos!");
                return;
            }

            App.setAdmin(rs.getBoolean("admin"));
            App.setNomeUsuarioLogado(rs.getString("nome_completo"));

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
