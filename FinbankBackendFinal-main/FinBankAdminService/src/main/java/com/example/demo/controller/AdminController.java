package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.CustomerProfileDTO;
import com.example.demo.dto.LoanResponseDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.feign.CustomerClient;
import com.example.demo.feign.EmiClient;
import com.example.demo.feign.LoanClient;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private LoanClient loanClient;

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private EmiClient emiClient;

    // ==============================
    // Delete EMI Record (ADMIN ONLY)
    // ==============================

    @DeleteMapping("/delete-emi/{applicationId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteEmiRecord(
            @PathVariable("applicationId") Long applicationId,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        // Forward deletion to emi-service
        String result = emiClient.deleteEmi(applicationId, token);

        return ResponseEntity.ok(result);
    }

    // ==============================
    // Get All Loans (ADMIN ONLY)
    // ==============================

    @GetMapping("/loans")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<LoanResponseDTO> getAllLoans(HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        return loanClient.getAllLoans(token);
    }

    // ==============================
    // Get Customer Profile
    // ==============================

    @GetMapping("/customer-profile/{accountNo}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CustomerProfileDTO> getCustomerProfileByAccountNo(
            @PathVariable("accountNo") Long accountNo,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        CustomerProfileDTO profile = customerClient.getCustomerByAccountNo(accountNo, token);

        if (profile == null) {
            throw new ResourceNotFoundException(
                    "Customer profile not found for Account No: " + accountNo);
        }

        return ResponseEntity.ok(profile);
    }

    // ==============================
    // Approve Loan
    // ==============================

    @PutMapping("/approve")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public LoanResponseDTO approveLoan(
            @RequestParam("applicationId") Long applicationId,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        return loanClient.updateLoanStatus(
                applicationId,
                "APPROVED",
                "Manually approved by Admin",
                token);
    }

    // ==============================
    // Deny Loan
    // ==============================

    @PutMapping("/deny")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public LoanResponseDTO denyLoan(
            @RequestParam("applicationId") Long applicationId,
            @RequestParam("reason") String reason,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        return loanClient.updateLoanStatus(
                applicationId,
                "DENIED",
                reason,
                token);
    }

    // ==============================
    // Put Loan In Abeyance
    // ==============================

    @PutMapping("/abeyance")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public LoanResponseDTO abeyanceLoan(
            @RequestParam("applicationId") Long applicationId,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        return loanClient.updateLoanStatus(
                applicationId,
                "ABEYANCE",
                "Placed in Abeyance",
                token);
    }

    // ==============================
    // Health Check
    // ==============================

    @GetMapping("/ping")
    public String ping() {
        return "PONG Admin! Authentication succeeded!";
    }
}

// package com.example.demo.controller;
//
// import com.example.demo.dto.CustomerProfileDTO;
// import com.example.demo.dto.LoanResponseDTO;
// import com.example.demo.exception.ResourceNotFoundException;
// import com.example.demo.feign.CustomerClient;
// import com.example.demo.feign.LoanClient;
//
// import jakarta.servlet.http.HttpServletRequest;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
//
// import java.util.List;
//
// @RestController
// public class AdminController {
//
// @Autowired
// private LoanClient loanClient;
//
// @Autowired
// private CustomerClient customerClient;
//
// // ==============================
// // Get All Loans
// // ==============================
// @GetMapping("/admin/loans")
// public List<LoanResponseDTO> getAllLoans(HttpServletRequest request) {
// String token = request.getHeader("Authorization");
// return loanClient.getAllLoans(token);
// }
//
// // ==============================
// // Get Customer Profile For Loan
// // ==============================
//
// @GetMapping("/customer-profile/{accountNo}")
// public ResponseEntity<CustomerProfileDTO> getCustomerProfileByAccountNo(
// @PathVariable("accountNo") Long accountNo,
// HttpServletRequest request) {
//
// String token = request.getHeader("Authorization");
//
// // Get Customer Profile directly
// CustomerProfileDTO profile = customerClient.getCustomerByAccountNo(accountNo,
// token);
//
// if (profile == null) {
// throw new ResourceNotFoundException("Customer profile not found for Account
// No: " + accountNo);
// }
//
// return ResponseEntity.ok(profile);
// }
// // @GetMapping("/customer-profile/{applicationNo}")
// // public ResponseEntity<Map<String, Object>> getCustomerProfileForLoan(
// // @PathVariable Long applicationNo,
// // HttpServletRequest request) {
// //
// // String token = request.getHeader("Authorization");
// //
// // // 1️⃣ Get Loan By ID (Better than filtering all loans)
// // LoanResponseDTO loan =
// // loanClient.getLoanById(applicationNo, token);
// //
// // if (loan == null) {
// // throw new RuntimeException("Loan not found");
// // }
// //
// // // 2️⃣ Call Customer-Service
// // CustomerProfileDTO profile =
// // customerClient.getCustomerByAccountNo(
// // loan.getAccountNo(),
// // token);
// //
// // if (profile == null) {
// // throw new RuntimeException("Customer profile not found");
// // }
// //
// // // 3️⃣ Prepare Response
// // Map<String, Object> response = new HashMap<>();
// // response.put("loanDetails", loan);
// // response.put("customerProfile", profile);
// //
// // return ResponseEntity.ok(response);
// // }
//
// // ==============================
// // Manual Approve
// // ==============================
// @PutMapping("/admin/approve")
// public LoanResponseDTO approveLoan(
// @RequestParam("applicationId") Long applicationId,
// HttpServletRequest request) {
//
// String token = request.getHeader("Authorization");
//
// return loanClient.updateLoanStatus(
// applicationId,
// "APPROVED",
// "Manually approved by Admin",
// token);
// }
//
// // ==============================
// // Manual Deny
// // ==============================
// @PutMapping("/admin/deny")
// public LoanResponseDTO denyLoan(
// @RequestParam("applicationId") Long applicationId,
// @RequestParam("reason") String reason,
// HttpServletRequest request) {
//
// String token = request.getHeader("Authorization");
//
// return loanClient.updateLoanStatus(
// applicationId,
// "DENIED",
// reason,
// token);
// }
//
// // ==============================
// // Manual Abeyance
// // ==============================
// @PutMapping("/admin/abeyance")
// public LoanResponseDTO abeyanceLoan(
// @RequestParam("applicationId") Long applicationId,
// HttpServletRequest request) {
//
// String token = request.getHeader("Authorization");
//
// return loanClient.updateLoanStatus(
// applicationId,
// "ABEYANCE",
// "Placed in Abeyance",
// token);
// }
//
// @GetMapping("/admin/ping")
// public String ping() {
// return "PONG Admin! Authentication succeeded!";
// }
// }