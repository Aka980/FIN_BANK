package com.example.demo.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="customers")
@Data
public class Customer {

    @Id
    private Long accountNo;

    @Column(nullable=false)
    private String customerName;

    @Column(unique=true,nullable=false)
    private String username;

    @Column(unique=true,nullable=false)
    private String email;

    @Column(nullable=false)
    private String phone;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable=false)
    private String password;

    @Column(nullable=false)
    private String role;

    private LocalDate createdDate;


    /*
     * Generate FinBank Account Number
     */
    @PrePersist
    public void generateAccountNumber(){

        if(accountNo == null){

            accountNo =
            5000000000L +
            System.currentTimeMillis() % 1000000;
        }
    }
}