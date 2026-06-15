package thompharma.telas;

import thompharma.App;
import thompharma.Conexao;
import thompharma.UiUtil;
import thompharma.modelo.Receita;
import thompharma.modelo.ReceitaIngrediente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
 */
public class ReceitasController {

    @FXML private TableView<Receita> tabelaReceitas;
    @FXML private TableColumn<Receita, String> colNome;
    @FXML private TableColumn<Receita, String> colTipo;
    @FXML private TableColumn<Receita, String> colNomeFantasia;

    @FXML private TextField campoFiltro;
    @FXML private ComboBox<String> comboFiltroTipo;

    @FXML private TextField campoNome;
    @FXML private TextField campoNomeFantasia;
    @FXML private ComboBox<String> comboTipo;
    @FXML private TextArea campoObservacoes;
    @FXML private Label mensagem;

    @FXML private TableView<ReceitaIngrediente> tabelaIngredientes;
    @FXML private TableColumn<ReceitaIngrediente, String> colIngMP;
    @FXML private TableColumn<ReceitaIngrediente, Double> colIngQtd;
    @FXML private TableColumn<ReceitaIngrediente, String> colIngUnidade;
    @FXML private TableColumn<ReceitaIngrediente, String> colIngObs;

    @FXML private ComboBox<String> comboIngMP;
    @FXML private TextField campoIngQtd;
    @FXML private ComboBox<String> comboIngUnidade;
    @FXML private TextField campoIngObs;

    private ObservableList<Integer> idsMPs = FXCollections.observableArrayList();
    private ObservableList<Receita> listaCompleta = FXCollections.observableArrayList();

    private static final String[] TIPOS = {
        "Cápsula", "Creme", "Solução", "Xarope", "Pomada", "Gel",
        "Suspensão", "Floral", "Homeopatia", "Outro"
    };

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colNomeFantasia.setCellValueFactory(new PropertyValueFactory<>("nomeFantasia"));
        tabelaReceitas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabelaReceitas.setPlaceholder(new Label("Nenhuma receita cadastrada."));

        colIngMP.setCellValueFactory(new PropertyValueFactory<>("nomeMateriaPrima"));
        colIngQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colIngUnidade.setCellValueFactory(new PropertyValueFactory<>("unidade"));
        colIngObs.setCellValueFactory(new PropertyValueFactory<>("observacao"));
        tabelaIngredientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabelaIngredientes.setPlaceholder(new Label("Nenhum ingrediente adicionado."));

        comboTipo.getItems().addAll(TIPOS);
        comboTipo.getSelectionModel().selectFirst();

        comboFiltroTipo.getItems().add("Todos");
        comboFiltroTipo.getItems().addAll(TIPOS);
        comboFiltroTipo.getSelectionModel().selectFirst();

        comboIngUnidade.getItems().addAll("mg", "g", "kg", "ml", "l", "un", "%");
        comboIngUnidade.getSelectionModel().selectFirst();

        carregarReceitas();
        carregarMPs();

        tabelaReceitas.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> {
                if (novo != null) {
                    preencherFormulario(novo);
                    carregarIngredientes(novo.getId());
                }
            }
        );

        campoFiltro.textProperty().addListener((obs, antigo, novo) -> filtrar());
        comboFiltroTipo.setOnAction(e -> filtrar());
        comboIngMP.setOnAction(e -> preencherUnidadeMP());
    }

    private void carregarReceitas() {
        listaCompleta.clear();
        String sql = "SELECT DISTINCT r.* FROM tb_receitas r " +
                     "LEFT JOIN tb_receita_ingredientes ri ON ri.id_receita = r.id " +
                     "LEFT JOIN tb_materias_primas mp ON mp.id = ri.id_materia_prima " +
                     "ORDER BY r.nome";
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Receita r = new Receita();
                r.setId(rs.getInt("id"));
                r.setNome(rs.getString("nome"));
                r.setNomeFantasia(rs.getString("nome_fantasia"));
                r.setTipo(rs.getString("tipo"));
                r.setObservacoes(rs.getString("observacoes"));
                listaCompleta.add(r);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar receitas: " + e.getMessage());
        }
        tabelaReceitas.setItems(listaCompleta);
    }

    private void carregarMPs() {
        idsMPs.clear();
        comboIngMP.getItems().clear();
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, nome FROM tb_materias_primas ORDER BY nome")) {
            while (rs.next()) {
                idsMPs.add(rs.getInt("id"));
                comboIngMP.getItems().add(rs.getString("nome"));
            }
            if (!comboIngMP.getItems().isEmpty()) {
                comboIngMP.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar materias primas: " + e.getMessage());
        }
    }

    private void carregarIngredientes(int idReceita) {
        ObservableList<ReceitaIngrediente> lista = FXCollections.observableArrayList();
        String sql = "SELECT ri.*, mp.nome as nome_mp FROM tb_receita_ingredientes ri " +
                     "JOIN tb_materias_primas mp ON mp.id = ri.id_materia_prima " +
                     "WHERE ri.id_receita = ? ORDER BY ri.id";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idReceita);
            try (ResultSet rs = stmt.executeQuery()) {
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
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar ingredientes: " + e.getMessage());
        }
        tabelaIngredientes.setItems(lista);
    }

    private void filtrar() {
        String texto = campoFiltro.getText();
        String tipo = comboFiltroTipo.getValue();
        boolean semFiltro = (texto == null || texto.isEmpty()) && "Todos".equals(tipo);

        if (semFiltro) {
            tabelaReceitas.setItems(listaCompleta);
            return;
        }

        String like = "%" + (texto != null ? texto.toLowerCase() : "") + "%";
        String sql = "SELECT DISTINCT r.* FROM tb_receitas r " +
                     "LEFT JOIN tb_receita_ingredientes ri ON ri.id_receita = r.id " +
                     "LEFT JOIN tb_materias_primas mp ON mp.id = ri.id_materia_prima " +
                     "WHERE (LOWER(r.nome) LIKE ? OR LOWER(mp.nome) LIKE ?) " +
                     (!"Todos".equals(tipo) ? "AND r.tipo = ? " : "") +
                     "ORDER BY r.nome";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, like);
            stmt.setString(2, like);
            if (!"Todos".equals(tipo)) stmt.setString(3, tipo);

            ObservableList<Receita> filtrada = FXCollections.observableArrayList();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Receita r = new Receita();
                    r.setId(rs.getInt("id"));
                    r.setNome(rs.getString("nome"));
                    r.setNomeFantasia(rs.getString("nome_fantasia"));
                    r.setTipo(rs.getString("tipo"));
                    r.setObservacoes(rs.getString("observacoes"));
                    filtrada.add(r);
                }
            }
            tabelaReceitas.setItems(filtrada);
        } catch (Exception e) {
            System.err.println("Erro ao filtrar receitas: " + e.getMessage());
        }
    }

    private void preencherFormulario(Receita r) {
        campoNome.setText(r.getNome());
        campoNomeFantasia.setText(r.getNomeFantasia() != null ? r.getNomeFantasia() : "");
        comboTipo.getSelectionModel().select(r.getTipo());
        campoObservacoes.setText(r.getObservacoes() != null ? r.getObservacoes() : "");
        UiUtil.limpar(mensagem);
    }

    private void preencherUnidadeMP() {
        int idx = comboIngMP.getSelectionModel().getSelectedIndex();
        if (idx < 0 || idsMPs.isEmpty()) return;
        int idMP = idsMPs.get(idx);
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT unidade FROM tb_materias_primas WHERE id = ?")) {
            stmt.setInt(1, idMP);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    comboIngUnidade.getSelectionModel().select(rs.getString("unidade"));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar unidade da MP: " + e.getMessage());
        }
    }

    @FXML
    private void novo() {
        campoNome.setText("");
        campoNomeFantasia.setText("");
        comboTipo.getSelectionModel().selectFirst();
        campoObservacoes.setText("");
        tabelaIngredientes.getItems().clear();
        UiUtil.limpar(mensagem);
        tabelaReceitas.getSelectionModel().clearSelection();
    }

    @FXML
    private void salvar() {
        if (campoNome.getText().isEmpty()) {
            UiUtil.erro(mensagem, "Preencha o nome da receita!");
            return;
        }
        Receita selecionada = tabelaReceitas.getSelectionModel().getSelectedItem();
        try (Connection conn = Conexao.conectar()) {
            if (selecionada == null) {
                String sql = "INSERT INTO tb_receitas (nome, nome_fantasia, tipo, observacoes) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, campoNome.getText());
                    stmt.setString(2, campoNomeFantasia.getText());
                    stmt.setString(3, comboTipo.getValue());
                    stmt.setString(4, campoObservacoes.getText());
                    stmt.executeUpdate();
                }
                UiUtil.sucesso(mensagem, "Receita cadastrada com sucesso!");
            } else {
                String sql = "UPDATE tb_receitas SET nome=?, nome_fantasia=?, tipo=?, observacoes=? WHERE id=?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, campoNome.getText());
                    stmt.setString(2, campoNomeFantasia.getText());
                    stmt.setString(3, comboTipo.getValue());
                    stmt.setString(4, campoObservacoes.getText());
                    stmt.setInt(5, selecionada.getId());
                    stmt.executeUpdate();
                }
                UiUtil.sucesso(mensagem, "Receita atualizada com sucesso!");
            }
            carregarReceitas();
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void excluir() {
        Receita selecionada = tabelaReceitas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            UiUtil.erro(mensagem, "Selecione uma receita!");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja excluir a receita \"" + selecionada.getNome() + "\"?\nTodos os ingredientes também serão removidos.");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_receitas WHERE id=?")) {
            stmt.setInt(1, selecionada.getId());
            stmt.executeUpdate();
            carregarReceitas();
            novo();
            UiUtil.sucesso(mensagem, "Receita excluída com sucesso!");
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao excluir: " + e.getMessage());
        }
    }

    @FXML
    private void adicionarIngrediente() {
        Receita selecionada = tabelaReceitas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            UiUtil.erro(mensagem, "Salve a receita antes de adicionar ingredientes!");
            return;
        }
        int idx = comboIngMP.getSelectionModel().getSelectedIndex();
        if (idx < 0) return;
        if (campoIngQtd.getText().isEmpty()) {
            UiUtil.erro(mensagem, "Informe a quantidade do ingrediente!");
            return;
        }
        String sql = "INSERT INTO tb_receita_ingredientes (id_receita, id_materia_prima, quantidade, unidade, observacao) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, selecionada.getId());
            stmt.setInt(2, idsMPs.get(idx));
            stmt.setDouble(3, Double.parseDouble(campoIngQtd.getText()));
            stmt.setString(4, comboIngUnidade.getValue());
            stmt.setString(5, campoIngObs.getText());
            stmt.executeUpdate();
            campoIngQtd.setText("");
            campoIngObs.setText("");
            carregarIngredientes(selecionada.getId());
            UiUtil.sucesso(mensagem, "Ingrediente adicionado!");
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao adicionar ingrediente: " + e.getMessage());
        }
    }

    @FXML
    private void removerIngrediente() {
        ReceitaIngrediente selecionado = tabelaIngredientes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            UiUtil.erro(mensagem, "Selecione um ingrediente para remover!");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar remoção");
        alert.setHeaderText(null);
        alert.setContentText("Deseja remover \"" + selecionado.getNomeMateriaPrima() + "\" da receita?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_receita_ingredientes WHERE id=?")) {
            stmt.setInt(1, selecionado.getId());
            stmt.executeUpdate();
            Receita r = tabelaReceitas.getSelectionModel().getSelectedItem();
            if (r != null) carregarIngredientes(r.getId());
            UiUtil.sucesso(mensagem, "Ingrediente removido!");
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao remover ingrediente: " + e.getMessage());
        }
    }

    @FXML
    private void fechar() {
        App.trocarTela("principal");
    }
}
