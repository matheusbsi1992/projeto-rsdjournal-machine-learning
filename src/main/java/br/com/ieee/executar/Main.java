package br.com.ieee.executar;

import br.com.ieee.classificacao.ClassesClassificadas;
import br.com.ieee.metricas.Processamento;

public class Main  {

    private static ClassesClassificadas classeficacoesAnalisar = new ClassesClassificadas();

    public static void main(String[] args) throws Exception {
        classeficacoesAnalisar.processamentodeDadosIEEE80211();
    }
}