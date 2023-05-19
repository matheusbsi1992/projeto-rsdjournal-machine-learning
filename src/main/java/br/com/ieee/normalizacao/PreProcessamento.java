package br.com.ieee.normalizacao;

import br.com.ieee.modelo.Model;
import br.com.ieee.sessao.SessaoSpark;
import lombok.val;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class PreProcessamento {

    private Model model;
    private SessaoSpark sessaoSpark;
    private List<Model> modelList;
    //private Gson gson;
    private Dataset<Row> rowDataset;

    public PreProcessamento() {
        model = new Model();
        sessaoSpark = new SessaoSpark();
        modelList = new ArrayList<Model>();
        //gson = new Gson();
    }

    private Model tipoAtributos(String itemv[]) {
        model.setProtocolVersion(itemv[0]);
        model.setType(itemv[1]);
        model.setToDS(itemv[2]);
        model.setFromDS(itemv[3]);
        model.setMoreFragment(itemv[4]);
        model.setRetry(itemv[5]);
        model.setPowerManagement(itemv[6]);
        model.setMoreData(itemv[7]);
        model.setWep(itemv[8]);
        model.setOrder(itemv[9]);
        model.setDuration(itemv[10]);
        model.setTransmitterAddress(itemv[11]);
        model.setDestinationAddress(itemv[12]);
        model.setSourceAddress(itemv[13]);
        model.setReceiverAddress(itemv[14]);
        model.setBssId(itemv[15]);
        model.setSequenceNumber(itemv[16]);
        model.setInfo(itemv[17]);
        itemv = null;
        return model;
    }

    public List<Model> dadosLeituraConjuntoDadosIEEE80211() {

        String itemv[]      = null;
        Iterator<Row> item  = null;

        try {

            //E--->>> item:0 | item:n (N)
            for (item = lerArquivoConjuntodeDadosIEEE80211().toLocalIterator(); item.hasNext(); ) {

                itemv = (item.next()).toString()
                        //.replaceAll("\"([^\"]+)\"", "\"$1\"")
                        .replace("[", "")
                        .replace("]", "")
                        .replace("\"", "")
                        .split(",");
                model = tipoAtributos(itemv);

                //|-- "Protocol Version","Type","To DS","From DS","More Fragment","Retry","Power Management","More Data","WEP","Order","Duration","Transmitter address","Destination address","Source address","Receiver address","BSS Id","Sequence number","Info": string
                modelList = normalizacaoDados(model);

            }
        transformarDadosemArquivoCSV();
        converterCSVparaARFF();

        } catch (Exception ex) {
            ex.getMessage();
        } finally {
            item        = null;
            itemv       = null;
            model       = null;
            sessaoSpark.fecharSessao();
        }

        return modelList;
    }

    private void converterCSVparaARFF(){
        try{
        String atributosArray[]   = {"protocolVersion, type, toDS, fromDS, moreFragment, retry, powerManagement, moreData, wep, order, duration, transmitterAddress, destinationAddress, sourceAddress, receiverAddress, bssId, sequenceNumber, info"};
        String valorArray = Arrays.toString(atributosArray);

        //CSVLoader csvLoader = new CSVLoader();
        //csvLoader.setSource(new File("F:/preprocessamentoieee80211.csv"));
        //Instances dataset   =   csvLoader.getDataSet();

        BufferedReader bufferedReader = new BufferedReader(
                new FileReader("Arquivos/preprocessamentoieee80211.csv"));

        BufferedWriter bufferedWriter = new BufferedWriter(
                    new FileWriter("Arquivos/preprocessamentoieee80211.arff")
            );

        String inline="";

        String valorInicial= "@relation preprocessamentoieee80211";
        bufferedWriter.write(valorInicial+"\n");

        for(int i=0; i < valorArray.length() - 1;i++){
            if(i>17){
                break;
            }
          val novoValor = valorArray.split(",");
          String valorCorrespondido    = novoValor[i].replace("[","")
                            .replace("]","")
                            .replace("\"","");

            if(i<17){
            bufferedWriter.write("\n");
            bufferedWriter.write("@attribute "+ valorCorrespondido+" REAL");
            bufferedWriter.write("\n");
            }else{
                //-- Os valore que correspondem ao (length) ou algo para retornar o valor, sendo indicado para um Array de posicoes unica
                //--,em uma necessidade especifica para a criacao do atributo tipo class -->>> Ex: info {valor 1 ...n}
                // "{model.getInfo()-->0,1,2,3}";
                Set<String> stringSet = new HashSet<>();
                for (int j=0;j<modelList.size();j++) {
                    stringSet.add(modelList.get(j).getInfo());
                }

                bufferedWriter.write("@attribute " + valorCorrespondido + "{"+stringSet.toString()
                        .replace("[","")
                        .replace("]","")+"}");
                bufferedWriter.write("\n");
                bufferedWriter.write("@data");
                bufferedWriter.write("\n");
                stringSet =null;
            }
        }
        int i=0;
        while ((inline =bufferedReader.readLine())!=null) {

            if(i==0){
            bufferedWriter.write("\n");
            }else{
//            inline = inline//.replaceAll("([a-zA-Z])", "")
//                    //.replace(",","")
//                   // .replace("\"", "")
//                    .replace("[", "")
//                    .replace("]", "");

            bufferedWriter.write(inline + "\n");
            //bufferedWriter.flush();
           }
            i++;
        }

            bufferedWriter.flush();
            bufferedWriter.close();

        }catch (IOException ioException){
            ioException.getMessage();
        }
    }


    private void transformarDadosemArquivoCSV(){
        String auxVetor[]   = {"protocolVersion, type, toDS, fromDS, moreFragment, retry, powerManagement, moreData, wep, order, duration, transmitterAddress, destinationAddress, sourceAddress, receiverAddress, bssId, sequenceNumber, info"};
        String novoTexto    =   "";
        try{

            FileWriter file = new FileWriter("Arquivos/preprocessamentoieee80211.csv");

            novoTexto    = Arrays.toString(auxVetor)
                    //.replaceAll("([a-zA-Z])", "")
                    .replace("[","")
                    .replace("]","")
                    .replace("'","");
            //.replace(",","");

            for (int i=0; i< modelList.size();i++){
                if(i==0){
                    file.write(novoTexto);
                    file.write("\n");


                }else{

                    String valorAux[] = modelList.get(i)
                            .toString()
                            .replaceAll("([a-zA-Z])","")
                            .replace("(","")
                            .replace(")","")
                            .replace("=","")
                            .split(",");

                    file.write(Arrays.asList(valorAux)
                            .stream()
                            .collect(Collectors.joining(",")));

                    file.write("\n");

                }
            }
            file.close();
        }catch (Exception ex){
            ex.getMessage();
        }finally {
        novoTexto=null;
        auxVetor =null;
        }
    }

    private List<Model> normalizacaoDados(Model modelo) {

        //|-- "Protocol Version","Type","To DS","From DS","More Fragment","Retry","Power Management","More Data","WEP","Order","Duration","Transmitter address","Destination address","Source address","Receiver address","BSS Id","Sequence number","Info": string
        try {

            //--Protocol Version - 0
            if (modelo.getProtocolVersion().equalsIgnoreCase("802.11")) {
                modelo.setProtocolVersion("1");
            } else {
                modelo.setProtocolVersion("0");
            }
            //---Protocol Version
            //--Type - 1
            if (modelo.getType().equalsIgnoreCase("Management frame")
                    || modelo.getType().equalsIgnoreCase("Control frame")
                    || modelo.getType().equalsIgnoreCase("Data frame")) {
                modelo.setType("1");
            } else {
                modelo.setType("0");
            }
            //---Type
            //--To DS - 2
            if (modelo.getToDS().equalsIgnoreCase("Frame is not entering DS")
                    || modelo.getToDS().trim().length() == 0) {
                modelo.setToDS("0");
            } else {
                modelo.setToDS("1");
            }
            //---To DS
            //--From DS - 3
            if (modelo.getFromDS().equalsIgnoreCase("Frame is not exiting DS")
                    || modelo.getFromDS().trim().length() == 0) {
                modelo.setFromDS("0");
            } else {
                modelo.setFromDS("1");
            }
            //---More Fragment - 4
            if (modelo.getMoreFragment().equalsIgnoreCase("This is the last fragment")
                    || modelo.getMoreFragment().trim().length() == 0) {
                modelo.setMoreFragment("0");
            } else {
                modelo.setMoreFragment("1");
            }
            //---More Fragment
            //---Retry - 5
            if (modelo.getRetry().equalsIgnoreCase("Frame is not being retransmitted")
                    || modelo.getRetry().trim().length() == 0) {
                modelo.setRetry("0");
            } else {
                modelo.setRetry("1");
            }
            //---Retry
            //---Power Management - 6
            if (modelo.getPowerManagement().equalsIgnoreCase("STA will stay up")
                    && modelo.getPowerManagement().trim().length() > 0) {
                modelo.setPowerManagement("1");
            } else {
                modelo.setPowerManagement("0");
            }
            //---Power Management
            //---More Data - 7
            if (modelo.getMoreData().equalsIgnoreCase("No data buffered")
                    || modelo.getMoreData().trim().length() == 0) {
                modelo.setMoreData("0");
            } else {
                modelo.setMoreData("1");
            }
            //---Power Management
            //---Dados criptografados - WEP - 8
            if (modelo.getWep().equalsIgnoreCase("Data is not protected")
                    || modelo.getWep().trim().length() == 0) {
                modelo.setWep("0");
            } else {
                modelo.setWep("1");
            }
            //---Dados criptografados - WEP
            //---Order - 9
            if (modelo.getOrder().equalsIgnoreCase("Not strictly ordered")
                    || modelo.getOrder().trim().length() == 0) {
                modelo.setOrder("0");
            } else {
                modelo.setOrder("1");
            }
            //---Order
            //---Duration - 10
            if (modelo.getDuration().equalsIgnoreCase("0")) {
                modelo.setDuration("0");
            } else {
                modelo.setDuration("1");
            }
            //---Duration
            //---Transmitter address - 11
            if (modelo.getTransmitterAddress().trim().length() == 0 || modelo.getTransmitterAddress().equalsIgnoreCase("00:00:00:00:00:00")) {
                modelo.setTransmitterAddress("0");
            } else {
                modelo.setTransmitterAddress("1");
            }
            //---Transmitter address
            //---Destination address - 12
            if (modelo.getDestinationAddress().trim().length() == 0 || modelo.getDestinationAddress().equalsIgnoreCase("00:00:00:00:00:00")) {
                modelo.setDestinationAddress("0");
            } else {
                modelo.setDestinationAddress("1");
            }
            //---Transmitter address
            //---Source address - 13
            if (modelo.getSourceAddress().trim().length() == 0 || modelo.getSourceAddress().equalsIgnoreCase("00:00:00:00:00:00")) {
                modelo.setSourceAddress("0");
            } else {
                modelo.setSourceAddress("1");
            }
            //---Source address
            //---Receiver address - 14
            if (modelo.getReceiverAddress().trim().length() == 0 || modelo.getReceiverAddress().equalsIgnoreCase("00:00:00:00:00:00")) {
                modelo.setReceiverAddress("0");
            } else {
                modelo.setReceiverAddress("1");
            }
            //---Receiver address
            //---BSS Id - 15
            if (modelo.getBssId().trim().length() == 0 || modelo.getBssId().equalsIgnoreCase("00:00:00:00:00:00")) {
                modelo.setBssId("0");
            } else {
                modelo.setBssId("1");
            }
            //---BSS Id
            //---Sequence number - 16
            if (modelo.getSequenceNumber().equalsIgnoreCase("0")) {
                modelo.setSequenceNumber("0");
            } else {
                modelo.setSequenceNumber("1");
            }
            //---Sequence number
            //---Info - 17 -> | 1: Deauth | 2: Beacon Flood | 3: Logoff | 0: Normal |
            if (modelo.getInfo().equalsIgnoreCase("Deauthentication")) {
               modelo.setInfo("1");
               // modelo.setInfo("1");
            } else if (modelo.getSequenceNumber().equalsIgnoreCase("0") && modelo.getInfo().equalsIgnoreCase("Beacon frame")) {
               modelo.setInfo("2");
               // modelo.setInfo("1");
            } else if (modelo.getInfo().equalsIgnoreCase("Logoff")) {
               modelo.setInfo("3");
                //modelo.setInfo("1");
            } else {
                modelo.setInfo("0");
            }
            //---Info

            //--adiciona ao modelo
            modelList.add(new Model(modelo.getProtocolVersion(),
                    modelo.getType(),
                    modelo.getToDS(),
                    modelo.getFromDS(),
                    modelo.getMoreFragment(),
                    modelo.getRetry(),
                    modelo.getPowerManagement(),
                    modelo.getMoreData(),
                    modelo.getWep(),
                    modelo.getOrder(),
                    modelo.getDuration(),
                    modelo.getTransmitterAddress(),
                    modelo.getDestinationAddress(),
                    modelo.getSourceAddress(),
                    modelo.getReceiverAddress(),
                    modelo.getBssId(),
                    modelo.getSequenceNumber(),
                    modelo.getInfo()
            ));

        } catch (Exception exception) {
            exception.getMessage();
        }
        return modelList;
    }

    private Dataset<Row> lerArquivoConjuntodeDadosIEEE80211() {

        try {
            rowDataset = sessaoSpark
                    .abrirSessao()
                    .read()
                    .format("csv")
                    .option("header", true)
                    .option("inferSchema", true)
                    .option("delimiter", ",")
                    .option("escape", "\"")
                    //.option("multiline",true)
                    //.option("quote"," ")
                    //.option("quote","\"")
                    //.option("sep",",")
                    .csv(
                             "Arquivos/ATAQUE_DEAUTH_5094.csv"
                            ,"Arquivos/ATAQUE_BEACON_FLOOD_1047.csv"
                             ,"Arquivos/ATAQUE_EAPOL_start_1429.csv"
                             ,"Arquivos/ATAQUE_NORMAL_9134.csv");
            //rowDataset.show(false);
            //rowDataset.printSchema();
        } catch (Exception ex) {
            ex.getMessage();
        }
        return rowDataset;
    }

    //-----Verificar posteriormente --- Error IllegalArgument
    //-------Verificar posteriormente

//    private void transformarDadosemArquivoJson() {
//
//        try {
//            String valorJson = gson.toJson(modelList);
//            FileWriter file = new FileWriter("F:/output.json");
//            file.write(valorJson);
//            file.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//
//    public Dataset<Row> dadosNovosPreprocessados() {
//        dadosLeitura();
//        //transformarDadosemArquivoJson();
//
//        //-----Biblioteca SVM:https://github.com/rzo1/zlibsvm
//
//        String schema = "bssId" +
//                ", destinationAddress" +
//                ", duration" +
//                ", fromDS" +
//                ", info" +
//                ", moreData" +
//                ", moreFragment" +
//                ", order" +
//                ", powerManagement" +
//                ", protocolVersion" +
//                ", receiverAddress" +
//                ", retry" +
//                ", sequenceNumber" +
//                ", sourceAddress" +
//                ", toDS" +
//                ", transmitterAddress" +
//                ", type" +
//                ", wep";
//
////        Encoder<Model> employeeEncoder = Encoders.bean(Model.class);
////        String jsonPath = "F:/output.json";
////        Dataset<Model> rowDataSet= sessaoSpark.abrirSessao().read().json(jsonPath).as(employeeEncoder);
////        rowDataSet.show();
////        rowDataSet.printSchema();
////        rowDataSet.summary().show();
//
//        Dataset<Row> dadosFeatures = sessaoSpark
//                .abrirSessao()
//                .read()
//                .format("csv")
//                //.schema(schema)
//               .option("header",true)
//               .option("inferSchema",true)
//                //.option("inferSchema",true)
//                .csv("F:/destino.csv");
//
//        dadosFeatures.printSchema();
//        dadosFeatures.show();
//
//        VectorAssembler vectorAssembler = new VectorAssembler();
//        vectorAssembler
//                .setInputCols(new String[]{"protocolVersion"
////                        ,"type"
//                        , "toDS"
//                        , "fromDS"
//                        , "moreFragment"})
//                .setOutputCol("features");
//
//        Dataset<Row> modelDataset = vectorAssembler
//                .transform(dadosFeatures)
//                .select("info","features")
//                .withColumnRenamed("info","label");
//        modelDataset.show();
//
////
////
////        // Load training data
////        Dataset<Row> dataFrame =
////                sessaoSpark.abrirSessao().read().format("libsvm").load("F:/output.json");
////// Split the data into train and test
////        Dataset<Row>[] splits = dados.randomSplit(new double[]{0.6, 0.4}, 1234L);
////        Dataset<Row> train = splits[0];
////        Dataset<Row> test = splits[1];
////
////// create the trainer and set its parameters
////        NaiveBayes nb = new NaiveBayes();
////
////// train the model
////        NaiveBayesModel model = nb.fit(train);
////
////// Select example rows to display.
////        Dataset<Row> predictions = model.transform(test);
////        predictions.show();
////
////// compute accuracy on the test set
////        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
////                .setLabelCol("label")
////                .setPredictionCol("prediction")
////                .setMetricName("accuracy");
////        double accuracy = evaluator.evaluate(predictions);
////        System.out.println("Test set accuracy = " + accuracy);
////
////        //dados.summary().show();
//
//
//
//  //              .handleInvalid();
//
////        PCAModel pca = new PCA()
////      .setInputCol("Diabetes")
////      .setOutputCol("features")
////                .setK(3)
////                .fit(dados);
////
////        Dataset<Row> result = pca.transform(dados).select("Diabetes");
////        result.show(true);
////
////        val vecAssembler = new VectorAssembler().
////                setInputCols(new String[]{"Insulin"}).
////                setOutputCol("features");
////        val features = vecAssembler.transform(dados);
////        features.show();
//
//
////         dadosFeatures = vectorAssembler
////                .transform(dados)
////                .select("Diabetes","features")
////                .withColumnRenamed("Diabetes","label");
////        dadosFeatures.show();
//
////            rowDataset.show();
////            rowDataset.printSchema();
////            rowDataset.summary().show();
////        JSONNode jsonNode = new JSONNode();
////        String valor = gson.toJson(modelList);
////        jsonNode.addArray(valor);
////        Instances instances = JSONInstances.toHeader(jsonNode,true);
////        instances.
//
////     schemaDads
//
////        VectorAssembler assembler = new VectorAssembler()
////                .setInputCols(rowDataset.columns())
////                .setOutputCol("features");
////
////        Dataset<Row> output = assembler.transform(rowDataset);
////        output.show(false);
////        output.printSchema();
////        output.summary().show(false);
//
////        VectorAssembler vectorAssembler = new VectorAssembler();
////        vectorAssembler
////                .setInputCols(new String[]{"bssId" +
////                        ", destinationAddress" +
////                        ", duration" +
////                        ", fromDS" +
////                        ", moreData" +
////                        ", moreFragment" +
////                        ", order" +
////                        ", powerManagement" +
////                        ", protocolVersion" +
////                        ", receiverAddress" +
////                        ", retry" +
////                        ", sequenceNumber" +
////                        ", sourceAddress" +
////                        ", toDS" +
////                        ", transmitterAddress" +
////                        ", type" +
////                        ", wep"})
////                .setOutputCol("features");
////
////       Dataset<Row> n= rowDataset.na().fill("info");
////       n.show();
////        Dataset<Model> modelDataset = vectorAssembler
////                .transform(rowDataSet)
////                .select("info","features")
////                .withColumnRenamed("info","label")
////                .as(employeeEncoder);
////
////        modelDataset.show();
//
//        //https://stackoverflow.com/questions/62467799/exception-in-thread-main-org-apache-spark-sql-analysisexception-cannot-resolv
//        //https://adinasarapu.github.io/posts/2020/08/blog-post-spark-mllib/
//        //https://gist.github.com/IgorBerman/8175eddd27860f05e258584c0d64b963
//        StructType schemaDadosUsados = new StructType()
//                .add("bssId" ,DataTypes.StringType,false)
//                .add("destinationAddress", DataTypes.StringType,false)
//                .add("duration",DataTypes.StringType,false)
//                .add("fromDS",DataTypes.StringType,false)
//                .add("info",DataTypes.StringType,false)
//                .add("moreData" ,DataTypes.StringType,false)
//                .add("moreFragment" ,DataTypes.StringType,false)
//                .add("order",DataTypes.StringType,false)
//                .add("powerManagement",DataTypes.StringType,false)
//                .add("protocolVersion",DataTypes.StringType,false)
//                .add("receiverAddress",DataTypes.StringType,false)
//                .add("retry" ,DataTypes.StringType,false)
//                .add("sequenceNumber",DataTypes.StringType,false)
//                .add("sourceAddress",DataTypes.StringType,false)
//                .add("toDS",DataTypes.StringType,false)
//                .add("transmitterAddress",DataTypes.StringType,false)
//                .add("type",DataTypes.StringType,false)
//                .add("wep",DataTypes.StringType,false);
////        // Subset Dataset
////        Dataset<Model> df_sample3 = rowDataset.selectExpr("CAST(info as String) as value")
////                .select(functions.from_json(functions.col("value"), schemaDadosUsados)
////                .as("json"))
////                .select("json.*")
////                .as(Encoders.bean(Model.class));
////        df_sample3.show();
////        df_sample3.printSchema();
//
//        Dataset<Model> select = rowDataset
//                .select(rowDataset.col(schema),functions.rpad(rowDataset.col("info"), 7,"0")
//                        .as("SalaryWithRightPadding"))
//                .as(Encoders.bean(Model.class));
//        //rowDataset=null;
//        select.show();
//
//        //df_sample3.first().getInfo();
//        // VectorAssembler is a transformer that combines a given list of columns into a single vector column
//        // We want to combine Genetic and Age into a single feature vector called features and use it to predict label (Disease) or not.
//        String[] myStrings = {"info"};
//        VectorAssembler VA = new VectorAssembler().setInputCols(myStrings).setOutputCol("features");
//        rowDataset = VA.transform(select);
//        rowDataset.show();
//
//
////        VectorAssembler vectorAssembler = new VectorAssembler()
////                .setInputCols(new String[]{"info"})
////                .setOutputCol("features");
//
////        Dataset<Row> dadosFatures = vectorAssembler
////                .transform(novoDaTaSet)
////                .select("features")
////                .withColumnRenamed("info","label");
//
////        Dataset<Row>          vectorData        = vectorAssembler.transform(rowDataset);
////        LinearRegression lr                     = new LinearRegression();
////        LinearRegressionModel lrModel           = lr.fit(modelDataset); // issue here
//
////        System.out.println(lrModel.getLoss());
//
////        Dataset<Row>[] splits = df_sample3.randomSplit(new double[]{0.7, 0.3}, 1234L);
////        Dataset<Row> train = splits[0];
////        Dataset<Row> test = splits[1];
////
////        // create the trainer and set its parameters
////        NaiveBayes nb = new NaiveBayes();
////
////        // train the model
////        NaiveBayesModel model = nb.fit(train);
////
////        // Select example rows to display.
////        Dataset<Row> predictions = model.transform(test);
////        predictions.show();
//
////
////        GeneralizedLinearRegression glr = new GeneralizedLinearRegression()
////                .setFamily("gaussian")
////                .setLink("identity")
////                .setMaxIter(10)
////                .setRegParam(0.3);
////
////        // Fit the model
////        GeneralizedLinearRegressionModel model = glr.fit(rowDataset);
////
////        // Print the coefficients and intercept for generalized linear regression model
////        System.out.println("Coefficients: " + model.coefficients());
////        System.out.println("Intercept: " + model.intercept());
//
//
////
////        Dataset<Row> rowDatasetValor = vectorAssembler.transform(rowDataset)
////                .select("info","features")
////                .withColumnRenamed("info","label");
////
////        rowDatasetValor.show(false);
//
//
//        String itemv[] = null;
//        int count0 = 0, count1 = 0, count2 = 0, count3 = 0;
//        for (Iterator<Row> itemr = rowDataset.toLocalIterator(); itemr.hasNext(); ) {
//
//            itemv = (itemr.next()).toString()
//                    //.replaceAll("\"([^\"]+)\"", "\"$1\"")
//                    .replace("[", "")
//                    .replace("]", "")
//                    .replace("\"", "")
//                    .split(",");
//
//            System.out.println("ENTROU AQUI---->>>" + itemv[4] + "<<<<<-----INFO");
//            if (itemv[4].equalsIgnoreCase("0")) {
//                count0++;
//            }
//            if (itemv[4].equalsIgnoreCase("1")) {
//                count1++;
//            }
//            if (itemv[4].equalsIgnoreCase("2")) {
//                count2++;
//            }
//            if (itemv[4].equalsIgnoreCase("3")) {
//                count3++;
//            }
//
//
//        }
//        //---Info - 17 -> | 1: Deauth | 2: Beacon Flood | 3: Logoff | 0: Normal |
//        System.out.println("Normal:--->>>" + count0);
//        System.out.println("Deauth:--->>>" + count1);
//        System.out.println("Beacon flood:--->>>" + count2);
//        System.out.println("Eapol:--->>>" + count3);
//        val total = count0 + count1 + count2 + count3;
//        ;
//        System.out.println("TOTAL---->>>>" + total);
//        //Encoder<Model> personEncoder = Encoders.bean(Model.class);
//        System.out.println("=======================================================================");
//        rowDataset.show(false);
//        rowDataset.printSchema();
////                System.out.println("ENTROU AQUI!!!!!");
////                //}
////            }
//        return rowDataset;
//    }


}