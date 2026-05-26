package com.template;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDao {
    private static final String URL = "jdbc:postgresql://localhost:5432/db_pelucia";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
}
