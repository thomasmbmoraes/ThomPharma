package thompharma.telas;

import thompharma.Conexao;
import thompharma.Mascara;
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
 * ao salvar, recalcula automaticamente o saldo da materia prima no banco
 */
public class LoteDialogController {

    @FXML private TextField campoNomeLote;
    @FXML private TextField campoValidade;
    @FXML private TextField campoQuantidade;
    @FXML private TextField campoCusto;
    @FXML private TextField campoFator;
    @FXML private TextField campoFator2;
    @FXML private TextField campoDensidade;
    @FXML private TextField campoEnderecoUso;
    @FXML private TextField campoEnderecoEstoque;
    @FXML private ComboBox<String> comboFornecedor;
    @FXML private Label mensagem;

    // id da materia prima a qual o lote pertence
    private int idMateriaPrima;

    // lista de ids dos fornecedores para salvar no banco
    private ObservableList<Integer> idsFornecedores = FXCollections.observableArrayList();

    // referencia ao controller pai para atualizar as tabelas apos salvar
    private MateriasPrimasController controllerPai;

    /**
     * executado automaticamente ao carregar o dialogo
     */
    @FXML
    public void initialize() {
        Mascara.data(campoValidade);
        // valores padrao: fator 1 e fator 2 iniciam em 1 (neutro)
        campoFator.setText("1");
        campoFator2.setText("1");
        campoDensidade.setText("1.0");
        carregarFornecedores();
    }

    /**
     * define o id da materia prima e o controller pai
     * chamado pelo MateriasPrimasController ao abrir o dialogo
     * @param idMateriaPrima id da materia prima selecionada
     * @param controllerPai referencia ao controller que abriu este dialogo
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
     * salva o lote no banco e atualiza o saldo da materia prima automaticamente
     * o saldo da MP e recalculado como a soma dos saldos de todos os seus lotes
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

            // insere o lote no banco
            String sql = "INSERT INTO tb_lotes (id_materia_prima, nome_lote, custo, fator, fator2, quantidade, saldo, densidade, validade, endereco_uso, endereco_estoque, id_fornecedor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idMateriaPrima);
            stmt.setString(2, campoNomeLote.getText());
            stmt.setDouble(3, Double.parseDouble(campoCusto.getText().isEmpty() ? "0" : campoCusto.getText()));
            stmt.setDouble(4, Double.parseDouble(campoFator.getText().isEmpty() ? "1" : campoFator.getText()));
            stmt.setDouble(5, Double.parseDouble(campoFator2.getText().isEmpty() ? "1" : campoFator2.getText()));
            stmt.setDouble(6, quantidade);
            stmt.setDouble(7, quantidade);
            stmt.setDouble(8, Double.parseDouble(campoDensidade.getText().isEmpty() ? "1" : campoDensidade.getText()));
            if (validadeFormatada.isEmpty()) {
                stmt.setNull(9, java.sql.Types.DATE);
            } else {
                stmt.setDate(9, java.sql.Date.valueOf(validadeFormatada));
            }
            stmt.setString(10, campoEnderecoUso.getText());
            stmt.setString(11, campoEnderecoEstoque.getText());
            stmt.setInt(12, idFornecedor);
            stmt.executeUpdate();

            // recalcula o saldo da materia prima como soma dos saldos de todos os seus lotes
            atualizarSaldoMateriaPrima(conn, idMateriaPrima);

            conn.close();

            // avisa o controller pai para recarregar os lotes e a lista de MPs
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
     * atualiza o saldo da materia prima como a soma dos saldos de todos os seus lotes
     * @param conn conexao aberta com o banco
     * @param idMateriaPrima id da materia prima a atualizar
     */
    private void atualizarSaldoMateriaPrima(Connection conn, int idMateriaPrima) throws Exception {
        PreparedStatement upd = conn.prepareStatement(
            "UPDATE tb_materias_primas SET saldo = " +
            "(SELECT COALESCE(SUM(saldo), 0) FROM tb_lotes WHERE id_materia_prima = ?) " +
            "WHERE id = ?"
        );
        upd.setInt(1, idMateriaPrima);
        upd.setInt(2, idMateriaPrima);
        upd.executeUpdate();
    }

    /**
     * fecha o dialogo sem salvar
     */
    @FXML
    private void cancelar() {
        ((Stage) campoNomeLote.getScene().getWindow()).close();
    }
}
