package com.example.demo.Dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanResponse {

    private Long applicationNo;      // loan id
    private Long accountNo;          // account number
    private LocalDate applicationDate;
    private Double amount;
    private String pan;              // added
    private Integer tenure;
    private Double roi;
    private String status;
    private String loanType;         // use String instead of enum

    // ✅ Full constructor
    public LoanResponse(Long applicationNo,
                        Long accountNo,
                        LocalDate applicationDate,
                        Double amount,
                        String pan,
                        Integer tenure,
                        Double roi,
                        String status,
                        String loanType) {

        this.applicationNo = applicationNo;
        this.accountNo = accountNo;
        this.applicationDate = applicationDate;
        this.amount = amount;
        this.pan = pan;
        this.tenure = tenure;
        this.roi = roi;
        this.status = status;
        this.loanType = loanType;
    }
}