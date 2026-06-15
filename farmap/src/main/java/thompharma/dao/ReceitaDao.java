package thompharma.dao;

import thompharma.Conexao;
import thompharma.modelo.Receita;
import thompharma.modelo.ReceitaIngrediente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceitaDao {

    public List<Receita> listarTodos() throws SQLException {
        List<Receita> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT r.* FROM tb_receitas r " +
                     "LEFT JOIN tb_receita_ingredientes ri ON ri.id_receita = r.id " +
                     "LEFT JOIN tb_materias_primas mp ON mp.id = ri.id_materia_prima " +
                     "ORDER BY r.nome";
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapearReceita(rs));
        }
        return lista;
    }

    public List<Receita> buscar(String texto, String tipo) throws SQLException {
        String like = "%" + (texto != null ? texto.toLowerCase() : "") + "%";
        boolean filtrarTipo = tipo != null && !"Todos".equals(tipo);
        String sql = "SELECT DISTINCT r.* FROM tb_receitas r " +
                     "LEFT JOIN tb_receita_ingredientes ri ON ri.id_receita = r.id " +
                     "LEFT JOIN tb_materias_primas mp ON mp.id = ri.id_materia_prima " +
                     "WHERE (LOWER(r.nome) LIKE ? OR LOWER(mp.nome) LIKE ?) " +
                     (filtrarTipo ? "AND r.tipo = ? " : "") +
                     "ORDER BY r.nome";
        List<Receita> lista = new ArrayList<>();
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, like);
            st.setString(2, like);
            if (filtrarTipo) st.setString(3, tipo);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) lista.add(mapearReceita(rs));
            }
        }
        return lista;
    }

    public List<ReceitaIngrediente> listarIngredientes(int idReceita) throws SQLException {
        List<ReceitaIngrediente> lista = new ArrayList<>();
        String sql = "SELECT ri.*, mp.nome as nome_mp FROM tb_receita_ingredientes ri " +
                     "JOIN tb_materias_primas mp ON mp.id = ri.id_materia_prima " +
                     "WHERE ri.id_receita = ? ORDER BY ri.id";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idReceita);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) lista.add(mapearIngrediente(rs));
            }
        }
        return lista;
    }

    public int inserir(Receita r) throws SQLException {
        String sql = "INSERT INTO tb_receitas (nome, nome_fantasia, tipo, observacoes) VALUES (?,?,?,?) RETURNING id";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, r.getNome());
            st.setString(2, r.getNomeFantasia());
            st.setString(3, r.getTipo());
            st.setString(4, r.getObservacoes());
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public void atualizar(Receita r) throws SQLException {
        String sql = "UPDATE tb_receitas SET nome=?, nome_fantasia=?, tipo=?, observacoes=? WHERE id=?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, r.getNome());
            st.setString(2, r.getNomeFantasia());
            st.setString(3, r.getTipo());
            st.setString(4, r.getObservacoes());
            st.setInt(5, r.getId());
            st.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement("DELETE FROM tb_receitas WHERE id=?")) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    public void adicionarIngrediente(ReceitaIngrediente ing) throws SQLException {
        String sql = "INSERT INTO tb_receita_ingredientes (id_receita, id_materia_prima, quantidade, unidade, observacao) " +
                     "VALUES (?,?,?,?,?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, ing.getIdReceita());
            st.setInt(2, ing.getIdMateriaPrima());
            st.setDouble(3, ing.getQuantidade());
            st.setString(4, ing.getUnidade());
            st.setString(5, ing.getObservacao());
            st.executeUpdate();
        }
    }

    public void removerIngrediente(int id) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement("DELETE FROM tb_receita_ingredientes WHERE id=?")) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    public int contarTotal() throws SQLException {
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tb_receitas")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Receita mapearReceita(ResultSet rs) throws SQLException {
        Receita r = new Receita();
        r.setId(rs.getInt("id"));
        r.setNome(rs.getString("nome"));
        r.setNomeFantasia(rs.getString("nome_fantasia"));
        r.setTipo(rs.getString("tipo"));
        r.setObservacoes(rs.getString("observacoes"));
        return r;
    }

    private ReceitaIngrediente mapearIngrediente(ResultSet rs) throws SQLException {
        ReceitaIngrediente ing = new ReceitaIngrediente();
        ing.setId(rs.getInt("id"));
        ing.setIdReceita(rs.getInt("id_receita"));
        ing.setIdMateriaPrima(rs.getInt("id_materia_prima"));
        ing.setNomeMateriaPrima(rs.getString("nome_mp"));
        ing.setQuantidade(rs.getDouble("quantidade"));
        ing.setUnidade(rs.getString("unidade"));
        ing.setObservacao(rs.getString("observacao"));
        return ing;
    }
}
