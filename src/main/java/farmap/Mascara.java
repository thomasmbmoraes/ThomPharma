package farmap;

import javafx.scene.control.TextField;

public class Mascara {

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
    
    public static void cnpjCpf(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            if (novo.length() > 18) {
                campo.setText(antigo);
                return;
            }
            String digits = novo.replaceAll("[^0-9]", "");
            StringBuilder formatted = new StringBuilder();

            if (digits.length() <= 11) {
                // formata como CPF
                for (int i = 0; i < digits.length(); i++) {
                    if (i == 3 || i == 6) formatted.append(".");
                    if (i == 9) formatted.append("-");
                    formatted.append(digits.charAt(i));
                }
            } else {
                // formata como CNPJ
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

    public static void soNumeros(TextField campo, int limite) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            if (!novo.matches("\\d*") || novo.length() > limite) {
                campo.setText(antigo);
            }
        });
    }
}