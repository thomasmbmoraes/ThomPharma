package thompharma.telas;

import thompharma.App;
import thompharma.AsyncUtil;
import thompharma.service.EstoqueService;
import thompharma.service.PedidoService;
import thompharma.service.ReceitaService;
import thompharma.dao.RotuloDao;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Dashboard executivo — exibe indicadores operacionais em tempo real.
 * Todas as queries rodam em background (Task<T>) para não bloquear a UI.
 */
public class DashboardController {

    @FXML private Label lblPedidosHoje;
    @FXML private Label lblAguardando;
    @FXML private Label lblEmProducao;
    @FXML private Label lblProntos;
    @FXML private Label lblEntreguesHoje;
    @FXML private Label lblEstoqueCritico;
    @FXML private Label lblLotesVencidos;
    @FXML private Label lblTotalReceitas;
    @FXML private Label lblTotalRotulos;
    @FXML private Label lblAtualizadoEm;

    private final PedidoService pedidoService = new PedidoService();
    private final EstoqueService estoqueService = new EstoqueService();
    private final ReceitaService receitaService = new ReceitaService();
    private final RotuloDao rotuloDao = new RotuloDao();

    @FXML
    public void initialize() {
        atualizar();
    }

    @FXML
    public void atualizar() {
        carregarPedidos();
        carregarEstoque();
        carregarCadastros();
    }

    private void carregarPedidos() {
        Task<int[]> task = AsyncUtil.task(() -> pedidoService.resumoDashboard());
        task.setOnSucceeded(e -> {
            int[] r = task.getValue();
            // [hoje, aguardando, emProducao, prontos, entreguesHoje]
            lblPedidosHoje.setText(String.valueOf(r[0]));
            lblAguardando.setText(String.valueOf(r[1]));
            lblEmProducao.setText(String.valueOf(r[2]));
            lblProntos.setText(String.valueOf(r[3]));
            lblEntreguesHoje.setText(String.valueOf(r[4]));
            lblAtualizadoEm.setText("Atualizado às " +
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        });
        task.setOnFailed(e -> {
            lblPedidosHoje.setText("!");
            System.err.println("Dashboard erro pedidos: " + task.getException().getMessage());
        });
        AsyncUtil.run(task);
    }

    private void carregarEstoque() {
        Task<int[]> task = AsyncUtil.task(() -> estoqueService.resumoDashboard());
        task.setOnSucceeded(e -> {
            int[] r = task.getValue();
            // [criticas, vencidas]
            lblEstoqueCritico.setText(String.valueOf(r[0]));
            lblLotesVencidos.setText(String.valueOf(r[1]));
            if (r[0] > 0) lblEstoqueCritico.setStyle("-fx-text-fill: #f7768e; -fx-font-size: 32px; -fx-font-weight: bold;");
            if (r[1] > 0) lblLotesVencidos.setStyle("-fx-text-fill: #f7768e; -fx-font-size: 32px; -fx-font-weight: bold;");
        });
        task.setOnFailed(e -> lblEstoqueCritico.setText("!"));
        AsyncUtil.run(task);
    }

    private void carregarCadastros() {
        Task<int[]> task = AsyncUtil.task(() -> new int[]{
            receitaService.contarTotal(),
            rotuloDao.contarTotal()
        });
        task.setOnSucceeded(e -> {
            int[] r = task.getValue();
            lblTotalReceitas.setText(String.valueOf(r[0]));
            lblTotalRotulos.setText(String.valueOf(r[1]));
        });
        task.setOnFailed(e -> lblTotalReceitas.setText("!"));
        AsyncUtil.run(task);
    }

    @FXML
    private void fechar() {
        App.trocarTela("principal");
    }
}
