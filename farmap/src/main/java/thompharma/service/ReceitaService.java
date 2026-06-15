package thompharma.service;

import thompharma.dao.ReceitaDao;
import thompharma.modelo.Receita;
import thompharma.modelo.ReceitaIngrediente;
import java.sql.SQLException;
import java.util.List;

/**
 * Regras de negócio para receitas (fórmulas).
 */
public class ReceitaService {

    private final ReceitaDao receitaDao = new ReceitaDao();

    public List<Receita> listarTodos() throws SQLException {
        return receitaDao.listarTodos();
    }

    public List<Receita> buscar(String texto, String tipo) throws SQLException {
        return receitaDao.buscar(texto, tipo);
    }

    public List<ReceitaIngrediente> listarIngredientes(int idReceita) throws SQLException {
        return receitaDao.listarIngredientes(idReceita);
    }

    public void salvar(Receita r) throws SQLException {
        if (r.getNome() == null || r.getNome().trim().isEmpty())
            throw new IllegalArgumentException("Nome da receita é obrigatório!");
        if (r.getId() == 0) receitaDao.inserir(r);
        else receitaDao.atualizar(r);
    }

    public void excluir(int id) throws SQLException {
        receitaDao.excluir(id);
    }

    public void adicionarIngrediente(ReceitaIngrediente ing) throws SQLException {
        if (ing.getQuantidade() <= 0)
            throw new IllegalArgumentException("Quantidade deve ser maior que zero!");
        receitaDao.adicionarIngrediente(ing);
    }

    public void removerIngrediente(int id) throws SQLException {
        receitaDao.removerIngrediente(id);
    }

    public int contarTotal() throws SQLException {
        return receitaDao.contarTotal();
    }
}
