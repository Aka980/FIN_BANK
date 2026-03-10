//package com.example.demo.services;
//
//import com.example.demo.model.EmiPaymentHistory;
//import com.example.demo.repo.EmiPaymentHistoryRepository;
//import com.example.demo.service.EmiServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class EmiPaymentServiceTest {
//
//    private EmiPaymentHistoryRepository paymentRepository;
//    private EmiServiceImpl emiService;
//
//    @BeforeEach
//    void setUp() {
//        paymentRepository = mock(EmiPaymentHistoryRepository.class);
//        emiService = new EmiServiceImpl(null, null, paymentRepository);
//    }
//
//    @Test
//    void testRecordPayment_success() {
//        Long loanId = 1L;
//        BigDecimal amountPaid = new BigDecimal("5000");
//        LocalDate paymentDate = LocalDate.now();
//
//        // Mock repository save
//        when(paymentRepository.save(any(EmiPaymentHistory.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        EmiPaymentHistory result = emiService.recordPayment(loanId, amountPaid, paymentDate);
//
//        // Verify repository save called once
//        ArgumentCaptor<EmiPaymentHistory> captor = ArgumentCaptor.forClass(EmiPaymentHistory.class);
//        verify(paymentRepository, times(1)).save(captor.capture());
//
//        EmiPaymentHistory saved = captor.getValue();
//        assertEquals(loanId, saved.getLoanId());
//        assertEquals(amountPaid, saved.getAmountPaid());
//        assertEquals(paymentDate, saved.getPaymentDate());
//
//        // Also verify returned object is the same
//        assertEquals(saved, result);
//    }
//
//    @Test
//    void testGetPaymentHistoryByLoanId_success() {
//        Long loanId = 1L;
//        EmiPaymentHistory payment1 = EmiPaymentHistory.builder()
//                .loanId(loanId)
//                .amountPaid(new BigDecimal("1000"))
//                .paymentDate(LocalDate.now())
//                .build();
//        EmiPaymentHistory payment2 = EmiPaymentHistory.builder()
//                .loanId(loanId)
//                .amountPaid(new BigDecimal("2000"))
//                .paymentDate(LocalDate.now())
//                .build();
//
//        when(paymentRepository.findByLoanId(loanId))
//                .thenReturn(Arrays.asList(payment1, payment2));
//
//        List<EmiPaymentHistory> history = emiService.getPaymentHistoryByLoanId(loanId);
//
//        assertEquals(2, history.size());
//        assertTrue(history.contains(payment1));
//        assertTrue(history.contains(payment2));
//
//        verify(paymentRepository, times(1)).findByLoanId(loanId);
//    }
//
//    @Test
//    void testGetPaymentHistoryByLoanId_emptyHistory_throwsException() {
//        Long loanId = 99L;
//
//        when(paymentRepository.findByLoanId(loanId))
//                .thenReturn(Collections.emptyList());
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () ->
//                emiService.getPaymentHistoryByLoanId(loanId)
//        );
//
//        assertTrue(exception.getMessage().contains("No payment history found"));
//        verify(paymentRepository, times(1)).findByLoanId(loanId);
//    }
//}
package com.example.demo.services;

import com.example.demo.Dto.LoanResponse;
import com.example.demo.model.EmiPaymentHistory;
import com.example.demo.model.EmiRecord;
import com.example.demo.repo.EmiPaymentHistoryRepository;
import com.example.demo.repo.EMIRepository;
import com.example.demo.service.EmiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmiPaymentServiceTest {

    @Mock
    private EMIRepository emiRecordRepository;

    @Mock
    private EmiPaymentHistoryRepository paymentRepository;

    @Mock
    private com.example.demo.controller.feignclient loanClient;

    private EmiServiceImpl emiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emiService = new EmiServiceImpl(loanClient, emiRecordRepository, paymentRepository);
    }

    @Test
    void testRecordPayment_success() {
        Long loanId = 1L;
        BigDecimal amountPaid = new BigDecimal("5000");
        LocalDate paymentDate = LocalDate.now();
        String dummyToken = "Bearer dummy.jwt.token";

        EmiRecord dummyEmiRecord = EmiRecord.builder()
                .loanId(loanId)
                .accountNo(101L)
                .principalAmount(new BigDecimal("10000"))
                .monthlyEmiAmount(new BigDecimal("2000"))
                .tenure(12)
                .createdDate(LocalDate.now())
                .build();

        when(emiRecordRepository.findAllByLoanId(loanId))
                .thenReturn(List.of(dummyEmiRecord));

        LoanResponse dummyLoan = new LoanResponse();
        dummyLoan.setApplicationNo(loanId);
        dummyLoan.setAccountNo(101L);
        dummyLoan.setAmount(10000.0);
        dummyLoan.setRoi(10.0);
        dummyLoan.setTenure(12);
        dummyLoan.setStatus("APPROVED");

        when(loanClient.getLoanById(dummyToken, loanId))
                .thenReturn(dummyLoan);

        when(paymentRepository.save(any(EmiPaymentHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EmiPaymentHistory result = emiService.recordPayment(dummyToken, loanId, amountPaid, paymentDate);

        assertEquals(loanId, result.getLoanId());
    }

    @Test
    void testGetPaymentHistoryByLoanId_success() {
        Long loanId = 1L;
        String dummyToken = "Bearer dummy.jwt.token";

        EmiPaymentHistory payment1 = EmiPaymentHistory.builder()
                .loanId(loanId)
                .amountPaid(new BigDecimal("1000"))
                .paymentDate(LocalDate.now())
                .build();
        EmiPaymentHistory payment2 = EmiPaymentHistory.builder()
                .loanId(loanId)
                .amountPaid(new BigDecimal("2000"))
                .paymentDate(LocalDate.now())
                .build();

        when(paymentRepository.findByLoanId(loanId))
                .thenReturn(Arrays.asList(payment1, payment2));

        // Mock Feign client to return a loan so token check passes
        LoanResponse dummyLoan = new LoanResponse();
        dummyLoan.setApplicationNo(loanId);
        dummyLoan.setAccountNo(101L);
        dummyLoan.setAmount(10000.0);
        dummyLoan.setRoi(10.0);
        dummyLoan.setTenure(12);
        dummyLoan.setStatus("APPROVED");

        when(loanClient.getLoanById(dummyToken, loanId))
                .thenReturn(dummyLoan);

        List<EmiPaymentHistory> history = emiService.getPaymentHistoryByLoanId(dummyToken, loanId);

        assertEquals(2, history.size());
        assertTrue(history.contains(payment1));
        assertTrue(history.contains(payment2));
        verify(paymentRepository, times(1)).findByLoanId(loanId);
    }

    @Test
    void testGetPaymentHistoryByLoanId_emptyHistory_throwsException() {
        Long loanId = 99L;
        String dummyToken = "Bearer dummy.jwt.token";

        when(paymentRepository.findByLoanId(loanId))
                .thenReturn(Collections.emptyList());

        // Mock Feign client to return a dummy loan so token check passes
        LoanResponse dummyLoan = new LoanResponse();
        dummyLoan.setApplicationNo(loanId);
        dummyLoan.setAccountNo(999L);
        dummyLoan.setAmount(0.0);
        dummyLoan.setRoi(0.0);
        dummyLoan.setTenure(0);
        dummyLoan.setStatus("APPROVED");

        when(loanClient.getLoanById(dummyToken, loanId))
                .thenReturn(dummyLoan);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emiService.getPaymentHistoryByLoanId(dummyToken, loanId)
        );

        assertTrue(exception.getMessage().contains("No payments found"));
        verify(paymentRepository, times(1)).findByLoanId(loanId);
    }
}