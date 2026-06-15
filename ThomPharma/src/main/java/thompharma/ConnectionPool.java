package thompharma;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Pool de conexões HikariCP — substitui o modelo de abrir/fechar por operação.
 * Inicializado em App.start() e encerrado em App.stop().
 */
public class ConnectionPool {

    private static HikariDataSource ds;

    public static void init(String jdbcUrl, String usuario, String senha) {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(jdbcUrl);
        cfg.setUsername(usuario);
        cfg.setPassword(senha);
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(2);
        cfg.setConnectionTimeout(30_000);
        cfg.setIdleTimeout(600_000);
        cfg.setMaxLifetime(1_800_000);
        cfg.setPoolName("ThomPharma-Pool");
        ds = new HikariDataSource(cfg);
    }

    public static Connection getConnection() throws SQLException {
        if (ds == null) throw new SQLException("Pool não inicializado. Verifique a conexão com o banco.");
        return ds.getConnection();
    }

    public static void shutdown() {
        if (ds != null && !ds.isClosed()) ds.close();
    }

    public static boolean isInitialized() {
        return ds != null && !ds.isClosed();
    }
}
