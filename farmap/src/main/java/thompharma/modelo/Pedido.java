package thompharma.modelo;

import java.time.LocalDate;

/**
 * modelo que representa um pedido de manipulacao
 * associa cliente, prescritor (opcional) e receita (opcional)
 * os itens do pedido ficam em PedidoItem
 */
public class Pedido {

    private int id;

    // cliente que solicitou o pedido
    private int idCliente;
    private String nomeCliente;

    // prescritor que assinou a receita — pode ser nulo (pedido sem prescricao)
    private int idPrescritor;
    private String nomePrescritor;

    // receita base do pedido — pode ser nula (formula avulsa)
    private int idReceita;
    private String nomeReceita;

    private LocalDate dataPedido;
    private LocalDate dataRetirada;

    // Aguardando | Em Producao | Pronto | Entregue | Cancelado
    private String status;

    private String observacoes;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public int getIdPrescritor() { return idPrescritor; }
    public void setIdPrescritor(int idPrescritor) { this.idPrescritor = idPrescritor; }

    public String getNomePrescritor() { return nomePrescritor; }
    public void setNomePrescritor(String nomePrescritor) { this.nomePrescritor = nomePrescritor; }

    public int getIdReceita() { return idReceita; }
    public void setIdReceita(int idReceita) { this.idReceita = idReceita; }

    public String getNomeReceita() { return nomeReceita; }
    public void setNomeReceita(String nomeReceita) { this.nomeReceita = nomeReceita; }

    public LocalDate getDataPedido() { return dataPedido; }
    public void setDataPedido(LocalDate dataPedido) { this.dataPedido = dataPedido; }

    public LocalDate getDataRetirada() { return dataRetirada; }
    public void setDataRetirada(LocalDate dataRetirada) { this.dataRetirada = dataRetirada; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public String toString() {
        return nomeCliente != null ? nomeCliente : "Pedido #" + id;
    }
}
