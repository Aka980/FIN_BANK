package com.example.demo.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(
	MethodArgumentNotValidException.class)
	
	public ResponseEntity<Map<String,String>>
	handleValidationErrors(
	MethodArgumentNotValidException ex){
	
	Map<String,String> errors =
	new HashMap<>();
	
	ex.getBindingResult()
	.getAllErrors()
	.forEach(error->{
	
	String field =
	((FieldError)error)
	.getField();
	
	String message =
	error.getDefaultMessage();
	
	errors.put(field,message);
	
	});
	
	return new ResponseEntity<>(
	errors,
	HttpStatus.BAD_REQUEST);
	
	}
	
	@ExceptionHandler(
	CustomerNotFoundException.class)
	
	public ResponseEntity<String>
	handleCustomerNotFound(
	
	CustomerNotFoundException ex){
	
	return new ResponseEntity<>(
	
	ex.getMessage(),
	
	HttpStatus.NOT_FOUND);
	
	}
	
	@ExceptionHandler(
	DuplicateUserException.class)
	
	public ResponseEntity<String>
	handleDuplicateUser(
	
	DuplicateUserException ex){
	
	return new ResponseEntity<>(
	
	ex.getMessage(),
	
	HttpStatus.CONFLICT);
	
	}
	
	@ExceptionHandler(Exception.class)
	
	public ResponseEntity<String>
	handleAllExceptions(
	Exception ex){
	
	return new ResponseEntity<>(
	
	ex.getMessage(),
	
	HttpStatus.BAD_REQUEST);
	
	}


}