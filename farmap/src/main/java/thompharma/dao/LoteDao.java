package thompharma.dao;

import thompharma.Conexao;
import thompharma.modelo.Lote;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoteDao {

    public List<Lote> listarPorMateriaPrima(int idMP) throws SQLException {
        List<Lote> lista = new ArrayList<>();
        String sql = "SELECT l.*, f.nome as nome_fornecedor FROM tb_lotes l " +
                     "LEFT JOIN tb_fornecedores f ON f.id = l.id_fornecedor " +
                     "WHERE l.id_materia_prima = ? ORDER BY l.id DESC";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idMP);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public void inserir(Lote l, Connection conn) throws SQLException {
        String sql = "INSERT INTO tb_lotes (id_materia_prima, nome_lote, custo, fator, fator2, " +
                     "quantidade, saldo, densidade, validade, endereco_uso, endereco_estoque, id_fornecedor) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            preencher(st, l);
            st.executeUpdate();
        }
    }

    public void atualizarSaldoMP(Connection conn, int idMP) throws SQLException {
        String sql = "UPDATE tb_materias_primas SET saldo = " +
                     "(SELECT COALESCE(SUM(saldo),0) FROM tb_lotes WHERE id_materia_prima = ?) WHERE id = ?";
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idMP);
            st.setInt(2, idMP);
            st.executeUpdate();
        }
    }

    private void preencher(PreparedStatement st, Lote l) throws SQLException {
        st.setInt(1, l.getIdMateriaPrima());
        st.setString(2, l.getNomeLote());
        st.setDouble(3, l.getCusto());
        st.setDouble(4, l.getFator());
        st.setDouble(5, l.getFator2());
        st.setDouble(6, l.getQuantidade());
        st.setDouble(7, l.getSaldo());
        st.setDouble(8, l.getDensidade());
        if (l.getValidade() != null) st.setDate(9, Date.valueOf(l.getValidade()));
        else st.setNull(9, Types.DATE);
        st.setString(10, l.getEnderecoUso());
        st.setString(11, l.getEnderecoEstoque());
        st.setInt(12, l.getIdFornecedor());
    }

    private Lote mapear(ResultSet rs) throws SQLException {
        Lote l = new Lote();
        l.setId(rs.getInt("id"));
        l.setIdMateriaPrima(rs.getInt("id_materia_prima"));
        l.setNomeLote(rs.getString("nome_lote"));
        l.setCusto(rs.getDouble("custo"));
        l.setFator(rs.getDouble("fator"));
        l.setFator2(rs.getDouble("fator2"));
        l.setQuantidade(rs.getDouble("quantidade"));
        l.setSaldo(rs.getDouble("saldo"));
        l.setDensidade(rs.getDouble("densidade"));
        Date v = rs.getDate("validade");
        if (v != null) l.setValidade(v.toLocalDate());
        l.setEnderecoUso(rs.getString("endereco_uso"));
        l.setEnderecoEstoque(rs.getString("endereco_estoque"));
        l.setIdFornecedor(rs.getInt("id_fornecedor"));
        try { l.setNomeFornecedor(rs.getString("nome_fornecedor")); } catch (SQLException ignored) {}
        return l;
    }
}
