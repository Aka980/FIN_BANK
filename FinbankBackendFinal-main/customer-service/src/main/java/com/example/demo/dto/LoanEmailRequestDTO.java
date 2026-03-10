package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanEmailRequestDTO {
    private String email;
    private String customerName;
    private Long applicationNo;
    private String status;
    private String message;
}
