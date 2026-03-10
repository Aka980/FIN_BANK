package com.example.demo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmiPaymentHistory {

    private Long paymentId;
    private Long loanId;
    private BigDecimal amountPaid;
    private LocalDate paymentDate;
}