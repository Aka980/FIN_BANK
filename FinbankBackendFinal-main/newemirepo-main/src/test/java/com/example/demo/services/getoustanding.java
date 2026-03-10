//package com.example.demo.services;
//
//import com.example.demo.Dto.EmiOutstandingResponse;
//import com.example.demo.model.EmiRecord;
//import com.example.demo.repo.EmiPaymentHistoryRepository;
//import com.example.demo.service.EmiServiceImpl;
//import com.example.demo.repo.EMIRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class getoustanding{
//
//    @Mock
//    private EMIRepository emiRecordRepository;
//
//    @Mock
//    private EmiPaymentHistoryRepository emiPaymentHistoryRepository;
//
//    @InjectMocks
//    private EmiServiceImpl emiService;
//
//    @Test
//    void testGetOutstanding_PartialPayment() {
//
//        Long loanId = 101L;
//
//        EmiRecord record = EmiRecord.builder()
//                .loanId(loanId)
//                .principalAmount(new BigDecimal("120000"))
//                .monthlyEmiAmount(new BigDecimal("10000"))
//                .tenure(12)
//                .createdDate(LocalDate.of(2026,1,1))
//                .build();
//
//        when(emiRecordRepository.findByloanId(loanId))
//                .thenReturn(List.of(record));
//
//        when(emiPaymentHistoryRepository.getTotalPaidByLoanId(loanId))
//                .thenReturn(new BigDecimal("30000"));
//
//        EmiOutstandingResponse response = emiService.getOutstanding(loanId);
//
//        assertEquals(new BigDecimal("120000"), response.getTotalLoanAmount());
//        assertEquals(new BigDecimal("30000"), response.getTotalPaid());
//        assertEquals(new BigDecimal("90000"), response.getRemainingAmount());
//    }
//    
//    @Test
//    void testGetNextDue_PartialPayment() {
//
//        Long loanId = 101L;
//
//        EmiRecord record = EmiRecord.builder()
//                .loanId(loanId)
//                .principalAmount(new BigDecimal("120000"))
//                .monthlyEmiAmount(new BigDecimal("10000"))
//                .tenure(12)
//                .createdDate(LocalDate.of(2026,1,1))
//                .build();
//
//        when(emiRecordRepository.findByloanId(loanId))
//                .thenReturn(List.of(record));
//
//        when(emiPaymentHistoryRepository.countByLoanId(loanId))
//                .thenReturn(3L);
//
//        var response = emiService.getNextDue(loanId);
//
//        assertFalse(response.isLoanClosed());
//        assertEquals(9, response.getRemainingEmis());
//        assertEquals(LocalDate.of(2026,4,1), response.getNextDueDate());
//    }
//}

package com.example.demo.services;

import com.example.demo.Dto.EmiNextDueResponse;
import com.example.demo.Dto.EmiOutstandingResponse;
import com.example.demo.Dto.LoanResponse;
import com.example.demo.controller.feignclient;
import com.example.demo.model.EmiRecord;
import com.example.demo.repo.EmiPaymentHistoryRepository;
import com.example.demo.repo.EMIRepository;
import com.example.demo.service.EmiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class  getoustanding{

    @Mock
    private EMIRepository emiRecordRepository;

    @Mock
    private EmiPaymentHistoryRepository emiPaymentHistoryRepository;

    @Mock
    private feignclient loanClient; // ✅ Mock Feign client

    @InjectMocks
    private EmiServiceImpl emiService;

    private String dummyToken = "Bearer dummy.jwt.token";

    private LoanResponse dummyLoan;

    @BeforeEach
    void setup() {
        // create a dummy loan
        dummyLoan = new LoanResponse();
        dummyLoan.setApplicationNo(101L);
        dummyLoan.setAccountNo(1001L);
        dummyLoan.setAmount(120000.0);
        dummyLoan.setRoi(12.0);
        dummyLoan.setTenure(12);
        dummyLoan.setStatus("APPROVED");
    }

    @Test
    void testGetOutstanding_PartialPayment() {
        EmiRecord record = EmiRecord.builder()
                .loanId(101L)
                .principalAmount(new BigDecimal("120000"))
                .monthlyEmiAmount(new BigDecimal("10000"))
                .tenure(12)
                .createdDate(LocalDate.of(2026, 1, 1))
                .build();

        when(loanClient.getLoanById(dummyToken, 101L)).thenReturn(dummyLoan);
        when(emiRecordRepository.findByloanId(101L)).thenReturn(List.of(record));
        when(emiPaymentHistoryRepository.getTotalPaidByLoanId(101L)).thenReturn(new BigDecimal("30000"));

        EmiOutstandingResponse response = emiService.getOutstanding(dummyToken, 101L);

        assertEquals(new BigDecimal("120000"), response.getTotalLoanAmount());
        assertEquals(new BigDecimal("30000"), response.getTotalPaid());
        assertEquals(new BigDecimal("90000"), response.getRemainingAmount());
    }

    @Test
    void testGetNextDue_PartialPayment() {
        EmiRecord record = EmiRecord.builder()
                .loanId(101L)
                .principalAmount(new BigDecimal("120000"))
                .monthlyEmiAmount(new BigDecimal("10000"))
                .tenure(12)
                .createdDate(LocalDate.of(2026, 1, 1))
                .build();

        when(loanClient.getLoanById(dummyToken, 101L)).thenReturn(dummyLoan);
        when(emiRecordRepository.findByloanId(101L)).thenReturn(List.of(record));
        when(emiPaymentHistoryRepository.countByLoanId(101L)).thenReturn(3L);

        EmiNextDueResponse response = emiService.getNextDue(dummyToken, 101L);

        assertFalse(response.isLoanClosed());
        assertEquals(9, response.getRemainingEmis());
        assertEquals(LocalDate.of(2026, 4, 1), response.getNextDueDate());
    }
}
