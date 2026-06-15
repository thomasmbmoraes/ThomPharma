package thompharma.telas;

import thompharma.App;
import thompharma.AsyncUtil;
import thompharma.Mascara;
import thompharma.UiUtil;
import thompharma.dao.ClienteDao;
import thompharma.modelo.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ClientesController {

    @FXML private TableView<Cliente> tabelaClientes;
    @FXML private TableColumn<Cliente, String> colNome;
    @FXML private TableColumn<Cliente, String> colCpf;
    @FXML private TableColumn<Cliente, String> colTelefone;
    @FXML private TextField campoFiltro;
    @FXML private TextField campoNome;
    @FXML private TextField campoCpf;
    @FXML private TextField campoTelefone;
    @FXML private TextField campoTelefone2;
    @FXML private TextField campoEmail;
    @FXML private TextField campoNascimento;
    @FXML private TextField campoDesconto;
    @FXML private TextField campoCep;
    @FXML private TextField campoEndereco;
    @FXML private TextField campoBairro;
    @FXML private TextField campoCidade;
    @FXML private TextField campoUf;
    @FXML private TextArea campoObservacoes;
    @FXML private Label mensagem;
    @FXML private javafx.scene.control.ScrollPane scrollFormulario;

    private final ClienteDao dao = new ClienteDao();
    private final ObservableList<Cliente> listaCompleta = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        tabelaClientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabelaClientes.setPlaceholder(new Label("Nenhum cliente cadastrado."));

        Mascara.cpf(campoCpf);
        Mascara.telefone(campoTelefone);
        Mascara.telefone(campoTelefone2);
        Mascara.cep(campoCep);
        Mascara.soNumeros(campoDesconto, 5);
        Mascara.data(campoNascimento);

        // limpar validação ao digitar
        campoNome.textProperty().addListener((o, a, n) -> UiUtil.limparValidacao(campoNome));

        carregarClientes();

        tabelaClientes.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> { if (novo != null) preencherFormulario(novo); });
        campoFiltro.textProperty().addListener((obs, antigo, novo) -> filtrar(novo));
    }

    private void carregarClientes() {
        Task<List<Cliente>> task = AsyncUtil.task(dao::listarTodos, mensagem);
        task.setOnSucceeded(e -> {
            listaCompleta.setAll(task.getValue());
            tabelaClientes.setItems(listaCompleta);
        });
        AsyncUtil.run(task);
    }

    private void filtrar(String texto) {
        if (texto == null || texto.isEmpty()) {
            tabelaClientes.setItems(listaCompleta);
            return;
        }
        ObservableList<Cliente> filtrada = FXCollections.observableArrayList();
        for (Cliente c : listaCompleta) {
            if (c.getNome().toLowerCase().contains(texto.toLowerCase())) filtrada.add(c);
        }
        tabelaClientes.setItems(filtrada);
    }

    private void preencherFormulario(Cliente c) {
        if (scrollFormulario != null) scrollFormulario.setVvalue(0);
        campoNome.setText(c.getNome());
        campoCpf.setText(c.getCpf() != null ? c.getCpf() : "");
        campoTelefone.setText(c.getTelefone() != null ? c.getTelefone() : "");
        campoTelefone2.setText(c.getTelefone2() != null ? c.getTelefone2() : "");
        campoEmail.setText(c.getEmail() != null ? c.getEmail() : "");
        campoCep.setText(c.getCep() != null ? c.getCep() : "");
        campoEndereco.setText(c.getEndereco() != null ? c.getEndereco() : "");
        campoBairro.setText(c.getBairro() != null ? c.getBairro() : "");
        campoCidade.setText(c.getCidade() != null ? c.getCidade() : "");
        campoUf.setText(c.getUf() != null ? c.getUf() : "");
        campoDesconto.setText(String.valueOf(c.getDesconto()));
        campoObservacoes.setText(c.getObservacoes() != null ? c.getObservacoes() : "");
        UiUtil.limpar(mensagem);
    }

    @FXML
    private void novo() {
        campoNome.setText(""); campoCpf.setText(""); campoTelefone.setText("");
        campoTelefone2.setText(""); campoEmail.setText(""); campoCep.setText("");
        campoEndereco.setText(""); campoBairro.setText(""); campoCidade.setText("");
        campoUf.setText(""); campoDesconto.setText("0"); campoObservacoes.setText("");
        UiUtil.limpar(mensagem);
        UiUtil.limparValidacao(campoNome);
        tabelaClientes.getSelectionModel().clearSelection();
    }

    @FXML
    private void salvar() {
        if (!UiUtil.validarObrigatorio(campoNome)) {
            UiUtil.erro(mensagem, "Preencha o nome do cliente!");
            return;
        }

        String nome = Mascara.padronizarNome(campoNome.getText());
        Cliente selecionado = tabelaClientes.getSelectionModel().getSelectedItem();

        Cliente c = new Cliente();
        if (selecionado != null) c.setId(selecionado.getId());
        c.setNome(nome);
        c.setCpf(campoCpf.getText());
        c.setTelefone(campoTelefone.getText());
        c.setTelefone2(campoTelefone2.getText());
        c.setEmail(campoEmail.getText());
        c.setCep(campoCep.getText());
        c.setEndereco(campoEndereco.getText());
        c.setBairro(campoBairro.getText());
        c.setCidade(campoCidade.getText());
        c.setUf(campoUf.getText());
        try { c.setDesconto(Double.parseDouble(campoDesconto.getText().isEmpty() ? "0" : campoDesconto.getText())); }
        catch (NumberFormatException e) { c.setDesconto(0); }
        c.setObservacoes(campoObservacoes.getText());

        Task<Void> task = AsyncUtil.task(() -> {
            if (!campoCpf.getText().isEmpty() && dao.cpfDuplicado(campoCpf.getText(), c.getId())) {
                throw new Exception("Já existe um cliente com este CPF!");
            }
            if (selecionado == null) dao.inserir(c);
            else dao.atualizar(c);
            return null;
        }, mensagem);

        task.setOnSucceeded(e -> {
            UiUtil.sucesso(mensagem, selecionado == null ? "Cliente cadastrado!" : "Cliente atualizado!");
            carregarClientes();
        });
        AsyncUtil.run(task);
    }

    @FXML
    private void excluir() {
        Cliente selecionado = tabelaClientes.getSelectionModel().getSelectedItem();
        if (selecionado == null) { UiUtil.erro(mensagem, "Selecione um cliente!"); return; }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja excluir o cliente \"" + selecionado.getNome() + "\"?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        Task<Void> task = AsyncUtil.task(() -> { dao.excluir(selecionado.getId()); return null; }, mensagem);
        task.setOnSucceeded(e -> { novo(); carregarClientes(); UiUtil.sucesso(mensagem, "Cliente excluído!"); });
        AsyncUtil.run(task);
    }

    @FXML
    private void fechar() { App.trocarTela("principal"); }
}
