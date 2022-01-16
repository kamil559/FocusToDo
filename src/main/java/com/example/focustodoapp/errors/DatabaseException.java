package com.example.focustodoapp.errors;

import com.example.focustodoapp.constants.ErrorCode;

import java.sql.SQLException;


public class DatabaseException extends SQLException {
    public DatabaseException() {}
    public DatabaseException(String message, ErrorCode errorCode) {
        super(message);
    }
}
