package thompharma.service;

import thompharma.dao.MateriaPrimaDao;
import java.sql.SQLException;

/**
 * Regras de negócio para gestão de estoque de matérias-primas.
 */
public class EstoqueService {

    private final MateriaPrimaDao mpDao = new MateriaPrimaDao();

    public int contarCriticas() throws SQLException {
        return mpDao.contarCriticas();
    }

    public int contarVencidas() throws SQLException {
        return mpDao.contarVencidas();
    }

    /** Resumo para o dashboard: retorna array [criticas, vencidas]. */
    public int[] resumoDashboard() throws SQLException {
        return new int[]{contarCriticas(), contarVencidas()};
    }
}
