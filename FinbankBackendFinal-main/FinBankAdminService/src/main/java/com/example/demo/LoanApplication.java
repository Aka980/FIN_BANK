package com.example.demo;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanApplication {
    private Long applicationNo;
    private LocalDate applicationDate;
    private double amount;
    private int tenure;
    private double roi;
    private String status;
    private String pan;
}
