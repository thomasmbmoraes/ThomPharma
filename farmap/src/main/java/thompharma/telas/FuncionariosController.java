package thompharma.telas;

import thompharma.App;
import thompharma.Conexao;
import thompharma.Mascara;
import thompharma.UiUtil;
import thompharma.modelo.Funcionario;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
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

public class FuncionariosController {

    @FXML private TableView<Funcionario> tabelaFuncionarios;
    @FXML private TableColumn<Funcionario, String> colNome;
    @FXML private TableColumn<Funcionario, String> colCargo;

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

    private ObservableList<Funcionario> listaCompleta = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        tabelaFuncionarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabelaFuncionarios.setPlaceholder(new Label("Nenhum funcionário cadastrado."));

        comboSexo.getItems().addAll("Masculino", "Feminino");
        comboSexo.getSelectionModel().selectFirst();
        comboEstadoCivil.getItems().addAll("Solteiro", "Casado", "Divorciado", "Viúvo", "União Estável");
        comboEstadoCivil.getSelectionModel().selectFirst();

        Mascara.cpf(campoCpf);
        Mascara.telefone(campoTelefone);
        Mascara.telefone(campoTelefone2);
        Mascara.telefone(campoCelular);
        Mascara.cep(campoCep);
        Mascara.data(campoNascimento);
        Mascara.rg(campoRg);

        carregarFuncionarios();

        tabelaFuncionarios.getSelectionModel().selectedItemProperty().addListener(
            (obs, antigo, novo) -> { if (novo != null) preencherFormulario(novo); }
        );
    }

    private void carregarFuncionarios() {
        listaCompleta.clear();
        try (Connection conn = Conexao.conectar();
             ResultSet rs = conn.createStatement().executeQuery(
                 "SELECT * FROM tb_funcionarios ORDER BY nome")) {
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
                // data_nascimento pode estar ausente dependendo da versao do schema
                try { Date dn = rs.getDate("data_nascimento");
                      if (dn != null) f.setNascimento(dn.toLocalDate()); }
                catch (Exception ignored) {}
                listaCompleta.add(f);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar funcionários: " + e.getMessage());
        }
        tabelaFuncionarios.setItems(listaCompleta);
    }

    private void preencherFormulario(Funcionario f) {
        campoNome.setText(f.getNome());
        campoMatricula.setText(nvl(f.getMatricula()));
        campoSetor.setText(nvl(f.getSetor()));
        campoCargo.setText(nvl(f.getCargo()));
        campoRg.setText(nvl(f.getRg()));
        campoCpf.setText(nvl(f.getCpf()));
        campoCep.setText(nvl(f.getCep()));
        campoEndereco.setText(nvl(f.getEndereco()));
        campoBairro.setText(nvl(f.getBairro()));
        campoCidade.setText(nvl(f.getCidade()));
        campoUf.setText(nvl(f.getUf()));
        campoTelefone.setText(nvl(f.getTelefone()));
        campoTelefone2.setText(nvl(f.getTelefone2()));
        campoCelular.setText(nvl(f.getCelular()));
        campoObservacoes.setText(nvl(f.getObservacoes()));
        checkAtivo.setSelected(f.isAtivo());
        campoNascimento.setText(f.getNascimento() != null
            ? f.getNascimento().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        comboSexo.getSelectionModel().select("M".equals(f.getSexo()) ? "Masculino" : "Feminino");
        if (f.getEstadoCivil() != null) comboEstadoCivil.getSelectionModel().select(f.getEstadoCivil());
        mensagem.setText("");
    }

    @FXML
    private void novo() {
        campoNome.setText(""); campoMatricula.setText(""); campoSetor.setText("");
        campoCargo.setText(""); campoNascimento.setText(""); campoRg.setText("");
        campoCpf.setText(""); campoCep.setText(""); campoEndereco.setText("");
        campoBairro.setText(""); campoCidade.setText(""); campoUf.setText("");
        campoTelefone.setText(""); campoTelefone2.setText(""); campoCelular.setText("");
        campoObservacoes.setText(""); checkAtivo.setSelected(true);
        comboSexo.getSelectionModel().selectFirst();
        comboEstadoCivil.getSelectionModel().selectFirst();
        mensagem.setText("");
        tabelaFuncionarios.getSelectionModel().clearSelection();
    }

    @FXML
    private void salvar() {
        if (campoNome.getText().isEmpty()) {
            UiUtil.erro(mensagem, "Preencha o nome!");
            return;
        }
        String sexo = "Masculino".equals(comboSexo.getValue()) ? "M" : "F";

        // converte data dd/MM/yyyy → java.sql.Date
        Date dataNasc = null;
        String nascStr = campoNascimento.getText();
        if (nascStr.length() == 10) {
            try {
                String[] p = nascStr.split("/");
                dataNasc = Date.valueOf(p[2] + "-" + p[1] + "-" + p[0]);
            } catch (Exception ignored) {}
        }

        Funcionario sel = tabelaFuncionarios.getSelectionModel().getSelectedItem();
        try (Connection conn = Conexao.conectar()) {
            if (sel == null) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO tb_funcionarios (nome, matricula, setor, cargo, sexo, estado_civil, rg, cpf, " +
                        "cep, endereco, bairro, cidade, uf, telefone, telefone2, celular, observacoes, ativo, data_nascimento) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    stmt.setString(1, campoNome.getText()); stmt.setString(2, campoMatricula.getText());
                    stmt.setString(3, campoSetor.getText()); stmt.setString(4, campoCargo.getText());
                    stmt.setString(5, sexo); stmt.setString(6, comboEstadoCivil.getValue());
                    stmt.setString(7, campoRg.getText()); stmt.setString(8, campoCpf.getText());
                    stmt.setString(9, campoCep.getText()); stmt.setString(10, campoEndereco.getText());
                    stmt.setString(11, campoBairro.getText()); stmt.setString(12, campoCidade.getText());
                    stmt.setString(13, campoUf.getText()); stmt.setString(14, campoTelefone.getText());
                    stmt.setString(15, campoTelefone2.getText()); stmt.setString(16, campoCelular.getText());
                    stmt.setString(17, campoObservacoes.getText()); stmt.setBoolean(18, checkAtivo.isSelected());
                    if (dataNasc != null) stmt.setDate(19, dataNasc); else stmt.setNull(19, Types.DATE);
                    stmt.executeUpdate();
                }
                UiUtil.sucesso(mensagem, "Funcionário cadastrado com sucesso!");
            } else {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE tb_funcionarios SET nome=?, matricula=?, setor=?, cargo=?, sexo=?, estado_civil=?, " +
                        "rg=?, cpf=?, cep=?, endereco=?, bairro=?, cidade=?, uf=?, telefone=?, telefone2=?, " +
                        "celular=?, observacoes=?, ativo=?, data_nascimento=? WHERE id=?")) {
                    stmt.setString(1, campoNome.getText()); stmt.setString(2, campoMatricula.getText());
                    stmt.setString(3, campoSetor.getText()); stmt.setString(4, campoCargo.getText());
                    stmt.setString(5, sexo); stmt.setString(6, comboEstadoCivil.getValue());
                    stmt.setString(7, campoRg.getText()); stmt.setString(8, campoCpf.getText());
                    stmt.setString(9, campoCep.getText()); stmt.setString(10, campoEndereco.getText());
                    stmt.setString(11, campoBairro.getText()); stmt.setString(12, campoCidade.getText());
                    stmt.setString(13, campoUf.getText()); stmt.setString(14, campoTelefone.getText());
                    stmt.setString(15, campoTelefone2.getText()); stmt.setString(16, campoCelular.getText());
                    stmt.setString(17, campoObservacoes.getText()); stmt.setBoolean(18, checkAtivo.isSelected());
                    if (dataNasc != null) stmt.setDate(19, dataNasc); else stmt.setNull(19, Types.DATE);
                    stmt.setInt(20, sel.getId());
                    stmt.executeUpdate();
                }
                UiUtil.sucesso(mensagem, "Funcionário atualizado com sucesso!");
            }
            carregarFuncionarios();
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void excluir() {
        Funcionario sel = tabelaFuncionarios.getSelectionModel().getSelectedItem();
        if (sel == null) { mensagem.setText("Selecione um funcionário!"); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja excluir o funcionário \"" + sel.getNome() + "\"?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM tb_funcionarios WHERE id=?")) {
            stmt.setInt(1, sel.getId());
            stmt.executeUpdate();
            carregarFuncionarios();
            novo();
            UiUtil.sucesso(mensagem, "Funcionário excluído com sucesso!");
        } catch (Exception e) {
            UiUtil.erro(mensagem, "Erro ao excluir: " + e.getMessage());
        }
    }

    private String nvl(String s) { return s != null ? s : ""; }

    @FXML
    private void fechar() { App.trocarTela("principal"); }
}
