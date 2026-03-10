package com.example.demo.controller;

import java.math.BigDecimal;


import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Dto.EmiCalculateResponse;
import com.example.demo.Dto.EmiFullHistoryResponse;
import com.example.demo.Dto.EmiNextDueResponse;
import com.example.demo.Dto.EmiOutstandingResponse;
import com.example.demo.Dto.EmiPaymentRequest;
import com.example.demo.Dto.QuickCheckRequest;
import com.example.demo.model.EmiPaymentHistory;
import com.example.demo.model.EmiRecord;
import com.example.demo.repo.EmiRecordRepository;
import com.example.demo.service.EmiService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/emi")
@RequiredArgsConstructor
public class EmiController {
    private final EmiService emiService;

    private final EmiRecordRepository emiRecordRepository;

    // Anyone authenticated can do a quick check
    @PostMapping("/quick-check")
    public BigDecimal quickCheck(@RequestBody QuickCheckRequest request) {
        return emiService.quickEmiCheck(
                request.getPrincipalAmount(),
                request.getRoi(),
                request.getTenure());
    }

    @PostMapping("/calculate/{loanId}")
    public EmiCalculateResponse calculateEmi(
            @RequestHeader("Authorization") String token,
            @PathVariable("loanId") Long loanId) {

        return emiService.calculateAndSaveEmi(token, loanId);
    }

    // @PostMapping("/calculate/{loanId}")
    // public EmiCalculateResponse calculateEmi(@PathVariable Long loanId) {
    // return emiService.calculateAndSaveEmi(loanId);
    // }

    // Only CUSTOMER can record payment

    @PostMapping("/record")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<EmiPaymentHistory> recordPayment(
            @RequestHeader("Authorization") String token,
            @RequestBody EmiPaymentRequest request) {

        EmiPaymentHistory savedPayment = emiService.recordPayment(
                token,
                request.getLoanId(),
                request.getAmountPaid(),
                request.getPaymentDate());

        return ResponseEntity.ok(savedPayment);
    }
    //

    @GetMapping("/history/{loanId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public List<EmiPaymentHistory> getPaymentHistory(
            @PathVariable Long loanId,
            @RequestHeader("Authorization") String token) {

        return emiService.getPaymentHistoryByLoanId(token, loanId);
    }

    // just for testing purpose
    @GetMapping("/test")

    public String test() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authorities: " + auth.getAuthorities());

        return "Check console";
    }

    @GetMapping("/details/{loanId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public EmiFullHistoryResponse getFullDetails(@RequestHeader("Authorization") String token,
            @PathVariable("loanId") Long loanId) {
        return emiService.getFullHistory(token, loanId);
    }

    @GetMapping("/outstanding/{loanId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public EmiOutstandingResponse getOutstanding(@RequestHeader("Authorization") String token,
            @PathVariable Long loanId) {
        return emiService.getOutstanding(token, loanId);
    }

    @GetMapping("/next-due/{loanId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public EmiNextDueResponse getNextDue(@RequestHeader("Authorization") String token,
            @PathVariable("loanId") Long loanId) {
        return emiService.getNextDue(token, loanId);
    }

    @DeleteMapping("/delete/{loanId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEmi(@PathVariable Long loanId) {

        emiService.deleteEmiRecordIfRejected(loanId);

        return ResponseEntity.ok("EMI record deleted successfully.");
    }
}

// @RestController
// @RequestMapping("/emi")
// @RequiredArgsConstructor
// public class EmiController {
// private final EmiService emiService;
//
// private final EmiRecordRepository emiRecordRepository;
//
// // Anyone authenticated can do a quick check
// @PostMapping("/quick-check")
// public BigDecimal quickCheck(@RequestBody QuickCheckRequest request) {
// return emiService.quickEmiCheck(
// request.getPrincipalAmount(),
// request.getRoi(),
// request.getTenure()
// );
// }
// // Anyone authenticated can calculate EMI
// @PostMapping("/calculate/{loanId}")
// public EmiCalculateResponse calculateEmi(
// @RequestHeader("Authorization") String token,
// @PathVariable("loanId") Long loanId) {
//
// return emiService.calculateAndSaveEmi( token, loanId);
// }
//
//// @PostMapping("/calculate/{loanId}")
//// public EmiCalculateResponse calculateEmi(@PathVariable Long loanId) {
//// return emiService.calculateAndSaveEmi(loanId);
//// }
//
// // Only CUSTOMER can record payment
//
// @PostMapping("/record")
// @PreAuthorize("hasRole('CUSTOMER')")
// public ResponseEntity<EmiPaymentHistory> recordPayment(
// @RequestBody EmiPaymentRequest request) {
//
// EmiPaymentHistory savedPayment = emiService.recordPayment(
// request.getLoanId(),
// request.getAmountPaid(),
// request.getPaymentDate()
// );
//
// return ResponseEntity.ok(savedPayment);
// }
////
//
// // Only CUSTOMER or ADMIN can view payment history
// @GetMapping("history/{loanId}")
// @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
// public List<EmiPaymentHistory> getPaymentHistory(
// @PathVariable Long loanId) {
//
// return emiService.getPaymentHistoryByLoanId(loanId);
// }
//
// //just for testing purpose
// @GetMapping("/test")
//
// public String test() {
//
// Authentication auth = SecurityContextHolder.getContext().getAuthentication();
// System.out.println("Authorities: " + auth.getAuthorities());
//
// return "Check console";
// }
//
// @GetMapping("/details/{loanId}")
// @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
// public EmiFullHistoryResponse getFullDetails(@PathVariable Long loanId) {
// return emiService.getFullHistory(loanId);
// }
//
// @GetMapping("/outstanding/{loanId}")
// @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
// public EmiOutstandingResponse getOutstanding(@PathVariable Long loanId) {
// return emiService.getOutstanding(loanId);
// }
//
// @GetMapping("/next-due/{loanId}")
// @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
// public EmiNextDueResponse getNextDue(@PathVariable Long loanId) {
// return emiService.getNextDue(loanId);
// }
//
//// @DeleteMapping("/delete/{loanId}")
//// @PreAuthorize("hasRole('ADMIN')")
//// public ResponseEntity<String> deleteEmi(@PathVariable Long loanId) {
////
//// emiService.deleteEmiRecordIfRejected(loanId);
////
//// return ResponseEntity.ok("EMI record deleted successfully.");
//// }
// @DeleteMapping("/loan/{loanId}")
// @Transactional
// public ResponseEntity<Void> deleteEmiRecordsByLoanId(@PathVariable Long
// loanId) {
// List<EmiRecord> records = emiRecordRepository.findAllByLoanId(loanId);
// if (!records.isEmpty()) {
// emiRecordRepository.deleteAll(records);
// }
// System.out.println("Received delete request for loanId: " + loanId);
// return ResponseEntity.noContent().build();
// }
// }