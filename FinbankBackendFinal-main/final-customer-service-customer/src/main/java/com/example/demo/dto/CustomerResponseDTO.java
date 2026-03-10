package com.example.demo.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CustomerResponseDTO {

private Long accountNo;

private String customerName;

private String username;

private String email;

private String phone;

private String role;

private LocalDate createdDate;

}