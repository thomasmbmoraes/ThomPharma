package thompharma.telas;

import thompharma.App;
import thompharma.Conexao;
import thompharma.Mascara;
import thompharma.modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * controller do cadastro de clientes
 * permite listar, filtrar, cadastrar, editar e excluir clientes
 * prescritores (medicos, dentistas, etc) sao gerenciados em modulo separado
 */
public class ClientesController {

    // componentes da tabela de listagem
    @FXML private TableView<Cliente> tabelaClientes;
    @FXML private TableColumn<Cliente, String> colNome;
    @FXML private TableColumn<Cliente, String> colCpf;
    @FXML private TableColumn<Cliente, String> colTelefone;

    // campo de filtro para busca por nome
    @FXML private TextField campoFiltro;

    // campos do formulario
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

    // lista completa de clientes carregada do banco
    private ObservableList<Cliente> listaCompleta = FXCollections.observableArrayList();

    /**
     * executado automaticamente ao carregar a tela
     * configura colunas, mascaras, listeners e carrega dados do banco
     */
    @FXML
    public void initialize() {
        // configura quais atributos aparecem nas colunas
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        tabelaClientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // aplica mascaras nos campos
        Mascara.cpf(campoCpf);
        Mascara.telefone(campoTelefone);
        Mascara.telefone(campoTelefone2);
        Mascara.cep(campoCep);
        Mascara.soNumeros(campoDesconto, 5);
        Mascara.data(campoNascimento);

        carregarClientes();

        // ao selecionar um cliente na tabela, preenche o formulario
        tabelaClientes.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> {
                if (novo != null) preencherFormulario(novo);
            }
        );

        // filtra a lista conforme o usuario digita
        campoFiltro.textProperty().addListener((obs, antigo, novo) -> filtrar(novo));
    }

    /**
     * busca todos os clientes do banco e exibe na tabela
     */
    private void carregarClientes() {
        listaCompleta.clear();
        try {
            Connection conn = Conexao.conectar();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM tb_clientes ORDER BY nome"
            );
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id"));
                c.setNome(rs.getString("nome"));
                c.setCpf(rs.getString("cpf"));
                c.setTelefone(rs.getString("telefone"));
                c.setTelefone2(rs.getString("telefone2"));
                c.setEmail(rs.getString("email"));
                c.setCep(rs.getString("cep"));
                c.setEndereco(rs.getString("endereco"));
                c.setBairro(rs.getString("bairro"));
                c.setCidade(rs.getString("cidade"));
                c.setUf(rs.getString("uf"));
                c.setDesconto(rs.getDouble("desconto"));
                c.setObservacoes(rs.getString("observacoes"));
                listaCompleta.add(c);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Erro ao carregar clientes: " + e.getMessage());
        }
        tabelaClientes.setItems(listaCompleta);
    }

    /**
     * filtra a lista de clientes pelo nome
     * @param texto texto digitado no campo de filtro
     */
    private void filtrar(String texto) {
        if (texto == null || texto.isEmpty()) {
            tabelaClientes.setItems(listaCompleta);
            return;
        }
        ObservableList<Cliente> filtrada = FXCollections.observableArrayList();
        for (Cliente c : listaCompleta) {
            if (c.getNome().toLowerCase().contains(texto.toLowerCase())) {
                filtrada.add(c);
            }
        }
        tabelaClientes.setItems(filtrada);
    }

    /**
     * preenche o formulario com os dados do cliente selecionado na tabela
     * @param c cliente selecionado
     */
    private void preencherFormulario(Cliente c) {
        scrollFormulario.setVvalue(0);
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
        mensagem.setText("");
    }

    /**
     * limpa o formulario para cadastrar um novo cliente
     */
    @FXML
    private void novo() {
        campoNome.setText("");
        campoCpf.setText("");
        campoTelefone.setText("");
        campoTelefone2.setText("");
        campoEmail.setText("");
        campoCep.setText("");
        campoEndereco.setText("");
        campoBairro.setText("");
        campoCidade.setText("");
        campoUf.setText("");
        campoDesconto.setText("0");
        campoObservacoes.setText("");
        mensagem.setText("");
        tabelaClientes.getSelectionModel().clearSelection();
    }

    /**
     * salva o cliente no banco
     * insere novo se nenhum estiver selecionado, atualiza se estiver
     * padroniza o nome antes de salvar e verifica CPF duplicado
     */
    @FXML
    private void salvar() {
        if (campoNome.getText().isEmpty()) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Preencha o nome!");
            return;
        }

        String nome = padronizarNome(campoNome.getText());

        try {
            Connection conn = Conexao.conectar();
            Cliente selecionado = tabelaClientes.getSelectionModel().getSelectedItem();

            // verifica se ja existe outro cliente com o mesmo CPF
            if (!campoCpf.getText().isEmpty()) {
                PreparedStatement check = conn.prepareStatement(
                    "SELECT id FROM tb_clientes WHERE cpf = ?"
                );
                check.setString(1, campoCpf.getText());
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    int idEncontrado = rs.getInt("id");
                    if (selecionado == null || idEncontrado != selecionado.getId()) {
                        mensagem.setStyle("-fx-text-fill: red;");
                        mensagem.setText("Já existe um cliente com este CPF!");
                        conn.close();
                        return;
                    }
                }
            }

            if (selecionado == null) {
                // insere novo cliente
                String sql = "INSERT INTO tb_clientes (nome, cpf, telefone, telefone2, email, cep, endereco, bairro, cidade, uf, desconto, observacoes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nome);
                stmt.setString(2, campoCpf.getText());
                stmt.setString(3, campoTelefone.getText());
                stmt.setString(4, campoTelefone2.getText());
                stmt.setString(5, campoEmail.getText());
                stmt.setString(6, campoCep.getText());
                stmt.setString(7, campoEndereco.getText());
                stmt.setString(8, campoBairro.getText());
                stmt.setString(9, campoCidade.getText());
                stmt.setString(10, campoUf.getText());
                stmt.setDouble(11, Double.parseDouble(campoDesconto.getText().isEmpty() ? "0" : campoDesconto.getText()));
                stmt.setString(12, campoObservacoes.getText());
                stmt.executeUpdate();
                mensagem.setStyle("-fx-text-fill: green;");
                mensagem.setText("Cliente cadastrado com sucesso!");
            } else {
                // atualiza cliente existente
                String sql = "UPDATE tb_clientes SET nome=?, cpf=?, telefone=?, telefone2=?, email=?, cep=?, endereco=?, bairro=?, cidade=?, uf=?, desconto=?, observacoes=? WHERE id=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nome);
                stmt.setString(2, campoCpf.getText());
                stmt.setString(3, campoTelefone.getText());
                stmt.setString(4, campoTelefone2.getText());
                stmt.setString(5, campoEmail.getText());
                stmt.setString(6, campoCep.getText());
                stmt.setString(7, campoEndereco.getText());
                stmt.setString(8, campoBairro.getText());
                stmt.setString(9, campoCidade.getText());
                stmt.setString(10, campoUf.getText());
                stmt.setDouble(11, Double.parseDouble(campoDesconto.getText().isEmpty() ? "0" : campoDesconto.getText()));
                stmt.setString(12, campoObservacoes.getText());
                stmt.setInt(13, selecionado.getId());
                stmt.executeUpdate();
                mensagem.setStyle("-fx-text-fill: green;");
                mensagem.setText("Cliente atualizado com sucesso!");
            }
            conn.close();
            carregarClientes();
            campoNome.setText(nome);
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao salvar: " + e.getMessage());
        }
    }

    /**
     * exclui o cliente selecionado na tabela
     * exibe dialogo de confirmacao antes de executar a exclusao
     */
    @FXML
    private void excluir() {
        Cliente selecionado = tabelaClientes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mensagem.setText("Selecione um cliente!");
            return;
        }
        // pede confirmacao antes de excluir para evitar exclusoes acidentais
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja excluir o cliente \"" + selecionado.getNome() + "\"?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_clientes WHERE id=?");
            stmt.setInt(1, selecionado.getId());
            stmt.executeUpdate();
            conn.close();
            carregarClientes();
            novo();
            mensagem.setStyle("-fx-text-fill: green;");
            mensagem.setText("Cliente excluído com sucesso!");
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao excluir: " + e.getMessage());
        }
    }

    /**
     * padroniza o nome capitalizando a primeira letra de cada palavra
     * artigos e preposicoes sao mantidos em minusculo
     * @param nome nome digitado pelo usuario
     * @return nome formatado
     */
    private String padronizarNome(String nome) {
        if (nome == null || nome.isEmpty()) return nome;
        String[] artigos = {"de", "da", "do", "das", "dos", "e", "a", "o", "em", "por"};
        java.util.Set<String> artigosSet = new java.util.HashSet<>(java.util.Arrays.asList(artigos));
        String[] palavras = nome.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < palavras.length; i++) {
            if (i > 0) sb.append(" ");
            if (i > 0 && artigosSet.contains(palavras[i])) {
                sb.append(palavras[i]);
            } else if (!palavras[i].isEmpty()) {
                sb.append(Character.toUpperCase(palavras[i].charAt(0)));
                sb.append(palavras[i].substring(1));
            }
        }
        return sb.toString();
    }

    /**
     * fecha a tela de clientes e volta para a tela principal
     */
    @FXML
    private void fechar() {
        App.trocarTela("principal");
    }
}
