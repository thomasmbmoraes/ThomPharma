package thompharma.dao;

import thompharma.Conexao;
import java.sql.*;

/**
 * DAO para proteção contra brute-force — persiste tentativas em banco.
 * Garante que reinicializações da app não zerem os bloqueios.
 *
 * DDL necessário (executar uma vez):
 *   CREATE TABLE IF NOT EXISTS tb_tentativas_login (
 *       usuario       VARCHAR(100) PRIMARY KEY,
 *       tentativas    INTEGER      NOT NULL DEFAULT 0,
 *       bloqueado_ate BIGINT       NOT NULL DEFAULT 0
 *   );
 */
public class TentativaLoginDao {

    /** Garante que a tabela existe (cria na primeira execução se necessário). */
    public void criarTabelaSeNecessario() {
        String ddl = "CREATE TABLE IF NOT EXISTS tb_tentativas_login (" +
                     "usuario VARCHAR(100) PRIMARY KEY, " +
                     "tentativas INTEGER NOT NULL DEFAULT 0, " +
                     "bloqueado_ate BIGINT NOT NULL DEFAULT 0)";
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement()) {
            st.execute(ddl);
        } catch (SQLException e) {
            System.err.println("Aviso: nao foi possivel criar tb_tentativas_login: " + e.getMessage());
        }
    }

    /** Retorna {tentativas, bloqueado_ate} ou null se nao existir registro. */
    public int[] buscarEstado(String usuario) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(
                 "SELECT tentativas, bloqueado_ate FROM tb_tentativas_login WHERE usuario = ?")) {
            st.setString(1, usuario);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return null;
                return new int[]{rs.getInt("tentativas"), (int) rs.getLong("bloqueado_ate")};
            }
        }
    }

    /** Incrementa contador e, se atingir max, define bloqueado_ate. */
    public void registrarFalha(String usuario, int maxTentativas, int bloqueioSegundos) throws SQLException {
        int[] estado = buscarEstado(usuario);
        int tentativas = estado == null ? 0 : estado[0];
        tentativas++;
        long bloqueadoAte = tentativas >= maxTentativas
            ? System.currentTimeMillis() / 1000 + bloqueioSegundos : 0;

        String upsert = "INSERT INTO tb_tentativas_login (usuario, tentativas, bloqueado_ate) VALUES (?,?,?) " +
                        "ON CONFLICT (usuario) DO UPDATE SET tentativas=EXCLUDED.tentativas, " +
                        "bloqueado_ate=EXCLUDED.bloqueado_ate";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(upsert)) {
            st.setString(1, usuario);
            st.setInt(2, tentativas);
            st.setLong(3, bloqueadoAte);
            st.executeUpdate();
        }
    }

    /** Limpa o registro após login bem-sucedido. */
    public void limpar(String usuario) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(
                 "DELETE FROM tb_tentativas_login WHERE usuario = ?")) {
            st.setString(1, usuario);
            st.executeUpdate();
        }
    }
}
