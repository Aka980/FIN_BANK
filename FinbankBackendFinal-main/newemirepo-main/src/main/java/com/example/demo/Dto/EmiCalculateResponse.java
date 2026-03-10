package com.example.demo.Dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EmiCalculateResponse {

    private Long loanId;
    private Long accountNo;

    private BigDecimal principalAmount;
    private BigDecimal roi;
    private Integer tenure;

    private BigDecimal monthlyEmi;
}