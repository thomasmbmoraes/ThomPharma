package thompharma;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

/**
 * Utilitário para executar operações de banco em thread separada (não na FX thread),
 * eliminando travamentos da interface durante consultas.
 *
 * Uso típico:
 *   Task<List<Cliente>> t = AsyncUtil.task(() -> clienteDao.listarTodos());
 *   t.setOnSucceeded(e -> tabela.setItems(FXCollections.observableList(t.getValue())));
 *   t.setOnFailed(e -> UiUtil.erro(mensagem, "Erro: " + t.getException().getMessage()));
 *   AsyncUtil.run(t);
 */
public class AsyncUtil {

    @FunctionalInterface
    public interface DbTask<T> {
        T execute() throws Exception;
    }

    /** Cria um Task<T> que executa work() em background. */
    public static <T> Task<T> task(DbTask<T> work) {
        return new Task<>() {
            @Override
            protected T call() throws Exception {
                return work.execute();
            }
        };
    }

    /**
     * Cria um Task<T> com handler de erro automático via Label.
     * setOnSucceeded ainda deve ser configurado pelo chamador.
     */
    public static <T> Task<T> task(DbTask<T> work, Label mensagem) {
        Task<T> t = task(work);
        t.setOnFailed(e -> Platform.runLater(() ->
            UiUtil.erro(mensagem, "Erro: " + t.getException().getMessage())));
        return t;
    }

    /** Executa o task em uma daemon thread gerenciada. */
    public static void run(Task<?> task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
