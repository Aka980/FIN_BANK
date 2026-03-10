package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/*
 * PasswordConfig
 * --------------
 * Creates BCryptPasswordEncoder Bean.
 * Used for password encryption.
 */

@Configuration
public class PasswordConfig {

    @Bean
    public BCryptPasswordEncoder encoder(){

        return new BCryptPasswordEncoder();
    }

}