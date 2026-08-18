package org.example;
import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDate;

public class EstudoDAO {
    public void inserirEstudo(Estudo estudo) {
        String sql = "INSERT INTO estudo (materia, status, dataCriacao) VALUES (?, ?, ?)";

        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, estudo.getMateria());
            stmt.setString(2, estudo.getStatus());
            stmt.setDate(3, Date.valueOf(estudo.getDataCriacao()));

            stmt.executeUpdate();

            System.out.println("Estudo inserido com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao inserir estudo! " + e.getMessage());
        }
    }
    public ArrayList<Estudo> ListarEstudos(){
        ArrayList <Estudo> estudos = new ArrayList<>();
        String Sql = "SELECT * FROM estudo";
        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(Sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Estudo estudo = new Estudo();
                estudo.setIdEstudo(rs.getInt("idEstudo"));
                estudo.setMateria(rs.getString("materia"));
                estudo.setStatus(rs.getString("status"));
                estudo.setDataCriacao(rs.getDate("dataCriacao").toLocalDate());

                estudos.add(estudo);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar estudos! " + e.getMessage());
        }
        return estudos;
    }

    

}



