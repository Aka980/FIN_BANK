package com.example.demo.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerProfileDTO {

    private Long accountNo;
    private String name;
    private String pan;
    private String email;
    private Double income;

    @com.fasterxml.jackson.annotation.JsonProperty("annualIncome")
    public void setAnnualIncome(Double annualIncome) {
        this.income = annualIncome;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("customer")
    public void unpackNestedCustomer(java.util.Map<String, Object> customer) {
        if (customer != null) {
            if (customer.containsKey("customerName")) {
                this.name = (String) customer.get("customerName");
            }
            if (customer.containsKey("email")) {
                this.email = (String) customer.get("email");
            }
        }
    }
}