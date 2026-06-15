package thompharma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MascaraTest {

    // --- padronizarNome ---

    @Test
    void padronizarNome_capitalizaPrimeiraLetra() {
        assertEquals("Maria Silva", Mascara.padronizarNome("maria silva"));
    }

    @Test
    void padronizarNome_converteUpperCase() {
        assertEquals("João das Neves", Mascara.padronizarNome("JOÃO DAS NEVES"));
    }

    @Test
    void padronizarNome_artigosEmMinusculo() {
        assertEquals("Maria de Lima", Mascara.padronizarNome("maria de lima"));
        assertEquals("Ana das Flores", Mascara.padronizarNome("ana das flores"));
        assertEquals("João do Campo", Mascara.padronizarNome("joão do campo"));
    }

    @Test
    void padronizarNome_primeiraWordSempreCapitalizada() {
        // "de" no início deve ser capitalizado (é a primeira palavra)
        assertEquals("De Oliveira", Mascara.padronizarNome("de oliveira"));
    }

    @Test
    void padronizarNome_retornaVazioParaVazio() {
        assertEquals("", Mascara.padronizarNome(""));
    }

    @Test
    void padronizarNome_retornaNullParaNull() {
        assertNull(Mascara.padronizarNome(null));
    }

    @Test
    void padronizarNome_removeEspacosExtras() {
        assertEquals("Carlos Souza", Mascara.padronizarNome("  carlos   souza  "));
    }

    // --- StatusPedido enum ---

    @Test
    void statusPedido_fromLabel_encontraCorreto() {
        assertEquals(thompharma.modelo.StatusPedido.EM_PRODUCAO,
            thompharma.modelo.StatusPedido.fromLabel("Em Produção"));
        assertEquals(thompharma.modelo.StatusPedido.ENTREGUE,
            thompharma.modelo.StatusPedido.fromLabel("Entregue"));
    }

    @Test
    void statusPedido_fromLabel_desconhecidoRetornaAguardando() {
        assertEquals(thompharma.modelo.StatusPedido.AGUARDANDO,
            thompharma.modelo.StatusPedido.fromLabel("Inexistente"));
    }

    @Test
    void statusPedido_toString_retornaLabel() {
        assertEquals("Pronto", thompharma.modelo.StatusPedido.PRONTO.toString());
    }
}
