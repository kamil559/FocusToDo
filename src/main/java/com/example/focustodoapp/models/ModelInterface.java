package com.example.focustodoapp.models;

import com.example.focustodoapp.SQLiteConnection;

import java.sql.*;

public class ModelInterface {
    Connection connection;

    public ModelInterface() {
        connection = SQLiteConnection.Connector();

        if (connection == null) {
            System.out.println("Could not acquire DB connection, exitting...");
            System.exit(1);
        }
    }

    public boolean isDbConnected() {
        try {
            return !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
