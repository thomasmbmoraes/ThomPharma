package thompharma.modelo;

/**
 * modelo que representa um usuario do sistema
 * cada atributo corresponde a uma coluna da tabela usuarios no banco
 */
public class Usuario {

    // identificador unico gerado automaticamente pelo banco
    private int id;

    // nome de login usado para acessar o sistema
    private String usuario;

    // nome completo exibido no cabecalho apos o login
    private String nomeCompleto;

    // senha de acesso (futuramente sera criptografada com bcrypt)
    private String senha;

    // se verdadeiro, o usuario tem acesso total ao sistema
    // se falso, o usuario tem acesso restrito (somente consulta e calculos)
    private boolean admin;

    // se falso, o usuario nao consegue fazer login mesmo com senha correta
    private boolean ativo;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}