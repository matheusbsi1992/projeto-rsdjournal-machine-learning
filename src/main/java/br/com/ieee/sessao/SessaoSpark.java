package br.com.ieee.sessao;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.SparkSession;

public class SessaoSpark {

    public SparkSession abrirSessao() {
        SparkSession sparkSession = SparkSession.builder().appName("Projeto IEEE802.11").master("local").getOrCreate();

        Logger.getLogger("org.apache").setLevel(Level.WARN);

        return sparkSession;
    }

    /*Metodo para encerrar a sessao*/
    public void fecharSessao() {
        try {
            abrirSessao().close();
        } catch (Exception exception) {
            exception.getMessage();
        }
    }

}
