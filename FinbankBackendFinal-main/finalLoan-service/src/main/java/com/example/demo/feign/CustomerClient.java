package com.example.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.demo.dto.CustomerResponseDTO;
import com.example.demo.dto.CustomerProfileDTO;

@FeignClient(name = "customer-service", url = "http://localhost:8081")
public interface CustomerClient {

    @GetMapping("/profile/my")
    CustomerProfileDTO getMyProfile(@RequestHeader("Authorization") String token);

//    @GetMapping("/customer/username/{username}")
//    CustomerResponseDTO getCustomerByUsername(
//            @PathVariable("username") String username,
//            @RequestHeader("Authorization") String token);
}
