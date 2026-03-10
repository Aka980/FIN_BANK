package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.CustomerRequestDTO;
import com.example.demo.dto.CustomerResponseDTO;
import com.example.demo.dto.LoginDTO;
import com.example.demo.entity.Customer;
import com.example.demo.jwt.JwtUtil;
import com.example.demo.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @Autowired
    private JwtUtil jwtUtil;

    public CustomerController(CustomerService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public CustomerResponseDTO registerCustomer(@Valid @RequestBody CustomerRequestDTO dto) {
        return service.registerCustomer(dto);
    }

    @PostMapping("/login")
    public String loginCustomer(@RequestBody LoginDTO dto) {
        Customer c = service.loginCustomer(dto.getUsername(), dto.getPassword());
        return jwtUtil.generateToken(c.getAccountNo(), c.getRole());
    }

    @GetMapping("/profile")
    public CustomerResponseDTO getProfile(Authentication auth) {
        Long accountNo;
        try {
            accountNo = Long.parseLong(auth.getName());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid account number in token.");
        }
        return service.getCustomerById(accountNo);
    }

    @GetMapping("/{id}")
    public CustomerResponseDTO getCustomerById(@PathVariable Long id, Authentication auth) {
        Long accountNo;
        try {
            accountNo = Long.parseLong(auth.getName());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid account number in token.");
        }

        if (!id.equals(accountNo)) {
            throw new RuntimeException("Access Denied");
        }
        return service.getCustomerById(id);
    }

    @GetMapping("/list")
    public List<CustomerResponseDTO> getAllCustomers(Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new RuntimeException("Admin Access Required");
        }
        return service.getAllCustomers();
    }

    @PutMapping("/update/{id}")
    public CustomerResponseDTO updateCustomer(@Valid @RequestBody CustomerRequestDTO dto, @PathVariable Long id,
            Authentication auth) {
        Long accountNo;
        try {
            accountNo = Long.parseLong(auth.getName());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid account number in token.");
        }

        if (!id.equals(accountNo)) {
            throw new RuntimeException("Access Denied");
        }
        return service.updateCustomer(dto, id);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, Authentication auth) {
        Long accountNo;
        try {
            accountNo = Long.parseLong(auth.getName());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid account number in token.");
        }

        if (!id.equals(accountNo)) {
            throw new RuntimeException("Access Denied");
        }
        service.deleteCustomer(id);
        return "Customer Deleted Successfully";
    }

    @Autowired
    private com.example.demo.service.EmailService emailService;

    @PostMapping("/send-loan-email")
    public String sendLoanEmail(@RequestBody com.example.demo.dto.LoanEmailRequestDTO dto) {
        emailService.sendLoanStatusEmail(dto);
        return "Email sent successfully";
    }
}