package farmap.telas;

import farmap.App;
import farmap.Conexao;
import farmap.modelo.Lote;
import farmap.modelo.MateriaPrima;
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

/**
 * controller do cadastro de materias primas
 * gerencia materias primas e seus lotes
 * ao selecionar uma materia prima, exibe seus lotes na tabela inferior
 */
public class MateriasPrimasController {

    // componentes da tabela de materias primas
    @FXML private TableView<MateriaPrima> tabelaMateriasPrimas;
    @FXML private TableColumn<MateriaPrima, String> colNome;
    @FXML private TableColumn<MateriaPrima, String> colUnidade;
    @FXML private TableColumn<MateriaPrima, String> colTipo;
    @FXML private TableColumn<MateriaPrima, Double> colSaldo;

    // componentes da tabela de lotes
    @FXML private TableView<Lote> tabelaLotes;
    @FXML private TableColumn<Lote, String> colLoteNome;
    @FXML private TableColumn<Lote, Double> colLoteCusto;
    @FXML private TableColumn<Lote, Double> colLoteQuantidade;
    @FXML private TableColumn<Lote, Double> colLoteSaldo;
    @FXML private TableColumn<Lote, String> colLoteValidade;
    @FXML private TableColumn<Lote, String> colLoteFornecedor;

    // campos do formulario de materia prima
    @FXML private TextField campoFiltro;
    @FXML private TextField campoNome;
    @FXML private ComboBox<String> comboUnidade;
    @FXML private ComboBox<String> comboTipo;
    @FXML private TextField campoDoseMinima;
    @FXML private TextField campoDoseMaxima;
    @FXML private TextField campoVolume;
    @FXML private TextField campoEstoqueMinimo;
    @FXML private TextField campoEstoqueCritico;
    @FXML private CheckBox checkRotulo;
    @FXML private CheckBox checkGeladeira;
    @FXML private CheckBox checkControlado;
    @FXML private TextArea campoObservacoes;
    @FXML private Label mensagem;

    // lista completa de materias primas
    private ObservableList<MateriaPrima> listaCompleta = FXCollections.observableArrayList();

    /**
     * executado automaticamente ao carregar a tela
     */
    @FXML
    public void initialize() {
        // configura colunas da tabela de materias primas
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colUnidade.setCellValueFactory(new PropertyValueFactory<>("unidade"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colSaldo.setCellValueFactory(new PropertyValueFactory<>("saldo"));
        tabelaMateriasPrimas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // configura colunas da tabela de lotes
        colLoteNome.setCellValueFactory(new PropertyValueFactory<>("nomeLote"));
        colLoteCusto.setCellValueFactory(new PropertyValueFactory<>("custo"));
        colLoteQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colLoteSaldo.setCellValueFactory(new PropertyValueFactory<>("saldo"));
        colLoteValidade.setCellValueFactory(new PropertyValueFactory<>("validade"));
        colLoteFornecedor.setCellValueFactory(new PropertyValueFactory<>("nomeFornecedor"));
        tabelaLotes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // preenche comboboxes
        comboUnidade.getItems().addAll("mg", "g", "kg", "ml", "l", "un");
        comboUnidade.getSelectionModel().selectFirst();

        comboTipo.getItems().addAll("Sólido", "Líquido", "Excipiente", "Outro");
        comboTipo.getSelectionModel().selectFirst();

        carregarMateriasPrimas();

        // ao selecionar uma materia prima, carrega seus lotes
        tabelaMateriasPrimas.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> {
                if (novo != null) {
                    preencherFormulario(novo);
                    carregarLotes(novo.getId());
                }
            }
        );

        // filtra conforme o usuario digita
        campoFiltro.textProperty().addListener((obs, antigo, novo) -> filtrar(novo));
    }

    /**
     * busca todas as materias primas do banco
     */
    private void carregarMateriasPrimas() {
        listaCompleta.clear();
        try {
            Connection conn = Conexao.conectar();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM tb_materias_primas ORDER BY nome"
            );
            while (rs.next()) {
                MateriaPrima mp = new MateriaPrima();
                mp.setId(rs.getInt("id"));
                mp.setNome(rs.getString("nome"));
                mp.setUnidade(rs.getString("unidade"));
                mp.setTipo(rs.getString("tipo"));
                mp.setDoseMinima(rs.getDouble("dose_minima"));
                mp.setDoseMaxima(rs.getDouble("dose_maxima"));
                mp.setVolume(rs.getDouble("volume"));
                mp.setSaldo(rs.getDouble("saldo"));
                mp.setRotulo(rs.getBoolean("rotulo"));
                mp.setGeladeira(rs.getBoolean("geladeira"));
                mp.setControlado(rs.getBoolean("controlado"));
                mp.setObservacoes(rs.getString("observacoes"));
                mp.setEstoqueMinimo(rs.getDouble("estoque_minimo"));
                mp.setEstoqueCritico(rs.getDouble("estoque_critico"));
                listaCompleta.add(mp);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Erro ao carregar materias primas: " + e.getMessage());
        }
        tabelaMateriasPrimas.setItems(listaCompleta);
    }

    /**
     * carrega os lotes da materia prima selecionada
     * @param idMateriaPrima id da materia prima selecionada
     */
    private void carregarLotes(int idMateriaPrima) {
        ObservableList<Lote> listaLotes = FXCollections.observableArrayList();
        try {
            Connection conn = Conexao.conectar();
            String sql = "SELECT l.*, f.nome as nome_fornecedor FROM tb_lotes l " +
                        "LEFT JOIN tb_fornecedores f ON l.id_fornecedor = f.id " +
                        "WHERE l.id_materia_prima = ? ORDER BY l.data_cadastro DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idMateriaPrima);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Lote l = new Lote();
                l.setId(rs.getInt("id"));
                l.setIdMateriaPrima(rs.getInt("id_materia_prima"));
                l.setNomeLote(rs.getString("nome_lote"));
                l.setCusto(rs.getDouble("custo"));
                l.setFator(rs.getInt("fator"));
                l.setQuantidade(rs.getDouble("quantidade"));
                l.setSaldo(rs.getDouble("saldo"));
                l.setDensidade(rs.getDouble("densidade"));
                l.setValidade(rs.getString("validade"));
                l.setEnderecoUso(rs.getString("endereco_uso"));
                l.setEnderecoEstoque(rs.getString("endereco_estoque"));
                l.setIdFornecedor(rs.getInt("id_fornecedor"));
                l.setNomeFornecedor(rs.getString("nome_fornecedor"));
                listaLotes.add(l);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Erro ao carregar lotes: " + e.getMessage());
        }
        tabelaLotes.setItems(listaLotes);
    }

    /**
     * filtra a lista de materias primas pelo nome
     */
    private void filtrar(String texto) {
        if (texto == null || texto.isEmpty()) {
            tabelaMateriasPrimas.setItems(listaCompleta);
            return;
        }
        ObservableList<MateriaPrima> filtrada = FXCollections.observableArrayList();
        for (MateriaPrima mp : listaCompleta) {
            if (mp.getNome().toLowerCase().contains(texto.toLowerCase())) {
                filtrada.add(mp);
            }
        }
        tabelaMateriasPrimas.setItems(filtrada);
    }

    /**
     * preenche o formulario com os dados da materia prima selecionada
     */
    private void preencherFormulario(MateriaPrima mp) {
        campoNome.setText(mp.getNome());
        comboUnidade.getSelectionModel().select(mp.getUnidade());
        comboTipo.getSelectionModel().select(mp.getTipo());
        campoDoseMinima.setText(String.valueOf(mp.getDoseMinima()));
        campoDoseMaxima.setText(String.valueOf(mp.getDoseMaxima()));
        campoVolume.setText(String.valueOf(mp.getVolume()));
        campoEstoqueMinimo.setText(String.valueOf(mp.getEstoqueMinimo()));
        campoEstoqueCritico.setText(String.valueOf(mp.getEstoqueCritico()));
        checkRotulo.setSelected(mp.isRotulo());
        checkGeladeira.setSelected(mp.isGeladeira());
        checkControlado.setSelected(mp.isControlado());
        campoObservacoes.setText(mp.getObservacoes() != null ? mp.getObservacoes() : "");
        mensagem.setText("");
    }

    /**
     * limpa o formulario para cadastrar uma nova materia prima
     */
    @FXML
    private void novo() {
        campoNome.setText("");
        comboUnidade.getSelectionModel().selectFirst();
        comboTipo.getSelectionModel().selectFirst();
        campoDoseMinima.setText("0");
        campoDoseMaxima.setText("0");
        campoVolume.setText("0");
        campoEstoqueMinimo.setText("0");
        campoEstoqueCritico.setText("0");
        checkRotulo.setSelected(false);
        checkGeladeira.setSelected(false);
        checkControlado.setSelected(false);
        campoObservacoes.setText("");
        mensagem.setText("");
        tabelaMateriasPrimas.getSelectionModel().clearSelection();
        tabelaLotes.getItems().clear();
    }

    /**
     * salva a materia prima no banco
     */
    @FXML
    private void salvar() {
        if (campoNome.getText().isEmpty()) {
            mensagem.setText("Preencha o nome!");
            return;
        }
        try {
            Connection conn = Conexao.conectar();
            MateriaPrima selecionada = tabelaMateriasPrimas.getSelectionModel().getSelectedItem();

            if (selecionada == null) {
                String sql = "INSERT INTO tb_materias_primas (nome, unidade, tipo, dose_minima, dose_maxima, volume, rotulo, geladeira, controlado, observacoes, estoque_minimo, estoque_critico) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, campoNome.getText());
                stmt.setString(2, comboUnidade.getValue());
                stmt.setString(3, comboTipo.getValue());
                stmt.setDouble(4, Double.parseDouble(campoDoseMinima.getText().isEmpty() ? "0" : campoDoseMinima.getText()));
                stmt.setDouble(5, Double.parseDouble(campoDoseMaxima.getText().isEmpty() ? "0" : campoDoseMaxima.getText()));
                stmt.setDouble(6, Double.parseDouble(campoVolume.getText().isEmpty() ? "0" : campoVolume.getText()));
                stmt.setBoolean(7, checkRotulo.isSelected());
                stmt.setBoolean(8, checkGeladeira.isSelected());
                stmt.setBoolean(9, checkControlado.isSelected());
                stmt.setString(10, campoObservacoes.getText());
                stmt.setDouble(11, Double.parseDouble(campoEstoqueMinimo.getText().isEmpty() ? "0" : campoEstoqueMinimo.getText()));
                stmt.setDouble(12, Double.parseDouble(campoEstoqueCritico.getText().isEmpty() ? "0" : campoEstoqueCritico.getText()));
                stmt.executeUpdate();
                mensagem.setStyle("-fx-text-fill: green;");
                mensagem.setText("Matéria-prima cadastrada com sucesso!");
            } else {
                String sql = "UPDATE tb_materias_primas SET nome=?, unidade=?, tipo=?, dose_minima=?, dose_maxima=?, volume=?, rotulo=?, geladeira=?, controlado=?, observacoes=?, estoque_minimo=?, estoque_critico=? WHERE id=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, campoNome.getText());
                stmt.setString(2, comboUnidade.getValue());
                stmt.setString(3, comboTipo.getValue());
                stmt.setDouble(4, Double.parseDouble(campoDoseMinima.getText().isEmpty() ? "0" : campoDoseMinima.getText()));
                stmt.setDouble(5, Double.parseDouble(campoDoseMaxima.getText().isEmpty() ? "0" : campoDoseMaxima.getText()));
                stmt.setDouble(6, Double.parseDouble(campoVolume.getText().isEmpty() ? "0" : campoVolume.getText()));
                stmt.setBoolean(7, checkRotulo.isSelected());
                stmt.setBoolean(8, checkGeladeira.isSelected());
                stmt.setBoolean(9, checkControlado.isSelected());
                stmt.setString(10, campoObservacoes.getText());
                stmt.setDouble(11, Double.parseDouble(campoEstoqueMinimo.getText().isEmpty() ? "0" : campoEstoqueMinimo.getText()));
                stmt.setDouble(12, Double.parseDouble(campoEstoqueCritico.getText().isEmpty() ? "0" : campoEstoqueCritico.getText()));
                stmt.setInt(13, selecionada.getId());
                stmt.executeUpdate();
                mensagem.setStyle("-fx-text-fill: green;");
                mensagem.setText("Matéria-prima atualizada com sucesso!");
            }
            conn.close();
            carregarMateriasPrimas();
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao salvar: " + e.getMessage());
        }
    }

    /**
     * exclui a materia prima selecionada
     */
    @FXML
    private void excluir() {
        MateriaPrima selecionada = tabelaMateriasPrimas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mensagem.setText("Selecione uma matéria-prima!");
            return;
        }
        try {
            Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_materias_primas WHERE id=?");
            stmt.setInt(1, selecionada.getId());
            stmt.executeUpdate();
            conn.close();
            carregarMateriasPrimas();
            novo();
            mensagem.setStyle("-fx-text-fill: green;");
            mensagem.setText("Matéria-prima excluída com sucesso!");
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao excluir: " + e.getMessage());
        }
    }

    /**
     * limpa a selecao de lote para adicionar um novo
     */
    @FXML
    private void novoLote() {
        tabelaLotes.getSelectionModel().clearSelection();
        mensagem.setStyle("-fx-text-fill: blue;");
        mensagem.setText("Selecione uma matéria-prima e clique em Salvar Lote para adicionar.");
    }

    /**
    * abre a janela de dialogo para cadastrar um novo lote
    */
    @FXML
    private void salvarLote() {
        MateriaPrima selecionada = tabelaMateriasPrimas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mensagem.setText("Selecione uma matéria-prima primeiro!");
            return;
        }
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/farmap/lote_dialog.fxml")
            );
            javafx.scene.Parent root = loader.load();
            LoteDialogController controller = loader.getController();
            controller.setDados(selecionada.getId(), this);
 
            javafx.stage.Stage dialog = new javafx.stage.Stage();
            dialog.setTitle("Novo Lote - " + selecionada.getNome());
            dialog.setScene(new javafx.scene.Scene(root));
            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialog.showAndWait();
        } catch (Exception e) {
            mensagem.setText("Erro ao abrir dialogo: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    /**
    * atualiza a tabela de lotes apos salvar um novo lote
    * chamado pelo LoteDialogController apos salvar
    * @param idMateriaPrima id da materia prima cujos lotes devem ser atualizados
    */
    public void atualizarLotes(int idMateriaPrima) {
        carregarLotes(idMateriaPrima);
    }

    /**
     * exclui o lote selecionado
     */
    @FXML
    private void excluirLote() {
        Lote selecionado = tabelaLotes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mensagem.setText("Selecione um lote!");
            return;
        }
        try {
            Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_lotes WHERE id=?");
            stmt.setInt(1, selecionado.getId());
            stmt.executeUpdate();
            conn.close();
            MateriaPrima mp = tabelaMateriasPrimas.getSelectionModel().getSelectedItem();
            if (mp != null) carregarLotes(mp.getId());
            mensagem.setStyle("-fx-text-fill: green;");
            mensagem.setText("Lote excluído com sucesso!");
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao excluir lote: " + e.getMessage());
        }
    }

    /**
     * fecha a tela e volta para a tela principal
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