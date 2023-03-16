package edu.aubg.ics.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static edu.aubg.ics.util.Constants.*;

public class PostgresDAO {

    private Connection connection;

    public PostgresDAO() throws SQLException {
        this.connection = DriverManager.getConnection(POSTGRES_CONNECTION, POSTGRES_USERNAME, POSTGRES_PASSWORD);
    }

    public void close() throws SQLException {
        connection.close();
    }

    public Connection getConnection() {
        return connection;
    }

    public Connection newConnection() throws SQLException {
        return DriverManager.getConnection(POSTGRES_CONNECTION, POSTGRES_USERNAME, POSTGRES_PASSWORD);
    }
}
