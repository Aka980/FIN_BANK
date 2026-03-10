package com.example.demo.Dto;

import java.math.BigDecimal;


import lombok.Data;

@Data
public class QuickCheckRequest {
	    private BigDecimal principalAmount;
	    private BigDecimal roi;
	    private Integer tenure;
	}
