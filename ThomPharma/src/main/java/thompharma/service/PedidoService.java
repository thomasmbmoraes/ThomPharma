package thompharma.service;

import thompharma.dao.PedidoDao;
import thompharma.modelo.Pedido;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Regras de negócio para pedidos de manipulação.
 */
public class PedidoService {

    private final PedidoDao pedidoDao = new PedidoDao();

    public List<Pedido> listarTodos() throws SQLException {
        return pedidoDao.listarTodos();
    }

    public List<Pedido> listarPorPeriodo(LocalDate de, LocalDate ate, String status) throws SQLException {
        return pedidoDao.listarPorPeriodoEStatus(de, ate, status);
    }

    /** Resumo para o dashboard: retorna array [hoje, pendentes, emProducao, prontos, entreguesHoje]. */
    public int[] resumoDashboard() throws SQLException {
        return new int[]{
            pedidoDao.contarHoje(),
            pedidoDao.contarPendentesPorStatus("Aguardando"),
            pedidoDao.contarPendentesPorStatus("Em Producao"),
            pedidoDao.contarPendentesPorStatus("Pronto"),
            pedidoDao.contarEntreguesHoje()
        };
    }

    public void salvar(Pedido p) throws SQLException {
        if (p.getId() == 0) pedidoDao.inserir(p);
        else pedidoDao.atualizar(p);
    }

    public void excluir(int id) throws SQLException {
        pedidoDao.excluir(id);
    }
}
