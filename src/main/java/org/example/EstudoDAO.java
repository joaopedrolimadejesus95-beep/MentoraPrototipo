package org.example;
import java.sql.*;
import java.util.ArrayList;

public class EstudoDAO {

    public void inserirEstudo(Estudo estudo) {
        String sql = "INSERT INTO estudo (materia, status, dataCriacao, streakAtual, melhorStreak) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, estudo.getMateria());
            stmt.setString(2, estudo.getStatus());
            stmt.setDate(3, Date.valueOf(estudo.getDataCriacao()));
            stmt.setInt(4, estudo.getStreakAtual());
            stmt.setInt(5, estudo.getMelhorStreak());

            stmt.executeUpdate();

            System.out.println("Estudo inserido com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao inserir estudo! " + e.getMessage());
        }
    }

    public ArrayList<Estudo> ListarEstudos() {
        ArrayList<Estudo> estudos = new ArrayList<>();
        String sql = "SELECT * FROM estudo";
        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                estudos.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar estudos! " + e.getMessage());
        }
        return estudos;
    }

    public Estudo buscarPorId(int idEstudo) {
        Estudo estudo = null;
        String sql = "SELECT * FROM estudo WHERE idEstudo = ?";

        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idEstudo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estudo = mapear(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar estudo! " + e.getMessage());
        }
        return estudo;
    }

    public void atualizarDados(Estudo estudo) {
        String sql = "UPDATE estudo SET materia = ?, status = ? WHERE idEstudo = ?";

        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, estudo.getMateria());
            stmt.setString(2, estudo.getStatus());
            stmt.setInt(3, estudo.getIdEstudo());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar estudo! " + e.getMessage());
        }
    }

    public void atualizarStreak(Estudo estudo) {
        String sql = "UPDATE estudo SET streakAtual = ?, melhorStreak = ? WHERE idEstudo = ?";

        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, estudo.getStreakAtual());
            stmt.setInt(2, estudo.getMelhorStreak());
            stmt.setInt(3, estudo.getIdEstudo());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar streak do estudo! " + e.getMessage());
        }
    }

    public void deletar(int idEstudo) {
        String sql = "DELETE FROM estudo WHERE idEstudo = ?";

        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idEstudo);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao deletar estudo! " + e.getMessage());
        }
    }

    private Estudo mapear(ResultSet rs) throws SQLException {
        Estudo estudo = new Estudo();
        estudo.setIdEstudo(rs.getInt("idEstudo"));
        estudo.setMateria(rs.getString("materia"));
        estudo.setStatus(rs.getString("status"));
        estudo.setDataCriacao(rs.getDate("dataCriacao").toLocalDate());
        estudo.setStreakAtual(rs.getInt("streakAtual"));
        estudo.setMelhorStreak(rs.getInt("melhorStreak"));
        return estudo;
    }
}
