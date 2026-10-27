package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PlanoTreinoDAO {
    public void criar(String nome, String tipo, int duracaoMin, List<String> exercicios) throws SQLException {
        String inserirPlano = "INSERT INTO plano_treino (nome, tipo, duracao_estimada_min) VALUES (?, ?, ?)";
        String inserirExercicio = "INSERT INTO exercicio_plano (plano_treino_id, nome, ordem) VALUES (?, ?, ?)";
        try (Connection conexao = ConnectionFactory.criarConexao()) {
            conexao.setAutoCommit(false);
            try (PreparedStatement planoStmt = conexao.prepareStatement(inserirPlano, Statement.RETURN_GENERATED_KEYS)) {
                planoStmt.setString(1, nome.trim());
                planoStmt.setString(2, tipo);
                planoStmt.setInt(3, duracaoMin);
                planoStmt.executeUpdate();
                long planoId;
                try (ResultSet keys = planoStmt.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("O banco não retornou o identificador do plano.");
                    planoId = keys.getLong(1);
                }
                try (PreparedStatement exercicioStmt = conexao.prepareStatement(inserirExercicio)) {
                    int ordem = 1;
                    for (String exercicio : exercicios) {
                        exercicioStmt.setLong(1, planoId);
                        exercicioStmt.setString(2, exercicio);
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

    public List<PlanoTreino> listarAtivos() throws SQLException {
        List<PlanoTreino> planos = new ArrayList<>();
        String sql = "SELECT id, nome, tipo, duracao_estimada_min, ativo FROM plano_treino WHERE ativo = TRUE ORDER BY criado_em, id";
        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) planos.add(mapear(rs));
        }
        try (Connection conexao = ConnectionFactory.criarConexao()) {
            for (PlanoTreino plano : planos) plano.setExercicios(listarExercicios(conexao, plano.getId()));
        }
        return planos;
    }

    public PlanoTreino buscarAtivo(long id) throws SQLException {
        String sql = "SELECT id, nome, tipo, duracao_estimada_min, ativo FROM plano_treino WHERE id = ? AND ativo = TRUE";
        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                PlanoTreino plano = mapear(rs);
                plano.setExercicios(listarExercicios(conexao, id));
                return plano;
            }
        }
    }

    public void arquivar(long id) throws SQLException {
        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement("UPDATE plano_treino SET ativo = FALSE WHERE id = ?")) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private List<ExercicioPlano> listarExercicios(Connection conexao, long planoId) throws SQLException {
        List<ExercicioPlano> exercicios = new ArrayList<>();
        try (PreparedStatement stmt = conexao.prepareStatement(
                "SELECT id, nome FROM exercicio_plano WHERE plano_treino_id = ? ORDER BY ordem")) {
            stmt.setLong(1, planoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ExercicioPlano exercicio = new ExercicioPlano();
                    exercicio.setId(rs.getLong("id"));
                    exercicio.setNome(rs.getString("nome"));
                    exercicios.add(exercicio);
                }
            }
        }
        return exercicios;
    }

    private PlanoTreino mapear(ResultSet rs) throws SQLException {
        PlanoTreino plano = new PlanoTreino();
        plano.setId(rs.getLong("id"));
        plano.setNome(rs.getString("nome"));
        plano.setTipo(rs.getString("tipo"));
        plano.setDuracaoEstimadaMin(rs.getInt("duracao_estimada_min"));
        plano.setAtivo(rs.getBoolean("ativo"));
        return plano;
    }
}
