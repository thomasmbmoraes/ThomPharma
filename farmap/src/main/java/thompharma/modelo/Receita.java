package thompharma.modelo;

/**
 * modelo que representa uma receita (formula) da farmacia
 * cada receita possui um nome real e um nome fantasia opcional para ocultar
 * os ingredientes no rotulo
 * os ingredientes sao armazenados em tb_receita_ingredientes
 */
public class Receita {

    // identificador unico gerado automaticamente pelo banco
    private int id;

    // nome real da formula
    private String nome;

    // nome fantasia para ocultar a composicao no rotulo
    // ex: "Creme Rejuvenescedor" no lugar de listar os ingredientes
    private String nomeFantasia;

    // tipo da formula: capsula, creme, solucao, xarope, etc
    private String tipo;

    // observacoes gerais de preparo ou armazenamento
    private String observacoes;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
