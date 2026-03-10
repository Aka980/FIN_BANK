package com.example.demo.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class EmiCalculatorUtil {

    private EmiCalculatorUtil() {}

    public static BigDecimal calculateEmi(BigDecimal principal,
                                          BigDecimal annualRoi,
                                          int tenureMonths) {

        BigDecimal monthlyRate = annualRoi
                .divide(BigDecimal.valueOf(12 * 100.0), 10, RoundingMode.HALF_UP);

        BigDecimal onePlusRPowerN =
                monthlyRate.add(BigDecimal.ONE)
                        .pow(tenureMonths, MathContext.DECIMAL64);

        BigDecimal numerator =
                principal.multiply(monthlyRate).multiply(onePlusRPowerN);

        BigDecimal denominator =
                onePlusRPowerN.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}