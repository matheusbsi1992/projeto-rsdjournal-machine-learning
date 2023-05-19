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
