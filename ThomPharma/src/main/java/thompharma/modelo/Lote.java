package thompharma.modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modelo que representa um lote de matéria-prima.
 * validade é LocalDate — formatar para exibição com getValidadeFormatada().
 */
public class Lote {

    private int id;
    private int idMateriaPrima;
    private String nomeLote;
    private double custo;
    private double fator;
    private double fator2;
    private double quantidade;
    private double saldo;
    private double densidade;
    private LocalDate validade;
    private String enderecoUso;
    private String enderecoEstoque;
    private int idFornecedor;
    private String nomeFornecedor;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdMateriaPrima() { return idMateriaPrima; }
    public void setIdMateriaPrima(int idMateriaPrima) { this.idMateriaPrima = idMateriaPrima; }

    public String getNomeLote() { return nomeLote; }
    public void setNomeLote(String nomeLote) { this.nomeLote = nomeLote; }

    public double getCusto() { return custo; }
    public void setCusto(double custo) { this.custo = custo; }

    public double getFator() { return fator; }
    public void setFator(double fator) { this.fator = fator; }

    public double getFator2() { return fator2; }
    public void setFator2(double fator2) { this.fator2 = fator2; }

    public double getQuantidade() { return quantidade; }
    public void setQuantidade(double quantidade) { this.quantidade = quantidade; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public double getDensidade() { return densidade; }
    public void setDensidade(double densidade) { this.densidade = densidade; }

    public LocalDate getValidade() { return validade; }
    public void setValidade(LocalDate validade) { this.validade = validade; }

    /** Retorna a validade formatada como dd/MM/yyyy, ou "" se nula. */
    public String getValidadeFormatada() {
        return validade != null ? validade.format(FMT) : "";
    }

    public String getEnderecoUso() { return enderecoUso; }
    public void setEnderecoUso(String enderecoUso) { this.enderecoUso = enderecoUso; }

    public String getEnderecoEstoque() { return enderecoEstoque; }
    public void setEnderecoEstoque(String enderecoEstoque) { this.enderecoEstoque = enderecoEstoque; }

    public int getIdFornecedor() { return idFornecedor; }
    public void setIdFornecedor(int idFornecedor) { this.idFornecedor = idFornecedor; }

    public String getNomeFornecedor() { return nomeFornecedor; }
    public void setNomeFornecedor(String nomeFornecedor) { this.nomeFornecedor = nomeFornecedor; }
}
