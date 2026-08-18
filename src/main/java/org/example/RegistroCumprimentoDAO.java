package org.example;

import java.sql.*;
import java.time.LocalDate;

public class RegistroCumprimentoDAO {

    public boolean existeRegistro(int idRotina, LocalDate data) {
        String sql = "SELECT 1 FROM registro_cumprimento WHERE idRotina = ? AND data = ?";

        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idRotina);
            stmt.setDate(2, Date.valueOf(data));

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("Erro ao verificar registro! " + e.getMessage());
            return false;
        }
    }

    public void inserir(RegistroCumprimento registro) {
        String sql = "INSERT INTO registro_cumprimento (idRotina, data, cumprido) VALUES (?, ?, ?)";

        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, registro.getIdRotina());
            stmt.setDate(2, Date.valueOf(registro.getData()));
            stmt.setBoolean(3, registro.isCumprido());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao inserir registro de cumprimento! " + e.getMessage());
        }
    }
}