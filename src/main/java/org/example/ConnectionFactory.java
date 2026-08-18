package org.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

    public class ConnectionFactory {

        private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
        private static final String USUARIO = "postgres";
        private static final String SENHA = "estudosjava";

        public static Connection criarConexao() throws SQLException {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        }
}
