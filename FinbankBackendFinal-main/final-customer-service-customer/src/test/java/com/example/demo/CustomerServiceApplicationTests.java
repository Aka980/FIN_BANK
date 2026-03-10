package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.jwt.JwtUtil;

@SpringBootTest
@ActiveProfiles("test")
class CustomerServiceApplicationTests {

	@MockBean
	private JavaMailSender javaMailSender;

	@MockBean
	private JwtUtil jwtUtil;

	@Test
	void contextLoads() {
	}

}
