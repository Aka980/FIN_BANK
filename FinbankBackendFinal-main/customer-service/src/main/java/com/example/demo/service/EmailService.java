package com.example.demo.service;

import com.example.demo.dto.LoanEmailRequestDTO;
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
                        "Thank you for choosing FinBank.");

        mailSender.send(message);
    }

    public void sendLoanStatusEmail(LoanEmailRequestDTO dto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(dto.getEmail());
        message.setSubject("FinBank Loan Application Update - #" + dto.getApplicationNo());
        message.setText(
                "Dear " + dto.getCustomerName() + ",\n\n" +
                        "This is to inform you that the status of your loan application #" + dto.getApplicationNo() +
                        " has been updated to: " + dto.getStatus() + ".\n\n" +
                        "Additional Message: " + dto.getMessage() + "\n\n" +
                        "Log in to your dashboard for more details.\n\n" +
                        "Thank you for banking with FinBank.");
        mailSender.send(message);
    }
}