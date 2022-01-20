package com.example.focustodoapp.models;

import com.example.focustodoapp.SQLiteConnection;

import java.sql.*;

public class ModelInterface {
    Connection connection;

    public void setConnection() {
        connection = SQLiteConnection.Connector();

        if (connection == null) {
            System.out.println("Could not acquire DB connection, exitting...");
            System.exit(1);
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            setConnection();
        }
        return connection;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
