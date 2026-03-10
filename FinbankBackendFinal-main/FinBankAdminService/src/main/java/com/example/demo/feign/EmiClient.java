package com.example.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.demo.config.FeignConfig;

@FeignClient(name = "emi-service", configuration = FeignConfig.class)
public interface EmiClient {

    @DeleteMapping("/emi/delete/{loanId}")
    String deleteEmi(
            @PathVariable("loanId") Long loanId,
            @RequestHeader("Authorization") String token);
}
