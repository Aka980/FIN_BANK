package com.example.demo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class EmiFullHistoryResponse {

    private EmiRecord emiRecord;

    private List<EmiPaymentHistory> paymentHistory;

    private BigDecimal outstandingAmount;

    private LocalDate nextDueDate;
}