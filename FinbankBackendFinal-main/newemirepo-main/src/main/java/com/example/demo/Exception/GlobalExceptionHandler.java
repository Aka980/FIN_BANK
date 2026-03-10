package com.example.demo.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("errorCode", ex.getErrorCode());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Map<String, String>> handleInvalidInput(InvalidInputException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("errorCode", ex.getErrorCode());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // This handles wrong types passed in JSON or query params
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("errorCode", "TYPE_MISMATCH");
        body.put("message", "Invalid value for parameter,Please give numbers Only'" + ex.getName() + "': " + ex.getValue());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

	/*
	 * @ExceptionHandler(Exception.class) public ResponseEntity<Map<String, String>>
	 * handleGeneralException(Exception ex) { Map<String, String> body = new
	 * HashMap<>(); body.put("errorCode", "INTERNAL_ERROR"); body.put("message",
	 * ex.getMessage()); return new ResponseEntity<>(body,
	 * HttpStatus.INTERNAL_SERVER_ERROR); }
	 */
}