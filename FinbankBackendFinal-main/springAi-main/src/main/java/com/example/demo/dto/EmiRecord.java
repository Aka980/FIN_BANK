package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmiRecord {

    private Long emiId;
    private Long loanId;
    private Long accountNo;
    private BigDecimal principalAmount;
    private BigDecimal roi;
    private Integer tenure;
    private BigDecimal monthlyEmiAmount;
    private LocalDate createdDate;
}