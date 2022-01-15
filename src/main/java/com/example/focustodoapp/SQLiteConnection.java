package com.example.focustodoapp;
import java.sql.*;

public class SQLiteConnection {
    public static Connection Connector() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:focus_to_do_db");
        } catch (Exception e) {
            return null;
        }
    }
}
