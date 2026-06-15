package thompharma;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Classe principal — inicializa o pool HikariCP antes de abrir a UI,
 * encerra o pool em stop() para liberar recursos ao fechar.
 */
public class App extends Application {

    private static Stage stage;
    private static boolean admin;
    private static String nomeUsuarioLogado;

    public static boolean isAdmin() { return admin; }
    public static void setAdmin(boolean a) { admin = a; }
    public static String getNomeUsuarioLogado() { return nomeUsuarioLogado; }
    public static void setNomeUsuarioLogado(String nome) { nomeUsuarioLogado = nome; }

    @Override
    public void start(Stage s) throws IOException {
        inicializarPool();
        stage = s;
        stage.setTitle("ThomPharma - Login");
        stage.setMaximized(true);
        setRoot("login");
        stage.show();
    }

    @Override
    public void stop() {
        ConnectionPool.shutdown();
    }

    private void inicializarPool() {
        Properties props = carregarProps();
        String urlLocal  = props.getProperty("db.url.local", "");
        String urlRemota = props.getProperty("db.url.remota", "");
        String usuario   = props.getProperty("db.usuario", "");
        String senha     = props.getProperty("db.senha", "");

        // tenta URL local primeiro, depois remota como fallback
        String urlEscolhida = urlLocal;
        try {
            DriverManager.getConnection(urlLocal, usuario, senha).close();
        } catch (SQLException e) {
            urlEscolhida = urlRemota;
        }

        try {
            ConnectionPool.init(urlEscolhida, usuario, senha);
        } catch (Exception e) {
            System.err.println("Falha ao inicializar pool: " + e.getMessage());
        }
    }

    private Properties carregarProps() {
        Properties props = new Properties();
        try {
            java.nio.file.Path externo = Paths.get("config.properties");
            if (Files.exists(externo)) {
                try (InputStream in = Files.newInputStream(externo)) {
                    props.load(in);
                    return props;
                }
            }
            InputStream in = App.class.getResourceAsStream("/config.properties");
            if (in == null) in = App.class.getClassLoader().getResourceAsStream("config.properties");
            if (in != null) { props.load(in); in.close(); }
            else System.err.println("config.properties não encontrado!");
        } catch (Exception e) {
            System.err.println("Erro ao carregar config.properties: " + e.getMessage());
        }
        return props;
    }

    public static void setRoot(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/thompharma/" + fxml + ".fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
    }

    public static FXMLLoader getLoader(String fxml) throws IOException {
        return new FXMLLoader(App.class.getResource("/thompharma/" + fxml + ".fxml"));
    }

    public static Stage getStage() { return stage; }

    public static void trocarTela(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/thompharma/" + fxml + ".fxml"));
            stage.getScene().setRoot(loader.load());
            stage.setMaximized(true);
        } catch (Exception e) {
            System.err.println("Erro ao trocar tela: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
