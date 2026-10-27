package org.example;

import java.util.ArrayList;
import java.util.List;

public class PlanoTreino {
    private long id;
    private String nome;
    private String tipo;
    private int duracaoEstimadaMin;
    private boolean ativo = true;
    private List<ExercicioPlano> exercicios = new ArrayList<>();

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public int getDuracaoEstimadaMin() { return duracaoEstimadaMin; }
    public void setDuracaoEstimadaMin(int duracaoEstimadaMin) { this.duracaoEstimadaMin = duracaoEstimadaMin; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public List<ExercicioPlano> getExercicios() { return exercicios; }
    public void setExercicios(List<ExercicioPlano> exercicios) { this.exercicios = exercicios; }
}
