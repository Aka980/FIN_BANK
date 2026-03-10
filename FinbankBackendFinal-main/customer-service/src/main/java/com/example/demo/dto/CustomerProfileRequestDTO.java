package com.example.demo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import com.example.demo.enums.IfscCode;

@Data
public class CustomerProfileRequestDTO {

    @NotBlank(message="PAN is required")
    @Pattern(
        regexp="^[A-Z]{5}[0-9]{4}[A-Z]{1}$",
        message="Invalid PAN format"
    )
    private String pan;


    @NotNull(message="Date of Birth is required")
    private LocalDate dob;


    @NotBlank(message="Address is required")
    private String address;


    /*
     * IFSC Dropdown Value
     */
    @NotNull(message="IFSC Code is required")
    private IfscCode ifsc;


    @Min(value=10000,
    message="Annual Income must be above 10000")
    private double annualIncome;


    @NotBlank(message="Occupation is required")
    private String occupation;

}