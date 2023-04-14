package edu.aubg.ics.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static edu.aubg.ics.util.Constants.*;

public class PostgresDAO {

    private Connection connection;

    public PostgresDAO(String url) throws SQLException {
        this.connection = DriverManager.getConnection(url, POSTGRES_USERNAME, POSTGRES_PASSWORD);
    }

    public void close() throws SQLException {
        connection.close();
    }

    public Connection getConnection() {
        return connection;
    }

    public Connection newConnection(String url) throws SQLException {
        return DriverManager.getConnection(url, POSTGRES_USERNAME, POSTGRES_PASSWORD);
    }
}
