<h1 align="center"> Projeto NIDS</h1>

<h4 align="justify">
Apresentação de uma abordagem baseada em técnicas de aprendizagem de máquina aplicadas à busca de ameaças, no objetivo de tentar detectar uma intrusão e portanto ajudar a prevenir que ataques ocorram. Foram testados algoritmos para classificação de ataques na rede, com três técnicas distintas: árvores de decisão, tabelas de decisão e Naive Bayes. A eficácia de cada técnica se deu por meio de experimentos usando a base de dados KDD'99. A avaliação do estudo se baseia na matriz de confusão que através de uma pequena porção (cerca de 10%) da base de dados obteve uma acurácia, precisão e recall acima de 80% sobre os classificadores analisados, afirmando a viabilidade de máquinas de aprendizagem em busca de classificação de anomalias em redes do tipo wireless.
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
