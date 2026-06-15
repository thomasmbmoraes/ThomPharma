package thompharma.dao;

import thompharma.Conexao;
import thompharma.modelo.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDao {

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM tb_clientes ORDER BY nome")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public boolean cpfDuplicado(String cpf, int idExcluir) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(
                 "SELECT id FROM tb_clientes WHERE cpf = ? AND id <> ?")) {
            st.setString(1, cpf);
            st.setInt(2, idExcluir);
            try (ResultSet rs = st.executeQuery()) { return rs.next(); }
        }
    }

    public void inserir(Cliente c) throws SQLException {
        String sql = "INSERT INTO tb_clientes (nome, cpf, telefone, telefone2, email, cep, " +
                     "endereco, bairro, cidade, uf, desconto, observacoes) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            preencher(st, c);
            st.executeUpdate();
        }
    }

    public void atualizar(Cliente c) throws SQLException {
        String sql = "UPDATE tb_clientes SET nome=?, cpf=?, telefone=?, telefone2=?, email=?, cep=?, " +
                     "endereco=?, bairro=?, cidade=?, uf=?, desconto=?, observacoes=? WHERE id=?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            preencher(st, c);
            st.setInt(13, c.getId());
            st.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement("DELETE FROM tb_clientes WHERE id=?")) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    private void preencher(PreparedStatement st, Cliente c) throws SQLException {
        st.setString(1, c.getNome());
        st.setString(2, c.getCpf());
        st.setString(3, c.getTelefone());
        st.setString(4, c.getTelefone2());
        st.setString(5, c.getEmail());
        st.setString(6, c.getCep());
        st.setString(7, c.getEndereco());
        st.setString(8, c.getBairro());
        st.setString(9, c.getCidade());
        st.setString(10, c.getUf());
        st.setDouble(11, c.getDesconto());
        st.setString(12, c.getObservacoes());
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setCpf(rs.getString("cpf"));
        c.setTelefone(rs.getString("telefone"));
        c.setTelefone2(rs.getString("telefone2"));
        c.setEmail(rs.getString("email"));
        c.setCep(rs.getString("cep"));
        c.setEndereco(rs.getString("endereco"));
        c.setBairro(rs.getString("bairro"));
        c.setCidade(rs.getString("cidade"));
        c.setUf(rs.getString("uf"));
        c.setDesconto(rs.getDouble("desconto"));
        c.setObservacoes(rs.getString("observacoes"));
        return c;
    }
}
