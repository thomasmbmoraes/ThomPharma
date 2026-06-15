package thompharma;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class UiUtil {

    public static void sucesso(Label label, String texto) {
        label.getStyleClass().removeAll("msg-sucesso", "msg-erro", "msg-info");
        label.getStyleClass().add("msg-sucesso");
        label.setText(texto);
        PauseTransition p = new PauseTransition(Duration.seconds(4));
        p.setOnFinished(e -> { label.getStyleClass().removeAll("msg-sucesso"); label.setText(""); });
        p.play();
    }

    public static void erro(Label label, String texto) {
        label.getStyleClass().removeAll("msg-sucesso", "msg-erro", "msg-info");
        label.getStyleClass().add("msg-erro");
        label.setText(texto);
    }

    public static void info(Label label, String texto) {
        label.getStyleClass().removeAll("msg-sucesso", "msg-erro", "msg-info");
        label.getStyleClass().add("msg-info");
        label.setText(texto);
    }

    public static void limpar(Label label) {
        label.getStyleClass().removeAll("msg-sucesso", "msg-erro", "msg-info");
        label.setText("");
    }

    /** Marca o campo com borda vermelha para indicar erro de validação. */
    public static void marcarInvalido(TextField campo) {
        if (!campo.getStyleClass().contains("campo-invalido")) {
            campo.getStyleClass().add("campo-invalido");
        }
    }

    /** Remove a marcação de erro de validação do campo. */
    public static void limparValidacao(TextField campo) {
        campo.getStyleClass().remove("campo-invalido");
    }

    /** Valida se o campo está preenchido; marca/desmarca visualmente. */
    public static boolean validarObrigatorio(TextField campo) {
        if (campo.getText() == null || campo.getText().trim().isEmpty()) {
            marcarInvalido(campo);
            return false;
        }
        limparValidacao(campo);
        return true;
    }
}
