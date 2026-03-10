package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Customer;

public interface CustomerRepository
extends JpaRepository<Customer, Long>{


Customer findByUsername(String username);

Customer findByEmail(String email);

}