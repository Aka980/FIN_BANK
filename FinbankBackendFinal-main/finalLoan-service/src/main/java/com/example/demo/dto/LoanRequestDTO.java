package com.example.demo.dto;

import lombok.Data;
import com.example.demo.entity.LoanType;

@Data
public class LoanRequestDTO {

    private Double amount;
    private Integer tenure;
    private LoanType loanType;
}