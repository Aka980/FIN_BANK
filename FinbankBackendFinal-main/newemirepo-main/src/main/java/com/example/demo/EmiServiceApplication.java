package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableFeignClients
@SpringBootApplication
@EnableMethodSecurity
public class EmiServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(EmiServiceApplication.class, args);
	}
}
