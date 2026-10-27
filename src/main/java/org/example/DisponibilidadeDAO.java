package org.example;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DisponibilidadeDAO {
    public int buscarAjuste(LocalDate data) throws SQLException {
        try (Connection c = ConnectionFactory.criarConexao(); PreparedStatement s = c.prepareStatement("SELECT minutos_disponiveis FROM ajuste_disponibilidade_dia WHERE data = ?")) {
            s.setObject(1, data); try (ResultSet r = s.executeQuery()) { return r.next() ? r.getInt(1) : 0; }
        }
    }
    public void salvarAjuste(LocalDate data, int minutos) throws SQLException {
        String q = "INSERT INTO ajuste_disponibilidade_dia(data,minutos_disponiveis) VALUES(?,?) ON CONFLICT(data) DO UPDATE SET minutos_disponiveis=EXCLUDED.minutos_disponiveis, atualizado_em=CURRENT_TIMESTAMP";
        try (Connection c = ConnectionFactory.criarConexao(); PreparedStatement s = c.prepareStatement(q)) { s.setObject(1,data); s.setInt(2,minutos); s.executeUpdate(); }
    }
    public void limparAjuste(LocalDate data) throws SQLException {
        try (Connection c = ConnectionFactory.criarConexao(); PreparedStatement s = c.prepareStatement("DELETE FROM ajuste_disponibilidade_dia WHERE data=?")) { s.setObject(1,data); s.executeUpdate(); }
    }
    public List<IntervaloDisponivel> listarSemana() throws SQLException {
        List<IntervaloDisponivel> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.criarConexao(); PreparedStatement s = c.prepareStatement("SELECT dia_semana,inicio,fim FROM disponibilidade_semanal ORDER BY dia_semana,inicio"); ResultSet r = s.executeQuery()) {
            while (r.next()) out.add(new IntervaloDisponivel(DayOfWeek.valueOf(r.getString("dia_semana")), r.getTime("inicio").toLocalTime(), r.getTime("fim").toLocalTime()));
        }
        return out;
    }
    public void substituirSemana(List<IntervaloDisponivel> intervalos) throws SQLException {
        try (Connection c = ConnectionFactory.criarConexao()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement s=c.prepareStatement("DELETE FROM disponibilidade_semanal")) { s.executeUpdate(); }
                try (PreparedStatement s=c.prepareStatement("INSERT INTO disponibilidade_semanal(dia_semana,inicio,fim) VALUES(?,?,?)")) {
                    for (IntervaloDisponivel i:intervalos) { s.setString(1,i.getDia().name()); s.setObject(2,i.getInicio()); s.setObject(3,i.getFim()); s.addBatch(); }
                    s.executeBatch();
                }
                c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
            finally { c.setAutoCommit(true); }
        }
    }
}
