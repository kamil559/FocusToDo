package com.example.focustodoapp.errors;

import com.example.focustodoapp.constants.ErrorCode;

public class ValidationError extends Exception {
    public ValidationError() {}
    public ValidationError(String message) {
        super(message);
    }
    public ValidationError(String message, ErrorCode errorCode) {
        super(message);
    }
}
