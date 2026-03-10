package com.example.demo.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class LoanApplicationTest {

    @Test
    void testEntityFields() {

        LoanApplication loan = new LoanApplication();

        loan.setApplicationNo(1L);
        loan.setAccountNo(1001L);
        loan.setApplicationDate(LocalDate.now());
        loan.setAmount(50000.0);
        loan.setTenure(12);
        loan.setRoi(8.5);
        loan.setStatus("PENDING");

        assertEquals(1L, loan.getApplicationNo());
        assertEquals("PENDING", loan.getStatus());
    }
}