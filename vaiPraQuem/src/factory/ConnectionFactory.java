package factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

public class ConnectionFactory {

    public static Connection getConnection() {
        try {
            var conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/vaipraquem?zeroDateTimeBehavior=CONVERT_TO_NULL&useTimezone=true&serverTimezone=UTC",
                    "root",
                    "!"); //No lugar da Interrogação coloque a senha configurada da conexão do banco MySQL
            return conn;

        } catch (SQLException erro) {
            throw new RuntimeException(
                    "Erro ao conectar ao banco de dados", erro
            );
        }
    }
}