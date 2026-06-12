package thompharma.telas;

import thompharma.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class PrincipalController {

    @FXML private Label labelUsuario;

    // botoes restritos a administradores
    @FXML private Button btnMatPrimas;
    @FXML private Button btnTabelas;
    @FXML private Button btnRelatorios;

    public void setUsuario(String nomeUsuario) {
        String perfil = App.isAdmin() ? "Admin" : "Operador";
        labelUsuario.setText("ThomPharma — " + nomeUsuario + "  [" + perfil + "]");
        aplicarPermissoes();
    }

    private void aplicarPermissoes() {
        if (!App.isAdmin()) {
            btnMatPrimas.setDisable(true);
            btnMatPrimas.setOpacity(0.35);
            btnTabelas.setDisable(true);
            btnTabelas.setOpacity(0.35);
            btnRelatorios.setDisable(true);
            btnRelatorios.setOpacity(0.35);
        }
    }

    @FXML
    private void abrirMatPrimas() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/thompharma/materias_primas.fxml")
            );
            App.getStage().getScene().setRoot(loader.load());
        } catch (Exception e) {
            System.out.println("Erro ao abrir materias primas: " + e.getMessage());
        }
    }

    @FXML
    private void abrirClientes() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/thompharma/clientes.fxml")
            );
            App.getStage().getScene().setRoot(loader.load());
        } catch (Exception e) {
            System.out.println("Erro ao abrir clientes: " + e.getMessage());
        }
    }

    @FXML
    private void abrirReceitas() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/thompharma/receitas.fxml")
            );
            App.getStage().getScene().setRoot(loader.load());
        } catch (Exception e) {
            System.out.println("Erro ao abrir receitas: " + e.getMessage());
        }
    }

    @FXML
    private void abrirCalculadora() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/thompharma/calculadora.fxml")
            );
            App.getStage().getScene().setRoot(loader.load());
        } catch (Exception e) {
            System.out.println("Erro ao abrir calculadora: " + e.getMessage());
        }
    }

    @FXML
    private void abrirPedidos() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/thompharma/pedidos.fxml")
            );
            App.getStage().getScene().setRoot(loader.load());
        } catch (Exception e) {
            System.out.println("Erro ao abrir pedidos: " + e.getMessage());
        }
    }

    @FXML
    private void abrirPrescritores() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/thompharma/prescritores.fxml")
            );
            App.getStage().getScene().setRoot(loader.load());
        } catch (Exception e) {
            System.out.println("Erro ao abrir prescritores: " + e.getMessage());
        }
    }

    @FXML
    private void abrirTabelas() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/thompharma/usuarios.fxml")
            );
            App.getStage().getScene().setRoot(loader.load());
        } catch (Exception e) {
            System.out.println("Erro ao abrir usuarios: " + e.getMessage());
        }
    }

    @FXML
    private void abrirRelatorios() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/thompharma/relatorios.fxml")
            );
            App.getStage().getScene().setRoot(loader.load());
        } catch (Exception e) {
            System.out.println("Erro ao abrir relatorios: " + e.getMessage());
        }
    }

    @FXML
    private void abrirRotulos() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/thompharma/rotulos.fxml")
            );
            App.getStage().getScene().setRoot(loader.load());
        } catch (Exception e) {
            System.out.println("Erro ao abrir rotulos: " + e.getMessage());
        }
    }

    @FXML
    private void fazerLogoff() {
        try {
            App.setAdmin(false);
            App.setNomeUsuarioLogado(null);
            App.setRoot("login");
            App.getStage().setTitle("ThomPharma - Login");
            App.getStage().setMaximized(true);
        } catch (Exception e) {
            System.out.println("Erro ao fazer logoff: " + e.getMessage());
        }
    }
}
