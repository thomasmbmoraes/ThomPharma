package farmap.modelo;

/**
 * modelo que representa um cliente da farmacia
 * cada atributo corresponde a uma coluna da tabela tb_clientes no banco
 * clientes podem ser tambem medicos, dentistas ou veterinarios
 */
public class Cliente {

    // identificador unico gerado automaticamente pelo banco
    private int id;

    // nome completo do cliente
    private String nome;

    // cpf do cliente formatado como xxx.xxx.xxx-xx
    private String cpf;

    // telefone principal formatado como (xx) xxxxx-xxxx
    private String telefone;

    // telefone secundario
    private String telefone2;

    // email do cliente
    private String email;

    // data de nascimento
    private String nascimento;

    // cep formatado como xxxxx-xxx
    private String cep;

    // endereco completo
    private String endereco;

    // bairro
    private String bairro;

    // cidade, valor padrao ribeirao preto
    private String cidade;

    // estado, valor padrao sp
    private String uf;

    // percentual de desconto padrao para este cliente
    private double desconto;

    // indica se o cliente e um profissional de saude (medico, dentista, veterinario)
    private boolean medico;

    // numero do registro profissional (crm, cro ou crv)
    private String crm;

    // id da especialidade medica vinculada
    private int idEspecialidade;

    // observacoes gerais sobre o cliente
    private String observacoes;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getTelefone2() { return telefone2; }
    public void setTelefone2(String telefone2) { this.telefone2 = telefone2; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNascimento() { return nascimento; }
    public void setNascimento(String nascimento) { this.nascimento = nascimento; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public double getDesconto() { return desconto; }
    public void setDesconto(double desconto) { this.desconto = desconto; }

    public boolean isMedico() { return medico; }
    public void setMedico(boolean medico) { this.medico = medico; }

    public String getCrm() { return crm; }
    public void setCrm(String crm) { this.crm = crm; }

    public int getIdEspecialidade() { return idEspecialidade; }
    public void setIdEspecialidade(int idEspecialidade) { this.idEspecialidade = idEspecialidade; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}