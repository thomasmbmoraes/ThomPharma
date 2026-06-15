package thompharma.dao;

import thompharma.Conexao;
import thompharma.modelo.Pedido;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PedidoDao {

    public List<Pedido> listarTodos() throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.*, c.nome as nome_cliente, pr.nome as nome_prescritor, " +
                     "r.nome as nome_receita FROM tb_pedidos p " +
                     "LEFT JOIN tb_clientes c ON c.id = p.id_cliente " +
                     "LEFT JOIN tb_prescritores pr ON pr.id = p.id_prescritor " +
                     "LEFT JOIN tb_receitas r ON r.id = p.id_receita " +
                     "ORDER BY p.data_pedido DESC";
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public int contarHoje() throws SQLException {
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT COUNT(*) FROM tb_pedidos WHERE data_pedido = CURRENT_DATE")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int contarPendentesPorStatus(String status) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(
                 "SELECT COUNT(*) FROM tb_pedidos WHERE status = ?")) {
            st.setString(1, status);
            try (ResultSet rs = st.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    public int contarEntreguesHoje() throws SQLException {
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT COUNT(*) FROM tb_pedidos WHERE status='Entregue' AND data_pedido=CURRENT_DATE")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int inserir(Pedido p) throws SQLException {
        String sql = "INSERT INTO tb_pedidos (id_cliente, id_prescritor, id_receita, data_pedido, " +
                     "data_retirada, status, observacoes) VALUES (?,?,?,?,?,?,?) RETURNING id";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            preencher(st, p);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public void atualizar(Pedido p) throws SQLException {
        String sql = "UPDATE tb_pedidos SET id_cliente=?, id_prescritor=?, id_receita=?, " +
                     "data_pedido=?, data_retirada=?, status=?, observacoes=? WHERE id=?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            preencher(st, p);
            st.setInt(8, p.getId());
            st.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement("DELETE FROM tb_pedidos WHERE id=?")) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    public List<Pedido> listarPorPeriodoEStatus(LocalDate de, LocalDate ate, String status) throws SQLException {
        boolean filtrarStatus = status != null && !status.isEmpty() && !"Todos".equals(status);
        String sql = "SELECT p.*, c.nome as nome_cliente, pr.nome as nome_prescritor, " +
                     "r.nome as nome_receita FROM tb_pedidos p " +
                     "LEFT JOIN tb_clientes c ON c.id = p.id_cliente " +
                     "LEFT JOIN tb_prescritores pr ON pr.id = p.id_prescritor " +
                     "LEFT JOIN tb_receitas r ON r.id = p.id_receita " +
                     "WHERE p.data_pedido BETWEEN ? AND ? " +
                     (filtrarStatus ? "AND p.status = ? " : "") +
                     "ORDER BY p.data_pedido DESC";
        List<Pedido> lista = new ArrayList<>();
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setDate(1, Date.valueOf(de));
            st.setDate(2, Date.valueOf(ate));
            if (filtrarStatus) st.setString(3, status);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private void preencher(PreparedStatement st, Pedido p) throws SQLException {
        if (p.getIdCliente() > 0) st.setInt(1, p.getIdCliente());
        else st.setNull(1, Types.INTEGER);
        if (p.getIdPrescritor() > 0) st.setInt(2, p.getIdPrescritor());
        else st.setNull(2, Types.INTEGER);
        if (p.getIdReceita() > 0) st.setInt(3, p.getIdReceita());
        else st.setNull(3, Types.INTEGER);
        if (p.getDataPedido() != null) st.setDate(4, Date.valueOf(p.getDataPedido()));
        else st.setDate(4, Date.valueOf(LocalDate.now()));
        if (p.getDataRetirada() != null) st.setDate(5, Date.valueOf(p.getDataRetirada()));
        else st.setNull(5, Types.DATE);
        st.setString(6, p.getStatus());
        st.setString(7, p.getObservacoes());
    }

    private Pedido mapear(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setId(rs.getInt("id"));
        p.setIdCliente(rs.getInt("id_cliente"));
        p.setNomeCliente(rs.getString("nome_cliente"));
        p.setIdPrescritor(rs.getInt("id_prescritor"));
        p.setNomePrescritor(rs.getString("nome_prescritor"));
        p.setIdReceita(rs.getInt("id_receita"));
        p.setNomeReceita(rs.getString("nome_receita"));
        Date dp = rs.getDate("data_pedido");
        if (dp != null) p.setDataPedido(dp.toLocalDate());
        Date dr = rs.getDate("data_retirada");
        if (dr != null) p.setDataRetirada(dr.toLocalDate());
        p.setStatus(rs.getString("status"));
        p.setObservacoes(rs.getString("observacoes"));
        return p;
    }
}
