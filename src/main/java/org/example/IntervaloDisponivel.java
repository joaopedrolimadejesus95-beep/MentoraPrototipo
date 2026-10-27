package org.example;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class IntervaloDisponivel {
    private final DayOfWeek dia;
    private final LocalTime inicio;
    private final LocalTime fim;
    public IntervaloDisponivel(DayOfWeek dia, LocalTime inicio, LocalTime fim) { this.dia = dia; this.inicio = inicio; this.fim = fim; }
    public DayOfWeek getDia() { return dia; }
    public LocalTime getInicio() { return inicio; }
    public LocalTime getFim() { return fim; }
}
