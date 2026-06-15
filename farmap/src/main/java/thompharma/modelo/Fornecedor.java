package thompharma.modelo;

/**
 * modelo que representa um fornecedor de materias primas
 * cada atributo corresponde a uma coluna da tabela fornecedores no banco
 */
public class Fornecedor {

    // identificador unico gerado automaticamente pelo banco
    private int id;

    // nome comercial do fornecedor
    private String nome;

    // documento do fornecedor, pode ser cpf ou cnpj
    private String cnpjCpf;

    // nome da pessoa de contato na empresa fornecedora
    private String contato;

    // telefone principal do fornecedor
    private String telefone;

    // email do fornecedor para comunicacao
    private String email;

    // cidade onde o fornecedor esta localizado
    // valor padrao: Ribeirao Preto
    private String cidade;

    // estado onde o fornecedor esta localizado
    // valor padrao: SP
    private String uf;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCnpjCpf() { return cnpjCpf; }
    public void setCnpjCpf(String cnpjCpf) { this.cnpjCpf = cnpjCpf; }

    public String getContato() { return contato; }
    public void setContato(String contato) { this.contato = contato; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
}