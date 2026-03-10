
package com.example.demo.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.CustomerRequestDTO;
import com.example.demo.dto.CustomerResponseDTO;
import com.example.demo.dto.LoginDTO;
import com.example.demo.entity.Customer;
import com.example.demo.exception.AdminAccessRequiredException;
import com.example.demo.exception.UnauthorizedAccessException;
import com.example.demo.jwt.JwtUtil;
import com.example.demo.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private CustomerService service;
    private JwtUtil jwtUtil;

    public CustomerController(
            CustomerService service,
            JwtUtil jwtUtil) {

        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public CustomerResponseDTO registerCustomer(
            @Valid @RequestBody CustomerRequestDTO dto) {

        return service.registerCustomer(dto);
    }

    /*
     * LOGIN → JWT contains accountNo
     */
    @PostMapping("/login")
    public String loginCustomer(
            @RequestBody LoginDTO dto) {

        Customer c = service.loginCustomer(
                dto.getUsername(),
                dto.getPassword());

        return jwtUtil.generateToken(
                c.getAccountNo(),
                c.getRole());
    }

    /*
     * GET OWN PROFILE
     */
    @GetMapping("/profile")
    public CustomerResponseDTO getProfile(
            Authentication auth) {

        Long accountNo = Long.parseLong(auth.getName());

        return service.getCustomerById(accountNo);
    }

    /*
     * GET CUSTOMER BY ID
     */
    @GetMapping("/{id}")
    public CustomerResponseDTO getCustomerById(
            @PathVariable Long id,
            Authentication auth) {

        Long accountNo = Long.parseLong(auth.getName());

        if (!id.equals(accountNo)) {

            throw new UnauthorizedAccessException(
                    "Access Denied: You can only access your own account");
        }

        return service.getCustomerById(id);
    }

    /*
     * ADMIN → LIST CUSTOMERS
     */
    @GetMapping("/list")
    public List<CustomerResponseDTO> getAllCustomers(Authentication auth) {

        boolean isAdmin =

                auth.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority()
                                .equals("ADMIN"));

        if (!isAdmin) {

            throw new AdminAccessRequiredException(
                    "Admin Access Required to list customers");

        }

        return service.getAllCustomers();

    }

    /*
     * UPDATE OWN ACCOUNT
     */
    @PutMapping("/update/{id}")
    public CustomerResponseDTO updateCustomer(

            @Valid @RequestBody CustomerRequestDTO dto,

            @PathVariable Long id) {

        return service.updateCustomer(dto, id);
    }

    /*
     * DELETE OWN ACCOUNT
     */
    @DeleteMapping("/delete/{id}")
    public String deleteCustomer(

            @PathVariable Long id,

            Authentication auth) {

        Long accountNo = Long.parseLong(auth.getName());

        if (!id.equals(accountNo)) {

            throw new UnauthorizedAccessException(
                    "Access Denied: You can only delete your own account");
        }

        service.deleteCustomer(id);

        return "Customer Deleted Successfully";

    }

}