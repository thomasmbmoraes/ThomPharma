package thompharma.telas;

import thompharma.App;
import thompharma.Conexao;
import thompharma.modelo.Receita;
import thompharma.modelo.ReceitaIngrediente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * controller do modulo de receitas (formulas)
 * permite cadastrar formulas com nome fantasia e lista de ingredientes
 * a busca funciona por nome da receita, nome do ingrediente ou tipo
 * a mesma materia prima pode aparecer mais de uma vez na mesma receita
 */
public class ReceitasController {

    // componentes da tabela de receitas
    @FXML private TableView<Receita> tabelaReceitas;
    @FXML private TableColumn<Receita, String> colNome;
    @FXML private TableColumn<Receita, String> colTipo;
    @FXML private TableColumn<Receita, String> colNomeFantasia;

    // campos de filtro
    @FXML private TextField campoFiltro;
    @FXML private ComboBox<String> comboFiltroTipo;

    // campos do formulario de receita
    @FXML private TextField campoNome;
    @FXML private TextField campoNomeFantasia;
    @FXML private ComboBox<String> comboTipo;
    @FXML private TextArea campoObservacoes;
    @FXML private Label mensagem;

    // componentes da tabela de ingredientes
    @FXML private TableView<ReceitaIngrediente> tabelaIngredientes;
    @FXML private TableColumn<ReceitaIngrediente, String> colIngMP;
    @FXML private TableColumn<ReceitaIngrediente, Double> colIngQtd;
    @FXML private TableColumn<ReceitaIngrediente, String> colIngUnidade;
    @FXML private TableColumn<ReceitaIngrediente, String> colIngObs;

    // campos do formulario inline para adicionar ingredientes
    @FXML private ComboBox<String> comboIngMP;
    @FXML private TextField campoIngQtd;
    @FXML private ComboBox<String> comboIngUnidade;
    @FXML private TextField campoIngObs;

    // lista de ids das MPs do combobox de ingredientes
    private ObservableList<Integer> idsMPs = FXCollections.observableArrayList();

    // lista completa de receitas em memoria
    private ObservableList<Receita> listaCompleta = FXCollections.observableArrayList();

    // tipos de formula disponiveis
    private static final String[] TIPOS = {
        "Cápsula", "Creme", "Solução", "Xarope", "Pomada", "Gel",
        "Suspensão", "Floral", "Homeopatia", "Outro"
    };

    /**
     * executado automaticamente ao carregar a tela
     */
    @FXML
    public void initialize() {
        // configura colunas da tabela de receitas
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colNomeFantasia.setCellValueFactory(new PropertyValueFactory<>("nomeFantasia"));
        tabelaReceitas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // configura colunas da tabela de ingredientes
        colIngMP.setCellValueFactory(new PropertyValueFactory<>("nomeMateriaPrima"));
        colIngQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colIngUnidade.setCellValueFactory(new PropertyValueFactory<>("unidade"));
        colIngObs.setCellValueFactory(new PropertyValueFactory<>("observacao"));
        tabelaIngredientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // preenche comboboxes de tipo
        comboTipo.getItems().addAll(TIPOS);
        comboTipo.getSelectionModel().selectFirst();

        comboFiltroTipo.getItems().add("Todos");
        comboFiltroTipo.getItems().addAll(TIPOS);
        comboFiltroTipo.getSelectionModel().selectFirst();

        // preenche combobox de unidade para ingredientes
        comboIngUnidade.getItems().addAll("mg", "g", "kg", "ml", "l", "un", "%");
        comboIngUnidade.getSelectionModel().selectFirst();

        carregarReceitas();
        carregarMPs();

        // ao selecionar uma receita, carrega seus ingredientes
        tabelaReceitas.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> {
                if (novo != null) {
                    preencherFormulario(novo);
                    carregarIngredientes(novo.getId());
                }
            }
        );

        // filtra conforme o usuario digita ou muda o tipo
        campoFiltro.textProperty().addListener((obs, antigo, novo) -> filtrar());
        comboFiltroTipo.setOnAction(e -> filtrar());

        // ao selecionar uma MP no combobox de ingredientes,
        // preenche a unidade automaticamente com a unidade da MP
        comboIngMP.setOnAction(e -> preencherUnidadeMP());
    }

    /**
     * busca as receitas do banco aplicando o filtro atual
     * a busca e feita por nome da receita, nome do ingrediente ou tipo
     */
    private void carregarReceitas() {
        listaCompleta.clear();
        try {
            Connection conn = Conexao.conectar();
            // busca receitas distintas filtrando por nome, ingrediente ou tipo
            String sql =
                "SELECT DISTINCT r.* FROM tb_receitas r " +
                "LEFT JOIN tb_receita_ingredientes ri ON ri.id_receita = r.id " +
                "LEFT JOIN tb_materias_primas mp ON mp.id = ri.id_materia_prima " +
                "ORDER BY r.nome";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                Receita r = new Receita();
                r.setId(rs.getInt("id"));
                r.setNome(rs.getString("nome"));
                r.setNomeFantasia(rs.getString("nome_fantasia"));
                r.setTipo(rs.getString("tipo"));
                r.setObservacoes(rs.getString("observacoes"));
                listaCompleta.add(r);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Erro ao carregar receitas: " + e.getMessage());
        }
        tabelaReceitas.setItems(listaCompleta);
    }

    /**
     * carrega as materias primas do banco para o combobox de ingredientes
     */
    private void carregarMPs() {
        idsMPs.clear();
        comboIngMP.getItems().clear();
        try {
            Connection conn = Conexao.conectar();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT id, nome FROM tb_materias_primas ORDER BY nome"
            );
            while (rs.next()) {
                idsMPs.add(rs.getInt("id"));
                comboIngMP.getItems().add(rs.getString("nome"));
            }
            conn.close();
            if (!comboIngMP.getItems().isEmpty()) {
                comboIngMP.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar materias primas: " + e.getMessage());
        }
    }

    /**
     * carrega os ingredientes da receita selecionada
     * @param idReceita id da receita selecionada
     */
    private void carregarIngredientes(int idReceita) {
        ObservableList<ReceitaIngrediente> lista = FXCollections.observableArrayList();
        try {
            Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT ri.*, mp.nome as nome_mp FROM tb_receita_ingredientes ri " +
                "JOIN tb_materias_primas mp ON mp.id = ri.id_materia_prima " +
                "WHERE ri.id_receita = ? ORDER BY ri.id"
            );
            stmt.setInt(1, idReceita);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ReceitaIngrediente ing = new ReceitaIngrediente();
                ing.setId(rs.getInt("id"));
                ing.setIdReceita(rs.getInt("id_receita"));
                ing.setIdMateriaPrima(rs.getInt("id_materia_prima"));
                ing.setNomeMateriaPrima(rs.getString("nome_mp"));
                ing.setQuantidade(rs.getDouble("quantidade"));
                ing.setUnidade(rs.getString("unidade"));
                ing.setObservacao(rs.getString("observacao"));
                lista.add(ing);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Erro ao carregar ingredientes: " + e.getMessage());
        }
        tabelaIngredientes.setItems(lista);
    }

    /**
     * filtra a lista de receitas pelo texto digitado e pelo tipo selecionado
     * a busca cobre nome da receita, nome do ingrediente e tipo
     */
    private void filtrar() {
        String texto = campoFiltro.getText();
        String tipo = comboFiltroTipo.getValue();
        boolean semFiltro = (texto == null || texto.isEmpty()) && "Todos".equals(tipo);

        if (semFiltro) {
            tabelaReceitas.setItems(listaCompleta);
            return;
        }

        // para busca por ingrediente, consulta o banco com JOIN
        try {
            Connection conn = Conexao.conectar();
            String like = "%" + (texto != null ? texto.toLowerCase() : "") + "%";
            String sql =
                "SELECT DISTINCT r.* FROM tb_receitas r " +
                "LEFT JOIN tb_receita_ingredientes ri ON ri.id_receita = r.id " +
                "LEFT JOIN tb_materias_primas mp ON mp.id = ri.id_materia_prima " +
                "WHERE (LOWER(r.nome) LIKE ? OR LOWER(mp.nome) LIKE ?) " +
                (!"Todos".equals(tipo) ? "AND r.tipo = ? " : "") +
                "ORDER BY r.nome";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, like);
            stmt.setString(2, like);
            if (!"Todos".equals(tipo)) stmt.setString(3, tipo);

            ObservableList<Receita> filtrada = FXCollections.observableArrayList();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Receita r = new Receita();
                r.setId(rs.getInt("id"));
                r.setNome(rs.getString("nome"));
                r.setNomeFantasia(rs.getString("nome_fantasia"));
                r.setTipo(rs.getString("tipo"));
                r.setObservacoes(rs.getString("observacoes"));
                filtrada.add(r);
            }
            conn.close();
            tabelaReceitas.setItems(filtrada);
        } catch (Exception e) {
            System.out.println("Erro ao filtrar receitas: " + e.getMessage());
        }
    }

    /**
     * preenche o formulario com os dados da receita selecionada
     * @param r receita selecionada
     */
    private void preencherFormulario(Receita r) {
        campoNome.setText(r.getNome());
        campoNomeFantasia.setText(r.getNomeFantasia() != null ? r.getNomeFantasia() : "");
        comboTipo.getSelectionModel().select(r.getTipo());
        campoObservacoes.setText(r.getObservacoes() != null ? r.getObservacoes() : "");
        mensagem.setText("");
    }

    /**
     * ao selecionar uma MP no combobox, preenche a unidade automaticamente
     * com a unidade padrao da materia prima
     */
    private void preencherUnidadeMP() {
        int idx = comboIngMP.getSelectionModel().getSelectedIndex();
        if (idx < 0 || idsMPs.isEmpty()) return;
        int idMP = idsMPs.get(idx);
        try {
            Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT unidade FROM tb_materias_primas WHERE id = ?"
            );
            stmt.setInt(1, idMP);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                comboIngUnidade.getSelectionModel().select(rs.getString("unidade"));
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Erro ao buscar unidade da MP: " + e.getMessage());
        }
    }

    /**
     * limpa o formulario para cadastrar uma nova receita
     */
    @FXML
    private void novo() {
        campoNome.setText("");
        campoNomeFantasia.setText("");
        comboTipo.getSelectionModel().selectFirst();
        campoObservacoes.setText("");
        tabelaIngredientes.getItems().clear();
        mensagem.setText("");
        tabelaReceitas.getSelectionModel().clearSelection();
    }

    /**
     * salva a receita no banco
     * insere nova se nenhuma estiver selecionada, atualiza se estiver
     */
    @FXML
    private void salvar() {
        if (campoNome.getText().isEmpty()) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Preencha o nome da receita!");
            return;
        }
        try {
            Connection conn = Conexao.conectar();
            Receita selecionada = tabelaReceitas.getSelectionModel().getSelectedItem();

            if (selecionada == null) {
                // insere nova receita
                String sql = "INSERT INTO tb_receitas (nome, nome_fantasia, tipo, observacoes) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, campoNome.getText());
                stmt.setString(2, campoNomeFantasia.getText());
                stmt.setString(3, comboTipo.getValue());
                stmt.setString(4, campoObservacoes.getText());
                stmt.executeUpdate();
                mensagem.setStyle("-fx-text-fill: green;");
                mensagem.setText("Receita cadastrada com sucesso!");
            } else {
                // atualiza receita existente
                String sql = "UPDATE tb_receitas SET nome=?, nome_fantasia=?, tipo=?, observacoes=? WHERE id=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, campoNome.getText());
                stmt.setString(2, campoNomeFantasia.getText());
                stmt.setString(3, comboTipo.getValue());
                stmt.setString(4, campoObservacoes.getText());
                stmt.setInt(5, selecionada.getId());
                stmt.executeUpdate();
                mensagem.setStyle("-fx-text-fill: green;");
                mensagem.setText("Receita atualizada com sucesso!");
            }
            conn.close();
            carregarReceitas();
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao salvar: " + e.getMessage());
        }
    }

    /**
     * exclui a receita selecionada e todos os seus ingredientes
     * exibe dialogo de confirmacao antes de executar a exclusao
     */
    @FXML
    private void excluir() {
        Receita selecionada = tabelaReceitas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mensagem.setText("Selecione uma receita!");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja excluir a receita \"" + selecionada.getNome() + "\"?\nTodos os ingredientes também serão removidos.");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_receitas WHERE id=?");
            stmt.setInt(1, selecionada.getId());
            stmt.executeUpdate();
            conn.close();
            carregarReceitas();
            novo();
            mensagem.setStyle("-fx-text-fill: green;");
            mensagem.setText("Receita excluída com sucesso!");
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao excluir: " + e.getMessage());
        }
    }

    /**
     * adiciona um ingrediente a receita selecionada
     */
    @FXML
    private void adicionarIngrediente() {
        Receita selecionada = tabelaReceitas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Salve a receita antes de adicionar ingredientes!");
            return;
        }
        int idx = comboIngMP.getSelectionModel().getSelectedIndex();
        if (idx < 0) return;
        if (campoIngQtd.getText().isEmpty()) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Informe a quantidade do ingrediente!");
            return;
        }
        try {
            Connection conn = Conexao.conectar();
            String sql = "INSERT INTO tb_receita_ingredientes (id_receita, id_materia_prima, quantidade, unidade, observacao) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, selecionada.getId());
            stmt.setInt(2, idsMPs.get(idx));
            stmt.setDouble(3, Double.parseDouble(campoIngQtd.getText()));
            stmt.setString(4, comboIngUnidade.getValue());
            stmt.setString(5, campoIngObs.getText());
            stmt.executeUpdate();
            conn.close();
            // limpa os campos do formulario de ingrediente
            campoIngQtd.setText("");
            campoIngObs.setText("");
            carregarIngredientes(selecionada.getId());
            mensagem.setStyle("-fx-text-fill: green;");
            mensagem.setText("Ingrediente adicionado!");
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao adicionar ingrediente: " + e.getMessage());
        }
    }

    /**
     * remove o ingrediente selecionado na tabela de ingredientes
     * exibe dialogo de confirmacao antes de executar a exclusao
     */
    @FXML
    private void removerIngrediente() {
        ReceitaIngrediente selecionado = tabelaIngredientes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mensagem.setText("Selecione um ingrediente para remover!");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar remoção");
        alert.setHeaderText(null);
        alert.setContentText("Deseja remover \"" + selecionado.getNomeMateriaPrima() + "\" da receita?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_receita_ingredientes WHERE id=?");
            stmt.setInt(1, selecionado.getId());
            stmt.executeUpdate();
            conn.close();
            Receita r = tabelaReceitas.getSelectionModel().getSelectedItem();
            if (r != null) carregarIngredientes(r.getId());
            mensagem.setStyle("-fx-text-fill: green;");
            mensagem.setText("Ingrediente removido!");
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao remover ingrediente: " + e.getMessage());
        }
    }

    /**
     * fecha a tela e volta para a tela principal
     */
    @FXML
    private void fechar() {
        App.trocarTela("principal");
    }
}
