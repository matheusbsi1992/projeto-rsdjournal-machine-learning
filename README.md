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
  
  ## Converter arquivo .CSV para .ARFF
  
  
  <H2> Reconhecimento do merge de arquivos </H2>
```java
  
  
  
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
                  
```
