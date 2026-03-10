package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.CustomerProfileRequestDTO;
import com.example.demo.entity.CustomerProfile;
import com.example.demo.service.CustomerProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/profile")
public class CustomerProfileController {

        @Autowired
        private CustomerProfileService profileService;

        @PostMapping("/create")
        public CustomerProfile createProfile(
                        Authentication auth,
                        @Valid @RequestBody CustomerProfileRequestDTO dto) {

                Long accountNo;
                try {
                        accountNo = Long.parseLong(auth.getName());
                } catch (NumberFormatException e) {
                        throw new RuntimeException(
                                        "Invalid account number in token. This endpoint is for customers only.");
                }

                CustomerProfile profile = new CustomerProfile();
                profile.setPan(dto.getPan());
                profile.setDob(dto.getDob());
                profile.setAddress(dto.getAddress());
                profile.setIfsc(dto.getIfsc());
                profile.setAnnualIncome(dto.getAnnualIncome());
                profile.setOccupation(dto.getOccupation());

                return profileService.createProfile(accountNo, profile);
        }

        @GetMapping("/my")
        public CustomerProfile getProfile(Authentication auth) {
                Long accountNo;
                try {
                        accountNo = Long.parseLong(auth.getName());
                } catch (NumberFormatException e) {
                        throw new RuntimeException(
                                        "Invalid account number in token. This endpoint is for customers only.");
                }
                return profileService.getProfile(accountNo);
        }

        @PutMapping("/update")
        public CustomerProfile updateProfile(
                        Authentication auth,
                        @Valid @RequestBody CustomerProfileRequestDTO dto) {

                Long accountNo;
                try {
                        accountNo = Long.parseLong(auth.getName());
                } catch (NumberFormatException e) {
                        throw new RuntimeException(
                                        "Invalid account number in token. This endpoint is for customers only.");
                }

                CustomerProfile profile = new CustomerProfile();
                profile.setPan(dto.getPan());
                profile.setDob(dto.getDob());
                profile.setAddress(dto.getAddress());
                profile.setIfsc(dto.getIfsc());
                profile.setAnnualIncome(dto.getAnnualIncome());
                profile.setOccupation(dto.getOccupation());

                return profileService.updateProfile(accountNo, profile);
        }

        @GetMapping("/pan/{pan}")
        public CustomerProfile getByPan(@PathVariable String pan, Authentication auth) {
                boolean isAdmin = auth.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                if (!isAdmin) {
                        throw new RuntimeException("Admin Access Required");
                }
                return profileService.getByPan(pan);
        }

        @GetMapping("/account/{accountNo}")
        public CustomerProfile getProfileByAccount(@PathVariable Long accountNo, Authentication auth) {
                boolean isAdmin = auth.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                if (!isAdmin) {
                        throw new RuntimeException("Admin Access Required");
                }
                return profileService.getProfile(accountNo);
        }
        
        
}