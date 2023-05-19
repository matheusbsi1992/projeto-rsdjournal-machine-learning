<h1 align="center"> Projeto ML IEEE 802.11 Métricas de Desempenho</h1>

<h4 align="justify">
A segurança presente em redes IEEE 802.11 faz
-
se diariamente mais relevante. Porém, a segurança n
a rede IEEE 
802.11 não tem acompanhado as ameaças com tanta significância. Por este motivo surge a proposta deprojetar um 
Sistema  de  Detecção  de  Intrusão
-
IDS  baseada  em  aprendizagem  de  máquina  que  será  capaz  de  possuir  auto
-
aperfeiçoamento,  visto  que,  irá 
criar  um  ambiente  seguro,  capaz  de  detectar  todas  as  ameaças  dissimuladas,
Deauthentication, 
EAPOL
-
Logoff 
e
Beacon Flood,
em que foram lançadas em uma rede corporativa real. 
Com
isto, 
correlacionado as métricas de desempenho, e entre uma delas, que preza pela qualidade da classificação, o 
<i>Matthews 
  Correlation Coefficient</i>.
A anomalia 
Deauthentication
acima do classificador 
Naive Bayes
foi obtido de (
88,71%
), já a 
valia  de  qualidade
do  classificador 
Logistic Regression
(
Logistic
)  equacionado  a  (
88,69%)
,  e  não
obstante,  o 
J48
apresentou um valor menor de (
88,47
%). Apesar disso, a identificação do ataque 
Beacon Flood
, se deu por conta do 
algoritmo 
Naive Bayes
exibindo a maior taxa de
de
tecção  (
100,00
%),  seguido  do 
Logistic
(
99,95
%)  e 
J48 
possuindo   o   menor   valor   (
98,85
%).   Conseguinte,   na   detecção   da   anomalia   EAPOL
-
Logoff
,   os 
classificadoresapresentaram similitude de (100,00%) e a demais, com a apresentação de uma detecção, em virtude de 
d
ados nãoanômalos (Normal), o 
Naive Bayes
foi acometido de (
89,92%
), seguido do 
Logistic
mantendo (
89,89%),
enquanto, o 
J48
foi testado com uma taxa menor de (
89,67
%). Com as evidências do estudo proveem a possibilidade 
de que épossível desenvolver um siste
ma de detecção de intrusão baseado em redes 
wireless
.
<h4 align="justify">
  
  ## ✔️ Tecnologias utilizadas

  
    <dependencies>
        <!-- https://mvnrepository.com/artifact/org.apache.spark/spark-core -->
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-core_2.13</artifactId>
            <version>3.3.1</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.apache.spark/spark-mllib -->
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-mllib_2.13</artifactId>
            <version>3.3.1</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.24</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/nz.ac.waikato.cms.weka/weka-stable -->
        <dependency>
            <groupId>nz.ac.waikato.cms.weka</groupId>
            <artifactId>weka-stable</artifactId>
            <version>3.8.6</version>
        </dependency>

    </dependencies>
  
  ## Classe Principal de Reconhecimento das Técnicas e Métricas de Desempenho aplicados ao Estudo
  
  

```java
  
  
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

                  
```
