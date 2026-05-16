package farmap.telas;

import farmap.App;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PrincipalController {

    @FXML
    private Label labelUsuario;

    public void setUsuario(String nomeUsuario) {
        labelUsuario.setText("Farmap - Bem vindo, " + nomeUsuario);
    }

    @FXML
    private void abrirMatPrimas() {
        System.out.println("Mat. Primas - em desenvolvimento");
    }

    @FXML
    private void abrirClientes() {
        System.out.println("Clientes - em desenvolvimento");
    }

    @FXML
    private void abrirReceitas() {
        System.out.println("Receitas - em desenvolvimento");
    }

    @FXML
    private void abrirOrcamentos() {
        System.out.println("Orcamentos - em desenvolvimento");
    }

    @FXML
    private void abrirPedidos() {
        System.out.println("Pedidos - em desenvolvimento");
    }

    @FXML
    private void abrirVendas() {
        System.out.println("Vendas - em desenvolvimento");
    }

    @FXML
    private void abrirTabelas() {
        System.out.println("Tabelas - em desenvolvimento");
    }

    @FXML
    private void abrirRelatorios() {
        System.out.println("Relatorios - em desenvolvimento");
    }

    @FXML
    private void abrirFerramentas() {
        System.out.println("Ferramentas - em desenvolvimento");
    }

    @FXML
    private void fazerLogoff() {
        try {
        App.setRoot("login");
        App.getStage().setTitle("Farmap - Login");
    } catch (Exception e) {
        System.out.println("Erro ao fazer logoff: " + e.getMessage());
    }
    }
}