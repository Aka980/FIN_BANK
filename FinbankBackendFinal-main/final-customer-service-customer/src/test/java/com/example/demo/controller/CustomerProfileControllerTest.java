package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.config.PasswordConfig;
import com.example.demo.config.SecurityConfig;
import com.example.demo.dto.CustomerProfileRequestDTO;
import com.example.demo.entity.CustomerProfile;
import com.example.demo.enums.IfscCode;
import com.example.demo.jwt.JwtUtil;
import com.example.demo.security.JwtFilter;
import com.example.demo.service.CustomerProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

@WebMvcTest(CustomerProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import({ SecurityConfig.class, PasswordConfig.class })
public class CustomerProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerProfileService profileService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerProfileRequestDTO testDto;
    private CustomerProfile testProfile;
    private UsernamePasswordAuthenticationToken customerAuth;
    private UsernamePasswordAuthenticationToken adminAuth;

    @BeforeEach
    void setUp() {
        testDto = new CustomerProfileRequestDTO();
        testDto.setPan("ABCDE1234F");
        testDto.setDob(LocalDate.now());
        testDto.setAddress("Test Address");
        testDto.setIfsc(IfscCode.FINBANKKAR001);
        testDto.setAnnualIncome(500000.0);
        testDto.setOccupation("Engineer");

        testProfile = new CustomerProfile();
        testProfile.setAccountNo(1L);
        testProfile.setPan("ABCDE1234F");

        customerAuth = new UsernamePasswordAuthenticationToken("1", null,
                Collections.singletonList(new SimpleGrantedAuthority("CUSTOMER")));

        adminAuth = new UsernamePasswordAuthenticationToken("2", null,
                Collections.singletonList(new SimpleGrantedAuthority("ADMIN")));
    }

    @Test
    void testCreateProfile() throws Exception {
        when(profileService.createProfile(anyLong(), any(CustomerProfile.class))).thenReturn(testProfile);

        mockMvc.perform(post("/profile/create")
                .principal(customerAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pan").value("ABCDE1234F"));
    }

    @Test
    void testGetMyProfile() throws Exception {
        when(profileService.getProfile(1L)).thenReturn(testProfile);

        mockMvc.perform(get("/profile/my").principal(customerAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pan").value("ABCDE1234F"));
    }

    @Test
    void testUpdateProfile() throws Exception {
        when(profileService.updateProfile(anyLong(), any(CustomerProfile.class))).thenReturn(testProfile);

        mockMvc.perform(put("/profile/update")
                .principal(customerAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pan").value("ABCDE1234F"));
    }

    @Test
    void testGetByPan_Admin() throws Exception {
        when(profileService.getByPan("ABCDE1234F")).thenReturn(testProfile);

        mockMvc.perform(get("/profile/pan/ABCDE1234F").principal(adminAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pan").value("ABCDE1234F"));
    }

    @Test
    void testGetByPan_Forbidden() throws Exception {
        mockMvc.perform(get("/profile/pan/ABCDE1234F").principal(customerAuth))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetProfileByAccount_Admin() throws Exception {
        when(profileService.getProfile(1L)).thenReturn(testProfile);

        mockMvc.perform(get("/profile/account/1").principal(adminAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pan").value("ABCDE1234F"));
    }

    @Test
    void testGetProfileByAccount_Forbidden() throws Exception {
        mockMvc.perform(get("/profile/account/1").principal(customerAuth))
                .andExpect(status().isForbidden());
    }
}
