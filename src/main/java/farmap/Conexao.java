package farmap;

import java.io.InputStream;
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
        try {
            props = new Properties();
            InputStream input = Conexao.class.getResourceAsStream("/config.properties");
            if (input == null) {
                input = Conexao.class.getClassLoader().getResourceAsStream("config.properties");
            }
            if (input != null) {
                props.load(input);
                input.close();
                System.out.println("config.properties carregado com sucesso!");
            } else {
                System.out.println("Arquivo config.properties nao encontrado!");
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar config.properties: " + e.getMessage());
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

        // tenta conexao local primeiro
        try {
            Connection conn = DriverManager.getConnection(urlLocal, usuario, senha);
            System.out.println("Conexao local estabelecida!");
            return conn;
        } catch (SQLException e) {
            System.out.println("Conexao local falhou, tentando remota...");
        }

        // tenta conexao remota
        try {
            Connection conn = DriverManager.getConnection(urlRemota, usuario, senha);
            System.out.println("Conexao remota estabelecida!");
            return conn;
        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
            return null;
        }
    }
}
