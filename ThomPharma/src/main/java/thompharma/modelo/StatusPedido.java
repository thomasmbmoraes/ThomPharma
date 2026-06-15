package thompharma.modelo;

public enum StatusPedido {
    AGUARDANDO("Aguardando"),
    EM_PRODUCAO("Em Produção"),
    PRONTO("Pronto"),
    ENTREGUE("Entregue");

    private final String label;

    StatusPedido(String label) { this.label = label; }

    public String getLabel() { return label; }

    @Override
    public String toString() { return label; }

    public static StatusPedido fromLabel(String label) {
        for (StatusPedido s : values()) {
            if (s.label.equals(label)) return s;
        }
        return AGUARDANDO;
    }
}
