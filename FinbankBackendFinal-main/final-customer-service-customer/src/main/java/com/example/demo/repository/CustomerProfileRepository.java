package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.CustomerProfile;

public interface CustomerProfileRepository
extends JpaRepository<CustomerProfile, Long>{

CustomerProfile findByAccountNo(Long accountNo);

CustomerProfile findByPan(String pan);

}