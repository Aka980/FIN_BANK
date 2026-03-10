package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;

import com.example.demo.enums.IfscCode;

/*
 * CustomerProfile Entity
 * ----------------------
 * Stores KYC and Loan Eligibility Details.
 * Primary Key = accountNo
 * accountNo is also Foreign Key to Customer table.
 */

@Entity
@Table(name="customer_profile")
@Data
public class CustomerProfile {


    @Id
    @Column(name="account_no")
    private Long accountNo;


    @OneToOne
    @MapsId
    @JoinColumn(name="account_no")
    private Customer customer;


    @Column(unique=true,nullable=false)
    private String pan;


    private LocalDate dob;


    private String address;


    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private IfscCode ifsc;


    private Double annualIncome;


    private String occupation;

    private LocalDate createdDate;

}