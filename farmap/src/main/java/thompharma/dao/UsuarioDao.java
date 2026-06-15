package thompharma.dao;

import thompharma.Conexao;
import thompharma.modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT id, usuario, nome_completo, admin, ativo FROM tb_usuarios ORDER BY usuario")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Usuario buscarPorLogin(String login) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(
                 "SELECT id, usuario, nome_completo, senha, admin, ativo FROM tb_usuarios " +
                 "WHERE usuario = ? AND ativo = true")) {
            st.setString(1, login);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? mapearComSenha(rs) : null;
            }
        }
    }

    public boolean loginDuplicado(String login, int idExcluir) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(
                 "SELECT id FROM tb_usuarios WHERE usuario = ? AND id <> ?")) {
            st.setString(1, login);
            st.setInt(2, idExcluir);
            try (ResultSet rs = st.executeQuery()) { return rs.next(); }
        }
    }

    public void inserir(String login, String nomeCompleto, String hashSenha, boolean admin) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(
                 "INSERT INTO tb_usuarios (usuario, nome_completo, senha, admin, ativo) VALUES (?,?,?,?,true)")) {
            st.setString(1, login);
            st.setString(2, nomeCompleto);
            st.setString(3, hashSenha);
            st.setBoolean(4, admin);
            st.executeUpdate();
        }
    }

    public void atualizar(int id, String nomeCompleto, boolean admin, boolean ativo) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(
                 "UPDATE tb_usuarios SET nome_completo=?, admin=?, ativo=? WHERE id=?")) {
            st.setString(1, nomeCompleto);
            st.setBoolean(2, admin);
            st.setBoolean(3, ativo);
            st.setInt(4, id);
            st.executeUpdate();
        }
    }

    public void atualizarSenha(int id, String hashSenha) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(
                 "UPDATE tb_usuarios SET senha=? WHERE id=?")) {
            st.setString(1, hashSenha);
            st.setInt(2, id);
            st.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement("DELETE FROM tb_usuarios WHERE id=?")) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setUsuario(rs.getString("usuario"));
        u.setNomeCompleto(rs.getString("nome_completo"));
        u.setAdmin(rs.getBoolean("admin"));
        u.setAtivo(rs.getBoolean("ativo"));
        return u;
    }

    private Usuario mapearComSenha(ResultSet rs) throws SQLException {
        Usuario u = mapear(rs);
        u.setSenha(rs.getString("senha"));
        return u;
    }
}
