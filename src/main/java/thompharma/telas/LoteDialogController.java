package thompharma.telas;

import thompharma.Conexao;
import thompharma.Mascara;
import thompharma.modelo.Fornecedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * controller da janela de dialogo para cadastro de lotes
 * aberto a partir da tela de materias primas
 */
public class LoteDialogController {

    @FXML private TextField campoNomeLote;
    @FXML private TextField campoValidade;
    @FXML private TextField campoQuantidade;
    @FXML private TextField campoCusto;
    @FXML private TextField campoFator;
    @FXML private TextField campoDensidade;
    @FXML private TextField campoEnderecoUso;
    @FXML private TextField campoEnderecoEstoque;
    @FXML private ComboBox<String> comboFornecedor;
    @FXML private Label mensagem;

    // id da materia prima a qual o lote pertence
    private int idMateriaPrima;

    // lista de ids dos fornecedores para salvar no banco
    private ObservableList<Integer> idsFornecedores = FXCollections.observableArrayList();

    // referencia ao controller pai para atualizar a tabela de lotes
    private MateriasPrimasController controllerPai;

    /**
     * executado automaticamente ao carregar a tela
     */
    @FXML
    public void initialize() {
        Mascara.data(campoValidade);
        campoFator.setText("1");
        campoDensidade.setText("1.0");
        carregarFornecedores();
    }

    /**
     * define o id da materia prima e o controller pai
     * chamado pelo MateriasPrimasController ao abrir o dialogo
     */
    public void setDados(int idMateriaPrima, MateriasPrimasController controllerPai) {
        this.idMateriaPrima = idMateriaPrima;
        this.controllerPai = controllerPai;
    }

    /**
     * carrega os fornecedores do banco para o combobox
     */
    private void carregarFornecedores() {
        try {
            Connection conn = Conexao.conectar();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT id, nome FROM tb_fornecedores ORDER BY nome"
            );
            while (rs.next()) {
                idsFornecedores.add(rs.getInt("id"));
                comboFornecedor.getItems().add(rs.getString("nome"));
            }
            conn.close();
            if (!comboFornecedor.getItems().isEmpty()) {
                comboFornecedor.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar fornecedores: " + e.getMessage());
        }
    }

    /**
     * salva o lote no banco e fecha o dialogo
     */
    @FXML
    private void salvar() {
        if (campoNomeLote.getText().isEmpty()) {
            mensagem.setText("Preencha o nome do lote!");
            return;
        }
        try {
            Connection conn = Conexao.conectar();

            // pega o id do fornecedor selecionado
            int idFornecedor = 0;
            if (!idsFornecedores.isEmpty() && comboFornecedor.getSelectionModel().getSelectedIndex() >= 0) {
                idFornecedor = idsFornecedores.get(comboFornecedor.getSelectionModel().getSelectedIndex());
            }

            double quantidade = Double.parseDouble(campoQuantidade.getText().isEmpty() ? "0" : campoQuantidade.getText());

            // converte a data do formato dd/mm/aaaa para aaaa-mm-dd que o postgresql aceita
            String validadeFormatada = "";
            if (!campoValidade.getText().isEmpty() && campoValidade.getText().length() == 10) {
                String[] partes = campoValidade.getText().split("/");
                validadeFormatada = partes[2] + "-" + partes[1] + "-" + partes[0];
            }

            String sql = "INSERT INTO tb_lotes (id_materia_prima, nome_lote, custo, fator, quantidade, saldo, densidade, validade, endereco_uso, endereco_estoque, id_fornecedor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idMateriaPrima);
            stmt.setString(2, campoNomeLote.getText());
            stmt.setDouble(3, Double.parseDouble(campoCusto.getText().isEmpty() ? "0" : campoCusto.getText()));
            stmt.setInt(4, Integer.parseInt(campoFator.getText().isEmpty() ? "1" : campoFator.getText()));
            stmt.setDouble(5, quantidade);
            stmt.setDouble(6, quantidade);
            stmt.setDouble(7, Double.parseDouble(campoDensidade.getText().isEmpty() ? "1" : campoDensidade.getText()));
            if (validadeFormatada.isEmpty()) {
                stmt.setNull(8, java.sql.Types.DATE);
            } else {
                stmt.setDate(8, java.sql.Date.valueOf(validadeFormatada));
            }
            stmt.setString(9, campoEnderecoUso.getText());
            stmt.setString(10, campoEnderecoEstoque.getText());
            stmt.setInt(11, idFornecedor);
            stmt.executeUpdate();
            conn.close();

            // atualiza a tabela de lotes no controller pai
            if (controllerPai != null) {
                controllerPai.atualizarLotes(idMateriaPrima);
            }

            // fecha o dialogo
            ((Stage) campoNomeLote.getScene().getWindow()).close();

        } catch (Exception e) {
            mensagem.setText("Erro ao salvar: " + e.getMessage());
        }
    }

    /**
     * fecha o dialogo sem salvar
     */
    @FXML
    private void cancelar() {
        ((Stage) campoNomeLote.getScene().getWindow()).close();
    }
}