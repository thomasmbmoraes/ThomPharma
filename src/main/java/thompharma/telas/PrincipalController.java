package thompharma.telas;

import thompharma.App;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * controller da tela principal do sistema
 * gerencia a navegacao entre os modulos
 * exibe o nome do usuario logado no cabecalho
 */
public class PrincipalController {

    // label do cabecalho que exibe o nome do usuario logado
    @FXML private Label labelUsuario;

    /**
     * define o nome do usuario logado no cabecalho
     * chamado pelo LoginController apos autenticacao bem sucedida
     * @param nomeUsuario nome completo do usuario logado
     */
    public void setUsuario(String nomeUsuario) {
        labelUsuario.setText("ThomPharma - Bem vindo, " + nomeUsuario);
    }

    /**
     * abre o modulo de cadastro de materias primas
     */
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

    /**
     * abre o modulo de cadastro de clientes
     * por enquanto abre fornecedores para teste
     */
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

    /**
     * abre o modulo de cadastro de receitas
     */
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

    /**
     * abre a calculadora farmaceutica
     * contem abas para floral, homeopatia liquida, globulos e dose unica
     */
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

    /**
     * abre o modulo de pedidos de manipulacao
     */
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

    /**
     * abre o modulo de cadastro de prescritores
     */
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

    /**
     * abre o modulo de tabelas de apoio
     * inclui usuarios, posologias, formas farmaceuticas, etc
     */
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

    /**
     * abre o modulo de relatorios
     */
    @FXML
    private void abrirRelatorios() {
        System.out.println("Relatorios - em desenvolvimento");
    }

    /**
     * abre o modulo de rotulos de embalagem
     */
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

    /**
     * realiza o logoff do usuario atual
     * volta para a tela de login
     */
    @FXML
    private void fazerLogoff() {
        try {
            App.setRoot("login");
            App.getStage().setTitle("Farmap - Login");
            App.getStage().setMaximized(true);
        } catch (Exception e) {
            System.out.println("Erro ao fazer logoff: " + e.getMessage());
        }
    }
}