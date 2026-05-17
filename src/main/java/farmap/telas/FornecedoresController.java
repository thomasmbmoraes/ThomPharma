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

public class FornecedoresController {

    @FXML private TableView<Fornecedor> tabelaFornecedores;
    @FXML private TableColumn<Fornecedor, String> colNome;
    @FXML private TextField campoFiltro;
    @FXML private TextField campoNome;
    @FXML private TextField campoCnpj;
    @FXML private TextField campoContato;
    @FXML private TextField campoTelefone;
    @FXML private TextField campoEmail;
    @FXML private TextField campoCidade;
    @FXML private TextField campoUf;
    @FXML private Label mensagem;
    @FXML private RadioButton radioCpf;
    @FXML private RadioButton radioCnpj;

    private ObservableList<Fornecedor> listaCompleta = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        // mascara padrao CNPJ
        Mascara.cnpj(campoCnpj);
        Mascara.telefone(campoTelefone);

        radioCpf.setOnAction(e -> {
            campoCnpj.setText("");
            Mascara.cpf(campoCnpj);
        });
        radioCnpj.setOnAction(e -> {
            campoCnpj.setText("");
            Mascara.cnpj(campoCnpj);
        });

        carregarFornecedores();

        tabelaFornecedores.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> {
                if (novo != null) preencherFormulario(novo);
            }
        );

        campoFiltro.textProperty().addListener((obs, antigo, novo) -> filtrar(novo));
    }

    private void carregarFornecedores() {
        listaCompleta.clear();
        try {
            Connection conn = Conexao.conectar();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM fornecedores ORDER BY nome"
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
                String sql = "INSERT INTO fornecedores (nome, cnpj_cpf, contato, telefone, email, cidade, uf) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
                String sql = "UPDATE fornecedores SET nome=?, cnpj_cpf=?, contato=?, telefone=?, email=?, cidade=?, uf=? WHERE id=?";
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

    @FXML
    private void excluir() {
        Fornecedor selecionado = tabelaFornecedores.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mensagem.setText("Selecione um fornecedor!");
            return;
        }
        try {
            Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM fornecedores WHERE id=?");
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