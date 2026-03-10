package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.LoanRequestDTO;
import com.example.demo.dto.LoanResponseDTO;

public interface LoanService {

    LoanResponseDTO applyLoan(LoanRequestDTO dto, Long accountNo, String token);
    // LoanResponseDTO applyLoan(
    // LoanRequestDTO dto,
    // Long accountNo,
    // String token);
    // LoanResponseDTO applyLoan(LoanRequestDTO dto, Long accountNo, String
    // username, String token);

    LoanResponseDTO updateLoan(Long id, LoanRequestDTO request);

    void cancelLoan(Long id);

    LoanResponseDTO getLoanStatus(Long id);

    List<LoanResponseDTO> getAllLoans();

    // LoanResponseDTO getLoanById(Long loanId, String username);

    LoanResponseDTO updateLoanStatus(Long id, String status, String reason);

    List<LoanResponseDTO> getLoansByAccountNo(Long accountNo);

    LoanResponseDTO getLoanById(Long loanId, Long accountNo, String role);
}