package com.example.demo.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class LoanResponseDTO {

    private Long applicationNo;
    private Long accountNo;
    private LocalDate applicationDate;
    private Double amount;
    private String pan;
    private Integer tenure;
    private Double roi;
    private String status;
    private LoanType loanType;
}