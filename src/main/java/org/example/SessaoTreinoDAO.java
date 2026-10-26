package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SessaoTreinoDAO {

    public void registrar(String tipo, LocalDate data, boolean concluida,
                          String observacao, List<String> exercicios) throws SQLException {
        String sqlSessao = "INSERT INTO sessao_treino (data, tipo, concluida, observacao) "
                + "VALUES (?, ?, ?, ?)";
        String sqlExercicio = "INSERT INTO exercicio_realizado (sessao_treino_id, nome, ordem) "
                + "VALUES (?, ?, ?)";

        try (Connection conexao = ConnectionFactory.criarConexao()) {
            conexao.setAutoCommit(false);
            try (PreparedStatement sessaoStmt = conexao.prepareStatement(sqlSessao, Statement.RETURN_GENERATED_KEYS)) {
                sessaoStmt.setObject(1, data);
                sessaoStmt.setString(2, tipo);
                sessaoStmt.setBoolean(3, concluida);
                if (observacao == null || observacao.isBlank()) {
                    sessaoStmt.setNull(4, Types.VARCHAR);
                } else {
                    sessaoStmt.setString(4, observacao.trim());
                }
                sessaoStmt.executeUpdate();

                long sessaoId;
                try (var keys = sessaoStmt.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("O banco não retornou o identificador da sessão.");
                    }
                    sessaoId = keys.getLong(1);
                }

                try (PreparedStatement exercicioStmt = conexao.prepareStatement(sqlExercicio)) {
                    int ordem = 1;
                    for (String nome : exercicios) {
                        exercicioStmt.setLong(1, sessaoId);
                        exercicioStmt.setString(2, nome);
                        exercicioStmt.setInt(3, ordem++);
                        exercicioStmt.addBatch();
                    }
                    exercicioStmt.executeBatch();
                }
                conexao.commit();
            } catch (SQLException e) {
                conexao.rollback();
                throw e;
            } finally {
                conexao.setAutoCommit(true);
            }
        }
    }

    public List<TreinoDoDia> listarHoje(LocalDate data) throws SQLException {
        String sql = "SELECT s.id, s.tipo, s.concluida, "
                + "string_agg(e.nome, ', ' ORDER BY e.ordem) AS exercicios, s.observacao "
                + "FROM sessao_treino s LEFT JOIN exercicio_realizado e ON e.sessao_treino_id = s.id "
                + "WHERE s.data = ? GROUP BY s.id ORDER BY s.registrado_em DESC";
        List<TreinoDoDia> treinos = new ArrayList<>();
        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setObject(1, data);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    treinos.add(new TreinoDoDia(rs.getLong("id"), rs.getString("tipo"),
                            rs.getBoolean("concluida"), rs.getString("exercicios"), rs.getString("observacao")));
                }
            }
        }
        return treinos;
    }

    public int contarSemana(String tipo) throws SQLException {
        LocalDate inicioSemana = LocalDate.now().with(DayOfWeek.MONDAY);
        String sql = "SELECT COUNT(*) FROM sessao_treino WHERE tipo = ? AND concluida = TRUE "
                + "AND data >= ? AND data <= ?";
        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, tipo);
            stmt.setObject(2, inicioSemana);
            stmt.setObject(3, inicioSemana.plusDays(6));
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
