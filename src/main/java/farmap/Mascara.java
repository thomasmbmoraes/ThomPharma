package farmap;

import javafx.scene.control.TextField;

/**
 * classe utilitaria com mascaras de formatacao para campos de texto
 * aplica formatacao automatica enquanto o usuario digita
 * deve ser chamada no metodo initialize() do controller
 */
public class Mascara {

    /**
     * formata o campo como CPF: xxx.xxx.xxx-xx
     * limita a 14 caracteres incluindo pontos e traco
     */
    public static void cpf(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            if (novo.length() > 14) {
                campo.setText(antigo);
                return;
            }
            String digits = novo.replaceAll("[^0-9]", "");
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                if (i == 3 || i == 6) formatted.append(".");
                if (i == 9) formatted.append("-");
                formatted.append(digits.charAt(i));
            }
            if (!formatted.toString().equals(novo)) {
                campo.setText(formatted.toString());
                campo.positionCaret(formatted.length());
            }
        });
    }

    /**
     * formata o campo como CNPJ: xx.xxx.xxx/xxxx-xx
     * limita a 18 caracteres incluindo pontos, barra e traco
     */
    public static void cnpj(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            if (novo.length() > 18) {
                campo.setText(antigo);
                return;
            }
            String digits = novo.replaceAll("[^0-9]", "");
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                if (i == 2 || i == 5) formatted.append(".");
                if (i == 8) formatted.append("/");
                if (i == 12) formatted.append("-");
                formatted.append(digits.charAt(i));
            }
            if (!formatted.toString().equals(novo)) {
                campo.setText(formatted.toString());
                campo.positionCaret(formatted.length());
            }
        });
    }

    /**
     * formata o campo como CPF ou CNPJ automaticamente
     * ate 11 digitos formata como CPF, acima formata como CNPJ
     */
    public static void cnpjCpf(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            if (novo.length() > 18) {
                campo.setText(antigo);
                return;
            }
            String digits = novo.replaceAll("[^0-9]", "");
            StringBuilder formatted = new StringBuilder();

            if (digits.length() <= 11) {
                // formata como cpf
                for (int i = 0; i < digits.length(); i++) {
                    if (i == 3 || i == 6) formatted.append(".");
                    if (i == 9) formatted.append("-");
                    formatted.append(digits.charAt(i));
                }
            } else {
                // formata como cnpj
                for (int i = 0; i < digits.length(); i++) {
                    if (i == 2 || i == 5) formatted.append(".");
                    if (i == 8) formatted.append("/");
                    if (i == 12) formatted.append("-");
                    formatted.append(digits.charAt(i));
                }
            }
            if (!formatted.toString().equals(novo)) {
                campo.setText(formatted.toString());
                campo.positionCaret(formatted.length());
            }
        });
    }

    /**
     * formata o campo como telefone fixo ou celular
     * fixo: (xx) xxxx-xxxx
     * celular: (xx) xxxxx-xxxx
     * detecta automaticamente pelo numero de digitos
     */
    public static void telefone(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            if (novo.length() > 15) {
                campo.setText(antigo);
                return;
            }
            String digits = novo.replaceAll("[^0-9]", "");
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                if (i == 0) formatted.append("(");
                if (i == 2) formatted.append(") ");
                if (digits.length() == 11 && i == 7) formatted.append("-");
                if (digits.length() <= 10 && i == 6) formatted.append("-");
                formatted.append(digits.charAt(i));
            }
            if (!formatted.toString().equals(novo)) {
                campo.setText(formatted.toString());
                campo.positionCaret(formatted.length());
            }
        });
    }

    /**
     * formata o campo como CEP: xxxxx-xxx
     * limita a 9 caracteres incluindo o traco
     */
    public static void cep(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            if (novo.length() > 9) {
                campo.setText(antigo);
                return;
            }
            String digits = novo.replaceAll("[^0-9]", "");
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                if (i == 5) formatted.append("-");
                formatted.append(digits.charAt(i));
            }
            if (!formatted.toString().equals(novo)) {
                campo.setText(formatted.toString());
                campo.positionCaret(formatted.length());
            }
        });
    }

    /**
     * permite apenas digitos numericos no campo
     * @param campo campo a ser restringido
     * @param limite numero maximo de digitos permitidos
     */
    public static void soNumeros(TextField campo, int limite) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            if (!novo.matches("\\d*") || novo.length() > limite) {
                campo.setText(antigo);
            }
        });
    }
    
    /**
    * formata o campo como data: dd/mm/aaaa
    * limita a 10 caracteres incluindo as barras
    */
    public static void data(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            if (novo.length() > 10) {
                campo.setText(antigo);
                return;
            }
            String digits = novo.replaceAll("[^0-9]", "");
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                if (i == 2 || i == 4) formatted.append("/");
                formatted.append(digits.charAt(i));
            }
            if (!formatted.toString().equals(novo)) {
                campo.setText(formatted.toString());
                campo.positionCaret(formatted.length());
            }
        });
    }
    
    /**
    * formata o campo como RG: xx.xxx.xxx-x
    * limita a 12 caracteres incluindo pontos e traco
    */
    public static void rg(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            if (novo.length() > 12) {
                campo.setText(antigo);
                return;
            }
            String digits = novo.replaceAll("[^0-9]", "");
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                if (i == 2 || i == 5) formatted.append(".");
                if (i == 8) formatted.append("-");
                formatted.append(digits.charAt(i));
            }
            if (!formatted.toString().equals(novo)) {
                campo.setText(formatted.toString());
                campo.positionCaret(formatted.length());
            }
        });
    }
}