
package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.CustomerProfileRequestDTO;
import com.example.demo.entity.CustomerProfile;
import com.example.demo.exception.AdminAccessRequiredException;
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

                Long accountNo = Long.parseLong(auth.getName());

                CustomerProfile profile = new CustomerProfile();

                profile.setPan(dto.getPan());
                profile.setDob(dto.getDob());
                profile.setAddress(dto.getAddress());
                profile.setIfsc(dto.getIfsc());
                profile.setAnnualIncome(dto.getAnnualIncome());
                profile.setOccupation(dto.getOccupation());

                return profileService
                                .createProfile(accountNo, profile);

        }

        @GetMapping("/my")
        public CustomerProfile getProfile(

                        Authentication auth) {

                Long accountNo = Long.parseLong(auth.getName());

                return profileService
                                .getProfile(accountNo);

        }

        @PutMapping("/update")
        public CustomerProfile updateProfile(

                        Authentication auth,

                        @Valid @RequestBody CustomerProfileRequestDTO dto) {

                Long accountNo = Long.parseLong(auth.getName());

                CustomerProfile profile = new CustomerProfile();

                profile.setPan(dto.getPan());
                profile.setDob(dto.getDob());
                profile.setAddress(dto.getAddress());
                profile.setIfsc(dto.getIfsc());
                profile.setAnnualIncome(dto.getAnnualIncome());
                profile.setOccupation(dto.getOccupation());

                return profileService
                                .updateProfile(accountNo, profile);

        }

        @GetMapping("/pan/{pan}")
        public CustomerProfile getByPan(
                        @PathVariable String pan,
                        Authentication auth) {

                boolean isAdmin =

                                auth.getAuthorities()
                                                .stream()
                                                .anyMatch(a -> a.getAuthority()
                                                		.equals("ADMIN"));

                if (!isAdmin) {

                        throw new AdminAccessRequiredException(
                                        "Admin Access Required to view PAN details");
                }

                return profileService
                                .getByPan(pan);

        }

        @GetMapping("/account/{accountNo}")
        public CustomerProfile getProfileByAccount(

                        @PathVariable Long accountNo,

                        Authentication auth) {

                boolean isAdmin =

                                auth.getAuthorities()
                                                .stream()
                                                .anyMatch(a -> a.getAuthority()
                                                		.equals("ADMIN"));

                if (!isAdmin) {

                        throw new AdminAccessRequiredException(
                                        "Admin Access Required to view other customer profiles");
                }

                return profileService
                                .getProfile(accountNo);

        }

}