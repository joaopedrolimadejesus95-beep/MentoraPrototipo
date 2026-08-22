package org.example;

import java.sql.*;
import java.time.LocalDate;

public class RegistroEstudoDiarioDAO {

    public boolean existeRegistro(int idEstudo, LocalDate data) {
        String sql = "SELECT 1 FROM registro_estudo_diario WHERE idEstudo = ? AND data = ?";
        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idEstudo);
            stmt.setDate(2, Date.valueOf(data));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar registro de estudo! " + e.getMessage());
            return false;
        }
    }

    public void inserir(int idEstudo, LocalDate data) {
        String sql = "INSERT INTO registro_estudo_diario (idEstudo, data) VALUES (?, ?)";
        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idEstudo);
            stmt.setDate(2, Date.valueOf(data));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao inserir registro de estudo! " + e.getMessage());
        }
    }
}
