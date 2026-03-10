package com.example.demo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanResponseDTO {

    private Long applicationNo;     
    private Long accountNo;        

    private BigDecimal amount;      
    private BigDecimal roi;         
    private Integer tenure;        

    private String loanType;       
    private String status;          

    private LocalDate appliedDate;
    private LocalDate approvedDate;

    private String rejectionReason;
}