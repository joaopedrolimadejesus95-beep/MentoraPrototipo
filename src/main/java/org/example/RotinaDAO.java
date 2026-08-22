package org.example;

import java.sql.*;
import java.util.ArrayList;

public class RotinaDAO {

    public void inserir(Rotina rotina) {
        String sql = "INSERT INTO rotinas (nome, descricao, frequencia, dataDeCriacao, obrigatorio, " +
                "streakAtual, melhorStreak, nivel, xpAtual, xpParaProximoNivel) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, rotina.getNome());
            stmt.setString(2, rotina.getDescricao());
            stmt.setString(3, rotina.getFrequencia());
            stmt.setDate(4, Date.valueOf(rotina.getDataCriacao()));
            stmt.setBoolean(5, rotina.isObrigatorio());
            stmt.setInt(6, rotina.getStreakAtual());
            stmt.setInt(7, rotina.getMelhorStreak());
            stmt.setInt(8, rotina.getNivel());
            stmt.setInt(9, rotina.getXpAtual());
            stmt.setInt(10, rotina.getXpParaProximoNivel());

            stmt.executeUpdate();

            System.out.println("Rotina inserida com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao inserir rotina! " + e.getMessage());
        }
    }

    public ArrayList<Rotina> listar() {
        ArrayList<Rotina> rotinas = new ArrayList<>();
        String sql = "SELECT * FROM rotinas";

        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                rotinas.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar rotinas! " + e.getMessage());
        }

        return rotinas;
    }

    public Rotina buscarPorId(int idRotina) {
        Rotina rotina = null;
        String sql = "SELECT * FROM rotinas WHERE idRotina = ?";

        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idRotina);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    rotina = mapear(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar rotina! " + e.getMessage());
        }

        return rotina;
    }

    public void atualizarDados(Rotina rotina) {
        String sql = "UPDATE rotinas SET nome = ?, descricao = ?, frequencia = ?, obrigatorio = ? WHERE idRotina = ?";

        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, rotina.getNome());
            stmt.setString(2, rotina.getDescricao());
            stmt.setString(3, rotina.getFrequencia());
            stmt.setBoolean(4, rotina.isObrigatorio());
            stmt.setInt(5, rotina.getIdRotina());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar dados da rotina! " + e.getMessage());
        }
    }

    public void atualizar(Rotina rotina) {
        String sql = "UPDATE rotinas SET streakAtual = ?, melhorStreak = ?, nivel = ?, xpAtual = ? WHERE idRotina = ?";

        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, rotina.getStreakAtual());
            stmt.setInt(2, rotina.getMelhorStreak());
            stmt.setInt(3, rotina.getNivel());
            stmt.setInt(4, rotina.getXpAtual());
            stmt.setInt(5, rotina.getIdRotina());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar rotina! " + e.getMessage());
        }
    }

    public void deletar(int idRotina) {
        // apaga primeiro os registros de cumprimento (dependem da rotina),
        // senão o banco recusa por causa da FK
        String sqlRegistros = "DELETE FROM registro_cumprimento WHERE idRotina = ?";
        String sqlRotina = "DELETE FROM rotinas WHERE idRotina = ?";

        try (Connection conexao = ConnectionFactory.criarConexao()) {

            try (PreparedStatement stmt = conexao.prepareStatement(sqlRegistros)) {
                stmt.setInt(1, idRotina);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conexao.prepareStatement(sqlRotina)) {
                stmt.setInt(1, idRotina);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("Erro ao deletar rotina! " + e.getMessage());
        }
    }

    private Rotina mapear(ResultSet rs) throws SQLException {
        Rotina rotina = new Rotina();
        rotina.setIdRotina(rs.getInt("idRotina"));
        rotina.setNome(rs.getString("nome"));
        rotina.setDescricao(rs.getString("descricao"));
        rotina.setFrequencia(rs.getString("frequencia"));
        rotina.setDataCriacao(rs.getDate("dataDeCriacao").toLocalDate());
        rotina.setObrigatorio(rs.getBoolean("obrigatorio"));
        rotina.setStreakAtual(rs.getInt("streakAtual"));
        rotina.setMelhorStreak(rs.getInt("melhorStreak"));
        rotina.setNivel(rs.getInt("nivel"));
        rotina.setXpAtual(rs.getInt("xpAtual"));
        rotina.setXpParaProximoNivel(rs.getInt("xpParaProximoNivel"));
        return rotina;
    }
}
