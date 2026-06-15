package thompharma.modelo;

/**
 * modelo que representa um item (ingrediente ou embalagem) de um pedido
 * herda a lista de ingredientes da receita mas pode ser ajustado ou acrescido
 */
public class PedidoItem {

    private int id;
    private int idPedido;
    private int idMateriaPrima;

    // nome da materia prima para exibicao na tabela (nao salvo no banco)
    private String nomeMateriaPrima;

    private double quantidade;
    private String unidade;
    private String observacao;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

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
