package org.example;

public class TreinoDoDia {
    private final long id;
    private final String tipo;
    private final boolean concluida;
    private final String exercicios;
    private final String observacao;

    public TreinoDoDia(long id, String tipo, boolean concluida, String exercicios, String observacao) {
        this.id = id;
        this.tipo = tipo;
        this.concluida = concluida;
        this.exercicios = exercicios;
        this.observacao = observacao;
    }

    public long getId() { return id; }
    public String getTipo() { return tipo; }
    public boolean isConcluida() { return concluida; }
    public String getExercicios() { return exercicios; }
    public String getObservacao() { return observacao; }
}
