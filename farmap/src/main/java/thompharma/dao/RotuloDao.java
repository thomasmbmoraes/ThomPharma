package thompharma.dao;

import thompharma.Conexao;
import thompharma.modelo.Rotulo;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RotuloDao {

    public List<Rotulo> listarTodos() throws SQLException {
        List<Rotulo> lista = new ArrayList<>();
        String sql = "SELECT * FROM tb_rotulos ORDER BY data_rotulo DESC, sequencia DESC";
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Rotulo> buscarPorFiltro(String texto, LocalDate data) throws SQLException {
        String like = "%" + (texto != null ? texto.toLowerCase() : "") + "%";
        boolean temTexto = texto != null && !texto.isEmpty();
        boolean temData = data != null;

        StringBuilder sb = new StringBuilder("SELECT * FROM tb_rotulos WHERE 1=1 ");
        if (temTexto) sb.append("AND (LOWER(nome_cliente) LIKE ? OR LOWER(codigo) LIKE ? OR LOWER(nome_formula) LIKE ?) ");
        if (temData) sb.append("AND data_rotulo = ? ");
        sb.append("ORDER BY data_rotulo DESC, sequencia DESC");

        List<Rotulo> lista = new ArrayList<>();
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sb.toString())) {
            int idx = 1;
            if (temTexto) { st.setString(idx++, like); st.setString(idx++, like); st.setString(idx++, like); }
            if (temData) st.setDate(idx, Date.valueOf(data));
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public boolean existeParaPedido(Connection conn, int idPedido) throws SQLException {
        try (PreparedStatement st = conn.prepareStatement(
                "SELECT id FROM tb_rotulos WHERE id_pedido = ?")) {
            st.setInt(1, idPedido);
            try (ResultSet rs = st.executeQuery()) { return rs.next(); }
        }
    }

    /** Gera rótulo automático em transação já aberta pelo chamador. */
    public void gerarAutomatico(Connection conn, int idPedido, String nomeFormula,
                                 String nomeCliente, String nomePrescritor) throws SQLException {
        int sequencia;
        try (PreparedStatement st = conn.prepareStatement(
                "SELECT COALESCE(MAX(sequencia), 0) + 1 FROM tb_rotulos WHERE data_rotulo = CURRENT_DATE")) {
            try (ResultSet rs = st.executeQuery()) {
                sequencia = rs.next() ? rs.getInt(1) : 1;
            }
        }

        LocalDate hoje = LocalDate.now();
        String codigo = String.format("%02d%02d%02d/%02d",
            hoje.getDayOfMonth(), hoje.getMonthValue(), hoje.getYear() % 100, sequencia);

        try (PreparedStatement st = conn.prepareStatement(
                "INSERT INTO tb_rotulos (id_pedido, codigo, data_rotulo, sequencia, " +
                "nome_formula, nome_cliente, nome_prescritor, largura_mm, altura_mm) " +
                "VALUES (?,?,CURRENT_DATE,?,?,?,?,100,50)")) {
            st.setInt(1, idPedido);
            st.setString(2, codigo);
            st.setInt(3, sequencia);
            st.setString(4, nomeFormula);
            st.setString(5, nomeCliente);
            st.setString(6, nomePrescritor);
            st.executeUpdate();
        }
    }

    public void atualizar(int id, String nomeFormula, String nomeCliente, String nomePrescritor,
                           String posologia, LocalDate validade, String observacoes,
                           int largura, int altura) throws SQLException {
        String sql = "UPDATE tb_rotulos SET nome_formula=?, nome_cliente=?, nome_prescritor=?, " +
                     "posologia=?, validade=?, observacoes=?, largura_mm=?, altura_mm=? WHERE id=?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, nomeFormula);
            st.setString(2, nomeCliente);
            st.setString(3, nomePrescritor);
            st.setString(4, posologia);
            if (validade != null) st.setDate(5, Date.valueOf(validade));
            else st.setNull(5, Types.DATE);
            st.setString(6, observacoes);
            st.setInt(7, largura);
            st.setInt(8, altura);
            st.setInt(9, id);
            st.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement("DELETE FROM tb_rotulos WHERE id=?")) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    public int contarTotal() throws SQLException {
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tb_rotulos")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Rotulo mapear(ResultSet rs) throws SQLException {
        Rotulo r = new Rotulo();
        r.setId(rs.getInt("id"));
        r.setIdPedido(rs.getInt("id_pedido"));
        r.setCodigo(rs.getString("codigo"));
        Date d = rs.getDate("data_rotulo");
        if (d != null) r.setDataRotulo(d.toLocalDate());
        r.setSequencia(rs.getInt("sequencia"));
        r.setNomeFormula(rs.getString("nome_formula"));
        r.setNomeCliente(rs.getString("nome_cliente"));
        r.setNomePrescritor(rs.getString("nome_prescritor"));
        r.setPosologia(rs.getString("posologia"));
        Date v = rs.getDate("validade");
        if (v != null) r.setValidade(v.toLocalDate());
        r.setObservacoes(rs.getString("observacoes"));
        r.setLarguraMm(rs.getInt("largura_mm"));
        r.setAlturaMm(rs.getInt("altura_mm"));
        return r;
    }
}
