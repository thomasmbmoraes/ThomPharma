package thompharma.modelo;

import java.time.LocalDate;

/**
 * modelo que representa um rotulo de embalagem de manipulacao
 * gerado automaticamente quando o pedido entra em producao
 * o codigo segue o padrao DDMMAA/NN reiniciando a sequencia todo dia
 */
public class Rotulo {

    private int id;
    private int idPedido;

    // codigo unico no formato DDMMAA/NN — ex: 110626/01
    private String codigo;

    private LocalDate dataRotulo;
    private int sequencia;

    private String nomeFormula;
    private String nomeCliente;
    private String nomePrescritor;

    // posologia digitada pelo farmaceutico apos gerar o rotulo
    private String posologia;

    private LocalDate validade;
    private String observacoes;

    // dimensoes em mm para impressao — padrao 100x50
    private int larguraMm;
    private int alturaMm;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public LocalDate getDataRotulo() { return dataRotulo; }
    public void setDataRotulo(LocalDate dataRotulo) { this.dataRotulo = dataRotulo; }

    public int getSequencia() { return sequencia; }
    public void setSequencia(int sequencia) { this.sequencia = sequencia; }

    public String getNomeFormula() { return nomeFormula; }
    public void setNomeFormula(String nomeFormula) { this.nomeFormula = nomeFormula; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public String getNomePrescritor() { return nomePrescritor; }
    public void setNomePrescritor(String nomePrescritor) { this.nomePrescritor = nomePrescritor; }

    public String getPosologia() { return posologia; }
    public void setPosologia(String posologia) { this.posologia = posologia; }

    public LocalDate getValidade() { return validade; }
    public void setValidade(LocalDate validade) { this.validade = validade; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public int getLarguraMm() { return larguraMm; }
    public void setLarguraMm(int larguraMm) { this.larguraMm = larguraMm; }

    public int getAlturaMm() { return alturaMm; }
    public void setAlturaMm(int alturaMm) { this.alturaMm = alturaMm; }

    @Override
    public String toString() {
        return codigo != null ? codigo : "Rótulo #" + id;
    }
}
