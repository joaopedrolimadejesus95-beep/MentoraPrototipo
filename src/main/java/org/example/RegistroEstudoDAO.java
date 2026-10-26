package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class RegistroEstudoDAO {

    public void registrar(String area, int duracaoMin, String assunto) throws SQLException {
        String sql = "INSERT INTO sessao_estudo (data, area, duracao_min, assunto) VALUES (?, ?, ?, ?)";
        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setObject(1, LocalDate.now());
            stmt.setString(2, area);
            stmt.setInt(3, duracaoMin);
            if (assunto == null || assunto.isBlank()) {
                stmt.setNull(4, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(4, assunto.trim());
            }
            stmt.executeUpdate();
        }
    }

    public int minutosHoje() throws SQLException {
        String sql = "SELECT COALESCE(SUM(duracao_min), 0) FROM sessao_estudo WHERE data = ?";
        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setObject(1, LocalDate.now());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public int metaDiariaMin() throws SQLException {
        String sql = "SELECT meta_diaria_min FROM configuracao_estudo WHERE id = 1";
        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 90;
        }
    }

    public int minimoDiarioMin() throws SQLException {
        String sql = "SELECT minimo_diario_min FROM configuracao_estudo WHERE id = 1";
        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 15;
        }
    }

    public int sequenciaAtual() throws SQLException {
        int minimo = minimoDiarioMin();
        String sql = "SELECT data, SUM(duracao_min) AS minutos FROM sessao_estudo "
                + "WHERE data <= ? GROUP BY data ORDER BY data DESC";
        Map<LocalDate, Integer> minutosPorDia = new HashMap<>();
        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setObject(1, LocalDate.now());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    minutosPorDia.put(rs.getDate("data").toLocalDate(), rs.getInt("minutos"));
                }
            }
        }

        LocalDate esperado = LocalDate.now();
        if (minutosPorDia.getOrDefault(esperado, 0) < minimo) {
            esperado = esperado.minusDays(1);
        }
        int sequencia = 0;
        while (minutosPorDia.getOrDefault(esperado, 0) >= minimo) {
            sequencia++;
            esperado = esperado.minusDays(1);
        }
        return sequencia;
    }
}
