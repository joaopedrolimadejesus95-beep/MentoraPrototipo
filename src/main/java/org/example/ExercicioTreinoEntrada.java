package org.example;

import java.math.BigDecimal;

public class ExercicioTreinoEntrada {
    private final String nome;
    private final Integer series;
    private final Integer repeticoes;
    private final BigDecimal carga;

    public ExercicioTreinoEntrada(String nome, Integer series, Integer repeticoes, BigDecimal carga) {
        this.nome = nome;
        this.series = series;
        this.repeticoes = repeticoes;
        this.carga = carga;
    }

    public String getNome() { return nome; }
    public Integer getSeries() { return series; }
    public Integer getRepeticoes() { return repeticoes; }
    public BigDecimal getCarga() { return carga; }
}
