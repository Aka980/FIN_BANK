package com.example.demo.services;

import com.example.demo.feignclients.LoanClient;
import com.example.demo.feignclients.EmiClient;
import com.example.demo.dto.*;
import com.example.demo.exception.*;
import feign.FeignException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final LoanClient loanClient;
    private final EmiClient emiClient;

    public ChatService(ChatClient.Builder builder, LoanClient loanClient, EmiClient emiClient) {
        this.chatClient = builder.build();
        this.loanClient = loanClient;
        this.emiClient = emiClient;
    }

    // -------------------------------
    // Test AI connection
    // -------------------------------
    public String testConnection() {
        return chatClient.prompt()
                .user("Say: AI connection successful")
                .call()
                .content();
    }

    // -------------------------------
    // Analyze loan full summary
    // -------------------------------
    public String analyzeLoan(Long loanId, String token) {
        LoanResponseDTO loan = fetchLoanSafe(loanId, token);

        EmiFullHistoryResponse emiDetails = safeEmiDetails(loanId, token);
        List<EmiPaymentHistory> payments = safePaymentHistory(loanId, token);
        String outstanding = safeOutstanding(loanId, token);
        String nextDue = safeNextDue(loanId, token);

        String monthlyEmi = resolveMonthlyEmi(loan, emiDetails);

        String formattedHistory = (payments == null || payments.isEmpty())
                ? "No payment history available."
                : payments.stream()
                        .map(p -> String.format("PaymentId: %d | Amount: %s | Date: %s",
                                p.getPaymentId(), p.getAmountPaid(), p.getPaymentDate()))
                        .collect(Collectors.joining("\n"));

        String promptData = String.format("""
                Loan Information:
                Loan ID: %s
                Account No: %s
                Principal Amount: %s
                ROI: %s
                Tenure: %s

                EMI Details:
                Monthly EMI: %s
                Outstanding Amount: %s
                Next Due Date: %s

                Payment History:
                %s
                """,
                loan.getApplicationNo(), loan.getAccountNo(), loan.getAmount(),
                loan.getRoi(), loan.getTenure(), monthlyEmi, outstanding, nextDue, formattedHistory
        );

        return chatClient.prompt()
                .system("""
                        You are a professional FinBank loan advisor.
                        Speak clearly and professionally.
                        If payment history is missing, inform the user.
                        Do not fabricate data.
                        """)
                .user(promptData + "\n\nExplain full loan and EMI status clearly.")
                .call()
                .content();
    }

    // -------------------------------
    // Chat with loan context
    // -------------------------------
    public String chatWithLoanContext(Long loanId, String token, String userMessage) {

        LoanResponseDTO loan;

            loan = fetchLoanSafe(loanId, token); // throws custom exceptions if not found or unauthorized
        

        // Fetch EMI details safely
        EmiFullHistoryResponse emiDetails = safeEmiDetails(loanId, token);
        List<EmiPaymentHistory> payments = safePaymentHistory(loanId, token);
        String outstanding = safeOutstanding(loanId, token);
        String nextDue = safeNextDue(loanId, token);
        String monthlyEmi = resolveMonthlyEmi(loan, emiDetails);

        // Format payment history
        String formattedHistory = (payments == null || payments.isEmpty())
                ? "No payment history available."
                : payments.stream()
                          .map(p -> String.format("PaymentId: %d | Amount: %s | Date: %s",
                                  p.getPaymentId(), p.getAmountPaid(), p.getPaymentDate()))
                          .collect(Collectors.joining("\n"));

        // Count total loans for this account
        int totalLoans = loanClient.getLoansByAccountNo(loan.getAccountNo(), token)
                                   .size();

        // Build prompt for AI
        String promptData = String.format("""
                Loan Information:
                Loan ID: %s
                Account No: %s
                Principal: %s
                ROI: %s
                Tenure: %s
                Status: %s
                Applied Date: %s
                Approved Date: %s
                Rejection Reason: %s
                Total Loans for this account: %d

                EMI Details:
                Monthly EMI: %s
                Outstanding Amount: %s
                Next Due Date: %s

                Payment History:
                %s

                Customer Question:
                %s
                """,
                loan.getApplicationNo(), loan.getAccountNo(), loan.getAmount(), loan.getRoi(),
                loan.getTenure(), loan.getStatus(), loan.getAppliedDate(), loan.getApprovedDate(),
                loan.getRejectionReason(), totalLoans, monthlyEmi, outstanding, nextDue,
                formattedHistory, userMessage
        );

        return chatClient.prompt()
                .system("""
                        You are a professional FinBank loan advisor.
                        Answer strictly using provided data.
                        If payment history is missing, inform the customer.
                        Refuse unrelated questions politely.
                        Keep answers customer-friendly.
                        """)
                .user(promptData)
                .call()
                .content();
    }

    private LoanResponseDTO fetchLoanSafe(Long loanId, String token) {
        try {
            return loanClient.getLoanById(loanId, token);
        } catch (FeignException.Forbidden ex) {
            throw new UnauthorizedAccessException("You are unauthorized. Cannot access this loan.");
        } catch (FeignException.NotFound ex) {
            throw new LoanNotFoundException("Loan not found. Check the Loan ID.");
        } catch (FeignException.Unauthorized ex) {
            throw new UnauthorizedAccessException("Authentication failed. Please login again.");
        } catch (Exception ex) {
            ex.printStackTrace(); // shows real error in console
            throw new RuntimeException("Unable to fetch loan details: " + ex.getMessage());
        }
    }

    private EmiFullHistoryResponse safeEmiDetails(Long loanId, String token) {
        try { return emiClient.getFullDetails(loanId, token); }
        catch (Exception e) { return null; }
    }

    private List<EmiPaymentHistory> safePaymentHistory(Long loanId, String token) {
        try { return emiClient.getPaymentHistory(loanId, token); }
        catch (Exception e) { return null; }
    }

    private String safeOutstanding(Long loanId, String token) {
        try { return emiClient.getOutstandingAmount(loanId, token); }
        catch (Exception e) { return "Not Available"; }
    }

    private String safeNextDue(Long loanId, String token) {
        try { return emiClient.getNextDueDate(loanId, token); }
        catch (Exception e) { return "Not Available"; }
    }

    private String resolveMonthlyEmi(LoanResponseDTO loan, EmiFullHistoryResponse emiDetails) {
        if (emiDetails != null && emiDetails.getEmiRecord() != null && emiDetails.getEmiRecord().getMonthlyEmiAmount() != null)
            return emiDetails.getEmiRecord().getMonthlyEmiAmount().toString();

        if (loan.getAmount() != null && loan.getRoi() != null && loan.getTenure() != null && loan.getTenure() > 0) {
            BigDecimal emi = calculateEmi(loan.getAmount(), loan.getRoi(), loan.getTenure());
            return emi.toString();
        }

        return "Not Available";
    }

    private BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRoi, Integer tenureMonths) {
        if (principal.compareTo(BigDecimal.ZERO) <= 0 || annualRoi.compareTo(BigDecimal.ZERO) <= 0 || tenureMonths <= 0)
            return BigDecimal.ZERO;

        BigDecimal monthlyRate = annualRoi.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP)
                                          .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRatePowerN = onePlusRate.pow(tenureMonths);

        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRatePowerN);
        BigDecimal denominator = onePlusRatePowerN.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}