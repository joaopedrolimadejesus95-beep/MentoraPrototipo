package org.example;

import java.time.LocalDate;

public class Habito {

    private int idHabito;
    private String nome;
    private String frequencia;
    private boolean ativo = true;
    private LocalDate dataCriacao;

    public int getIdHabito() {
        return idHabito;
    }

    public void setIdHabito(int idHabito) {
        this.idHabito = idHabito;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(String frequencia) {
        this.frequencia = frequencia;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
