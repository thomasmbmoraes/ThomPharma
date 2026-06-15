package thompharma.dao;

import thompharma.Conexao;
import thompharma.modelo.Funcionario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDao {

    public List<Funcionario> listarTodos() throws SQLException {
        List<Funcionario> lista = new ArrayList<>();
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM tb_funcionarios ORDER BY nome")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public void inserir(Funcionario f) throws SQLException {
        String sql = "INSERT INTO tb_funcionarios (nome, matricula, setor, cargo, nascimento, sexo, " +
                     "estado_civil, rg, cpf, cep, endereco, bairro, cidade, uf, telefone, telefone2, " +
                     "celular, observacoes, ativo) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            preencher(st, f);
            st.executeUpdate();
        }
    }

    public void atualizar(Funcionario f) throws SQLException {
        String sql = "UPDATE tb_funcionarios SET nome=?, matricula=?, setor=?, cargo=?, nascimento=?, " +
                     "sexo=?, estado_civil=?, rg=?, cpf=?, cep=?, endereco=?, bairro=?, cidade=?, uf=?, " +
                     "telefone=?, telefone2=?, celular=?, observacoes=?, ativo=? WHERE id=?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement(sql)) {
            preencher(st, f);
            st.setInt(20, f.getId());
            st.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection conn = Conexao.conectar();
             PreparedStatement st = conn.prepareStatement("DELETE FROM tb_funcionarios WHERE id=?")) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    private void preencher(PreparedStatement st, Funcionario f) throws SQLException {
        st.setString(1, f.getNome());
        st.setString(2, f.getMatricula());
        st.setString(3, f.getSetor());
        st.setString(4, f.getCargo());
        if (f.getNascimento() != null) st.setDate(5, Date.valueOf(f.getNascimento()));
        else st.setNull(5, Types.DATE);
        st.setString(6, f.getSexo());
        st.setString(7, f.getEstadoCivil());
        st.setString(8, f.getRg());
        st.setString(9, f.getCpf());
        st.setString(10, f.getCep());
        st.setString(11, f.getEndereco());
        st.setString(12, f.getBairro());
        st.setString(13, f.getCidade());
        st.setString(14, f.getUf());
        st.setString(15, f.getTelefone());
        st.setString(16, f.getTelefone2());
        st.setString(17, f.getCelular());
        st.setString(18, f.getObservacoes());
        st.setBoolean(19, f.isAtivo());
    }

    private Funcionario mapear(ResultSet rs) throws SQLException {
        Funcionario f = new Funcionario();
        f.setId(rs.getInt("id"));
        f.setNome(rs.getString("nome"));
        f.setMatricula(rs.getString("matricula"));
        f.setSetor(rs.getString("setor"));
        f.setCargo(rs.getString("cargo"));
        Date nasc = rs.getDate("nascimento");
        if (nasc != null) f.setNascimento(nasc.toLocalDate());
        f.setSexo(rs.getString("sexo"));
        f.setEstadoCivil(rs.getString("estado_civil"));
        f.setRg(rs.getString("rg"));
        f.setCpf(rs.getString("cpf"));
        f.setCep(rs.getString("cep"));
        f.setEndereco(rs.getString("endereco"));
        f.setBairro(rs.getString("bairro"));
        f.setCidade(rs.getString("cidade"));
        f.setUf(rs.getString("uf"));
        f.setTelefone(rs.getString("telefone"));
        f.setTelefone2(rs.getString("telefone2"));
        f.setCelular(rs.getString("celular"));
        f.setObservacoes(rs.getString("observacoes"));
        f.setAtivo(rs.getBoolean("ativo"));
        return f;
    }
}
