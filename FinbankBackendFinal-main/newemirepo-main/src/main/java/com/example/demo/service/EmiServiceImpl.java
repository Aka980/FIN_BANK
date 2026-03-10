package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.demo.Dto.*;
import com.example.demo.controller.feignclient;
import com.example.demo.Exception.InvalidInputException;
import com.example.demo.Exception.ResourceNotFoundException;
import com.example.demo.model.EmiCalculatorUtil;
import com.example.demo.model.EmiPaymentHistory;
import com.example.demo.model.EmiRecord;
import com.example.demo.repo.EMIRepository;
import com.example.demo.repo.EmiPaymentHistoryRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmiServiceImpl implements EmiService {

    private final feignclient loanClient;
    private final EMIRepository emiRecordRepository;
    private final EmiPaymentHistoryRepository repository;

    @Override
    public EmiCalculateResponse calculateAndSaveEmi(String token, Long loanId) {
        if (loanId == null) {
            throw new InvalidInputException("INVALID_ID", "Loan ID must not be null");
        }

        LoanResponse loan = loanClient.getLoanById(token, loanId);

        if (loan == null) {
            throw new ResourceNotFoundException("LOAN_NOT_FOUND",
                    "Loan not found for ID: " + loanId);
        }

        List<EmiRecord> existingRecords = emiRecordRepository.findAllByLoanId(loan.getApplicationNo());
        if (!existingRecords.isEmpty()) {
            throw new InvalidInputException("EMI_ALREADY_CALCULATED",
                    "EMI has already been calculated for loan ID: " + loanId);
        }

        BigDecimal monthlyEmi = EmiCalculatorUtil.calculateEmi(
                BigDecimal.valueOf(loan.getAmount()),
                BigDecimal.valueOf(loan.getRoi()),
                loan.getTenure());

        EmiRecord record = EmiRecord.builder()
                .loanId(loan.getApplicationNo())
                .accountNo(loan.getAccountNo())
                .principalAmount(BigDecimal.valueOf(loan.getAmount()))
                .roi(BigDecimal.valueOf(loan.getRoi()))
                .tenure(loan.getTenure())
                .monthlyEmiAmount(monthlyEmi)
                .createdDate(LocalDate.now())
                .build();

        emiRecordRepository.save(record);

        return EmiCalculateResponse.builder()
                .loanId(loan.getApplicationNo())
                .accountNo(loan.getAccountNo())
                .principalAmount(BigDecimal.valueOf(loan.getAmount()))
                .roi(BigDecimal.valueOf(loan.getRoi()))
                .tenure(loan.getTenure())
                .monthlyEmi(monthlyEmi)
                .build();
    }

    @Override
    @Transactional
    public EmiPaymentHistory recordPayment(String token,
            Long loanId,
            BigDecimal amountPaid,
            LocalDate paymentDate) {

        if (loanId == null || loanId <= 0) {
            throw new InvalidInputException("INVALID_LOAN_ID", "Loan ID must be a positive number");
        }

        if (amountPaid == null || amountPaid.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("INVALID_AMOUNT", "Amount paid must be greater than zero");
        }

        if (paymentDate == null) {
            throw new InvalidInputException("INVALID_DATE", "Payment date must not be null");
        }

        // Validate user access via loan service
        LoanResponse loan = loanClient.getLoanById(token, loanId);

        if (loan == null) {
            throw new ResourceNotFoundException("LOAN_NOT_FOUND", "Loan not found for ID: " + loanId);
        }

        // 🔴 IMPORTANT CHECK
        if (!"APPROVED".equalsIgnoreCase(loan.getStatus())) {
            throw new InvalidInputException(
                    "LOAN_NOT_APPROVED",
                    "Payment cannot be recorded because loan is not approved"
            );
        }

        List<EmiRecord> emiRecords = emiRecordRepository.findAllByLoanId(loanId);
        if (emiRecords.isEmpty()) {
            throw new InvalidInputException("INVALID_LOAN_ID", "Loan ID does not exist in EMI records");
        }

        EmiPaymentHistory payment = EmiPaymentHistory.builder()
                .loanId(loanId)
                .amountPaid(amountPaid)
                .paymentDate(paymentDate)
                .build();

        return repository.save(payment);
    }

    @Override
    public List<EmiPaymentHistory> getPaymentHistoryByLoanId(String token, Long loanId) {
        if (loanId == null)
            throw new InvalidInputException("INVALID_ID", "Loan ID required");

        // Validate user access via loan service
        LoanResponse loan = loanClient.getLoanById(token, loanId);

        if (loan == null)
            throw new ResourceNotFoundException("LOAN_NOT_FOUND", "Loan not found");
        // if loan service denies access, Feign throws 403 automatically

        List<EmiPaymentHistory> history = repository.findByLoanId(loanId);
        if (history.isEmpty())
            throw new ResourceNotFoundException("PAYMENT_HISTORY_NOT_FOUND", "No payments found");

        return history;
    }

    @Override
    public BigDecimal quickEmiCheck(BigDecimal principal, BigDecimal roi, Integer tenure) {
        if (principal == null || roi == null || tenure == null) {
            throw new InvalidInputException("INVALID_INPUT", "Principal, ROI, and Tenure must not be null");
        }
        if (principal.compareTo(BigDecimal.ZERO) <= 0 || roi.compareTo(BigDecimal.ZERO) <= 0 || tenure <= 0) {
            throw new InvalidInputException("INVALID_INPUT", "Principal, ROI, and Tenure must be positive");
        }

        return EmiCalculatorUtil.calculateEmi(principal, roi, tenure);
    }

    @Override
    public EmiFullHistoryResponse getFullHistory(String token, Long loanId) {

        if (loanId == null) {
            throw new InvalidInputException("INVALID_ID", "Loan ID must not be null");
        }

        LoanResponse loan = loanClient.getLoanById(token, loanId);

        // repository returns LIST
        List<EmiRecord> records = emiRecordRepository.findByloanId(loanId);

        if (records == null || records.isEmpty()) {
            throw new ResourceNotFoundException(
                    "EMI_NOT_FOUND",
                    "EMI not found for loan ID: " + loanId);
        }

        // take first record (normally only one per loan)
        EmiRecord record = records.get(0);

        return EmiFullHistoryResponse.builder()
                .accnumber(record.getAccountNo())
                .emiPlans(records)
                .build();
    }

    @Override
    public EmiOutstandingResponse getOutstanding(String token, Long loanId) {

        if (loanId == null) {
            throw new InvalidInputException("INVALID_ID", "Loan ID must not be null");
        }

        LoanResponse loan = loanClient.getLoanById(token, loanId);

        // Get EMI record
        List<EmiRecord> records = emiRecordRepository.findByloanId(loanId);

        if (records.isEmpty()) {
            throw new ResourceNotFoundException(
                    "EMI_NOT_FOUND",
                    "EMI not found for loan ID: " + loanId);
        }

        EmiRecord record = records.get(0);

        BigDecimal totalLoanAmount = record.getPrincipalAmount();
        BigDecimal monthlyEmi = record.getMonthlyEmiAmount();

        // Get total paid from DB (calculated)
        BigDecimal totalPaid = repository.getTotalPaidByLoanId(loanId);

        BigDecimal remainingAmount = totalLoanAmount.subtract(totalPaid);

        if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
            remainingAmount = BigDecimal.ZERO;
        }

        return EmiOutstandingResponse.builder()
                .loanId(loanId)
                .totalLoanAmount(totalLoanAmount)
                .totalPaid(totalPaid)
                .remainingAmount(remainingAmount)
                .monthlyEmi(monthlyEmi)
                .build();
    }

    @Override
    public EmiNextDueResponse getNextDue(String token, Long loanId) {

        if (loanId == null) {
            throw new InvalidInputException("INVALID_ID", "Loan ID cannot be null");
        }

        LoanResponse loan = loanClient.getLoanById(token, loanId);

        List<EmiRecord> records = emiRecordRepository.findByloanId(loanId);

        if (records.isEmpty()) {
            throw new ResourceNotFoundException(
                    "EMI_NOT_FOUND",
                    "No EMI record found for loan ID: " + loanId);
        }

        EmiRecord record = records.get(0);

        int tenure = record.getTenure();
        BigDecimal emiAmount = record.getMonthlyEmiAmount();
        LocalDate startDate = record.getCreatedDate();

        long paidCount = repository.countByLoanId(loanId);

        int remaining = tenure - (int) paidCount;

        if (remaining <= 0) {
            return EmiNextDueResponse.builder()
                    .loanId(loanId)
                    .loanClosed(true)
                    .remainingEmis(0)
                    .emiAmount(BigDecimal.ZERO)
                    .nextDueDate(null)
                    .build();
        }

        LocalDate nextDueDate = startDate.plusMonths(paidCount);

        return EmiNextDueResponse.builder()
                .loanId(loanId)
                .nextDueDate(nextDueDate)
                .emiAmount(emiAmount)
                .remainingEmis(remaining)
                .loanClosed(false)
                .build();
    }

    @Override
    @Transactional
    public void deleteEmiRecordIfRejected(Long loanId) {

        if (loanId == null || loanId <= 0) {
            throw new InvalidInputException(
                    "INVALID_LOAN_ID",
                    "Loan ID must be positive");
        }

        // 🔥 Step 1: Verify Loan Status
        LoanResponse loan = loanClient.getLoanStatus(loanId);

        if (loan == null) {
            throw new ResourceNotFoundException(
                    "LOAN_NOT_FOUND",
                    "Loan not found for ID: " + loanId);
        }

        if (!"DENIED".equalsIgnoreCase(loan.getStatus())) {
            throw new InvalidInputException(
                    "LOAN_NOT_REJECTED",
                    "EMI can only be deleted when loan status is REJECTED");
        }

        // 🔥 Step 2: Check EMI record exists
        List<EmiRecord> records = emiRecordRepository.findAllByLoanId(loanId);

        if (records.isEmpty()) {
            throw new ResourceNotFoundException(
                    "EMI_NOT_FOUND",
                    "No EMI record found for loan ID: " + loanId);
        }

        // 🔥 Step 3: Delete EMI records
        emiRecordRepository.deleteAll(records);
    }
}

// @Override
// @Transactional
// public EmiPaymentHistory recordPayment(Long loanId,
// BigDecimal amountPaid,
// LocalDate paymentDate) {
//
// if (loanId == null || loanId <= 0) {
// throw new InvalidInputException("INVALID_LOAN_ID", "Loan ID must be a
// positive number");
// }
//
// if (amountPaid == null || amountPaid.compareTo(BigDecimal.ZERO) <= 0) {
// throw new InvalidInputException("INVALID_AMOUNT", "Amount paid must be
// greater than zero");
// }
//
// if (paymentDate == null) {
// throw new InvalidInputException("INVALID_DATE", "Payment date must not be
// null");
// }
//
// List<EmiRecord> emiRecords = emiRecordRepository.findAllByLoanId(loanId);
// if (emiRecords.isEmpty()) {
// throw new InvalidInputException("INVALID_LOAN_ID", "Loan ID does not exist in
// EMI records");
// }
//
// EmiPaymentHistory payment = EmiPaymentHistory.builder()
// .loanId(loanId)
// .amountPaid(amountPaid)
// .paymentDate(paymentDate)
// .build();
//
// return repository.save(payment);
// }
//
// @Override
// public List<EmiPaymentHistory> getPaymentHistoryByLoanId(Long loanId) {
//
// if (loanId == null) {
// throw new InvalidInputException("INVALID_ID", "Loan ID must not be null");
// }
//
// List<EmiPaymentHistory> history = repository.findByLoanId(loanId);
//
// if (history.isEmpty()) {
// throw new ResourceNotFoundException("PAYMENT_HISTORY_NOT_FOUND",
// "No payment history found for loan ID: " + loanId);
// }
//
// return history;
// }
// @Override
// public BigDecimal quickEmiCheck(BigDecimal principal, BigDecimal roi, Integer
// tenure) {
// if (principal == null || roi == null || tenure == null) {
// throw new InvalidInputException("INVALID_INPUT", "Principal, ROI, and Tenure
// must not be null");
// }
// if (principal.compareTo(BigDecimal.ZERO) <= 0 ||
// roi.compareTo(BigDecimal.ZERO) <= 0 || tenure <= 0) {
// throw new InvalidInputException("INVALID_INPUT", "Principal, ROI, and Tenure
// must be positive");
// }
//
// return EmiCalculatorUtil.calculateEmi(principal, roi, tenure);
// }
//
// @Override
// public EmiFullHistoryResponse getFullHistory(Long loanId) {
//
// if (loanId == null) {
// throw new InvalidInputException("INVALID_ID", "Loan ID must not be null");
// }
//
// // repository returns LIST
// List<EmiRecord> records = emiRecordRepository.findByloanId(loanId);
//
// if (records == null || records.isEmpty()) {
// throw new ResourceNotFoundException(
// "EMI_NOT_FOUND",
// "EMI not found for loan ID: " + loanId
// );
// }
//
// // take first record (normally only one per loan)
// EmiRecord record = records.get(0);
//
// return EmiFullHistoryResponse.builder()
// .accnumber(record.getAccountNo())
// .emiPlans(records)
// .build();
// }
//
// @Override
// public EmiOutstandingResponse getOutstanding(Long loanId) {
//
// if (loanId == null) {
// throw new InvalidInputException("INVALID_ID", "Loan ID must not be null");
// }
//
// // Get EMI record
// List<EmiRecord> records = emiRecordRepository.findByloanId(loanId);
//
// if (records.isEmpty()) {
// throw new ResourceNotFoundException(
// "EMI_NOT_FOUND",
// "EMI not found for loan ID: " + loanId
// );
// }
//
// EmiRecord record = records.get(0);
//
// BigDecimal totalLoanAmount = record.getPrincipalAmount();
// BigDecimal monthlyEmi = record.getMonthlyEmiAmount();
//
// // Get total paid from DB (calculated)
// BigDecimal totalPaid = repository.getTotalPaidByLoanId(loanId);
//
// BigDecimal remainingAmount = totalLoanAmount.subtract(totalPaid);
//
// if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
// remainingAmount = BigDecimal.ZERO;
// }
//
// return EmiOutstandingResponse.builder()
// .loanId(loanId)
// .totalLoanAmount(totalLoanAmount)
// .totalPaid(totalPaid)
// .remainingAmount(remainingAmount)
// .monthlyEmi(monthlyEmi)
// .build();
// }
//
// @Override
// public EmiNextDueResponse getNextDue(Long loanId) {
//
// if (loanId == null) {
// throw new InvalidInputException("INVALID_ID", "Loan ID cannot be null");
// }
//
// List<EmiRecord> records = emiRecordRepository.findByloanId(loanId);
//
// if (records.isEmpty()) {
// throw new ResourceNotFoundException(
// "EMI_NOT_FOUND",
// "No EMI record found for loan ID: " + loanId
// );
// }
//
// EmiRecord record = records.get(0);
//
// int tenure = record.getTenure();
// BigDecimal emiAmount = record.getMonthlyEmiAmount();
// LocalDate startDate = record.getCreatedDate();
//
// long paidCount = repository.countByLoanId(loanId);
//
// int remaining = tenure - (int) paidCount;
//
// if (remaining <= 0) {
// return EmiNextDueResponse.builder()
// .loanId(loanId)
// .loanClosed(true)
// .remainingEmis(0)
// .emiAmount(BigDecimal.ZERO)
// .nextDueDate(null)
// .build();
// }
//
// LocalDate nextDueDate = startDate.plusMonths(paidCount);
//
// return EmiNextDueResponse.builder()
// .loanId(loanId)
// .nextDueDate(nextDueDate)
// .emiAmount(emiAmount)
// .remainingEmis(remaining)
// .loanClosed(false)
// .build();
// }
// }
//

// package com.example.demo.service;
//
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import com.example.demo.Dto.*;
// import com.example.demo.controller.feignclient;
// import com.example.demo.Exception.InvalidInputException;
// import com.example.demo.Exception.ResourceNotFoundException;
// import com.example.demo.model.EmiCalculatorUtil;
// import com.example.demo.model.EmiPaymentHistory;
// import com.example.demo.model.EmiRecord;
// import com.example.demo.repo.EMIRepository;
// import com.example.demo.repo.EmiPaymentHistoryRepository;
// import org.springframework.transaction.annotation.Transactional;
//
// import java.math.BigDecimal;
// import java.time.LocalDate;
// import java.util.List;
//
// @Service
// @RequiredArgsConstructor
// public class EmiServiceImpl implements EmiService {
//
// private final feignclient loanClient;
// private final EMIRepository emiRecordRepository;
// private final EmiPaymentHistoryRepository repository;
//
// @Override
// public EmiCalculateResponse calculateAndSaveEmi(String token, Long loanId) {
// if (loanId == null) {
// throw new InvalidInputException("INVALID_ID", "Loan ID must not be null");
// }
//
// LoanResponse loan = loanClient.getLoanById(token, loanId);
//
// if (loan == null) {
// throw new ResourceNotFoundException("LOAN_NOT_FOUND",
// "Loan not found for ID: " + loanId);
// }
//
// List<EmiRecord> existingRecords =
// emiRecordRepository.findAllByLoanId(loan.getApplicationNo());
// if (!existingRecords.isEmpty()) {
// throw new InvalidInputException("EMI_ALREADY_CALCULATED",
// "EMI has already been calculated for loan ID: " + loanId);
// }
//
// BigDecimal monthlyEmi = EmiCalculatorUtil.calculateEmi(
// BigDecimal.valueOf(loan.getAmount()),
// BigDecimal.valueOf(loan.getRoi()),
// loan.getTenure()
// );
//
// EmiRecord record = EmiRecord.builder()
// .loanId(loan.getApplicationNo())
// .accountNo(loan.getAccountNo())
// .principalAmount(BigDecimal.valueOf(loan.getAmount()))
// .roi(BigDecimal.valueOf(loan.getRoi()))
// .tenure(loan.getTenure())
// .monthlyEmiAmount(monthlyEmi)
// .createdDate(LocalDate.now())
// .build();
//
// emiRecordRepository.save(record);
//
// return EmiCalculateResponse.builder()
// .loanId(loan.getApplicationNo())
// .accountNo(loan.getAccountNo())
// .principalAmount(BigDecimal.valueOf(loan.getAmount()))
// .roi(BigDecimal.valueOf(loan.getRoi()))
// .tenure(loan.getTenure())
// .monthlyEmi(monthlyEmi)
// .build();
// }
//
//// @Override
//// public EmiCalculateResponse calculateAndSaveEmi(/* String token, */ Long
// loanId) {
//// if (loanId == null) {
//// throw new InvalidInputException("INVALID_ID", "Loan ID must not be null");
//// }
////
//// LoanResponse loan = loanClient.getLoanById(/* token, */ loanId);
////
//// if (loan == null) {
//// throw new ResourceNotFoundException("LOAN_NOT_FOUND",
//// "Loan not found for ID: " + loanId);
//// }
////
//// List<EmiRecord> existingRecords =
// emiRecordRepository.findAllByLoanId(loan.getApplicationNo());
//// if (!existingRecords.isEmpty()) {
//// throw new InvalidInputException("EMI_ALREADY_CALCULATED",
//// "EMI has already been calculated for loan ID: " + loanId);
//// }
////
//// BigDecimal monthlyEmi = EmiCalculatorUtil.calculateEmi(
//// BigDecimal.valueOf(loan.getAmount()),
//// BigDecimal.valueOf(loan.getRoi()),
//// loan.getTenure()
//// );
////
//// EmiRecord record = EmiRecord.builder()
//// .loanId(loan.getApplicationNo())
//// .accountNo(loan.getAccountNo())
//// .principalAmount(BigDecimal.valueOf(loan.getAmount()))
//// .roi(BigDecimal.valueOf(loan.getRoi()))
//// .tenure(loan.getTenure())
//// .monthlyEmiAmount(monthlyEmi)
//// .createdDate(LocalDate.now())
//// .build();
////
//// emiRecordRepository.save(record);
////
//// return EmiCalculateResponse.builder()
//// .loanId(loan.getApplicationNo())
//// .accountNo(loan.getAccountNo())
//// .principalAmount(BigDecimal.valueOf(loan.getAmount()))
//// .roi(BigDecimal.valueOf(loan.getRoi()))
//// .tenure(loan.getTenure())
//// .monthlyEmi(monthlyEmi)
//// .build();
//// }
//
// @Override
// @Transactional
// public EmiPaymentHistory recordPayment(Long loanId,
// BigDecimal amountPaid,
// LocalDate paymentDate) {
//
// if (loanId == null || loanId <= 0) {
// throw new InvalidInputException("INVALID_LOAN_ID", "Loan ID must be a
// positive number");
// }
//
// if (amountPaid == null || amountPaid.compareTo(BigDecimal.ZERO) <= 0) {
// throw new InvalidInputException("INVALID_AMOUNT", "Amount paid must be
// greater than zero");
// }
//
// if (paymentDate == null) {
// throw new InvalidInputException("INVALID_DATE", "Payment date must not be
// null");
// }
//
// List<EmiRecord> emiRecords = emiRecordRepository.findAllByLoanId(loanId);
// if (emiRecords.isEmpty()) {
// throw new InvalidInputException("INVALID_LOAN_ID", "Loan ID does not exist in
// EMI records");
// }
//
// EmiPaymentHistory payment = EmiPaymentHistory.builder()
// .loanId(loanId)
// .amountPaid(amountPaid)
// .paymentDate(paymentDate)
// .build();
//
// return repository.save(payment);
// }
//
// @Override
// public List<EmiPaymentHistory> getPaymentHistoryByLoanId(Long loanId) {
//
// if (loanId == null) {
// throw new InvalidInputException("INVALID_ID", "Loan ID must not be null");
// }
//
// List<EmiPaymentHistory> history = repository.findByLoanId(loanId);
//
// if (history.isEmpty()) {
// throw new ResourceNotFoundException("PAYMENT_HISTORY_NOT_FOUND",
// "No payment history found for loan ID: " + loanId);
// }
//
// return history;
// }
// @Override
// public BigDecimal quickEmiCheck(BigDecimal principal, BigDecimal roi, Integer
// tenure) {
// if (principal == null || roi == null || tenure == null) {
// throw new InvalidInputException("INVALID_INPUT", "Principal, ROI, and Tenure
// must not be null");
// }
// if (principal.compareTo(BigDecimal.ZERO) <= 0 ||
// roi.compareTo(BigDecimal.ZERO) <= 0 || tenure <= 0) {
// throw new InvalidInputException("INVALID_INPUT", "Principal, ROI, and Tenure
// must be positive");
// }
//
// return EmiCalculatorUtil.calculateEmi(principal, roi, tenure);
// }
//
// @Override
// public EmiFullHistoryResponse getFullHistory(Long loanId) {
//
// if (loanId == null) {
// throw new InvalidInputException("INVALID_ID", "Loan ID must not be null");
// }
//
// // repository returns LIST
// List<EmiRecord> records = emiRecordRepository.findByloanId(loanId);
//
// if (records == null || records.isEmpty()) {
// throw new ResourceNotFoundException(
// "EMI_NOT_FOUND",
// "EMI not found for loan ID: " + loanId
// );
// }
//
// // take first record (normally only one per loan)
// EmiRecord record = records.get(0);
//
// return EmiFullHistoryResponse.builder()
// .accnumber(record.getAccountNo())
// .emiPlans(records)
// .build();
// }
//
// @Override
// public EmiOutstandingResponse getOutstanding(Long loanId) {
//
// if (loanId == null) {
// throw new InvalidInputException("INVALID_ID", "Loan ID must not be null");
// }
//
// // Get EMI record
// List<EmiRecord> records = emiRecordRepository.findByloanId(loanId);
//
// if (records.isEmpty()) {
// throw new ResourceNotFoundException(
// "EMI_NOT_FOUND",
// "EMI not found for loan ID: " + loanId
// );
// }
//
// EmiRecord record = records.get(0);
//
// BigDecimal totalLoanAmount = record.getPrincipalAmount();
// BigDecimal monthlyEmi = record.getMonthlyEmiAmount();
//
// // Get total paid from DB (calculated)
// BigDecimal totalPaid = repository.getTotalPaidByLoanId(loanId);
//
// BigDecimal remainingAmount = totalLoanAmount.subtract(totalPaid);
//
// if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
// remainingAmount = BigDecimal.ZERO;
// }
//
// return EmiOutstandingResponse.builder()
// .loanId(loanId)
// .totalLoanAmount(totalLoanAmount)
// .totalPaid(totalPaid)
// .remainingAmount(remainingAmount)
// .monthlyEmi(monthlyEmi)
// .build();
// }
//
// @Override
// public EmiNextDueResponse getNextDue(Long loanId) {
//
// if (loanId == null) {
// throw new InvalidInputException("INVALID_ID", "Loan ID cannot be null");
// }
//
// List<EmiRecord> records = emiRecordRepository.findByloanId(loanId);
//
// if (records.isEmpty()) {
// throw new ResourceNotFoundException(
// "EMI_NOT_FOUND",
// "No EMI record found for loan ID: " + loanId
// );
// }
//
// EmiRecord record = records.get(0);
//
// int tenure = record.getTenure();
// BigDecimal emiAmount = record.getMonthlyEmiAmount();
// LocalDate startDate = record.getCreatedDate();
//
// long paidCount = repository.countByLoanId(loanId);
//
// int remaining = tenure - (int) paidCount;
//
// if (remaining <= 0) {
// return EmiNextDueResponse.builder()
// .loanId(loanId)
// .loanClosed(true)
// .remainingEmis(0)
// .emiAmount(BigDecimal.ZERO)
// .nextDueDate(null)
// .build();
// }
//
// LocalDate nextDueDate = startDate.plusMonths(paidCount);
//
// return EmiNextDueResponse.builder()
// .loanId(loanId)
// .nextDueDate(nextDueDate)
// .emiAmount(emiAmount)
// .remainingEmis(remaining)
// .loanClosed(false)
// .build();
// }
//
//
// @Override
// @Transactional
// public void deleteEmiRecordIfRejected(Long loanId) {
//
// if (loanId == null || loanId <= 0) {
// throw new InvalidInputException(
// "INVALID_LOAN_ID",
// "Loan ID must be positive"
// );
// }
//
// // 🔥 Step 1: Verify Loan Status
// LoanResponse loan = loanClient.getLoanStatus(loanId);
//
// if (loan == null) {
// throw new ResourceNotFoundException(
// "LOAN_NOT_FOUND",
// "Loan not found for ID: " + loanId
// );
// }
//
// if (!"REJECTED".equalsIgnoreCase(loan.getStatus())) {
// throw new InvalidInputException(
// "LOAN_NOT_REJECTED",
// "EMI can only be deleted when loan status is REJECTED"
// );
// }
//
// // 🔥 Step 2: Check EMI record exists
// List<EmiRecord> records = emiRecordRepository.findAllByLoanId(loanId);
//
// if (records.isEmpty()) {
// throw new ResourceNotFoundException(
// "EMI_NOT_FOUND",
// "No EMI record found for loan ID: " + loanId
// );
// }
//
// // 🔥 Step 3: Delete EMI records
// emiRecordRepository.deleteAll(records);
// }
// }