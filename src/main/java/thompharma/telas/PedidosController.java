package thompharma.telas;

import thompharma.App;
import thompharma.modelo.Pedido;
import thompharma.modelo.PedidoItem;
import thompharma.Conexao;
import thompharma.telas.RotulosController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * controller do modulo de pedidos de manipulacao
 * gerencia pedidos associando cliente, prescritor (opcional) e receita (opcional)
 * ao selecionar uma receita, os ingredientes sao carregados automaticamente nos itens
 */
public class PedidosController {

    // --- lista de pedidos ---
    @FXML private TableView<Pedido> tabelaPedidos;
    @FXML private TableColumn<Pedido, String> colCliente;
    @FXML private TableColumn<Pedido, String> colStatus;
    @FXML private TableColumn<Pedido, LocalDate> colDataPedido;
    @FXML private TableColumn<Pedido, LocalDate> colDataRetirada;
    @FXML private TextField campoFiltro;
    @FXML private ComboBox<String> comboFiltroStatus;

    // --- formulario do pedido ---
    @FXML private ComboBox<String> comboCliente;
    @FXML private ComboBox<String> comboPrescritor;
    @FXML private ComboBox<String> comboReceita;
    @FXML private DatePicker dateDataPedido;
    @FXML private DatePicker dateDataRetirada;
    @FXML private ComboBox<String> comboStatus;
    @FXML private TextArea campoObservacoes;
    @FXML private Label mensagem;

    // --- itens do pedido ---
    @FXML private TableView<PedidoItem> tabelaItens;
    @FXML private TableColumn<PedidoItem, String> colItemMP;
    @FXML private TableColumn<PedidoItem, Double> colItemQtd;
    @FXML private TableColumn<PedidoItem, String> colItemUnidade;
    @FXML private TableColumn<PedidoItem, String> colItemObs;

    // inline add de item
    @FXML private ComboBox<String> comboItemMP;
    @FXML private TextField campoItemQtd;
    @FXML private ComboBox<String> comboItemUnidade;
    @FXML private TextField campoItemObs;

    // mapas para converter nome → id nas combos
    private Map<String, Integer> idsClientes = new HashMap<>();
    private Map<String, Integer> idsPrescritores = new HashMap<>();
    private Map<String, Integer> idsReceitas = new HashMap<>();
    private Map<String, Integer> idsMPs = new HashMap<>();
    private Map<String, String> unidadesPorMP = new HashMap<>();

    private ObservableList<Pedido> listaPedidos = FXCollections.observableArrayList();
    private ObservableList<PedidoItem> listaItens = FXCollections.observableArrayList();

    // id do pedido atualmente selecionado/editado
    private int idPedidoAtual = -1;

    /**
     * inicializa combos, tabelas e listeners ao carregar a tela
     */
    @FXML
    public void initialize() {
        configurarColunas();
        carregarCombosAuxiliares();

        comboFiltroStatus.getItems().addAll("Todos", "Aguardando", "Em Produção", "Pronto", "Entregue", "Cancelado");
        comboFiltroStatus.getSelectionModel().selectFirst();
        comboStatus.getItems().addAll("Aguardando", "Em Produção", "Pronto", "Entregue", "Cancelado");
        comboStatus.getSelectionModel().selectFirst();

        comboItemUnidade.getItems().addAll("mg", "g", "ml", "mcg", "UI", "gotas", "cápsulas", "comprimidos", "un");

        // ao selecionar receita, carrega os ingredientes automaticamente
        comboReceita.setOnAction(e -> carregarIngredientesDaReceita());

        // ao selecionar mp no inline, preenche a unidade automaticamente
        comboItemMP.setOnAction(e -> preencherUnidadeItem());

        // ao selecionar pedido na tabela, preenche o formulario
        tabelaPedidos.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> { if (novo != null) selecionarPedido(novo); }
        );

        carregarPedidos();
        dateDataPedido.setValue(LocalDate.now());
    }

    /**
     * vincula as colunas da tabela as propriedades do modelo
     */
    private void configurarColunas() {
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nomeCliente"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDataPedido.setCellValueFactory(new PropertyValueFactory<>("dataPedido"));
        colDataRetirada.setCellValueFactory(new PropertyValueFactory<>("dataRetirada"));
        tabelaPedidos.setItems(listaPedidos);

        colItemMP.setCellValueFactory(new PropertyValueFactory<>("nomeMateriaPrima"));
        colItemQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colItemUnidade.setCellValueFactory(new PropertyValueFactory<>("unidade"));
        colItemObs.setCellValueFactory(new PropertyValueFactory<>("observacao"));
        tabelaItens.setItems(listaItens);

        // cor de linha por status
        tabelaPedidos.setRowFactory(tv -> new TableRow<Pedido>() {
            @Override
            protected void updateItem(Pedido p, boolean empty) {
                super.updateItem(p, empty);
                if (p == null || empty) {
                    setStyle("");
                } else {
                    switch (p.getStatus()) {
                        case "Pronto":
                            setStyle("-fx-background-color: #d5f5e3;"); break;
                        case "Cancelado":
                            setStyle("-fx-background-color: #fadbd8;"); break;
                        case "Em Produção":
                            setStyle("-fx-background-color: #fef9e7;"); break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
    }

    /**
     * carrega clientes, prescritores, receitas e materias primas nas combos
     */
    private void carregarCombosAuxiliares() {
        try (Connection conn = Conexao.conectar()) {

            // clientes
            ResultSet rsC = conn.createStatement().executeQuery(
                "SELECT id, nome FROM tb_clientes ORDER BY nome");
            comboCliente.getItems().clear();
            comboCliente.getItems().add("-- Selecione --");
            idsClientes.clear();
            while (rsC.next()) {
                String nome = rsC.getString("nome");
                comboCliente.getItems().add(nome);
                idsClientes.put(nome, rsC.getInt("id"));
            }
            comboCliente.getSelectionModel().selectFirst();

            // prescritores — primeiro item vazio (prescricao opcional)
            ResultSet rsP = conn.createStatement().executeQuery(
                "SELECT id, nome FROM tb_prescritores ORDER BY nome");
            comboPrescritor.getItems().clear();
            comboPrescritor.getItems().add("-- Sem prescritor --");
            idsPrescritores.clear();
            while (rsP.next()) {
                String nome = rsP.getString("nome");
                comboPrescritor.getItems().add(nome);
                idsPrescritores.put(nome, rsP.getInt("id"));
            }
            comboPrescritor.getSelectionModel().selectFirst();

            // receitas
            ResultSet rsR = conn.createStatement().executeQuery(
                "SELECT id, nome FROM tb_receitas ORDER BY nome");
            comboReceita.getItems().clear();
            comboReceita.getItems().add("-- Sem receita base --");
            idsReceitas.clear();
            while (rsR.next()) {
                String nome = rsR.getString("nome");
                comboReceita.getItems().add(nome);
                idsReceitas.put(nome, rsR.getInt("id"));
            }
            comboReceita.getSelectionModel().selectFirst();

            // materias primas para combo de item inline
            ResultSet rsMp = conn.createStatement().executeQuery(
                "SELECT id, nome, unidade FROM tb_materias_primas ORDER BY nome");
            comboItemMP.getItems().clear();
            idsMPs.clear();
            unidadesPorMP.clear();
            while (rsMp.next()) {
                String nome = rsMp.getString("nome");
                comboItemMP.getItems().add(nome);
                idsMPs.put(nome, rsMp.getInt("id"));
                unidadesPorMP.put(nome, rsMp.getString("unidade"));
            }

        } catch (Exception e) {
            mensagem.setText("Erro ao carregar dados: " + e.getMessage());
        }
    }

    /**
     * carrega todos os pedidos respeitando o filtro de status ativo
     */
    private void carregarPedidos() {
        listaPedidos.clear();
        String filtroStatus = comboFiltroStatus.getValue();
        String filtroTexto = campoFiltro.getText().trim().toLowerCase();

        String sql = "SELECT p.id, p.data_pedido, p.data_retirada, p.status, p.observacoes, " +
                     "c.nome AS nome_cliente, pr.nome AS nome_prescritor, r.nome AS nome_receita, " +
                     "p.id_cliente, p.id_prescritor, p.id_receita " +
                     "FROM tb_pedidos p " +
                     "LEFT JOIN tb_clientes c ON p.id_cliente = c.id " +
                     "LEFT JOIN tb_prescritores pr ON p.id_prescritor = pr.id " +
                     "LEFT JOIN tb_receitas r ON p.id_receita = r.id " +
                     "WHERE 1=1 ";

        if (!"Todos".equals(filtroStatus)) {
            sql += "AND p.status = ? ";
        }
        if (!filtroTexto.isEmpty()) {
            sql += "AND LOWER(c.nome) LIKE ? ";
        }
        sql += "ORDER BY p.data_pedido DESC, p.id DESC";

        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;
            if (!"Todos".equals(filtroStatus)) ps.setString(idx++, filtroStatus);
            if (!filtroTexto.isEmpty()) ps.setString(idx++, "%" + filtroTexto + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Pedido p = new Pedido();
                p.setId(rs.getInt("id"));
                p.setIdCliente(rs.getInt("id_cliente"));
                p.setNomeCliente(rs.getString("nome_cliente"));
                p.setIdPrescritor(rs.getInt("id_prescritor"));
                p.setNomePrescritor(rs.getString("nome_prescritor"));
                p.setIdReceita(rs.getInt("id_receita"));
                p.setNomeReceita(rs.getString("nome_receita"));
                Date dp = rs.getDate("data_pedido");
                if (dp != null) p.setDataPedido(dp.toLocalDate());
                Date dr = rs.getDate("data_retirada");
                if (dr != null) p.setDataRetirada(dr.toLocalDate());
                p.setStatus(rs.getString("status"));
                p.setObservacoes(rs.getString("observacoes"));
                listaPedidos.add(p);
            }

        } catch (Exception e) {
            mensagem.setText("Erro ao carregar pedidos: " + e.getMessage());
        }
    }

    /**
     * carrega os itens de um pedido especifico na tabela de itens
     */
    private void carregarItensDoPedido(int idPedido) {
        listaItens.clear();
        String sql = "SELECT pi.id, pi.id_pedido, pi.id_materia_prima, " +
                     "mp.nome AS nome_mp, pi.quantidade, pi.unidade, pi.observacao " +
                     "FROM tb_pedido_itens pi " +
                     "JOIN tb_materias_primas mp ON pi.id_materia_prima = mp.id " +
                     "WHERE pi.id_pedido = ? ORDER BY pi.id";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PedidoItem item = new PedidoItem();
                item.setId(rs.getInt("id"));
                item.setIdPedido(rs.getInt("id_pedido"));
                item.setIdMateriaPrima(rs.getInt("id_materia_prima"));
                item.setNomeMateriaPrima(rs.getString("nome_mp"));
                item.setQuantidade(rs.getDouble("quantidade"));
                item.setUnidade(rs.getString("unidade"));
                item.setObservacao(rs.getString("observacao"));
                listaItens.add(item);
            }
        } catch (Exception e) {
            mensagem.setText("Erro ao carregar itens: " + e.getMessage());
        }
    }

    /**
     * preenche o formulario com os dados do pedido selecionado na tabela
     */
    private void selecionarPedido(Pedido p) {
        idPedidoAtual = p.getId();
        mensagem.setText("");

        // cliente
        if (p.getNomeCliente() != null && comboCliente.getItems().contains(p.getNomeCliente())) {
            comboCliente.setValue(p.getNomeCliente());
        } else {
            comboCliente.getSelectionModel().selectFirst();
        }

        // prescritor
        if (p.getNomePrescritor() != null && comboPrescritor.getItems().contains(p.getNomePrescritor())) {
            comboPrescritor.setValue(p.getNomePrescritor());
        } else {
            comboPrescritor.getSelectionModel().selectFirst();
        }

        // receita — sem disparar o listener que recarregaria os itens
        comboReceita.setOnAction(null);
        if (p.getNomeReceita() != null && comboReceita.getItems().contains(p.getNomeReceita())) {
            comboReceita.setValue(p.getNomeReceita());
        } else {
            comboReceita.getSelectionModel().selectFirst();
        }
        comboReceita.setOnAction(e -> carregarIngredientesDaReceita());

        dateDataPedido.setValue(p.getDataPedido());
        dateDataRetirada.setValue(p.getDataRetirada());
        comboStatus.setValue(p.getStatus());
        campoObservacoes.setText(p.getObservacoes() != null ? p.getObservacoes() : "");

        carregarItensDoPedido(idPedidoAtual);
    }

    /**
     * ao escolher uma receita, carrega seus ingredientes como itens do pedido em edicao
     * so funciona se o pedido ja estiver salvo (idPedidoAtual != -1)
     * se for pedido novo, os ingredientes serao importados no momento do salvar
     */
    private void carregarIngredientesDaReceita() {
        String nomeReceita = comboReceita.getValue();
        if (nomeReceita == null || nomeReceita.startsWith("--")) return;
        if (idPedidoAtual == -1) return; // pedido novo — importa ao salvar

        Integer idReceita = idsReceitas.get(nomeReceita);
        if (idReceita == null) return;

        // pergunta se quer substituir os itens atuais
        if (!listaItens.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Importar Ingredientes");
            alert.setHeaderText("Importar ingredientes da receita?");
            alert.setContentText("Os itens atuais do pedido serão substituídos pelos ingredientes da receita selecionada.");
            if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

            // remove itens atuais do banco
            try (Connection conn = Conexao.conectar();
                 PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM tb_pedido_itens WHERE id_pedido = ?")) {
                ps.setInt(1, idPedidoAtual);
                ps.executeUpdate();
            } catch (Exception e) {
                mensagem.setText("Erro ao limpar itens: " + e.getMessage());
                return;
            }
        }

        importarIngredientesReceita(idReceita, idPedidoAtual);
        carregarItensDoPedido(idPedidoAtual);
    }

    /**
     * copia os ingredientes de uma receita para tb_pedido_itens de um pedido especifico
     */
    private void importarIngredientesReceita(int idReceita, int idPedido) {
        String sql = "INSERT INTO tb_pedido_itens (id_pedido, id_materia_prima, quantidade, unidade, observacao) " +
                     "SELECT ?, id_materia_prima, quantidade, unidade, observacao " +
                     "FROM tb_receita_ingredientes WHERE id_receita = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ps.setInt(2, idReceita);
            ps.executeUpdate();
        } catch (Exception e) {
            mensagem.setText("Erro ao importar ingredientes: " + e.getMessage());
        }
    }

    /**
     * prepara o formulario para um novo pedido em branco
     */
    @FXML
    private void novo() {
        idPedidoAtual = -1;
        comboCliente.getSelectionModel().selectFirst();
        comboPrescritor.getSelectionModel().selectFirst();
        comboReceita.getSelectionModel().selectFirst();
        dateDataPedido.setValue(LocalDate.now());
        dateDataRetirada.setValue(null);
        comboStatus.getSelectionModel().selectFirst();
        campoObservacoes.setText("");
        listaItens.clear();
        mensagem.setText("");
        tabelaPedidos.getSelectionModel().clearSelection();
    }

    /**
     * salva ou atualiza o pedido no banco de dados
     * se a receita foi selecionada e o pedido e novo, importa os ingredientes automaticamente
     */
    @FXML
    private void salvar() {
        String nomeCliente = comboCliente.getValue();
        if (nomeCliente == null || nomeCliente.startsWith("--")) {
            mensagem.setText("Selecione um cliente.");
            return;
        }
        if (dateDataPedido.getValue() == null) {
            mensagem.setText("Informe a data do pedido.");
            return;
        }

        Integer idCliente = idsClientes.get(nomeCliente);
        Integer idPrescritor = idsPrescritores.get(comboPrescritor.getValue());
        Integer idReceita = idsReceitas.get(comboReceita.getValue());
        LocalDate dataPedido = dateDataPedido.getValue();
        LocalDate dataRetirada = dateDataRetirada.getValue();
        String status = comboStatus.getValue();
        String obs = campoObservacoes.getText().trim();

        try (Connection conn = Conexao.conectar()) {
            if (idPedidoAtual == -1) {
                // INSERT novo pedido
                String sql = "INSERT INTO tb_pedidos (id_cliente, id_prescritor, id_receita, " +
                             "data_pedido, data_retirada, status, observacoes) VALUES (?,?,?,?,?,?,?) RETURNING id";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idCliente);
                if (idPrescritor != null) ps.setInt(2, idPrescritor); else ps.setNull(2, Types.INTEGER);
                if (idReceita != null) ps.setInt(3, idReceita); else ps.setNull(3, Types.INTEGER);
                ps.setDate(4, Date.valueOf(dataPedido));
                if (dataRetirada != null) ps.setDate(5, Date.valueOf(dataRetirada)); else ps.setNull(5, Types.DATE);
                ps.setString(6, status);
                ps.setString(7, obs.isEmpty() ? null : obs);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    idPedidoAtual = rs.getInt(1);
                }

                // importa ingredientes da receita automaticamente se houver
                if (idReceita != null) {
                    importarIngredientesReceita(idReceita, idPedidoAtual);
                    carregarItensDoPedido(idPedidoAtual);
                }

                mensagem.setText("Pedido criado com sucesso.");

            } else {
                // UPDATE pedido existente
                String sql = "UPDATE tb_pedidos SET id_cliente=?, id_prescritor=?, id_receita=?, " +
                             "data_pedido=?, data_retirada=?, status=?, observacoes=? WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idCliente);
                if (idPrescritor != null) ps.setInt(2, idPrescritor); else ps.setNull(2, Types.INTEGER);
                if (idReceita != null) ps.setInt(3, idReceita); else ps.setNull(3, Types.INTEGER);
                ps.setDate(4, Date.valueOf(dataPedido));
                if (dataRetirada != null) ps.setDate(5, Date.valueOf(dataRetirada)); else ps.setNull(5, Types.DATE);
                ps.setString(6, status);
                ps.setString(7, obs.isEmpty() ? null : obs);
                ps.setInt(8, idPedidoAtual);
                ps.executeUpdate();

                mensagem.setText("Pedido atualizado com sucesso.");
            }

            // gera rotulo automaticamente se o status for Em Producao
            if ("Em Produção".equals(status)) {
                String nomeFormula = idsReceitas.entrySet().stream()
                    .filter(e2 -> e2.getValue().equals(idReceita))
                    .map(Map.Entry::getKey).findFirst().orElse("Fórmula avulsa");
                String nomePrescritorVal = idsPrescritores.entrySet().stream()
                    .filter(e2 -> e2.getValue().equals(idPrescritor))
                    .map(Map.Entry::getKey).findFirst().orElse(null);
                RotulosController.gerarSeNaoExistir(idPedidoAtual, nomeFormula,
                    nomeCliente, nomePrescritorVal);
            }

            carregarPedidos();

        } catch (Exception e) {
            mensagem.setText("Erro ao salvar pedido: " + e.getMessage());
        }
    }

    /**
     * exclui o pedido selecionado com confirmacao
     * os itens sao excluidos automaticamente pelo ON DELETE CASCADE
     */
    @FXML
    private void excluir() {
        if (idPedidoAtual == -1) {
            mensagem.setText("Selecione um pedido para excluir.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Excluir pedido?");
        alert.setContentText("Os itens do pedido também serão excluídos. Essa ação não pode ser desfeita.");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_pedidos WHERE id = ?")) {
            ps.setInt(1, idPedidoAtual);
            ps.executeUpdate();
            novo();
            carregarPedidos();
            mensagem.setText("Pedido excluído.");
        } catch (Exception e) {
            mensagem.setText("Erro ao excluir pedido: " + e.getMessage());
        }
    }

    /**
     * aplica o filtro de texto e status na lista de pedidos
     */
    @FXML
    private void filtrar() {
        carregarPedidos();
    }

    /**
     * preenche a unidade automaticamente ao selecionar uma materia prima no inline add
     */
    private void preencherUnidadeItem() {
        String nomeMP = comboItemMP.getValue();
        if (nomeMP == null) return;
        String unidade = unidadesPorMP.get(nomeMP);
        if (unidade != null && !unidade.isEmpty()) {
            comboItemUnidade.setValue(unidade);
        }
    }

    /**
     * adiciona um item manualmente ao pedido (ex: embalagem, mp extra)
     * o pedido deve estar salvo antes de adicionar itens
     */
    @FXML
    private void adicionarItem() {
        if (idPedidoAtual == -1) {
            mensagem.setText("Salve o pedido primeiro para adicionar itens.");
            return;
        }

        String nomeMP = comboItemMP.getValue();
        String qtdStr = campoItemQtd.getText().replace(",", ".").trim();
        String unidade = comboItemUnidade.getValue();
        String obs = campoItemObs.getText().trim();

        if (nomeMP == null || nomeMP.isEmpty()) {
            mensagem.setText("Selecione uma matéria-prima.");
            return;
        }
        if (qtdStr.isEmpty()) {
            mensagem.setText("Informe a quantidade.");
            return;
        }

        double qtd;
        try {
            qtd = Double.parseDouble(qtdStr);
        } catch (NumberFormatException e) {
            mensagem.setText("Quantidade inválida.");
            return;
        }

        Integer idMP = idsMPs.get(nomeMP);
        if (idMP == null) return;

        String sql = "INSERT INTO tb_pedido_itens (id_pedido, id_materia_prima, quantidade, unidade, observacao) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPedidoAtual);
            ps.setInt(2, idMP);
            ps.setDouble(3, qtd);
            ps.setString(4, unidade != null ? unidade : "");
            ps.setString(5, obs.isEmpty() ? null : obs);
            ps.executeUpdate();

            carregarItensDoPedido(idPedidoAtual);
            campoItemQtd.setText("");
            campoItemObs.setText("");
            mensagem.setText("");

        } catch (Exception e) {
            mensagem.setText("Erro ao adicionar item: " + e.getMessage());
        }
    }

    /**
     * remove o item selecionado na tabela de itens com confirmacao
     */
    @FXML
    private void removerItem() {
        PedidoItem item = tabelaItens.getSelectionModel().getSelectedItem();
        if (item == null) {
            mensagem.setText("Selecione um item para remover.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remover Item");
        alert.setHeaderText("Remover \"" + item.getNomeMateriaPrima() + "\" do pedido?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_pedido_itens WHERE id = ?")) {
            ps.setInt(1, item.getId());
            ps.executeUpdate();
            carregarItensDoPedido(idPedidoAtual);
        } catch (Exception e) {
            mensagem.setText("Erro ao remover item: " + e.getMessage());
        }
    }

    /**
     * fecha a tela e volta para o menu principal
     */
    @FXML
    private void fechar() {
        App.trocarTela("principal");
    }
}
