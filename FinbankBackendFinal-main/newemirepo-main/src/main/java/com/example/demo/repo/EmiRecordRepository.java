package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.EmiRecord;

import java.util.List;

@Repository
public interface EmiRecordRepository extends JpaRepository<EmiRecord, Long> {

    // Find all EMI records by loan ID
    List<EmiRecord> findAllByLoanId(Long loanId);

    // Optional: if you want to check existence easily
    boolean existsByLoanId(Long loanId);

    // Delete all records by loan ID
    void deleteAllByLoanId(Long loanId);
}