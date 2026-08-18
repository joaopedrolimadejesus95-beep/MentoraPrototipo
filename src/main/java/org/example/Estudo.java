package org.example;

import java.time.LocalDate;

public class Estudo {
    private int idEstudo;
    private String materia;        // ex: "Estrutura de Dados"
    private LocalDate dataCriacao;
    private String status;         // "EM_ANDAMENTO", "CONCLUIDO"


    public int getIdEstudo() {
        return idEstudo;
    }
    public void setIdEstudo(int idEstudo) {

        this.idEstudo = idEstudo;
    }
    public String getMateria() {
        return materia;
    }
    public void setMateria(String materia) {
        this.materia = materia;
    }
    public LocalDate getDataCriacao() {
        return dataCriacao;
    }
    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

}