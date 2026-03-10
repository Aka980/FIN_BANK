package com.example.demo.controller;

import com.example.demo.dto.*;

import com.example.demo.service.LoanService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import java.util.List;
import com.example.demo.security.*;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final JwtUtil jwtUtil;

    /*
     * @PostMapping("/apply") public ResponseEntity<?> applyLoan(
     * 
     * @RequestBody LoanRequestDTO dto, Authentication authentication,
     * 
     * @RequestHeader("Authorization") String token) {
     * 
     * // 🔥 Extract username from JWT String username = authentication.getName();
     * 
     * return ResponseEntity.ok( loanService.applyLoan(dto, username, token)); }
     */

    // @PostMapping("/apply")
    // public ResponseEntity<?> applyLoan(
    // @RequestBody LoanRequestDTO dto,
    // Authentication authentication,
    // @RequestHeader("Authorization") String token) {
    //
    // Long accountNo = Long.parseLong(authentication.getName());
    //
    // String cleanToken = token.replace("Bearer ", "");
    // String username = jwtUtil.extractUsername(cleanToken); // ⭐ ADD THIS
    //
    // return ResponseEntity.ok(
    // loanService.applyLoan(dto, username, token));
    // }

    // @PostMapping("/apply")
    // public ResponseEntity<LoanResponseDTO> applyLoan(
    // @RequestBody LoanRequestDTO request,
    // @RequestHeader("Authorization") String token) {
    //
    // String cleanToken = token.replace("Bearer ", "");
    // String username = jwtUtil.extractUsername(cleanToken);
    //
    // LoanResponseDTO response =
    // loanService.applyLoan(request, username, token);
    //
    // return ResponseEntity.ok(response);
    // }
    @PostMapping("/apply")
    public ResponseEntity<LoanResponseDTO> applyLoan(
            @RequestBody LoanRequestDTO request,
            @RequestHeader("Authorization") String token) {

        String cleanToken = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(cleanToken);
        Long accountNo = jwtUtil.extractAccountNo(cleanToken);
        System.out.println("DEBUG: Extracted accountNo: " + accountNo + " for username: " + username);

        // 🔥 PASS ORIGINAL TOKEN (with Bearer) to service
        LoanResponseDTO response = loanService.applyLoan(request, accountNo, token);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<LoanResponseDTO> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody LoanRequestDTO request) {

        return ResponseEntity.ok(loanService.updateLoan(id, request));
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<String> cancel(@PathVariable("id") Long id) {
        loanService.cancelLoan(id);
        return ResponseEntity.ok("Loan Cancelled Successfully");
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<LoanResponseDTO> status(@PathVariable("id") Long id) {
        return ResponseEntity.ok(loanService.getLoanStatus(id));
    }

    /*
     * @GetMapping("/id/{loanId}") public ResponseEntity<LoanResponseDTO>
     * getLoanById(
     * 
     * @PathVariable("loanId") Long loanId) {
     * 
     * return ResponseEntity.ok( loanService.getLoanById(loanId)); }
     */

    // @GetMapping("/id/{loanId}")
    // public ResponseEntity<LoanResponseDTO> getLoanById(
    // @PathVariable Long loanId,
    // @RequestHeader("Authorization") String token) {
    //
    // // 🔥 Remove Bearer before parsing
    // String cleanToken = token.replace("Bearer ", "");
    //
    // String username = jwtUtil.extractUsername(cleanToken);
    //
    // LoanResponseDTO loan =
    // loanService.getLoanById(loanId, username);
    //
    // return ResponseEntity.ok(loan);
    // }
    @GetMapping("/id/{loanId}")
    public ResponseEntity<LoanResponseDTO> getLoanById(
            @PathVariable Long loanId,
            @RequestHeader("Authorization") String token) {

        String cleanToken = token.replace("Bearer ", "");

        Long accountNo = jwtUtil.extractAccountNo(cleanToken);   // FIXED
        String role = jwtUtil.extractRole(cleanToken);

        LoanResponseDTO loan = loanService.getLoanById(loanId, accountNo, role);

        return ResponseEntity.ok(loan);
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanResponseDTO> updateLoanStatus(
            @RequestParam("applicationId") Long applicationId,
            @RequestParam("status") String status,
            @RequestParam(value = "reason", required = false) String reason) {
        return ResponseEntity.ok(loanService.updateLoanStatus(applicationId, status, reason));
    }

    @GetMapping("/all")
    public ResponseEntity<List<LoanResponseDTO>> getCustomerLoans(Authentication authentication) {

        Long accountNo = Long.parseLong(authentication.getName());

        return ResponseEntity.ok(
                loanService.getLoansByAccountNo(accountNo));
    }

    @GetMapping("/admin/all")
    // @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<LoanResponseDTO>> getAllLoansForAdmin() {

        return ResponseEntity.ok(
                loanService.getAllLoans());
    }

    // @GetMapping("/all")
    // @PreAuthorize("hasRole('ADMIN')")
    // public ResponseEntity<List<LoanResponseDTO>> getAllLoans() {
    // return ResponseEntity.ok(loanService.getAllLoans());
    // }
    //
    @GetMapping("/account/{accountNo}")
    public ResponseEntity<List<LoanResponseDTO>> getLoansByAccountNo(
            @PathVariable Long accountNo,
            @RequestHeader("Authorization") String token) {

        List<LoanResponseDTO> loans = loanService.getLoansByAccountNo(accountNo);
        return ResponseEntity.ok(loans);
    }
}