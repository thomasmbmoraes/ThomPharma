package thompharma.modelo;

/**
 * modelo que representa um prescritor (medico, dentista, veterinario, etc)
 * separado de clientes para facilitar a gestao de receitas e pedidos
 * o tipo_registro define a categoria: CRM, CRO, CRV, CRP, CRN, CREFITO, Terapeuta, Outro
 */
public class Prescritor {

    // identificador unico gerado automaticamente pelo banco
    private int id;

    // nome completo do prescritor
    private String nome;

    // tipo do registro profissional: CRM, CRO, CRV, CRP, CRN, CREFITO, Terapeuta, Outro
    private String tipoRegistro;

    // numero do registro profissional
    private String numeroRegistro;

    // telefone de contato
    private String telefone;

    // email de contato
    private String email;

    // observacoes gerais sobre o prescritor
    private String observacoes;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipoRegistro() { return tipoRegistro; }
    public void setTipoRegistro(String tipoRegistro) { this.tipoRegistro = tipoRegistro; }

    public String getNumeroRegistro() { return numeroRegistro; }
    public void setNumeroRegistro(String numeroRegistro) { this.numeroRegistro = numeroRegistro; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
