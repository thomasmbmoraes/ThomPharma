package farmap.modelo;

/**
 * modelo que representa uma materia prima da farmacia
 * cada atributo corresponde a uma coluna da tabela tb_materias_primas no banco
 */
public class MateriaPrima {

    // identificador unico gerado automaticamente pelo banco
    private int id;

    // nome da materia prima
    private String nome;

    // unidade de medida: mg, g, ml, etc
    private String unidade;

    // tipo: solido, liquido, excipiente
    private String tipo;

    // dose minima recomendada
    private double doseMinima;

    // dose maxima recomendada
    private double doseMaxima;

    // volume da materia prima
    private double volume;

    // saldo atual em estoque
    private double saldo;

    // indica se deve aparecer no rotulo
    private boolean rotulo;

    // indica se deve ser armazenada em geladeira
    private boolean geladeira;

    // indica se e uma substancia controlada pela anvisa
    private boolean controlado;

    // observacoes tecnicas sobre a materia prima
    private String observacoes;

    // quantidade minima para alerta de estoque minimo
    private double estoqueMinimo;

    // quantidade minima para alerta de estoque critico
    private double estoqueCritico;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public double getDoseMinima() { return doseMinima; }
    public void setDoseMinima(double doseMinima) { this.doseMinima = doseMinima; }

    public double getDoseMaxima() { return doseMaxima; }
    public void setDoseMaxima(double doseMaxima) { this.doseMaxima = doseMaxima; }

    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = volume; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public boolean isRotulo() { return rotulo; }
    public void setRotulo(boolean rotulo) { this.rotulo = rotulo; }

    public boolean isGeladeira() { return geladeira; }
    public void setGeladeira(boolean geladeira) { this.geladeira = geladeira; }

    public boolean isControlado() { return controlado; }
    public void setControlado(boolean controlado) { this.controlado = controlado; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public double getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(double estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }

    public double getEstoqueCritico() { return estoqueCritico; }
    public void setEstoqueCritico(double estoqueCritico) { this.estoqueCritico = estoqueCritico; }
}