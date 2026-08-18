import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TesteConexao {

    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost:5432/postgres";
        String usuario = "postgres";
        String senha = "estudosjava"; // a senha que funcionou no psql

        try (Connection conexao = DriverManager.getConnection(url, usuario, senha)) {

            System.out.println("Conectado com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
        }
    }
}