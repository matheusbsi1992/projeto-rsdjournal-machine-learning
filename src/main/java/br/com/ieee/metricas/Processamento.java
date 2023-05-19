package br.com.ieee.metricas;

import br.com.ieee.normalizacao.PreProcessamento;
import lombok.Data;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.sql.catalyst.util.SQLOrderingUtil;
import org.jetbrains.annotations.NotNull;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.SingleClassifierEnhancer;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomialUpdateable;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.trees.J48;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.RandomForest;
import weka.core.*;
import lombok.val;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Randomize;
import weka.filters.unsupervised.instance.RemovePercentage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

@Data
public class Processamento {

    private PreProcessamento preProcessamento;
    private static Evaluation evaluation;


    private  double VP = 0;
    private  double VN = 0;
    private  double FP = 0;
    private  double FN = 0;
    private double areaROC = 0;

    public Processamento(){
        preProcessamento = new PreProcessamento();
    }

    public Instances dataInstanciaInfo(){
        preProcessamento.dadosLeituraConjuntoDadosIEEE80211();

        Instances dataInstancesInfo =null;
        try{
            DataSource dataSource = new DataSource("Arquivos/preprocessamentoieee80211.arff");
            dataInstancesInfo   = dataSource.getDataSet();
            dataInstancesInfo.setClassIndex(dataInstancesInfo.numAttributes()-1);

        }catch (Exception ex){
            ex.getMessage();
        }
        return dataInstancesInfo;
    }

    public static Classifier[] classificadoresWeka() throws Exception{
        Classifier[] classifiers ={new NaiveBayes()
                ,new Logistic()
                ,new J48()
        };
        return  classifiers;
    }

    public static Instances[] divisaoTreinoeTeste(Instances data, double numerodePassagens) throws Exception{
        // Randomize data
        Randomize rand = new Randomize();
        rand.setInputFormat(data);
        rand.setRandomSeed((int)numerodePassagens);
        data = Filter.useFilter(data, rand);
        // Remove testpercentage from data to get the train set
        RemovePercentage rp = new RemovePercentage();
        rp.setInputFormat(data);
        rp.setPercentage(numerodePassagens);
        Instances train = Filter.useFilter(data, rp);
        // Remove trainpercentage from data to get the test set
        rp = new RemovePercentage();
        rp.setInputFormat(data);
        //70% treino e 30% teste de forma aleatoria
        rp.setPercentage(numerodePassagens);
        rp.setInvertSelection(true);
        Instances test = Filter.useFilter(data, rp);
        return new Instances[]{train, test};
    }

    public Evaluation classificao(Classifier model
            , Instances trainingSet
            , Instances testingSet) throws Exception {

        evaluation = new Evaluation(trainingSet);

        model.buildClassifier(trainingSet);

        evaluation.evaluateModel(model, testingSet);

        evaluation.crossValidateModel(model, trainingSet, Math.min(10,testingSet.size()),new Random(1));

        return evaluation;
    }


   public Map<Integer, List<Double>> evaluationResultados(Evaluation eval)throws Exception {

       //double[] vals = new double[(int)eval.numInstances()];

       //int offset = 11;

       Map<Integer, List<Double>> valoresVals = new HashMap<Integer, List<Double>>();

       if (eval.getHeader().classAttribute().isNominal()) {
         //   vals[offset++]      =eval.kappa();
            String nome         =eval.getHeader().classAttribute().name();//nome da classe
            Attribute attribute =eval.getHeader().classAttribute();//identificacao dos atributos do tipo da classe
            for (int i = 0; i < eval.getHeader().classAttribute().numValues(); i++) {

                int numeroValorClasse = eval.getHeader().classAttribute().numValues();//quantidade de atributos da classe

//                vals[offset++] = eval.truePositiveRate(i);
//                vals[offset++] = eval.falseNegativeRate(i);
//                vals[offset++] = eval.precision(i);
//                vals[offset++] = eval.recall(i);
//                vals[offset++] = eval.fMeasure(i);
//                vals[offset++] = eval.areaUnderROC(i);
//                vals[offset++] = eval.areaUnderPRC(i);
//                vals[offset++] = eval.numTruePositives(i);
//                vals[offset++] = eval.numFalsePositives(i);
//                vals[offset++] = eval.numTrueNegatives(i);
//                vals[offset++] = eval.numFalseNegatives(i);

                VP=(eval.numTruePositives(i));


                VN=(eval.numTrueNegatives(i));


                FP=(eval.numFalsePositives(i));


                FN=(eval.numFalseNegatives(i));


                areaROC=(eval.areaUnderROC(i));

                valoresVals.put(i,new ArrayList<Double>());

                //VP
                valoresVals.get(i).add(VP);
                //VN
                valoresVals.get(i).add(VN);
                //FP
                valoresVals.get(i).add(FP);
                //FN
                valoresVals.get(i).add(FN);
                //Area ROC
                valoresVals.get(i).add(areaROC);
            }
        }
        return valoresVals;
    }


    //--metricas de desempenho
    public double acuracia(){
        //verdadeiroPositivo = new BigDecimal(VP).add(new BigDecimal(VN));
        //verdadeiroPositivo=verdadeiroPositivo.divide(new BigDecimal(FP+VP+VN+FN),3,RoundingMode.UP);
        //verdadeiroNegativo = new BigDecimal(FP).add(new BigDecimal(VP+VN+FN));
        return (VP+VN)/(FP+VP+VN+FN);
    }
    public double precisao(){
        return (VP)/(VP+FP);
    }
    public double recall(){
        return (VP)/(VP+FN);
    }
    public double fMedida(){
        return (2*(precisao()*recall())/(precisao()+recall()));
    }
    public double taxaVerdadeiroPositivo(){
        return VP/(VP+FN);
    }
    public double taxaFalsoPositivo(){
        return FP/(FP+VN);
    }
    public double taxaAlarmeFalso(){
        return  (FP+FN)/VP;
    }
    public double mcc(){
        double equacaoRaiz=0;
        equacaoRaiz=(VP + FP)*(VP + FN)*(VN + FP)*(VN + FN);
        return ((VP * VN)-(FP * FN))/Math.sqrt(equacaoRaiz);
    }
    public double valorTotal(){
        return VP+VN+FP+FN;
    }
    //--encerra os metodos de metrica de desempenho;
    public void limparValores(){
        setVP(0);
        setFN(0);
        setVN(0);
        setFP(0);
        setAreaROC(0);
    }

}
