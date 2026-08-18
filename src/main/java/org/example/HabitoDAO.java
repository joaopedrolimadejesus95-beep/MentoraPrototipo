package org.example;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class HabitoDAO{

    // CADASTRAR
    public void inserir(Habito habito) {

        String sql = "INSERT INTO habitos (nome, frequencia) VALUES (?, ?)";

        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, habito.getNome());
            stmt.setString(2, habito.getFrequencia());

            stmt.executeUpdate();

            System.out.println("Hábito cadastrado com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar hábito: " + e.getMessage());
        }
    }

    // LISTAR TODOS (só os ativos)
    public ArrayList<Habito> listarAtivos() {

        ArrayList<Habito> habitos = new ArrayList<>();
        String sql = "SELECT * FROM habitos WHERE ativo = true";

        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Habito habito = new Habito();
                habito.setIdHabito(rs.getInt("id_habito"));
                habito.setNome(rs.getString("nome"));
                habito.setFrequencia(rs.getString("frequencia"));
                habito.setAtivo(rs.getBoolean("ativo"));
                habito.setDataCriacao(rs.getObject("data_criacao", LocalDate.class));

                habitos.add(habito);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar hábitos: " + e.getMessage());
        }

        return habitos;
    }

    // "EXCLUIR" (na verdade, pausa — não perde histórico)
    public void pausar(int idHabito) {

        String sql = "UPDATE habitos SET ativo = false WHERE id_habito = ?";

        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idHabito);
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Hábito pausado com sucesso!");
            } else {
                System.out.println("Hábito não encontrado.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao pausar hábito: " + e.getMessage());
        }
    }
}