package com.example.demo.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanApplicationDTO {
    private Long applicationNo;
    private LocalDate applicationDate;
    private double amount;
    private int tenure;
    private double roi;
    private String status;
    private String pan;
}
