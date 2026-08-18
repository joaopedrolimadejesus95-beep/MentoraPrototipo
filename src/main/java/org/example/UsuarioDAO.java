package org.example;

import java.sql.*;
import java.util.ArrayList;

public class UsuarioDAO {

    public void inserir(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, email, numero, senha) VALUES (?,?,?,?)";

        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setInt(3, usuario.getNumero());
            stmt.setString(4, usuario.getSenha());

            stmt.executeUpdate();

            System.out.printf("Registro inserido com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao inserir registro! " + e.getMessage());
        }
    }

    public Usuario buscarPorId(int idUsuario) {
        Usuario usuario = null;
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";

        try (Connection conexao = ConnectionFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setNumero(rs.getInt("numero"));
                    usuario.setSenha(rs.getString("senha"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuário! " + e.getMessage());
        }

        return usuario;
    }
}