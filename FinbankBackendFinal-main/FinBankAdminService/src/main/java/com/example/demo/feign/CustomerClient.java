package com.example.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.demo.config.FeignConfig;
import com.example.demo.dto.CustomerProfileDTO;

@FeignClient(name = "customer-service", configuration = FeignConfig.class)
public interface CustomerClient {

    // @GetMapping("/profile/pan/{pan}")
    // CustomerProfileRespaconseDTO getCustomerProfileByPan(
    // @PathVariable("pan") String pan,
    // @RequestHeader("Authorization") String token);

    @GetMapping("/profile/account/{accountNo}")
    CustomerProfileDTO getCustomerByAccountNo(
            @PathVariable("accountNo") Long accountNo,
            @RequestHeader("Authorization") String token);

    @org.springframework.web.bind.annotation.PostMapping("/customer/send-loan-email")
    String sendLoanEmail(
            @org.springframework.web.bind.annotation.RequestBody com.example.demo.dto.LoanEmailRequestDTO emailRequest,
            @RequestHeader("Authorization") String token);

}
