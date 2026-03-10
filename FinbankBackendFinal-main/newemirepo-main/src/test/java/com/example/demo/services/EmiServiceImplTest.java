//package com.example.demo.services;
//
//import com.example.demo.Dto.EmiCalculateResponse;
//import com.example.demo.Dto.LoanResponse;
//import com.example.demo.controller.feignclient;
//import com.example.demo.model.EmiCalculatorUtil;
//import com.example.demo.model.EmiRecord;
//import com.example.demo.repo.EMIRepository;
//import com.example.demo.repo.EmiPaymentHistoryRepository;
//import com.example.demo.service.EmiServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class EmiServiceImplTest {
//
//    private EMIRepository emiRecordRepository;
//    private EmiPaymentHistoryRepository emiPaymentHistoryRepository;
//
//    private List<LoanResponse> mockLoans;
//
//    @BeforeEach
//    void setUp() {
//
//        emiRecordRepository = mock(EMIRepository.class);
//        emiPaymentHistoryRepository = mock(EmiPaymentHistoryRepository.class);
//
//        mockLoans = new ArrayList<>();
//
//        LoanResponse loan1 = new LoanResponse();
//        loan1.setApplicationNo(1L);
//        loan1.setAccountNo(101L);
//        loan1.setAmount(10000.0);
//        loan1.setRoi(12.0);
//        loan1.setTenure(12);
//        loan1.setStatus("APPROVED");
//
//        LoanResponse loan2 = new LoanResponse();
//        loan2.setApplicationNo(2L);
//        loan2.setAccountNo(102L);
//        loan2.setAmount(50000.0);
//        loan2.setRoi(10.0);
//        loan2.setTenure(24);
//        loan2.setStatus("APPROVED");
//
//        mockLoans.add(loan1);
//        mockLoans.add(loan2);
//    }
//
//    @Test
//    void testCalculateAndSaveEmi_success() {
//
//        EmiServiceImpl service =
//                new EmiServiceImpl(
//                        new FeignClientFake(mockLoans),
//                        emiRecordRepository,
//                        emiPaymentHistoryRepository
//                );
//
//        LoanResponse loan = mockLoans.get(0);
//
//        EmiCalculateResponse response =
//                service.calculateAndSaveEmi(loan.getApplicationNo());
//
//        assertNotNull(response);
//        assertEquals(loan.getApplicationNo(), response.getLoanId());
//        assertEquals(loan.getAccountNo(), response.getAccountNo());
//
//        BigDecimal expectedEmi = EmiCalculatorUtil.calculateEmi(
//                BigDecimal.valueOf(loan.getAmount()),
//                BigDecimal.valueOf(loan.getRoi()),
//                loan.getTenure()
//        );
//
//        assertEquals(0, expectedEmi.compareTo(response.getMonthlyEmi()));
//
//        ArgumentCaptor<EmiRecord> captor =
//                ArgumentCaptor.forClass(EmiRecord.class);
//
//        verify(emiRecordRepository, times(1)).save(captor.capture());
//
//        EmiRecord savedRecord = captor.getValue();
//
//        assertEquals(loan.getApplicationNo(), savedRecord.getLoanId());
//        assertEquals(loan.getAccountNo(), savedRecord.getAccountNo());
//        assertEquals(0, expectedEmi.compareTo(savedRecord.getMonthlyEmiAmount()));
//        assertEquals(LocalDate.now(), savedRecord.getCreatedDate());
//    }
//
//    @Test
//    void testCalculateAndSaveEmi_loanNotFound() {
//
//        EmiServiceImpl service =
//                new EmiServiceImpl(
//                        new FeignClientFake(mockLoans),
//                        emiRecordRepository,
//                        emiPaymentHistoryRepository
//                );
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () ->
//                service.calculateAndSaveEmi(999L)
//        );
//
//        assertTrue(exception.getMessage().contains("Loan not found"));
//
//        verify(emiRecordRepository, never()).save(any());
//    }
//
//    // Fake Feign Client
//    static class FeignClientFake implements feignclient {
//
//        private final List<LoanResponse> loans;
//
//        public FeignClientFake(List<LoanResponse> loans) {
//            this.loans = loans;
//        }
//
//        @Override
//        public LoanResponse getLoanById(Long id) {
//            return loans.stream()
//                    .filter(l -> l.getApplicationNo().equals(id))
//                    .findFirst()
//                    .orElse(null);
//        }
//
//		@Override
//		public LoanResponse getLoanStatus(Long id) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//    }
//}

package com.example.demo.services;

import com.example.demo.Dto.EmiCalculateResponse;
import com.example.demo.Dto.LoanResponse;
import com.example.demo.controller.feignclient;
import com.example.demo.model.EmiCalculatorUtil;
import com.example.demo.model.EmiRecord;
import com.example.demo.repo.EMIRepository;
import com.example.demo.repo.EmiPaymentHistoryRepository;
import com.example.demo.service.EmiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmiServiceImplTest {

    private EMIRepository emiRecordRepository;
    private EmiPaymentHistoryRepository emiPaymentHistoryRepository;

    private List<LoanResponse> mockLoans;

    @BeforeEach
    void setUp() {
        emiRecordRepository = mock(EMIRepository.class);
        emiPaymentHistoryRepository = mock(EmiPaymentHistoryRepository.class);

        mockLoans = new ArrayList<>();

        LoanResponse loan1 = new LoanResponse();
        loan1.setApplicationNo(1L);
        loan1.setAccountNo(101L);
        loan1.setAmount(10000.0);
        loan1.setRoi(12.0);
        loan1.setTenure(12);
        loan1.setStatus("APPROVED");

        LoanResponse loan2 = new LoanResponse();
        loan2.setApplicationNo(2L);
        loan2.setAccountNo(102L);
        loan2.setAmount(50000.0);
        loan2.setRoi(10.0);
        loan2.setTenure(24);
        loan2.setStatus("APPROVED");

        mockLoans.add(loan1);
        mockLoans.add(loan2);
    }

    @Test
    void testCalculateAndSaveEmi_success() {
        String dummyToken = "Bearer dummy.jwt.token";

        EmiServiceImpl service =
                new EmiServiceImpl(
                        new FeignClientFake(mockLoans),
                        emiRecordRepository,
                        emiPaymentHistoryRepository
                );

        LoanResponse loan = mockLoans.get(0);

        EmiCalculateResponse response =
                service.calculateAndSaveEmi(dummyToken, loan.getApplicationNo());

        assertNotNull(response);
        assertEquals(loan.getApplicationNo(), response.getLoanId());
        assertEquals(loan.getAccountNo(), response.getAccountNo());

        BigDecimal expectedEmi = EmiCalculatorUtil.calculateEmi(
                BigDecimal.valueOf(loan.getAmount()),
                BigDecimal.valueOf(loan.getRoi()),
                loan.getTenure()
        );

        assertEquals(0, expectedEmi.compareTo(response.getMonthlyEmi()));

        ArgumentCaptor<EmiRecord> captor =
                ArgumentCaptor.forClass(EmiRecord.class);

        verify(emiRecordRepository, times(1)).save(captor.capture());

        EmiRecord savedRecord = captor.getValue();

        assertEquals(loan.getApplicationNo(), savedRecord.getLoanId());
        assertEquals(loan.getAccountNo(), savedRecord.getAccountNo());
        assertEquals(0, expectedEmi.compareTo(savedRecord.getMonthlyEmiAmount()));
        assertEquals(LocalDate.now(), savedRecord.getCreatedDate());
    }

    @Test
    void testCalculateAndSaveEmi_loanNotFound() {
        String dummyToken = "Bearer dummy.jwt.token";

        EmiServiceImpl service =
                new EmiServiceImpl(
                        new FeignClientFake(mockLoans),
                        emiRecordRepository,
                        emiPaymentHistoryRepository
                );

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.calculateAndSaveEmi(dummyToken, 999L)
        );

        assertTrue(exception.getMessage().contains("Loan not found"));

        verify(emiRecordRepository, never()).save(any());
    }

    // Fake Feign Client with token support
    static class FeignClientFake implements feignclient {

        private final List<LoanResponse> loans;

        public FeignClientFake(List<LoanResponse> loans) {
            this.loans = loans;
        }

        @Override
        public LoanResponse getLoanById(String token, Long id) {
            // In tests, we ignore the token and just return the loan
            return loans.stream()
                    .filter(l -> l.getApplicationNo().equals(id))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public LoanResponse getLoanStatus(Long id) {
            return null;
        }
    }
}
