package com.example.demo;

import java.time.LocalDate;

public class CustomerProfileResponseDTO {

    private Long accountNo;
    private String pan;
    private LocalDate dob;
    private String address;
    private String ifsc;
    private Double annualIncome;
    private String occupation;

    // Getters
    public Long getAccountNo() {
        return accountNo;
    }

    public String getPan() {
        return pan;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getAddress() {
        return address;
    }

    public String getIfsc() {
        return ifsc;
    }

    public Double getAnnualIncome() {
        return annualIncome;
    }

    public String getOccupation() {
        return occupation;
    }

    // Setters
    public void setAccountNo(Long accountNo) {
        this.accountNo = accountNo;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public void setAnnualIncome(Double annualIncome) {
        this.annualIncome = annualIncome;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
}
