package com.example.demo.dto;

import lombok.Data;

/*
 * LoginDTO
 * --------
 * Used for Login API.
 */

@Data
public class LoginDTO {

    private String username;
    private String password;

}