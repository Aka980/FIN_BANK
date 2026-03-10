package com.example.demo.feignclients;

import com.example.demo.dto.*;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "emi-service",
        url = "http://localhost:8060"
)
public interface EmiClient {

    @GetMapping("emi/details/{loanId}")
    EmiFullHistoryResponse getFullDetails(
            @PathVariable("loanId") Long loanId,
            @RequestHeader("Authorization") String token);

    @GetMapping("emi/history/{loanId}")
    List<EmiPaymentHistory> getPaymentHistory(
            @PathVariable("loanId") Long loanId,
            @RequestHeader("Authorization") String token);

    @GetMapping("emi/outstanding/{loanId}")
    String getOutstandingAmount(
            @PathVariable("loanId") Long loanId,
            @RequestHeader("Authorization") String token);

    @GetMapping("emi/next-due/{loanId}")
    String getNextDueDate(
            @PathVariable("loanId") Long loanId,
            @RequestHeader("Authorization") String token);
}