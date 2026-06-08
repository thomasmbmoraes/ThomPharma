package farmap;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * classe principal do sistema farmap
 * responsavel por iniciar a aplicacao e gerenciar a troca de telas
 */
public class App extends Application {

    // janela principal do sistema, estatica para ser acessada de qualquer classe
    private static Stage stage;

    /**
     * metodo de inicializacao do javafx
     * carrega a tela de login ao iniciar
     */    
    @Override
    public void start(Stage s) throws IOException {
        stage = s;
        stage.setTitle("ThomPharma - Login");
        stage.setMaximized(true);
        setRoot("login");
        stage.show();
    }

    /**
     * troca a tela atual pelo fxml informado
     * @param fxml nome do arquivo fxml sem extensao
     */
    public static void setRoot(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
    }

    /**
     * retorna um loader para o fxml informado
     * usado quando precisamos acessar o controller apos carregar a tela
     * @param fxml nome do arquivo fxml sem extensao
     */
    public static FXMLLoader getLoader(String fxml) throws IOException {
        return new FXMLLoader(App.class.getResource(fxml + ".fxml"));
    }

    /**
     * retorna a janela principal do sistema
     */
    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void trocarTela(String fxml) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                App.class.getResource(fxml + ".fxml")
            );
            stage.getScene().setRoot(loader.load());
            stage.setMaximized(true);
        } catch (Exception e) {
            System.out.println("Erro ao trocar tela: " + e.getMessage());
        }
    }
}