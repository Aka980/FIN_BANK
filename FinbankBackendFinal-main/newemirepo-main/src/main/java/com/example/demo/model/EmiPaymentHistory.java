package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "emi_payment_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmiPaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @NotNull(message = "Loan ID is required")
    @Column(nullable = false)
    private Long loanId;

    @NotNull(message = "Amount paid is required")
    @DecimalMin(value = "1.00", message = "Amount must be greater than 0")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amountPaid;

    @NotNull(message = "Payment date is required")
    @PastOrPresent(message = "Payment date cannot be in the future")
    @Column(nullable = false)
    private LocalDate paymentDate;
}