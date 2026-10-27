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
                          String observacao, List<ExercicioTreinoEntrada> exercicios, Long planoId) throws SQLException {
        String sqlSessao = "INSERT INTO sessao_treino (data, tipo, concluida, observacao, plano_treino_id) "
                + "VALUES (?, ?, ?, ?, ?)";
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
                if (planoId == null) sessaoStmt.setNull(5, Types.BIGINT);
                else sessaoStmt.setLong(5, planoId);
                sessaoStmt.executeUpdate();

                long sessaoId;
                try (var keys = sessaoStmt.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("O banco não retornou o identificador da sessão.");
                    }
                    sessaoId = keys.getLong(1);
                }

                try (PreparedStatement exercicioStmt = conexao.prepareStatement(sqlExercicio, Statement.RETURN_GENERATED_KEYS);
                     PreparedStatement serieStmt = conexao.prepareStatement(
                             "INSERT INTO serie_realizada (exercicio_realizado_id, numero, repeticoes, carga) VALUES (?, ?, ?, ?)")) {
                    int ordem = 1;
                    for (ExercicioTreinoEntrada exercicio : exercicios) {
                        exercicioStmt.setLong(1, sessaoId);
                        exercicioStmt.setString(2, exercicio.getNome());
                        exercicioStmt.setInt(3, ordem++);
                        exercicioStmt.executeUpdate();
                        long exercicioId;
                        try (var keys = exercicioStmt.getGeneratedKeys()) {
                            if (!keys.next()) throw new SQLException("O banco não retornou o identificador do exercício.");
                            exercicioId = keys.getLong(1);
                        }

                        if (exercicio.getSeries() != null || exercicio.getRepeticoes() != null || exercicio.getCarga() != null) {
                            int quantidade = exercicio.getSeries() == null ? 1 : exercicio.getSeries();
                            for (int numero = 1; numero <= quantidade; numero++) {
                                serieStmt.setLong(1, exercicioId);
                                serieStmt.setInt(2, numero);
                                if (exercicio.getRepeticoes() == null) serieStmt.setNull(3, Types.INTEGER);
                                else serieStmt.setInt(3, exercicio.getRepeticoes());
                                if (exercicio.getCarga() == null) serieStmt.setNull(4, Types.NUMERIC);
                                else serieStmt.setBigDecimal(4, exercicio.getCarga().setScale(2, java.math.RoundingMode.HALF_UP));
                                serieStmt.addBatch();
                            }
                            serieStmt.executeBatch();
                            serieStmt.clearBatch();
                        }
                    }
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
                + "string_agg(e.nome || CASE WHEN r.detalhes IS NULL THEN '' ELSE ' (' || r.detalhes || ')' END, ' · ' ORDER BY e.ordem) AS exercicios, s.observacao "
                + "FROM sessao_treino s LEFT JOIN exercicio_realizado e ON e.sessao_treino_id = s.id "
                + "LEFT JOIN (SELECT exercicio_realizado_id, string_agg(concat_ws(', ', 'Série ' || numero::text, "
                + "CASE WHEN repeticoes IS NULL THEN NULL ELSE repeticoes::text || ' rep.' END, "
                + "CASE WHEN carga IS NULL THEN NULL ELSE carga::text || ' kg' END), '; ' ORDER BY numero) AS detalhes "
                + "FROM serie_realizada GROUP BY exercicio_realizado_id) r ON r.exercicio_realizado_id = e.id "
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
