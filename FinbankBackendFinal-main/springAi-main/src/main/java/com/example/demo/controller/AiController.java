package com.example.demo.controller;

import com.example.demo.services.ChatService;

import jakarta.validation.Valid;

import com.example.demo.dto.ChatRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final ChatService chatService;

    public AiController(ChatService chatService) {
        this.chatService = chatService;
    }

    // -------------------------------------------------
    // Test AI Connection
    // -------------------------------------------------
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        String result = chatService.testConnection();
        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------
    // Analyze Loan Full Summary
    // -------------------------------------------------
    @GetMapping("/loan-analysis/{loanId}")
    public ResponseEntity<String> analyzeLoan(
            @PathVariable Long loanId,
            @RequestHeader("Authorization") String token) {

        // Pass the Bearer token as-is to service
        String response = chatService.analyzeLoan(loanId, token);
        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------
    // Chat with AI about specific Loan
    // -------------------------------------------------
    @PostMapping("/chat/{loanId}")
    public ResponseEntity<String> chatWithLoan(
            @PathVariable Long loanId,
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ChatRequest request) {

        String response = chatService.chatWithLoanContext(loanId, token, request.getMessage());
        return ResponseEntity.ok(response);
    }
}