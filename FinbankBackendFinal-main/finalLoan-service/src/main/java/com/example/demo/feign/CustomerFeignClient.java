//package com.example.demo.feign;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//import com.example.demo.dto.CustomerLoanInfoDTO;
//import com.example.demo.dto.CustomerResponseDTO;
//import com.example.demo.feignconfig.FeignConfig;
//
//@FeignClient(
//        name = "customer-service",
//        url = "http://localhost:8081",
//        configuration = FeignConfig.class
//)
//public interface CustomerFeignClient {
//
//	@GetMapping("/customer/loan-info/{accountNo}")
//    CustomerLoanInfoDTO getCustomerLoanInfo(@PathVariable Long accountNo);
//}