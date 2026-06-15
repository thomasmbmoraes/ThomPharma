package thompharma.telas;

import thompharma.App;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * controller da calculadora farmaceutica
 * realiza calculos automaticos para formulas homeopaticas e florais
 * cada aba corresponde a um tipo de calculo com regras proprias
 */
public class CalculadoraController {

    // --- aba floral ---
    @FXML private TextField floralVolume;
    @FXML private TextField floralNumFlorais;
    @FXML private TextArea floralResultado;

    // --- aba homeopatia liquida ---
    @FXML private TextField homoLiqVolume;
    @FXML private TextField homoLiqNumMeds;
    @FXML private ComboBox<String> homoLiqEscala;
    @FXML private TextArea homoLiqResultado;

    // --- aba homeopatia globulos ---
    @FXML private TextField homoGlobVolume;
    @FXML private TextField homoGlobNumMeds;
    @FXML private TextArea homoGlobResultado;

    // --- aba homeopatia dose unica ---
    @FXML private TextField homoDoseNumMeds;
    @FXML private TextArea homoDoseResultado;

    /**
     * executado automaticamente ao carregar a tela
     */
    @FXML
    public void initialize() {
        // escala padrao CH conforme pratica usual em homeopatia
        homoLiqEscala.getItems().addAll("CH (1:100)", "DH (1:10)");
        homoLiqEscala.getSelectionModel().selectFirst();
    }

    // =================================================================
    // FLORAL
    // Referencia: 30ml = 0,06ml por floral | 100ml = 0,2ml por floral
    // Regra: ml por floral = volume x 0,002
    // Conversao: 0,1ml = 3 gotas  →  1ml = 30 gotas
    // =================================================================

    /**
     * calcula os volumes para formula floral
     * ml por floral = volume x 0.002
     * gotas por floral = ml_por_floral x 30
     * volume do veiculo = volume total - (ml_por_floral x num_florais)
     */
    @FXML
    private void calcularFloral() {
        try {
            double volume = Double.parseDouble(floralVolume.getText().replace(",", "."));
            int numFlorais = Integer.parseInt(floralNumFlorais.getText().trim());

            if (volume <= 0 || numFlorais <= 0) {
                floralResultado.setText("Informe valores maiores que zero.");
                return;
            }

            double mlPorFloral = volume * 0.002;
            double gotasPorFloral = mlPorFloral * 30;
            int gotasInteiras = (int) Math.round(gotasPorFloral);
            double mlPorFloralReal = gotasInteiras / 30.0;
            double mlTotalFlorais = mlPorFloralReal * numFlorais;
            double mlVeiculo = volume - mlTotalFlorais;

            StringBuilder sb = new StringBuilder();
            sb.append("══ RESULTADO FLORAL ══\n\n");
            sb.append(String.format("Volume total:       %.0f ml%n", volume));
            sb.append(String.format("Número de florais:  %d%n%n", numFlorais));
            sb.append(String.format("Ml por floral:      %.3f ml%n", mlPorFloralReal));
            sb.append(String.format("Gotas por floral:   %d gotas%n%n", gotasInteiras));
            sb.append(String.format("Ml total florais:   %.2f ml%n", mlTotalFlorais));
            sb.append(String.format("Volume do veículo:  %.2f ml%n%n", mlVeiculo));
            sb.append("── LISTA DE FLORAIS ──\n");
            for (int i = 1; i <= numFlorais; i++) {
                sb.append(String.format("Floral %d:  %d gotas (%.3f ml)%n",
                    i, gotasInteiras, mlPorFloralReal));
            }

            floralResultado.setText(sb.toString());

        } catch (NumberFormatException e) {
            floralResultado.setText("Preencha todos os campos com valores numéricos válidos.");
        }
    }

    /**
     * limpa os campos e resultado da aba floral
     */
    @FXML
    private void limparFloral() {
        floralVolume.setText("");
        floralNumFlorais.setText("");
        floralResultado.setText("");
    }

    // =================================================================
    // HOMEOPATIA LIQUIDA
    // Regra: total de gotas = volume x 30
    // Gotas por medicamento = total / num_meds (arredondado)
    // Ml por medicamento = gotas / 30
    // =================================================================

    /**
     * calcula as gotas por medicamento para homeopatia liquida
     * distribui as gotas igualmente entre os medicamentos
     * arredonda para gotas inteiras e ajusta o volume do veiculo
     */
    @FXML
    private void calcularHomoLiq() {
        try {
            double volume = Double.parseDouble(homoLiqVolume.getText().replace(",", "."));
            int numMeds = Integer.parseInt(homoLiqNumMeds.getText().trim());
            String escala = homoLiqEscala.getValue();

            if (volume <= 0 || numMeds <= 0) {
                homoLiqResultado.setText("Informe valores maiores que zero.");
                return;
            }

            double totalGotas = volume * 30;
            // calcula gotas por medicamento e arredonda para cima para garantir
            // que cada medicamento tenha ao menos 1 gota
            int gotasPorMed = (int) Math.ceil(totalGotas / numMeds);
            double mlPorMed = gotasPorMed / 30.0;
            double mlTotalMeds = mlPorMed * numMeds;
            double mlVeiculo = volume - mlTotalMeds;

            // garante que o veiculo nao fique negativo por arredondamento
            if (mlVeiculo < 0) {
                gotasPorMed = (int) Math.floor(totalGotas / numMeds);
                mlPorMed = gotasPorMed / 30.0;
                mlTotalMeds = mlPorMed * numMeds;
                mlVeiculo = volume - mlTotalMeds;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("══ RESULTADO HOMEOPATIA LÍQUIDA ══\n\n");
            sb.append(String.format("Volume total:         %.0f ml%n", volume));
            sb.append(String.format("Medicamentos:         %d%n", numMeds));
            sb.append(String.format("Escala:               %s%n%n", escala));
            sb.append(String.format("Gotas por medicamento: %d gotas%n", gotasPorMed));
            sb.append(String.format("Ml por medicamento:    %.4f ml%n%n", mlPorMed));
            sb.append(String.format("Ml total medicamentos: %.4f ml%n", mlTotalMeds));
            sb.append(String.format("Volume do veículo:     %.4f ml%n", mlVeiculo));

            homoLiqResultado.setText(sb.toString());

        } catch (NumberFormatException e) {
            homoLiqResultado.setText("Preencha todos os campos com valores numéricos válidos.");
        }
    }

    /**
     * limpa os campos e resultado da aba homeopatia liquida
     */
    @FXML
    private void limparHomoLiq() {
        homoLiqVolume.setText("");
        homoLiqNumMeds.setText("");
        homoLiqEscala.getSelectionModel().selectFirst();
        homoLiqResultado.setText("");
    }

    // =================================================================
    // HOMEOPATIA GLOBULOS
    // Referencia: 20ml = 0,6ml total | 30ml = 0,9ml total
    // Regra: ml total globulos = volume x 0,03
    // Distribui entre os medicamentos arredondando para gotas inteiras
    // O total sempre fecha: ultimo medicamento absorve a diferenca
    // =================================================================

    /**
     * calcula os globulos por medicamento para homeopatia em globulos
     * usa regra de 3% do volume total para os globulos
     * distribui entre medicamentos em gotas inteiras
     * o ultimo medicamento ajusta a diferenca para o total sempre fechar
     */
    @FXML
    private void calcularHomoGlob() {
        try {
            double volume = Double.parseDouble(homoGlobVolume.getText().replace(",", "."));
            int numMeds = Integer.parseInt(homoGlobNumMeds.getText().trim());

            if (volume <= 0 || numMeds <= 0) {
                homoGlobResultado.setText("Informe valores maiores que zero.");
                return;
            }

            // 3% do volume total para os globulos
            double mlTotal = volume * 0.03;
            double gotasTotal = mlTotal * 30;

            // distribui as gotas em inteiros — ultimo medicamento absorve a diferenca
            int gotasPorMed = (int) Math.floor(gotasTotal / numMeds);
            int gotasUltimo = (int) Math.round(gotasTotal) - (gotasPorMed * (numMeds - 1));
            double mlPorMed = gotasPorMed / 30.0;
            double mlUltimo = gotasUltimo / 30.0;
            double mlTotalReal = mlPorMed * (numMeds - 1) + mlUltimo;

            StringBuilder sb = new StringBuilder();
            sb.append("══ RESULTADO HOMEOPATIA GLÓBULOS ══\n\n");
            sb.append(String.format("Volume total:        %.0f ml%n", volume));
            sb.append(String.format("Medicamentos:        %d%n%n", numMeds));
            sb.append(String.format("Ml total glóbulos:   %.4f ml  (%.1f gotas)%n%n",
                mlTotal, gotasTotal));

            sb.append("── DISTRIBUIÇÃO ──\n");
            for (int i = 1; i <= numMeds; i++) {
                if (i < numMeds) {
                    sb.append(String.format("Med. %d:  %d gotas  (%.4f ml)%n",
                        i, gotasPorMed, mlPorMed));
                } else {
                    sb.append(String.format("Med. %d:  %d gotas  (%.4f ml)  ← ajuste%n",
                        i, gotasUltimo, mlUltimo));
                }
            }
            sb.append(String.format("%nTotal real: %.4f ml%n", mlTotalReal));

            homoGlobResultado.setText(sb.toString());

        } catch (NumberFormatException e) {
            homoGlobResultado.setText("Preencha todos os campos com valores numéricos válidos.");
        }
    }

    /**
     * limpa os campos e resultado da aba homeopatia globulos
     */
    @FXML
    private void limparHomoGlob() {
        homoGlobVolume.setText("");
        homoGlobNumMeds.setText("");
        homoGlobResultado.setText("");
    }

    // =================================================================
    // HOMEOPATIA DOSE UNICA
    // Regra: 1 medicamento CH = 0,6ml total
    // N medicamentos: 0,6 / N ml por medicamento
    // Total sempre = 0,6ml
    // =================================================================

    /**
     * calcula o volume por medicamento para dose unica homeopatica
     * total fixo de 0,6ml dividido igualmente entre os medicamentos
     */
    @FXML
    private void calcularHomoDose() {
        try {
            int numMeds = Integer.parseInt(homoDoseNumMeds.getText().trim());

            if (numMeds <= 0) {
                homoDoseResultado.setText("Informe ao menos 1 medicamento.");
                return;
            }

            double totalMl = 0.6;
            double mlPorMed = totalMl / numMeds;
            double gotasPorMed = mlPorMed * 30;
            int gotasInteiras = (int) Math.round(gotasPorMed);
            double mlReal = gotasInteiras / 30.0;
            double totalReal = mlReal * numMeds;

            StringBuilder sb = new StringBuilder();
            sb.append("══ RESULTADO DOSE ÚNICA ══\n\n");
            sb.append(String.format("Medicamentos:        %d%n", numMeds));
            sb.append(String.format("Volume total:        0,60 ml (fixo)%n%n"));
            sb.append(String.format("Ml por medicamento:  %.4f ml%n", mlPorMed));
            sb.append(String.format("Gotas por medic.:    %.1f → %d gotas%n%n",
                gotasPorMed, gotasInteiras));
            sb.append("── DISTRIBUIÇÃO ──\n");
            for (int i = 1; i <= numMeds; i++) {
                sb.append(String.format("Med. %d:  %d gotas  (%.4f ml)%n",
                    i, gotasInteiras, mlReal));
            }
            sb.append(String.format("%nTotal real: %.4f ml%n", totalReal));

            homoDoseResultado.setText(sb.toString());

        } catch (NumberFormatException e) {
            homoDoseResultado.setText("Preencha o número de medicamentos.");
        }
    }

    /**
     * limpa os campos e resultado da aba dose unica
     */
    @FXML
    private void limparHomoDose() {
        homoDoseNumMeds.setText("");
        homoDoseResultado.setText("");
    }

    /**
     * fecha a tela e volta para a tela principal
     */
    @FXML
    private void fechar() {
        App.trocarTela("principal");
    }
}
