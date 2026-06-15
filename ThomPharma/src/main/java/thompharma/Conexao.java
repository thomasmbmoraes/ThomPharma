package thompharma;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Ponto único de acesso a conexões com o banco.
 * Delega ao ConnectionPool (HikariCP) — inicializado em App.start().
 */
public class Conexao {

    public static Connection conectar() throws SQLException {
        return ConnectionPool.getConnection();
    }
}
