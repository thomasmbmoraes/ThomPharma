package thompharma.telas;

import thompharma.App;
import thompharma.Conexao;
import thompharma.Mascara;
import thompharma.modelo.Prescritor;
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
 * controller do cadastro de prescritores
 * gerencia medicos, dentistas, veterinarios e outros profissionais de saude
 * separado de clientes para facilitar o vinculo com receitas e pedidos
 */
public class PrescritoresController {

    // componentes da tabela de listagem
    @FXML private TableView<Prescritor> tabelaPrescritores;
    @FXML private TableColumn<Prescritor, String> colNome;
    @FXML private TableColumn<Prescritor, String> colTipo;
    @FXML private TableColumn<Prescritor, String> colRegistro;

    // campos de filtro
    @FXML private TextField campoFiltro;
    @FXML private ComboBox<String> comboFiltroTipo;

    // campos do formulario
    @FXML private TextField campoNome;
    @FXML private ComboBox<String> comboTipoRegistro;
    @FXML private TextField campoNumeroRegistro;
    @FXML private TextField campoTelefone;
    @FXML private TextField campoEmail;
    @FXML private TextArea campoObservacoes;
    @FXML private Label mensagem;

    // lista completa de prescritores carregada do banco
    private ObservableList<Prescritor> listaCompleta = FXCollections.observableArrayList();

    // opcoes de tipo de registro profissional
    private static final String[] TIPOS = {
        "CRM", "CRO", "CRV", "CRP", "CRN", "CREFITO", "Terapeuta", "Outro"
    };

    /**
     * executado automaticamente ao carregar a tela
     * configura colunas, mascaras, listeners e carrega dados do banco
     */
    @FXML
    public void initialize() {
        // configura quais atributos aparecem nas colunas
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoRegistro"));
        colRegistro.setCellValueFactory(new PropertyValueFactory<>("numeroRegistro"));
        tabelaPrescritores.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // preenche os comboboxes de tipo
        comboTipoRegistro.getItems().addAll(TIPOS);
        comboTipoRegistro.getSelectionModel().selectFirst();

        // combobox de filtro por tipo inclui opcao "Todos"
        comboFiltroTipo.getItems().add("Todos");
        comboFiltroTipo.getItems().addAll(TIPOS);
        comboFiltroTipo.getSelectionModel().selectFirst();

        Mascara.telefone(campoTelefone);

        carregarPrescritores();

        // ao selecionar um prescritor na tabela, preenche o formulario
        tabelaPrescritores.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> {
                if (novo != null) preencherFormulario(novo);
            }
        );

        // filtra conforme o usuario digita ou muda o tipo
        campoFiltro.textProperty().addListener((obs, antigo, novo) -> filtrar());
        comboFiltroTipo.setOnAction(e -> filtrar());
    }

    /**
     * busca todos os prescritores do banco e exibe na tabela
     */
    private void carregarPrescritores() {
        listaCompleta.clear();
        try {
            Connection conn = Conexao.conectar();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT * FROM tb_prescritores ORDER BY nome"
            );
            while (rs.next()) {
                Prescritor p = new Prescritor();
                p.setId(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setTipoRegistro(rs.getString("tipo_registro"));
                p.setNumeroRegistro(rs.getString("numero_registro"));
                p.setTelefone(rs.getString("telefone"));
                p.setEmail(rs.getString("email"));
                p.setObservacoes(rs.getString("observacoes"));
                listaCompleta.add(p);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Erro ao carregar prescritores: " + e.getMessage());
        }
        tabelaPrescritores.setItems(listaCompleta);
    }

    /**
     * filtra a lista pelo nome digitado e pelo tipo selecionado
     * ambos os filtros sao aplicados simultaneamente
     */
    private void filtrar() {
        String texto = campoFiltro.getText();
        String tipo = comboFiltroTipo.getValue();

        if ((texto == null || texto.isEmpty()) && "Todos".equals(tipo)) {
            tabelaPrescritores.setItems(listaCompleta);
            return;
        }

        ObservableList<Prescritor> filtrada = FXCollections.observableArrayList();
        for (Prescritor p : listaCompleta) {
            boolean passaNome = texto == null || texto.isEmpty()
                || p.getNome().toLowerCase().contains(texto.toLowerCase());
            boolean passaTipo = "Todos".equals(tipo)
                || tipo.equals(p.getTipoRegistro());
            if (passaNome && passaTipo) filtrada.add(p);
        }
        tabelaPrescritores.setItems(filtrada);
    }

    /**
     * preenche o formulario com os dados do prescritor selecionado na tabela
     * @param p prescritor selecionado
     */
    private void preencherFormulario(Prescritor p) {
        campoNome.setText(p.getNome());
        comboTipoRegistro.getSelectionModel().select(p.getTipoRegistro());
        campoNumeroRegistro.setText(p.getNumeroRegistro() != null ? p.getNumeroRegistro() : "");
        campoTelefone.setText(p.getTelefone() != null ? p.getTelefone() : "");
        campoEmail.setText(p.getEmail() != null ? p.getEmail() : "");
        campoObservacoes.setText(p.getObservacoes() != null ? p.getObservacoes() : "");
        mensagem.setText("");
    }

    /**
     * limpa o formulario para cadastrar um novo prescritor
     */
    @FXML
    private void novo() {
        campoNome.setText("");
        comboTipoRegistro.getSelectionModel().selectFirst();
        campoNumeroRegistro.setText("");
        campoTelefone.setText("");
        campoEmail.setText("");
        campoObservacoes.setText("");
        mensagem.setText("");
        tabelaPrescritores.getSelectionModel().clearSelection();
    }

    /**
     * salva o prescritor no banco
     * insere novo se nenhum estiver selecionado, atualiza se estiver
     * padroniza o nome antes de salvar e verifica duplicados pelo numero de registro
     */
    @FXML
    private void salvar() {
        if (campoNome.getText().isEmpty()) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Preencha o nome!");
            return;
        }

        String nome = Mascara.padronizarNome(campoNome.getText());

        try {
            Connection conn = Conexao.conectar();
            Prescritor selecionado = tabelaPrescritores.getSelectionModel().getSelectedItem();

            // verifica se ja existe outro prescritor com o mesmo numero de registro
            if (!campoNumeroRegistro.getText().isEmpty()) {
                PreparedStatement check = conn.prepareStatement(
                    "SELECT id FROM tb_prescritores WHERE numero_registro = ? AND tipo_registro = ?"
                );
                check.setString(1, campoNumeroRegistro.getText());
                check.setString(2, comboTipoRegistro.getValue());
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    int idEncontrado = rs.getInt("id");
                    if (selecionado == null || idEncontrado != selecionado.getId()) {
                        mensagem.setStyle("-fx-text-fill: red;");
                        mensagem.setText("Já existe um prescritor com este número de registro!");
                        conn.close();
                        return;
                    }
                }
            }

            if (selecionado == null) {
                // insere novo prescritor
                String sql = "INSERT INTO tb_prescritores (nome, tipo_registro, numero_registro, telefone, email, observacoes) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nome);
                stmt.setString(2, comboTipoRegistro.getValue());
                stmt.setString(3, campoNumeroRegistro.getText());
                stmt.setString(4, campoTelefone.getText());
                stmt.setString(5, campoEmail.getText());
                stmt.setString(6, campoObservacoes.getText());
                stmt.executeUpdate();
                mensagem.setStyle("-fx-text-fill: green;");
                mensagem.setText("Prescritor cadastrado com sucesso!");
            } else {
                // atualiza prescritor existente
                String sql = "UPDATE tb_prescritores SET nome=?, tipo_registro=?, numero_registro=?, telefone=?, email=?, observacoes=? WHERE id=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nome);
                stmt.setString(2, comboTipoRegistro.getValue());
                stmt.setString(3, campoNumeroRegistro.getText());
                stmt.setString(4, campoTelefone.getText());
                stmt.setString(5, campoEmail.getText());
                stmt.setString(6, campoObservacoes.getText());
                stmt.setInt(7, selecionado.getId());
                stmt.executeUpdate();
                mensagem.setStyle("-fx-text-fill: green;");
                mensagem.setText("Prescritor atualizado com sucesso!");
            }
            conn.close();
            carregarPrescritores();
            campoNome.setText(nome);
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao salvar: " + e.getMessage());
        }
    }

    /**
     * exclui o prescritor selecionado na tabela
     * exibe dialogo de confirmacao antes de executar a exclusao
     */
    @FXML
    private void excluir() {
        Prescritor selecionado = tabelaPrescritores.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mensagem.setText("Selecione um prescritor!");
            return;
        }
        // pede confirmacao antes de excluir para evitar exclusoes acidentais
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja excluir o prescritor \"" + selecionado.getNome() + "\"?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_prescritores WHERE id=?");
            stmt.setInt(1, selecionado.getId());
            stmt.executeUpdate();
            conn.close();
            carregarPrescritores();
            novo();
            mensagem.setStyle("-fx-text-fill: green;");
            mensagem.setText("Prescritor excluído com sucesso!");
        } catch (Exception e) {
            mensagem.setStyle("-fx-text-fill: red;");
            mensagem.setText("Erro ao excluir: " + e.getMessage());
        }
    }

    /**
     * fecha a tela de prescritores e volta para a tela principal
     */
    @FXML
    private void fechar() {
        App.trocarTela("principal");
    }
}
