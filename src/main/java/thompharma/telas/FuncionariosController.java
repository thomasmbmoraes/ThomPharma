package thompharma.telas;

import thompharma.App;
import thompharma.Conexao;
import thompharma.Mascara;
import thompharma.modelo.Funcionario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * controller do cadastro de funcionarios
 * permite listar, cadastrar, editar e excluir funcionarios
 */
public class FuncionariosController {

    // componentes da tabela de listagem
    @FXML private TableView<Funcionario> tabelaFuncionarios;
    @FXML private TableColumn<Funcionario, String> colNome;
    @FXML private TableColumn<Funcionario, String> colCargo;

    // campos do formulario
    @FXML private TextField campoNome;
    @FXML private TextField campoMatricula;
    @FXML private TextField campoSetor;
    @FXML private TextField campoCargo;
    @FXML private TextField campoNascimento;
    @FXML private TextField campoRg;
    @FXML private TextField campoCpf;
    @FXML private TextField campoCep;
    @FXML private TextField campoEndereco;
    @FXML private TextField campoBairro;
    @FXML private TextField campoCidade;
    @FXML private TextField campoUf;
    @FXML private TextField campoTelefone;
    @FXML private TextField campoTelefone2;
    @FXML private TextField campoCelular;
    @FXML private TextArea campoObservacoes;
    @FXML private CheckBox checkAtivo;
    @FXML private ComboBox<String> comboSexo;
    @FXML private ComboBox<String> comboEstadoCivil;
    @FXML private Label mensagem;

    // lista completa de funcionarios carregada do banco
    private ObservableList<Funcionario> listaCompleta = FXCollections.observableArrayList();

    /**
     * executado automaticamente ao carregar a tela
     * configura colunas, mascaras, comboboxes e carrega dados do banco
     */
    @FXML
    public void initialize() {
        // configura quais atributos aparecem nas colunas
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        tabelaFuncionarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // preenche os comboboxes com opcoes fixas
        comboSexo.getItems().addAll("Masculino", "Feminino");
        comboSexo.getSelectionModel().selectFirst();

        comboEstadoCivil.getItems().addAll(
            "Solteiro", "Casado", "Divorciado", "Viúvo", "União Estável"
        );
        comboEstadoCivil.getSelectionModel().selectFirst();

        // aplica mascaras nos campos
        Mascara.cpf(campoCpf);
        Mascara.telefone(campoTelefone);
        Mascara.telefone(campoTelefone2);
        Mascara.telefone(campoCelular);
        Mascara.cep(campoCep);
        Mascara.data(campoNascimento);
        Mascara.rg(campoRg);

        carregarFuncionarios();

        // ao selecionar um funcionario na tabela, preenche o formulario
        tabelaFuncionarios.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> {
                if (novo != null) preencherFormulario(novo);
            }
        );
    }

    /**
     * busca todos os funcionarios do banco e exibe na tabela
     */
    private void carregarFuncionarios() {
        listaCompleta.clear();
        try {
            Connection conn = Conexao.conectar();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM tb_funcionarios ORDER BY nome"
            );
            while (rs.next()) {
                Funcionario f = new Funcionario();
                f.setId(rs.getInt("id"));
                f.setNome(rs.getString("nome"));
                f.setMatricula(rs.getString("matricula"));
                f.setSetor(rs.getString("setor"));
                f.setCargo(rs.getString("cargo"));
                f.setSexo(rs.getString("sexo"));
                f.setEstadoCivil(rs.getString("estado_civil"));
                f.setRg(rs.getString("rg"));
                f.setCpf(rs.getString("cpf"));
                f.setCep(rs.getString("cep"));
                f.setEndereco(rs.getString("endereco"));
                f.setBairro(rs.getString("bairro"));
                f.setCidade(rs.getString("cidade"));
                f.setUf(rs.getString("uf"));
                f.setTelefone(rs.getString("telefone"));
                f.setTelefone2(rs.getString("telefone2"));
                f.setCelular(rs.getString("celular"));
                f.setObservacoes(rs.getString("observacoes"));
                f.setAtivo(rs.getBoolean("ativo"));
                listaCompleta.add(f);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Erro ao carregar funcionarios: " + e.getMessage());
        }
        tabelaFuncionarios.setItems(listaCompleta);
    }

    /**
     * preenche o formulario com os dados do funcionario selecionado na tabela
     * @param f funcionario selecionado
     */
    private void preencherFormulario(Funcionario f) {
        campoNome.setText(f.getNome());
        campoMatricula.setText(f.getMatricula() != null ? f.getMatricula() : "");
        campoSetor.setText(f.getSetor() != null ? f.getSetor() : "");
        campoCargo.setText(f.getCargo() != null ? f.getCargo() : "");
        campoRg.setText(f.getRg() != null ? f.getRg() : "");
        campoCpf.setText(f.getCpf() != null ? f.getCpf() : "");
        campoCep.setText(f.getCep() != null ? f.getCep() : "");
        campoEndereco.setText(f.getEndereco() != null ? f.getEndereco() : "");
        campoBairro.setText(f.getBairro() != null ? f.getBairro() : "");
        campoCidade.setText(f.getCidade() != null ? f.getCidade() : "");
        campoUf.setText(f.getUf() != null ? f.getUf() : "");
        campoTelefone.setText(f.getTelefone() != null ? f.getTelefone() : "");
        campoTelefone2.setText(f.getTelefone2() != null ? f.getTelefone2() : "");
        campoCelular.setText(f.getCelular() != null ? f.getCelular() : "");
        campoObservacoes.setText(f.getObservacoes() != null ? f.getObservacoes() : "");
        checkAtivo.setSelected(f.isAtivo());

        // seleciona o sexo correto no combobox
        if ("M".equals(f.getSexo())) {
            comboSexo.getSelectionModel().select("Masculino");
        } else {
            comboSexo.getSelectionModel().select("Feminino");
        }

        // seleciona o estado civil correto no combobox
        if (f.getEstadoCivil() != null) {
            comboEstadoCivil.getSelectionModel().select(f.getEstadoCivil());
        }

        mensagem.setText("");
    }

    /**
     * limpa o formulario para cadastrar um novo funcionario
     */
    @FXML
    private void novo() {
        campoNome.setText("");
        campoMatricula.setText("");
        campoSetor.setText("");
        campoCargo.setText("");
        campoNascimento.setText("");
        campoRg.setText("");
        campoCpf.setText("");
        campoCep.setText("");
        campoEndereco.setText("");
        campoBairro.setText("");
        campoCidade.setText("");
        campoUf.setText("");
        campoTelefone.setText("");
        campoTelefone2.setText("");
        campoCelular.setText("");
        campoObservacoes.setText("");
        checkAtivo.setSelected(true);
        comboSexo.getSelectionModel().selectFirst();
        comboEstadoCivil.getSelectionModel().selectFirst();
        mensagem.setText("");
        tabelaFuncionarios.getSelectionModel().clearSelection();
    }

    /**
     * salva o funcionario no banco
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
            Funcionario selecionado = tabelaFuncionarios.getSelectionModel().getSelectedItem();

            // converte sexo para char M ou F
            String sexo = comboSexo.getValue().equals("Masculino") ? "M" : "F";

            if (selecionado == null) {
                // insere novo funcionario
                String sql = "INSERT INTO tb_funcionarios (nome, matricula, setor, cargo, sexo, estado_civil, rg, cpf, cep, endereco, bairro, cidade, uf, telefone, telefone2, celular, observacoes, ativo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, campoNome.getText());
                stmt.setString(2, campoMatricula.getText());
                stmt.setString(3, campoSetor.getText());
                stmt.setString(4, campoCargo.getText());
                stmt.setString(5, sexo);
                stmt.setString(6, comboEstadoCivil.getValue());
                stmt.setString(7, campoRg.getText());
                stmt.setString(8, campoCpf.getText());
                stmt.setString(9, campoCep.getText());
                stmt.setString(10, campoEndereco.getText());
                stmt.setString(11, campoBairro.getText());
                stmt.setString(12, campoCidade.getText());
                stmt.setString(13, campoUf.getText());
                stmt.setString(14, campoTelefone.getText());
                stmt.setString(15, campoTelefone2.getText());
                stmt.setString(16, campoCelular.getText());
                stmt.setString(17, campoObservacoes.getText());
                stmt.setBoolean(18, checkAtivo.isSelected());
                stmt.executeUpdate();
                mensagem.setStyle("-fx-text-fill: green;");
                mensagem.setText("Funcionário cadastrado com sucesso!");
            } else {
                // atualiza funcionario existente
                String sql = "UPDATE tb_funcionarios SET nome=?, matricula=?, setor=?, cargo=?, sexo=?, estado_civil=?, rg=?, cpf=?, cep=?, endereco=?, bairro=?, cidade=?, uf=?, telefone=?, telefone2=?, celular=?, observacoes=?, ativo=? WHERE id=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, campoNome.getText());
                stmt.setString(2, campoMatricula.getText());
                stmt.setString(3, campoSetor.getText());
                stmt.setString(4, campoCargo.getText());
                stmt.setString(5, sexo);
                stmt.setString(6, comboEstadoCivil.getValue());
                stmt.setString(7, campoRg.getText());
                stmt.setString(8, campoCpf.getText());
                stmt.setString(9, campoCep.getText());
                stmt.setString(10, campoEndereco.getText());
                stmt.setString(11, campoBairro.getText());
                stmt.setString(12, campoCidade.getText());
                stmt.setString(13, campoUf.getText());
                stmt.setString(14, campoTelefone.getText());
                stmt.setString(15, campoTelefone2.getText());
                stmt.setString(16, campoCelular.getText());
                stmt.setString(17, campoObservacoes.getText());
                stmt.setBoolean(18, checkAtivo.isSelected());
                stmt.setInt(19, selecionado.getId());
                stmt.executeUpdate();
                mensagem.setStyle("-fx-text-fill: green;");
                mensagem.setText("Funcionário atualizado com sucesso!");
            }
            conn.close();
            carregarFuncionarios();
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao salvar: " + e.getMessage());
        }
    }

    /**
     * exclui o funcionario selecionado na tabela
     * exibe dialogo de confirmacao antes de executar a exclusao
     */
    @FXML
    private void excluir() {
        Funcionario selecionado = tabelaFuncionarios.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mensagem.setText("Selecione um funcionário!");
            return;
        }
        // pede confirmacao antes de excluir para evitar exclusoes acidentais
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja excluir o funcionário \"" + selecionado.getNome() + "\"?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_funcionarios WHERE id=?");
            stmt.setInt(1, selecionado.getId());
            stmt.executeUpdate();
            conn.close();
            carregarFuncionarios();
            novo();
            mensagem.setStyle("-fx-text-fill: green;");
            mensagem.setText("Funcionário excluido com sucesso!");
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao excluir: " + e.getMessage());
        }
    }

    /**
     * fecha a tela de funcionarios e volta para a tela principal
     */
    @FXML
    private void fechar() {
        App.trocarTela("principal");
    }
}