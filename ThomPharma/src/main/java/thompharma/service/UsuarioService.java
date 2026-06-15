package thompharma.service;

import thompharma.dao.TentativaLoginDao;
import thompharma.dao.UsuarioDao;
import thompharma.modelo.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;

/**
 * Regras de negócio para usuários e autenticação.
 */
public class UsuarioService {

    private static final int MAX_TENTATIVAS = 5;
    private static final int BLOQUEIO_SEGUNDOS = 900;

    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final TentativaLoginDao tentativaDao = new TentativaLoginDao();

    public UsuarioService() {
        tentativaDao.criarTabelaSeNecessario();
    }

    /** Resultado da tentativa de login. */
    public static final class ResultadoLogin {
        private final boolean sucesso;
        private final boolean bloqueado;
        private final String mensagem;
        private final Usuario usuario;

        public ResultadoLogin(boolean sucesso, boolean bloqueado, String mensagem, Usuario usuario) {
            this.sucesso = sucesso; this.bloqueado = bloqueado;
            this.mensagem = mensagem; this.usuario = usuario;
        }

        public boolean sucesso() { return sucesso; }
        public boolean bloqueado() { return bloqueado; }
        public String mensagem() { return mensagem; }
        public Usuario usuario() { return usuario; }
    }

    public ResultadoLogin autenticar(String login, String senha) throws SQLException {
        // verifica bloqueio
        int[] estado = tentativaDao.buscarEstado(login);
        if (estado != null && estado[0] >= MAX_TENTATIVAS) {
            long agora = System.currentTimeMillis() / 1000;
            if (agora < estado[1]) {
                return new ResultadoLogin(false, true,
                    "Conta bloqueada por excesso de tentativas. Tente em 15 minutos.", null);
            } else {
                tentativaDao.limpar(login);
            }
        }

        Usuario u = usuarioDao.buscarPorLogin(login);
        if (u == null) {
            tentativaDao.registrarFalha(login, MAX_TENTATIVAS, BLOQUEIO_SEGUNDOS);
            return new ResultadoLogin(false, false, "Usuário ou senha incorretos!", null);
        }

        String hash = u.getSenha();
        boolean ok;
        if (hash != null && hash.startsWith("$2a$")) {
            ok = BCrypt.checkpw(senha, hash);
        } else {
            // migração automática de senha em texto puro
            ok = senha.equals(hash);
            if (ok) {
                usuarioDao.atualizarSenha(u.getId(), BCrypt.hashpw(senha, BCrypt.gensalt()));
            }
        }

        if (!ok) {
            tentativaDao.registrarFalha(login, MAX_TENTATIVAS, BLOQUEIO_SEGUNDOS);
            return new ResultadoLogin(false, false, "Usuário ou senha incorretos!", null);
        }

        tentativaDao.limpar(login);
        return new ResultadoLogin(true, false, null, u);
    }

    public void salvarNovo(String login, String nomeCompleto, String senha, boolean admin) throws SQLException {
        if (usuarioDao.loginDuplicado(login, -1))
            throw new IllegalArgumentException("Já existe um usuário com este login!");
        String hash = BCrypt.hashpw(senha, BCrypt.gensalt());
        usuarioDao.inserir(login, nomeCompleto, hash, admin);
    }

    public void atualizar(int id, String nomeCompleto, boolean admin, boolean ativo) throws SQLException {
        usuarioDao.atualizar(id, nomeCompleto, admin, ativo);
    }

    public void alterarSenha(int id, String novaSenha) throws SQLException {
        usuarioDao.atualizarSenha(id, BCrypt.hashpw(novaSenha, BCrypt.gensalt()));
    }

    public void excluir(int id) throws SQLException {
        usuarioDao.excluir(id);
    }
}
