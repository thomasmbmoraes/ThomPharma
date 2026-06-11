package thompharma.modelo;

/**
 * modelo que representa um ingrediente de uma receita
 * a mesma materia prima pode aparecer mais de uma vez na mesma receita
 * com quantidades ou observacoes diferentes
 */
public class ReceitaIngrediente {

    // identificador unico gerado automaticamente pelo banco
    private int id;

    // id da receita a qual este ingrediente pertence
    private int idReceita;

    // id da materia prima usada como ingrediente
    private int idMateriaPrima;

    // nome da materia prima para exibicao na tabela (nao salvo no banco)
    private String nomeMateriaPrima;

    // quantidade do ingrediente na formula
    private double quantidade;

    // unidade de medida: mg, g, ml, etc
    private String unidade;

    // observacao especifica deste ingrediente na formula
    // ex: "adicionar por ultimo", "dissolver antes"
    private String observacao;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdReceita() { return idReceita; }
    public void setIdReceita(int idReceita) { this.idReceita = idReceita; }

    public int getIdMateriaPrima() { return idMateriaPrima; }
    public void setIdMateriaPrima(int idMateriaPrima) { this.idMateriaPrima = idMateriaPrima; }

    public String getNomeMateriaPrima() { return nomeMateriaPrima; }
    public void setNomeMateriaPrima(String nomeMateriaPrima) { this.nomeMateriaPrima = nomeMateriaPrima; }

    public double getQuantidade() { return quantidade; }
    public void setQuantidade(double quantidade) { this.quantidade = quantidade; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
