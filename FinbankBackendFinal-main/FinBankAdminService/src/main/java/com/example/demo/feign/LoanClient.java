package com.example.demo.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.config.FeignConfig;
import com.example.demo.dto.LoanResponseDTO;

@FeignClient(name = "loan-service", configuration = FeignConfig.class)
public interface LoanClient {

        // ✅ Update Loan Status (Approve / Deny / Abeyance)
        @PutMapping("/loans/status")
        LoanResponseDTO updateLoanStatus(
                        @RequestParam("applicationId") Long applicationId,
                        @RequestParam("status") String status,
                        @RequestParam(value = "reason", required = false) String reason,
                        @RequestHeader("Authorization") String token);

        // ✅ Get All Loans
        @GetMapping("/loans/admin/all")
        List<LoanResponseDTO> getAllLoans(
                        @RequestHeader("Authorization") String token);

        // // ✅ Get Loan By Application ID (THIS IS WHAT YOU ASKED)
        // @GetMapping("/loans/{applicationNo}")
        // LoanResponseDTO getLoanById(
        // @PathVariable("applicationNo") Long applicationNo);
        // // @RequestHeader("Authorization") String token);

        @GetMapping("/loans/id/{loanId}")
        LoanResponseDTO getLoanById(
                        @PathVariable("loanId") Long loanId,
                        @RequestHeader("Authorization") String token);
}