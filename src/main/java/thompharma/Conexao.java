package thompharma;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * classe responsavel por gerenciar a conexao com o banco de dados postgresql
 * as configuracoes de conexao sao lidas do arquivo config.properties
 * que nao e versionado no github por conter dados sensiveis
 */
public class Conexao {

    // propriedades carregadas do arquivo config.properties
    private static Properties props = null;

    /**
     * carrega as propriedades do arquivo config.properties
     * executado uma vez e mantido em memoria
     */
    private static void carregarProps() {
        if (props != null) return;
        props = new Properties();
        try {
            // Prioridade: arquivo no disco (permite configurar sem recompilar)
            java.nio.file.Path externo = Paths.get("config.properties");
            if (Files.exists(externo)) {
                try (InputStream in = Files.newInputStream(externo)) {
                    props.load(in);
                    return;
                }
            }
            // Fallback: arquivo dentro do JAR (ambiente de desenvolvimento)
            InputStream input = Conexao.class.getResourceAsStream("/config.properties");
            if (input == null) {
                input = Conexao.class.getClassLoader().getResourceAsStream("config.properties");
            }
            if (input != null) {
                props.load(input);
                input.close();
            } else {
                System.err.println("config.properties nao encontrado!");
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar config.properties: " + e.getMessage());
        }
    }

    /**
     * abre e retorna uma conexao com o banco de dados
     * tenta conexao local primeiro, depois remota se falhar
     * cada modulo deve fechar a conexao apos o uso com conn.close()
     * @return objeto Connection ou null em caso de erro
     */
    public static Connection conectar() {
        carregarProps();

        String urlLocal = props.getProperty("db.url.local");
        String urlRemota = props.getProperty("db.url.remota");
        String usuario = props.getProperty("db.usuario");
        String senha = props.getProperty("db.senha");

        // tenta conexao local primeiro, depois remota como fallback
        try {
            return DriverManager.getConnection(urlLocal, usuario, senha);
        } catch (SQLException e) {
            // silencioso — tenta remota antes de reportar erro
        }

        try {
            return DriverManager.getConnection(urlRemota, usuario, senha);
        } catch (SQLException e) {
            System.err.println("Erro ao conectar com o banco: " + e.getMessage());
            return null;
        }
    }
}
