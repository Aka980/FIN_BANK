package com.example.demo.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class EmiPaymentRequest {
    private Long loanId;
    private BigDecimal amountPaid;
    private LocalDate paymentDate;
}
