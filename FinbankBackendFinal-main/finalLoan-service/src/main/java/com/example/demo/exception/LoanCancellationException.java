package com.example.demo.exception;

public class LoanCancellationException extends RuntimeException {
    public LoanCancellationException(String message) {
        super(message);
    }
}