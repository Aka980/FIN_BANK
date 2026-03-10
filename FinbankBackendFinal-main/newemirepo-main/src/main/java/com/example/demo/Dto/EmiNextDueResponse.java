package com.example.demo.Dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class EmiNextDueResponse {

    private Long loanId;
    private LocalDate nextDueDate;
    private BigDecimal emiAmount;
    private Integer remainingEmis;
    private boolean loanClosed;
}