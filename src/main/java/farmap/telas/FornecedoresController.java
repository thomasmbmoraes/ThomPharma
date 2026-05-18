package farmap.telas;

import farmap.App;
import farmap.Conexao;
import farmap.Mascara;
import farmap.modelo.Fornecedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * controller do cadastro de fornecedores
 * permite listar, filtrar, cadastrar, editar e excluir fornecedores
 * aplica mascaras de formatacao nos campos de documento e telefone
 */
public class FornecedoresController {

    // componentes da tabela de listagem
    @FXML private TableView<Fornecedor> tabelaFornecedores;
    @FXML private TableColumn<Fornecedor, String> colNome;

    // campo de filtro para busca por nome
    @FXML private TextField campoFiltro;

    // campos do formulario
    @FXML private TextField campoNome;
    @FXML private TextField campoCnpj;
    @FXML private TextField campoContato;
    @FXML private TextField campoTelefone;
    @FXML private TextField campoEmail;
    @FXML private TextField campoCidade;
    @FXML private TextField campoUf;
    @FXML private Label mensagem;

    // radio buttons para selecao do tipo de documento
    @FXML private RadioButton radioCpf;
    @FXML private RadioButton radioCnpj;

    // lista completa de fornecedores carregada do banco
    // mantida em memoria para filtrar sem consultar o banco novamente
    private ObservableList<Fornecedor> listaCompleta = FXCollections.observableArrayList();

    /**
     * executado automaticamente ao carregar a tela
     * configura colunas, mascaras, listeners e carrega dados do banco
     */
    @FXML
    public void initialize() {
        // configura qual atributo do modelo aparece na coluna nome
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        // aplica mascara de cnpj por padrao
        Mascara.cnpj(campoCnpj);
        Mascara.telefone(campoTelefone);

        // ao trocar o tipo de documento, limpa o campo e aplica a nova mascara
        radioCpf.setOnAction(e -> {
            campoCnpj.setText("");
            Mascara.cpf(campoCnpj);
        });
        radioCnpj.setOnAction(e -> {
            campoCnpj.setText("");
            Mascara.cnpj(campoCnpj);
        });

        carregarFornecedores();

        // ao selecionar um fornecedor na tabela, preenche o formulario automaticamente
        tabelaFornecedores.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> {
                if (novo != null) preencherFormulario(novo);
            }
        );

        // filtra a lista conforme o usuario digita no campo de filtro
        campoFiltro.textProperty().addListener((obs, antigo, novo) -> filtrar(novo));
        
        tabelaFornecedores.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * busca todos os fornecedores do banco e exibe na tabela
     */
    private void carregarFornecedores() {
        listaCompleta.clear();
        try {
            Connection conn = Conexao.conectar();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM tb_fornecedores ORDER BY nome"
            );
            while (rs.next()) {
                Fornecedor f = new Fornecedor();
                f.setId(rs.getInt("id"));
                f.setNome(rs.getString("nome"));
                f.setCnpjCpf(rs.getString("cnpj_cpf"));
                f.setContato(rs.getString("contato"));
                f.setTelefone(rs.getString("telefone"));
                f.setEmail(rs.getString("email"));
                f.setCidade(rs.getString("cidade"));
                f.setUf(rs.getString("uf"));
                listaCompleta.add(f);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Erro ao carregar fornecedores: " + e.getMessage());
        }
        tabelaFornecedores.setItems(listaCompleta);
    }

    /**
     * filtra a lista de fornecedores pelo nome
     * a filtragem e feita na lista em memoria sem consultar o banco
     * @param texto texto digitado no campo de filtro
     */
    private void filtrar(String texto) {
        if (texto == null || texto.isEmpty()) {
            tabelaFornecedores.setItems(listaCompleta);
            return;
        }
        ObservableList<Fornecedor> filtrada = FXCollections.observableArrayList();
        for (Fornecedor f : listaCompleta) {
            if (f.getNome().toLowerCase().contains(texto.toLowerCase())) {
                filtrada.add(f);
            }
        }
        tabelaFornecedores.setItems(filtrada);
    }

    /**
     * preenche o formulario com os dados do fornecedor selecionado na tabela
     * @param f fornecedor selecionado
     */
    private void preencherFormulario(Fornecedor f) {
        campoNome.setText(f.getNome());
        campoCnpj.setText(f.getCnpjCpf() != null ? f.getCnpjCpf() : "");
        campoContato.setText(f.getContato() != null ? f.getContato() : "");
        campoTelefone.setText(f.getTelefone() != null ? f.getTelefone() : "");
        campoEmail.setText(f.getEmail() != null ? f.getEmail() : "");
        campoCidade.setText(f.getCidade() != null ? f.getCidade() : "");
        campoUf.setText(f.getUf() != null ? f.getUf() : "");
        mensagem.setText("");
    }

    /**
     * limpa o formulario para cadastrar um novo fornecedor
     * cidade e uf sao preenchidos com valores padrao
     */
    @FXML
    private void novo() {
        campoNome.setText("");
        campoCnpj.setText("");
        campoContato.setText("");
        campoTelefone.setText("");
        campoEmail.setText("");
        campoCidade.setText("Ribeirão Preto");
        campoUf.setText("SP");
        mensagem.setText("");
        tabelaFornecedores.getSelectionModel().clearSelection();
    }

    /**
     * salva o fornecedor no banco
     * se nenhum fornecedor estiver selecionado, insere um novo
     * se um fornecedor estiver selecionado, atualiza os dados dele
     */
    @FXML
    private void salvar() {
        if (campoNome.getText().isEmpty()) {
            mensagem.setText("Preencha o nome!");
            return;
        }
        try {
            Connection conn = Conexao.conectar();
            Fornecedor selecionado = tabelaFornecedores.getSelectionModel().getSelectedItem();

            if (selecionado == null) {
                // insere novo fornecedor
                String sql = "INSERT INTO tb_fornecedores (nome, cnpj_cpf, contato, telefone, email, cidade, uf) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, campoNome.getText());
                stmt.setString(2, campoCnpj.getText());
                stmt.setString(3, campoContato.getText());
                stmt.setString(4, campoTelefone.getText());
                stmt.setString(5, campoEmail.getText());
                stmt.setString(6, campoCidade.getText());
                stmt.setString(7, campoUf.getText());
                stmt.executeUpdate();
                mensagem.setStyle("-fx-text-fill: green;");
                mensagem.setText("Fornecedor cadastrado com sucesso!");
            } else {
                // atualiza fornecedor existente
                String sql = "UPDATE tb_fornecedores SET nome=?, cnpj_cpf=?, contato=?, telefone=?, email=?, cidade=?, uf=? WHERE id=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, campoNome.getText());
                stmt.setString(2, campoCnpj.getText());
                stmt.setString(3, campoContato.getText());
                stmt.setString(4, campoTelefone.getText());
                stmt.setString(5, campoEmail.getText());
                stmt.setString(6, campoCidade.getText());
                stmt.setString(7, campoUf.getText());
                stmt.setInt(8, selecionado.getId());
                stmt.executeUpdate();
                mensagem.setStyle("-fx-text-fill: green;");
                mensagem.setText("Fornecedor atualizado com sucesso!");
            }
            conn.close();
            carregarFornecedores();
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao salvar: " + e.getMessage());
        }
    }

    /**
     * exclui o fornecedor selecionado na tabela
     */
    @FXML
    private void excluir() {
        Fornecedor selecionado = tabelaFornecedores.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mensagem.setText("Selecione um fornecedor!");
            return;
        }
        try {
            Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_fornecedores WHERE id=?");
            stmt.setInt(1, selecionado.getId());
            stmt.executeUpdate();
            conn.close();
            carregarFornecedores();
            novo();
            mensagem.setStyle("-fx-text-fill: green;");
            mensagem.setText("Fornecedor excluido com sucesso!");
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao excluir: " + e.getMessage());
        }
    }

    /**
     * fecha a tela de fornecedores e volta para a tela principal
     */
    @FXML
    private void fechar() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/farmap/principal.fxml")
            );
            App.getStage().getScene().setRoot(loader.load());
            App.getStage().setWidth(600);
            App.getStage().setHeight(400);
            App.getStage().centerOnScreen();
        } catch (Exception e) {
            System.out.println("Erro ao fechar: " + e.getMessage());
        }
    }
}