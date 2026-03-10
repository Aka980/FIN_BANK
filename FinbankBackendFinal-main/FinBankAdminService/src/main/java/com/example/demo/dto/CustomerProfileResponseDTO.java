package com.example.demo.dto;

import java.time.LocalDate;

import lombok.Data;

@Data

public class CustomerProfileResponseDTO {

    private Long accountNo;
    private String pan;
    private LocalDate dob;
    private String address;
    private String ifsc;
    private Double annualIncome;
    private String occupation;

}
