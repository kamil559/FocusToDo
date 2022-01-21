package com.example.focustodoapp;
import org.sqlite.SQLiteConfig;

import java.sql.*;

public class SQLiteConnection {
    public static Connection Connector() {
        try {
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig config = new SQLiteConfig();
            String DB_URL = "jdbc:sqlite:focus_to_do_db";
            config.enforceForeignKeys(true);
            return DriverManager.getConnection(DB_URL,config.toProperties());
        } catch (Exception e) {
            return null;
        }
    }
}
