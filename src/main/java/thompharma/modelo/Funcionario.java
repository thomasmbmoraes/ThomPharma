package thompharma.modelo;

/**
 * modelo que representa um funcionario da farmacia
 * cada atributo corresponde a uma coluna da tabela tb_funcionarios no banco
 */
public class Funcionario {

    // identificador unico gerado automaticamente pelo banco
    private int id;

    // nome completo do funcionario
    private String nome;

    // numero de matricula interno da farmacia
    private String matricula;

    // setor onde o funcionario trabalha
    private String setor;

    // cargo ou funcao do funcionario
    private String cargo;

    // data de nascimento
    private String nascimento;

    // sexo: M para masculino, F para feminino
    private String sexo;

    // estado civil: solteiro, casado, divorciado, viuvo
    private String estadoCivil;

    // numero do rg
    private String rg;

    // cpf formatado como xxx.xxx.xxx-xx
    private String cpf;

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

    // telefone residencial
    private String telefone;

    // telefone comercial
    private String telefone2;

    // celular
    private String celular;

    // observacoes gerais sobre o funcionario
    private String observacoes;

    // indica se o funcionario esta ativo na farmacia
    private boolean ativo;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getSetor() { return setor; }
    public void setSetor(String setor) { this.setor = setor; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getNascimento() { return nascimento; }
    public void setNascimento(String nascimento) { this.nascimento = nascimento; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(String estadoCivil) { this.estadoCivil = estadoCivil; }

    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

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

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getTelefone2() { return telefone2; }
    public void setTelefone2(String telefone2) { this.telefone2 = telefone2; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}