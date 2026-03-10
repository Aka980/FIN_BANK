package com.example.demo.repo;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.EmiRecord;

@Repository
public interface EMIRepository extends JpaRepository<EmiRecord, Long> {
	
	List<EmiRecord> findByloanId(Long loanId);
	List<EmiRecord> findAllByLoanId(Long loanId);
}