package com.example.demo.service;

import com.example.demo.dto.AuthRequestDTO;
import com.example.demo.entity.Admin;
import com.example.demo.exception.DuplicateUsernameException;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.repository.AdminRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String registerAdmin(AuthRequestDTO dto) {
        if (dto.getFullName() == null || dto.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        String username = dto.getUsername().trim();

        if (adminRepository.findByUsername(username) != null) {
            throw new DuplicateUsernameException("Username already exists");
        }

        Admin admin = new Admin();
        admin.setFullName(dto.getFullName().trim());
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        admin.setRole("ADMIN"); // Hardcoded since only admins use this service

        adminRepository.save(admin);
        return "Admin registered successfully";
    }

    public String loginAdmin(AuthRequestDTO dto) {
        String username = dto.getUsername() == null ? "" : dto.getUsername().trim();
        Admin admin = adminRepository.findByUsername(username);
        if (admin == null || !passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String normalizedRole = normalizeRole(admin.getRole());
        return jwtUtil.generateToken(admin.getUsername(), normalizedRole);
    }

    private String normalizeRole(String role) {
        String candidate = role == null ? "ADMIN" : role.trim();
        if (candidate.startsWith("ROLE_")) {
            candidate = candidate.substring("ROLE_".length());
        }
        return candidate.toUpperCase(Locale.ROOT);
    }
}
