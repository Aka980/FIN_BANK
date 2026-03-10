package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FinBankAdminServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinBankAdminServiceApplication.class, args);
		System.out.println("admin service...");
	}

}
