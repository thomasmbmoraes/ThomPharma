package thompharma.telas;

import thompharma.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class PrincipalController {

    @FXML private Label labelUsuario;
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
            btnMatPrimas.setDisable(true);  btnMatPrimas.setOpacity(0.35);
            btnTabelas.setDisable(true);    btnTabelas.setOpacity(0.35);
            btnRelatorios.setDisable(true); btnRelatorios.setOpacity(0.35);
        }
    }

    private void abrirTela(String fxml) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/thompharma/" + fxml + ".fxml"));
            App.getStage().getScene().setRoot(loader.load());
        } catch (Exception e) {
            System.err.println("Erro ao abrir " + fxml + ": " + e.getMessage());
        }
    }

    @FXML private void abrirMatPrimas()   { abrirTela("materias_primas"); }
    @FXML private void abrirClientes()    { abrirTela("clientes"); }
    @FXML private void abrirPrescritores(){ abrirTela("prescritores"); }
    @FXML private void abrirReceitas()    { abrirTela("receitas"); }
    @FXML private void abrirCalculadora() { abrirTela("calculadora"); }
    @FXML private void abrirPedidos()     { abrirTela("pedidos"); }
    @FXML private void abrirTabelas()     { abrirTela("usuarios"); }
    @FXML private void abrirRelatorios()  { abrirTela("relatorios"); }
    @FXML private void abrirRotulos()     { abrirTela("rotulos"); }
    @FXML private void abrirDashboard()   { abrirTela("dashboard"); }

    @FXML
    private void fazerLogoff() {
        try {
            App.setAdmin(false);
            App.setNomeUsuarioLogado(null);
            App.setRoot("login");
            App.getStage().setTitle("ThomPharma - Login");
            App.getStage().setMaximized(true);
        } catch (Exception e) {
            System.err.println("Erro ao fazer logoff: " + e.getMessage());
        }
    }
}
