package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRegistrationMail(String email, Long accountNo) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("Welcome to FinBank");

        message.setText(
                "Dear Customer,\n\n" +
                "Welcome to FinBank!\n\n" +
                "Your account has been successfully created.\n" +
                "Your Bank Account Number is: " + accountNo + "\n\n" +
                "Thank you for choosing FinBank."
        );

        mailSender.send(message);
    }
}