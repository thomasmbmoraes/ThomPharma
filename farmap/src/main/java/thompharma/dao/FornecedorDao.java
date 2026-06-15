package thompharma.dao;

import thompharma.Conexao;
import thompharma.modelo.Fornecedor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FornecedorDao {

    public List<Fornecedor> listarTodos() throws SQLException {
        List<Fornecedor> lista = new ArrayList<>();
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM tb_fornecedores ORDER BY nome")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public void inserir(Fornecedor f) throws SQLException {
        String sql = "INSERT INTO tb_fornecedores (nome, cnpj_cpf, contato, telefone, email, cidade, uf) " +
                     "VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            preencher(st, f);
            st.executeUpdate();
        }
    }

    public void atualizar(Fornecedor f) throws SQLException {
        String sql = "UPDATE tb_fornecedores SET nome=?, cnpj_cpf=?, contato=?, telefone=?, " +
                     "email=?, cidade=?, uf=? WHERE id=?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            preencher(st, f);
            st.setInt(8, f.getId());
            st.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement("DELETE FROM tb_fornecedores WHERE id=?")) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    private void preencher(PreparedStatement st, Fornecedor f) throws SQLException {
        st.setString(1, f.getNome());
        st.setString(2, f.getCnpjCpf());
        st.setString(3, f.getContato());
        st.setString(4, f.getTelefone());
        st.setString(5, f.getEmail());
        st.setString(6, f.getCidade());
        st.setString(7, f.getUf());
    }

    private Fornecedor mapear(ResultSet rs) throws SQLException {
        Fornecedor f = new Fornecedor();
        f.setId(rs.getInt("id"));
        f.setNome(rs.getString("nome"));
        f.setCnpjCpf(rs.getString("cnpj_cpf"));
        f.setContato(rs.getString("contato"));
        f.setTelefone(rs.getString("telefone"));
        f.setEmail(rs.getString("email"));
        f.setCidade(rs.getString("cidade"));
        f.setUf(rs.getString("uf"));
        return f;
    }
}
