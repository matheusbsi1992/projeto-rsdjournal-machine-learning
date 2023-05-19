package br.com.ieee.classificacao;

import br.com.ieee.metricas.Processamento;
import lombok.val;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassesClassificadas {


    private static Processamento processamento = new Processamento();


    public void processamentodeDadosIEEE80211() throws Exception {

        Instances[] instances   = processamento.divisaoTreinoeTeste(processamento.dataInstanciaInfo(), 30);
        Instances treino        = instances[0];
        Instances teste         = instances[1];

        long tempoinicial;
        long tempofinal;
        double tempoTotal=0;

        Evaluation evaluation;
        List<Double> resultado;

        int countLine = 0;

        for (int i = 0; i < processamento.classificadoresWeka().length; i++) {
            resultado = new ArrayList<>();
            tempoinicial = System.currentTimeMillis();
            evaluation = processamento.classificao(processamento.classificadoresWeka()[i], treino, teste);

            for (Map.Entry<Integer, List<Double>> valoresCorrespondidos : processamento.evaluationResultados(evaluation).entrySet()) {

                int classe=valoresCorrespondidos.getKey();
                resultado =valoresCorrespondidos.getValue();

                System.out.println("\n---------------------------------------------------------------------------------");

                if(classe==0){
                    System.out.println("NORMAL de " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ""
                            + "\n---------------------------------------------------------------------------------");
                }
                if(classe==1){
                    System.out.println("DEAUTH de " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ""
                            + "\n---------------------------------------------------------------------------------");
                }
                if(classe==2){
                    System.out.println("BEACON FLOOD de " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ""
                            + "\n---------------------------------------------------------------------------------");
                }
                if(classe==3){
                    System.out.println("EAPOL de " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ""
                            + "\n---------------------------------------------------------------------------------");
                }

                System.out.println("\n---------------------------------------------------------------------------------");
                processamento.setVP(resultado.get(countLine++));
                System.out.println("VP de " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ": "
                        + processamento.getVP()
                        + "\n---------------------------------------------------------------------------------");
                processamento.setVN(resultado.get(countLine++));
                System.out.println("VN de " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ": "
                        + processamento.getVN()
                        + "\n---------------------------------------------------------------------------------");
                processamento.setFP(resultado.get(countLine++));
                System.out.println("FP de " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ": "
                        + processamento.getFP()
                        + "\n---------------------------------------------------------------------------------");
                processamento.setFN(resultado.get(countLine++));
                System.out.println("FN de " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ": "
                        + processamento.getFN()
                        + "\n---------------------------------------------------------------------------------");
                System.out.println("Acuracia de " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ": "
                        + String.format("%.2f%%", processamento.acuracia()*100)
                        + "\n---------------------------------------------------------------------------------");
                System.out.println("Precisao de " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ": "
                        + String.format("%.2f%%", processamento.precisao()*100)
                        + "\n---------------------------------------------------------------------------------");
                System.out.println("Recall de " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ": "
                        + String.format("%.2f%%", processamento.recall()*100)
                        + "\n---------------------------------------------------------------------------------");
                System.out.println("F-Measure de " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ": "
                        + String.format("%.2f%%", processamento.fMedida()*100)
                        + "\n---------------------------------------------------------------------------------");
                System.out.println("Taxa de Alarme Falso de " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ": "
                        + String.format("%.2f%%", processamento.taxaAlarmeFalso()*100)
                        + "\n---------------------------------------------------------------------------------");
                System.out.println("Taxa de Verdadeiro Positivo de " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ": "
                        + String.format("%.2f%%", processamento.taxaVerdadeiroPositivo()*100)
                        + "\n---------------------------------------------------------------------------------");
                System.out.println("Taxa de Falso Positivo de " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ": "
                        + String.format("%.2f%%", processamento.taxaFalsoPositivo()*100)
                        + "\n---------------------------------------------------------------------------------");
                processamento.setAreaROC(resultado.get(countLine++));
                System.out.println("ROC Area de " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ": "
                        + String.format("%.2f%%", processamento.getAreaROC()*100)
                        + "\n---------------------------------------------------------------------------------");
                System.out.println("MCC de " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ": "
                        + String.format("%.2f%%", processamento.mcc()*100)
                        + "\n---------------------------------------------------------------------------------");
                System.out.println("Total de  " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ": "
                        + processamento.valorTotal()
                        + "\n---------------------------------------------------------------------------------");

                tempofinal = System.currentTimeMillis();

                System.out.printf("Tempo decorrido: %.2f ms %n", (tempofinal - tempoinicial) / 100d);

                tempoTotal+=((tempofinal - tempoinicial) / 100d);

                processamento.limparValores();
                countLine=0;

            }
            System.out.printf("Tempo Total: %.2f ms %n", tempoTotal,
                    "de " + processamento.classificadoresWeka()[i].getClass().getSimpleName() + ": ");
            tempoTotal=0;

        }

        evaluation = null;
    }

}

