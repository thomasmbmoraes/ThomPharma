package thompharma.dao;

import thompharma.Conexao;
import thompharma.modelo.Prescritor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescritoresDao {

    public List<Prescritor> listarTodos() throws SQLException {
        List<Prescritor> lista = new ArrayList<>();
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM tb_prescritores ORDER BY nome")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public boolean registroDuplicado(String tipo, String numero, int idExcluir) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(
                 "SELECT id FROM tb_prescritores WHERE tipo_registro=? AND numero_registro=? AND id<>?")) {
            st.setString(1, tipo);
            st.setString(2, numero);
            st.setInt(3, idExcluir);
            try (ResultSet rs = st.executeQuery()) { return rs.next(); }
        }
    }

    public void inserir(Prescritor p) throws SQLException {
        String sql = "INSERT INTO tb_prescritores (nome, tipo_registro, numero_registro, telefone, email, observacoes) " +
                     "VALUES (?,?,?,?,?,?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            preencher(st, p);
            st.executeUpdate();
        }
    }

    public void atualizar(Prescritor p) throws SQLException {
        String sql = "UPDATE tb_prescritores SET nome=?, tipo_registro=?, numero_registro=?, " +
                     "telefone=?, email=?, observacoes=? WHERE id=?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            preencher(st, p);
            st.setInt(7, p.getId());
            st.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement("DELETE FROM tb_prescritores WHERE id=?")) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    private void preencher(PreparedStatement st, Prescritor p) throws SQLException {
        st.setString(1, p.getNome());
        st.setString(2, p.getTipoRegistro());
        st.setString(3, p.getNumeroRegistro());
        st.setString(4, p.getTelefone());
        st.setString(5, p.getEmail());
        st.setString(6, p.getObservacoes());
    }

    private Prescritor mapear(ResultSet rs) throws SQLException {
        Prescritor p = new Prescritor();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setTipoRegistro(rs.getString("tipo_registro"));
        p.setNumeroRegistro(rs.getString("numero_registro"));
        p.setTelefone(rs.getString("telefone"));
        p.setEmail(rs.getString("email"));
        p.setObservacoes(rs.getString("observacoes"));
        return p;
    }
}
