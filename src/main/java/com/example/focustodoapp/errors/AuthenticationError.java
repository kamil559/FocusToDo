package com.example.focustodoapp.errors;

import com.example.focustodoapp.constants.ErrorCode;

public class AuthenticationError extends Exception {
    public AuthenticationError() {}
    public AuthenticationError(String message) {
        super(message);
    }
    public AuthenticationError(String message, ErrorCode errorCode) {
        super(message);
    }
}
