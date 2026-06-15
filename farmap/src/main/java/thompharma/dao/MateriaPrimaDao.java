package thompharma.dao;

import thompharma.Conexao;
import thompharma.modelo.MateriaPrima;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MateriaPrimaDao {

    public List<MateriaPrima> listarTodos() throws SQLException {
        List<MateriaPrima> lista = new ArrayList<>();
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM tb_materias_primas ORDER BY nome")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<MateriaPrima> listarCriticas() throws SQLException {
        List<MateriaPrima> lista = new ArrayList<>();
        String sql = "SELECT * FROM tb_materias_primas WHERE saldo <= estoque_critico ORDER BY nome";
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public int contarCriticas() throws SQLException {
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT COUNT(*) FROM tb_materias_primas WHERE saldo <= estoque_critico")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int contarVencidas() throws SQLException {
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT COUNT(*) FROM tb_lotes WHERE validade < CURRENT_DATE AND saldo > 0")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public void inserir(MateriaPrima mp) throws SQLException {
        String sql = "INSERT INTO tb_materias_primas (nome, unidade, tipo, dose_minima, dose_maxima, " +
                     "volume, saldo, rotulo, geladeira, controlado, observacoes, estoque_minimo, " +
                     "estoque_critico, codigo, controlada_tipo, classe_anvisa, volume_caps, peso_caps) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            preencher(st, mp);
            st.executeUpdate();
        }
    }

    public void atualizar(MateriaPrima mp) throws SQLException {
        String sql = "UPDATE tb_materias_primas SET nome=?, unidade=?, tipo=?, dose_minima=?, dose_maxima=?, " +
                     "volume=?, saldo=?, rotulo=?, geladeira=?, controlado=?, observacoes=?, estoque_minimo=?, " +
                     "estoque_critico=?, codigo=?, controlada_tipo=?, classe_anvisa=?, volume_caps=?, peso_caps=? " +
                     "WHERE id=?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            preencher(st, mp);
            st.setInt(19, mp.getId());
            st.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement("DELETE FROM tb_materias_primas WHERE id=?")) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    private void preencher(PreparedStatement st, MateriaPrima mp) throws SQLException {
        st.setString(1, mp.getNome());
        st.setString(2, mp.getUnidade());
        st.setString(3, mp.getTipo());
        st.setDouble(4, mp.getDoseMinima());
        st.setDouble(5, mp.getDoseMaxima());
        st.setDouble(6, mp.getVolume());
        st.setDouble(7, mp.getSaldo());
        st.setBoolean(8, mp.isRotulo());
        st.setBoolean(9, mp.isGeladeira());
        st.setBoolean(10, mp.isControlado());
        st.setString(11, mp.getObservacoes());
        st.setDouble(12, mp.getEstoqueMinimo());
        st.setDouble(13, mp.getEstoqueCritico());
        st.setString(14, mp.getCodigo());
        st.setString(15, mp.getControladaTipo());
        st.setString(16, mp.getClasseAnvisa());
        st.setDouble(17, mp.getVolumeCaps());
        st.setDouble(18, mp.getPesoCaps());
    }

    public MateriaPrima mapear(ResultSet rs) throws SQLException {
        MateriaPrima mp = new MateriaPrima();
        mp.setId(rs.getInt("id"));
        mp.setNome(rs.getString("nome"));
        mp.setUnidade(rs.getString("unidade"));
        mp.setTipo(rs.getString("tipo"));
        mp.setDoseMinima(rs.getDouble("dose_minima"));
        mp.setDoseMaxima(rs.getDouble("dose_maxima"));
        mp.setVolume(rs.getDouble("volume"));
        mp.setSaldo(rs.getDouble("saldo"));
        mp.setRotulo(rs.getBoolean("rotulo"));
        mp.setGeladeira(rs.getBoolean("geladeira"));
        mp.setControlado(rs.getBoolean("controlado"));
        mp.setObservacoes(rs.getString("observacoes"));
        mp.setEstoqueMinimo(rs.getDouble("estoque_minimo"));
        mp.setEstoqueCritico(rs.getDouble("estoque_critico"));
        mp.setCodigo(rs.getString("codigo"));
        mp.setControladaTipo(rs.getString("controlada_tipo"));
        mp.setClasseAnvisa(rs.getString("classe_anvisa"));
        mp.setVolumeCaps(rs.getDouble("volume_caps"));
        mp.setPesoCaps(rs.getDouble("peso_caps"));
        return mp;
    }
}
