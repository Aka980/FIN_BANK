package com.example.demo.Dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmiOutstandingResponse {

    private Long loanId;
    private BigDecimal totalLoanAmount;
    private BigDecimal totalPaid;
    private BigDecimal remainingAmount;
    private BigDecimal monthlyEmi;
}