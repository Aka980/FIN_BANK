package com.example.demo.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import com.example.demo.dto.CustomerProfileDTO;
import com.example.demo.dto.LoanResponseDTO;
import com.example.demo.dto.LoanType;
import com.example.demo.feign.CustomerClient;
import com.example.demo.feign.LoanClient;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    private LoanClient loanClient;

    @Mock
    private CustomerClient customerClient;

    @InjectMocks
    private AdminController adminController;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer test-token");
    }

    @Test
    void testGetAllLoans() {
        LoanResponseDTO loan1 = new LoanResponseDTO();
        loan1.setApplicationNo(1L);
        loan1.setAccountNo(100L);
        loan1.setApplicationDate(LocalDate.now());
        loan1.setAmount(50000.0);
        loan1.setPan("ABCDE1234F");
        loan1.setTenure(12);
        loan1.setRoi(10.5);
        loan1.setStatus("PENDING");
        loan1.setLoanType(LoanType.CAR_LOAN);

        LoanResponseDTO loan2 = new LoanResponseDTO();
        loan2.setApplicationNo(2L);
        loan2.setAccountNo(101L);
        loan2.setApplicationDate(LocalDate.now());
        loan2.setAmount(20000.0);
        loan2.setPan("VWXYZ5678G");
        loan2.setTenure(24);
        loan2.setRoi(12.5);
        loan2.setStatus("APPROVED");
        loan2.setLoanType(LoanType.BIKE_LOAN);
        when(loanClient.getAllLoans("Bearer test-token")).thenReturn(Arrays.asList(loan1, loan2));

        List<LoanResponseDTO> result = adminController.getAllLoans(request);

        assertEquals(2, result.size());
        verify(loanClient, times(1)).getAllLoans(anyString());
    }

    @Test
    void testGetCustomerProfileByAccountNo_Success() {
        CustomerProfileDTO mockProfile = new CustomerProfileDTO();
        mockProfile.setAccountNo(100L);
        mockProfile.setName("John Doe");
        mockProfile.setPan("ABCDE1234F");
        mockProfile.setIncome(500000.0);

        when(customerClient.getCustomerByAccountNo(100L, "Bearer test-token")).thenReturn(mockProfile);

        ResponseEntity<CustomerProfileDTO> response = adminController.getCustomerProfileByAccountNo(100L, request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("John Doe", response.getBody().getName());
        assertEquals(500000.0, response.getBody().getIncome());
        verify(customerClient, times(1)).getCustomerByAccountNo(100L, "Bearer test-token");
    }

//    @Test
//    void testGetCustomerProfileByAccountNo_NotFound() {
//        when(customerClient.getCustomerByAccountNo(999L, "Bearer test-token")).thenReturn(null);
//
//        assertThrows(ResourceNotFoundException.class, () -> adminController.getCustomerProfileByAccountNo(999L, request));
//        verify(customerClient, times(1)).getCustomerByAccountNo(999L, "Bearer test-token");
//    }

    @Test
    void testApproveLoan() {
        LoanResponseDTO mockLoan = new LoanResponseDTO();
        mockLoan.setStatus("APPROVED");

        when(loanClient.updateLoanStatus(1L, "APPROVED", "Manually approved by Admin", "Bearer test-token"))
                .thenReturn(mockLoan);

        LoanResponseDTO result = adminController.approveLoan(1L, request);

        assertEquals("APPROVED", result.getStatus());
        verify(loanClient, times(1)).updateLoanStatus(1L, "APPROVED", "Manually approved by Admin",
                "Bearer test-token");
    }

    @Test
    void testDenyLoan() {
        LoanResponseDTO mockLoan = new LoanResponseDTO();
        mockLoan.setStatus("DENIED");

        when(loanClient.updateLoanStatus(1L, "DENIED", "Low Credit Score", "Bearer test-token")).thenReturn(mockLoan);

        LoanResponseDTO result = adminController.denyLoan(1L, "Low Credit Score", request);

        assertEquals("DENIED", result.getStatus());
        verify(loanClient, times(1)).updateLoanStatus(1L, "DENIED", "Low Credit Score", "Bearer test-token");
    }

    @Test
    void testAbeyanceLoan() {
        LoanResponseDTO mockLoan = new LoanResponseDTO();
        mockLoan.setStatus("ABEYANCE");

        when(loanClient.updateLoanStatus(1L, "ABEYANCE", "Placed in Abeyance", "Bearer test-token"))
                .thenReturn(mockLoan);

        LoanResponseDTO result = adminController.abeyanceLoan(1L, request);

        assertEquals("ABEYANCE", result.getStatus());
        verify(loanClient, times(1)).updateLoanStatus(1L, "ABEYANCE", "Placed in Abeyance", "Bearer test-token");
    }

    @Test
    void testPing() {
        String result = adminController.ping();
        assertEquals("PONG Admin! Authentication succeeded!", result);
    }
}
