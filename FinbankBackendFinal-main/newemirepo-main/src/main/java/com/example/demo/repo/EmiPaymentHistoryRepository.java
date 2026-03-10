package com.example.demo.repo;

import com.example.demo.model.EmiPaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface EmiPaymentHistoryRepository
        extends JpaRepository<EmiPaymentHistory, Long> {

    // Get total amount paid for a loan
    @Query("SELECT COALESCE(SUM(e.amountPaid), 0) " +
           "FROM EmiPaymentHistory e " +
           "WHERE e.loanId = :loanId")
    BigDecimal getTotalPaidByLoanId(@Param("loanId") Long loanId);

    long countByLoanId(Long loanId);
    
    List<EmiPaymentHistory> findByLoanId(Long loanId);
   
}