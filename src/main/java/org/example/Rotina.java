package org.example;
import java.time.LocalDate;

public class Rotina {
    private int idRotina;
    private String nome;              // ex: "Beber água", "Malhar"
    private String descricao;
    private String frequencia;        // "DIARIA", "SEMANAL", etc.
    private LocalDate dataCriacao;
    private boolean obrigatorio;      // se false, pular um dia não quebra o streak
    private int streakAtual;
    private int melhorStreak;
    private int nivel;
    private int xpAtual;
    private int xpParaProximoNivel;

    //setters

    public void setIdRotina(int idRotina) {
        this.idRotina = idRotina;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public void setFrequencia(String frequencia) {
        this.frequencia = frequencia;
    }
    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    public void setObrigatorio(boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }
    public void setStreakAtual(int streakAtual) {
        this.streakAtual = streakAtual;
    }
    public void setMelhorStreak(int melhorStreak) {
        this.melhorStreak = melhorStreak;
    }
    public void setNivel(int nivel) {
        this.nivel = nivel;
    }
    public void setXpAtual(int xpAtual) {
        this.xpAtual = xpAtual;
    }
    public void setXpParaProximoNivel(int xpParaProximoNivel) {
        this.xpParaProximoNivel = xpParaProximoNivel;
    }

    // Getters

    public int getIdRotina() {
        return idRotina;
    }
    public String getNome() {
        return nome;
    }
    public String getDescricao() {
        return descricao;
    }
    public String getFrequencia() {
        return frequencia;
    }
    public LocalDate getDataCriacao() {
        return dataCriacao;
    }
    public boolean isObrigatorio() {
        return obrigatorio;
    }
    public int getStreakAtual() {
        return streakAtual;
    }

    public int getMelhorStreak() {
        return melhorStreak;
    }
    public int getNivel() {
        return nivel;
    }
    public int getXpAtual() {
        return xpAtual;
    }
    public int getXpParaProximoNivel() {
        return xpParaProximoNivel;
    }
}