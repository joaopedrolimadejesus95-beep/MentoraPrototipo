package org.example;

import java.time.LocalDate;

public class RegistroCumprimento {
    private int idRegistro;
    private int idRotina;
    private LocalDate data;
    private boolean cumprido;

    public int getIdRegistro() { return idRegistro; }
    public void setIdRegistro(int idRegistro) { this.idRegistro = idRegistro; }

    public int getIdRotina() { return idRotina; }
    public void setIdRotina(int idRotina) { this.idRotina = idRotina; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public boolean isCumprido() { return cumprido; }
    public void setCumprido(boolean cumprido) { this.cumprido = cumprido; }
}