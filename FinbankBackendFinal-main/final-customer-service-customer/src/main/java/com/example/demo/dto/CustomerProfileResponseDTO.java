package com.example.demo.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CustomerProfileResponseDTO {

    private String pan;

    private LocalDate dob;

    private String address;

    private String bankAccount;

    private String ifsc;

    private double annualIncome;

    private String occupation;

}