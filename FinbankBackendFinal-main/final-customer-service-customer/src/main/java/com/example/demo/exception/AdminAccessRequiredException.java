package com.example.demo.exception;

public class AdminAccessRequiredException extends RuntimeException {
    public AdminAccessRequiredException(String message) {
        super(message);
    }
}
