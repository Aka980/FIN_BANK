package com.example.demo.feignclients;

import com.example.demo.dto.*;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "loan-service",
        url = "http://localhost:8082" 
)
public interface LoanClient {

    @GetMapping("/loans/id/{loanId}")
    LoanResponseDTO getLoanById(
            @PathVariable("loanId") Long loanId,
            @RequestHeader("Authorization") String token);

    @GetMapping("/loans/status/{id}")
    LoanResponseDTO getLoanStatus(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token);
    @GetMapping("/loans/account/{accountNo}")
    List<LoanResponseDTO> getLoansByAccountNo(
            @PathVariable("accountNo") Long accountNo,
            @RequestHeader("Authorization") String token
    );
}