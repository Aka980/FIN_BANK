package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "loan_application")
@Data
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationNo;

    private Long accountNo;

    private String pan;

    private Double amount;

    private Integer tenure;

    private Double roi;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType;   // ✔ FIXED
    private String status;
    private LocalDate applicationDate;
    
}

