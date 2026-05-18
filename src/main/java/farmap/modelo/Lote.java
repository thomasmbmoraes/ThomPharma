package farmap.modelo;

/**
 * modelo que representa um lote de materia prima
 * cada lote pertence a uma materia prima e tem seu proprio custo e validade
 * cada atributo corresponde a uma coluna da tabela tb_lotes no banco
 */
public class Lote {

    // identificador unico gerado automaticamente pelo banco
    private int id;

    // id da materia prima a qual este lote pertence
    private int idMateriaPrima;

    // nome ou codigo do lote fornecido pelo fabricante
    private String nomeLote;

    // custo por unidade deste lote
    private double custo;

    // fator de conversao da unidade
    private int fator;

    // quantidade total comprada neste lote
    private double quantidade;

    // saldo disponivel deste lote
    private double saldo;

    // densidade da materia prima neste lote
    private double densidade;

    // data de validade do lote
    private String validade;

    // endereco de uso no laboratorio
    private String enderecoUso;

    // endereco de estoque no almoxarifado
    private String enderecoEstoque;

    // id do fornecedor deste lote
    private int idFornecedor;

    // nome do fornecedor para exibicao na tabela
    private String nomeFornecedor;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdMateriaPrima() { return idMateriaPrima; }
    public void setIdMateriaPrima(int idMateriaPrima) { this.idMateriaPrima = idMateriaPrima; }

    public String getNomeLote() { return nomeLote; }
    public void setNomeLote(String nomeLote) { this.nomeLote = nomeLote; }

    public double getCusto() { return custo; }
    public void setCusto(double custo) { this.custo = custo; }

    public int getFator() { return fator; }
    public void setFator(int fator) { this.fator = fator; }

    public double getQuantidade() { return quantidade; }
    public void setQuantidade(double quantidade) { this.quantidade = quantidade; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public double getDensidade() { return densidade; }
    public void setDensidade(double densidade) { this.densidade = densidade; }

    public String getValidade() { return validade; }
    public void setValidade(String validade) { this.validade = validade; }

    public String getEnderecoUso() { return enderecoUso; }
    public void setEnderecoUso(String enderecoUso) { this.enderecoUso = enderecoUso; }

    public String getEnderecoEstoque() { return enderecoEstoque; }
    public void setEnderecoEstoque(String enderecoEstoque) { this.enderecoEstoque = enderecoEstoque; }

    public int getIdFornecedor() { return idFornecedor; }
    public void setIdFornecedor(int idFornecedor) { this.idFornecedor = idFornecedor; }

    public String getNomeFornecedor() { return nomeFornecedor; }
    public void setNomeFornecedor(String nomeFornecedor) { this.nomeFornecedor = nomeFornecedor; }
}