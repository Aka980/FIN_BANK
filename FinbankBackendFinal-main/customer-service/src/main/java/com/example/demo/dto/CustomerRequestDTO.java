package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;



@Data
public class CustomerRequestDTO {

    @NotBlank(message="Customer name is required")
    @Size(min=3,max=50,
    message="Name must be 3-50 characters")
    private String customerName;



    @NotBlank(message="Username is required")
    @Size(min=4,max=20,
    message="Username must be 4-20 characters")
    private String username;



    @NotBlank(message="Email is required")
    @Email(message="Invalid Email Format")
    private String email;



    @NotBlank(message="Phone is required")
    @Pattern(
    regexp="^[0-9]{10}$",
    message="Phone must be 10 digits")
    private String phone;



    @NotBlank(message="Password is required")
    @Size(min=6,
    message="Password must be minimum 6 characters")
    private String password;

}