package farmap.telas;

import farmap.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField campoUsuario;

    @FXML
    private PasswordField campoSenha;

    @FXML
    private Label mensagemErro;

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
            String sql = "SELECT * FROM usuarios WHERE usuario = ? AND senha = ? AND ativo = true";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Login realizado com sucesso! Bem vindo " + rs.getString("nome_completo"));
                mensagemErro.setStyle("-fx-text-fill: green;");
                mensagemErro.setText("Login realizado com sucesso!");
            } else {
                mensagemErro.setText("Usuario ou senha incorretos!");
            }

            conn.close();
        } catch (Exception e) {
            mensagemErro.setText("Erro ao conectar com banco!");
            System.out.println(e.getMessage());
        }
    }
}