package com.example.demo.Exception;

public class InvalidInputException extends RuntimeException {

    private final String errorCode;

    public InvalidInputException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}