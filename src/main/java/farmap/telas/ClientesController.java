package farmap.telas;

import farmap.App;
import farmap.Conexao;
import farmap.Mascara;
import farmap.modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

/**
 * controller do cadastro de clientes
 * permite listar, filtrar, cadastrar, editar e excluir clientes
 * suporta cadastro de medicos com crm e especialidade
 */
public class ClientesController {

    // componentes da tabela de listagem
    @FXML private TableView<Cliente> tabelaClientes;
    @FXML private TableColumn<Cliente, String> colNome;
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
    @FXML private CheckBox checkMedico;
    @FXML private TextField campoCrm;
    @FXML private ComboBox<String> comboEspecialidade;
    @FXML private VBox painelMedico;
    @FXML private Label mensagem;
    @FXML private TableColumn<Cliente, String> colEndereco;
    @FXML private javafx.scene.control.ScrollPane scrollFormulario;

    // lista completa de clientes carregada do banco
    private ObservableList<Cliente> listaCompleta = FXCollections.observableArrayList();

    // lista de ids das especialidades para salvar no banco
    private ObservableList<Integer> idsEspecialidades = FXCollections.observableArrayList();

    /**
     * executado automaticamente ao carregar a tela
     * configura colunas, mascaras, listeners e carrega dados do banco
     */
    @FXML
    public void initialize() {
        // configura quais atributos aparecem nas colunas
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTelefone.setCellValueFactory(data -> {
            Cliente c = data.getValue();
            if (c.isMedico() && c.getCrm() != null && !c.getCrm().isEmpty()) {
                return new javafx.beans.property.SimpleStringProperty("CRM: " + c.getCrm());
            }
            return new javafx.beans.property.SimpleStringProperty(
                c.getTelefone() != null ? c.getTelefone() : ""
            );
        });
        colEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));

        // aplica mascaras nos campos
        Mascara.cpf(campoCpf);
        Mascara.telefone(campoTelefone);
        Mascara.telefone(campoTelefone2);
        Mascara.cep(campoCep);
        Mascara.soNumeros(campoDesconto, 5);
        Mascara.data(campoNascimento);

        carregarEspecialidades();
        carregarClientes();

        // ao selecionar um cliente na tabela, preenche o formulario
        tabelaClientes.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> {
                if (novo != null) preencherFormulario(novo);
            }
        );

        // filtra a lista conforme o usuario digita
        campoFiltro.textProperty().addListener((obs, antigo, novo) -> filtrar(novo));
        
        tabelaClientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * carrega as especialidades medicas do banco para o combobox
     */
    private void carregarEspecialidades() {
        try {
            Connection conn = Conexao.conectar();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM tb_especialidades_medicas ORDER BY id"
            );
            while (rs.next()) {
                idsEspecialidades.add(rs.getInt("id"));
                comboEspecialidade.getItems().add(rs.getString("nome"));
            }
            conn.close();
            comboEspecialidade.getSelectionModel().selectFirst();
        } catch (Exception e) {
            System.out.println("Erro ao carregar especialidades: " + e.getMessage());
        }
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
                c.setMedico(rs.getBoolean("medico"));
                c.setCrm(rs.getString("crm"));
                c.setIdEspecialidade(rs.getInt("id_especialidade"));
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
        checkMedico.setSelected(c.isMedico());
        campoCrm.setText(c.getCrm() != null ? c.getCrm() : "");

        // seleciona a especialidade correta no combobox
        int idx = idsEspecialidades.indexOf(c.getIdEspecialidade());
        if (idx >= 0) comboEspecialidade.getSelectionModel().select(idx);

        // mostra ou oculta o painel de medico
        painelMedico.setVisible(c.isMedico());
        painelMedico.setManaged(c.isMedico());
        mensagem.setText("");
    }

    /**
     * mostra ou oculta o painel de medico conforme o checkbox
     */
    @FXML
    private void toggleMedico() {
        boolean medico = checkMedico.isSelected();
        painelMedico.setVisible(medico);
        painelMedico.setManaged(medico);
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
        checkMedico.setSelected(false);
        campoCrm.setText("");
        comboEspecialidade.getSelectionModel().selectFirst();
        painelMedico.setVisible(false);
        painelMedico.setManaged(false);
        mensagem.setText("");
        tabelaClientes.getSelectionModel().clearSelection();
    }

    /**
     * salva o cliente no banco
     * insere novo se nenhum estiver selecionado, atualiza se estiver
     */
    @FXML
    private void salvar() {
        if (campoNome.getText().isEmpty()) {
            mensagem.setText("Preencha o nome!");
            return;
        }
        try {
            Connection conn = Conexao.conectar();
            Cliente selecionado = tabelaClientes.getSelectionModel().getSelectedItem();

            // pega o id da especialidade selecionada no combobox
            int idEspecialidade = idsEspecialidades.get(
                comboEspecialidade.getSelectionModel().getSelectedIndex()
            );

            if (selecionado == null) {
                // insere novo cliente
                String sql = "INSERT INTO tb_clientes (nome, cpf, telefone, telefone2, email, cep, endereco, bairro, cidade, uf, desconto, medico, crm, id_especialidade, observacoes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, campoNome.getText());
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
                stmt.setBoolean(12, checkMedico.isSelected());
                stmt.setString(13, campoCrm.getText());
                stmt.setInt(14, idEspecialidade);
                stmt.setString(15, campoObservacoes.getText());
                stmt.executeUpdate();
                mensagem.setStyle("-fx-text-fill: green;");
                mensagem.setText("Cliente cadastrado com sucesso!");
            } else {
                // atualiza cliente existente
                String sql = "UPDATE tb_clientes SET nome=?, cpf=?, telefone=?, telefone2=?, email=?, cep=?, endereco=?, bairro=?, cidade=?, uf=?, desconto=?, medico=?, crm=?, id_especialidade=?, observacoes=? WHERE id=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, campoNome.getText());
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
                stmt.setBoolean(12, checkMedico.isSelected());
                stmt.setString(13, campoCrm.getText());
                stmt.setInt(14, idEspecialidade);
                stmt.setString(15, campoObservacoes.getText());
                stmt.setInt(16, selecionado.getId());
                stmt.executeUpdate();
                mensagem.setStyle("-fx-text-fill: green;");
                mensagem.setText("Cliente atualizado com sucesso!");
            }
            conn.close();
            carregarClientes();
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao salvar: " + e.getMessage());
        }
    }

    /**
     * exclui o cliente selecionado na tabela
     */
    @FXML
    private void excluir() {
        Cliente selecionado = tabelaClientes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mensagem.setText("Selecione um cliente!");
            return;
        }
        try {
            Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_clientes WHERE id=?");
            stmt.setInt(1, selecionado.getId());
            stmt.executeUpdate();
            conn.close();
            carregarClientes();
            novo();
            mensagem.setStyle("-fx-text-fill: green;");
            mensagem.setText("Cliente excluido com sucesso!");
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao excluir: " + e.getMessage());
        }
    }

    /**
     * fecha a tela de clientes e volta para a tela principal
     */
    @FXML
    private void fechar() {
        App.trocarTela("principal");
    }
}