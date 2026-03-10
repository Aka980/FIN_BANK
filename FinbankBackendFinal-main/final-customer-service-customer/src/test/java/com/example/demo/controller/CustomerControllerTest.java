package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.demo.config.PasswordConfig;
import com.example.demo.config.SecurityConfig;
import com.example.demo.dto.CustomerRequestDTO;
import com.example.demo.dto.CustomerResponseDTO;
import com.example.demo.dto.LoginDTO;
import com.example.demo.entity.Customer;
import com.example.demo.jwt.JwtUtil;
import com.example.demo.security.JwtFilter;
import com.example.demo.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters to avoid filter/CSRF complexity
@ActiveProfiles("test")
@Import({ SecurityConfig.class, PasswordConfig.class })
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService service;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerRequestDTO testDto;
    private CustomerResponseDTO testResponse;
    private UsernamePasswordAuthenticationToken auth;

    @BeforeEach
    void setUp() throws Exception {
        testDto = new CustomerRequestDTO();
        testDto.setCustomerName("Test User");
        testDto.setUsername("testuser");
        testDto.setEmail("test@gmail.com");
        testDto.setPhone("9999999999");
        testDto.setPassword("password123");

        testResponse = new CustomerResponseDTO();
        testResponse.setAccountNo(1L);
        testResponse.setCustomerName("Test User");
        testResponse.setUsername("testuser");

        // Prepare a token to be used with .principal() in controller tests
        auth = new UsernamePasswordAuthenticationToken("1", null,
                Collections.singletonList(new SimpleGrantedAuthority("CUSTOMER")));
    }

    @Test
    void testRegisterCustomer() throws Exception {
        when(service.registerCustomer(any(CustomerRequestDTO.class))).thenReturn(testResponse);

        mockMvc.perform(post("/customer/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testRegisterCustomer_InvalidData() throws Exception {
        testDto.setPassword("123"); // Too short, fails @Size(min=6)

        mockMvc.perform(post("/customer/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginCustomer() throws Exception {
        LoginDTO loginDto = new LoginDTO();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password123");

        Customer c = new Customer();
        c.setAccountNo(1L);
        c.setUsername("testuser");
        c.setRole("CUSTOMER");

        when(service.loginCustomer("testuser", "password123")).thenReturn(c);
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("mock-token");

        mockMvc.perform(post("/customer/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("mock-token"));
    }

    @Test
    void testGetCustomerById() throws Exception {
        when(service.getCustomerById(1L)).thenReturn(testResponse);

        mockMvc.perform(get("/customer/1").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNo").value(1L));
    }

    @Test
    void testGetCustomerById_Unauthorized() throws Exception {
        mockMvc.perform(get("/customer/2").principal(auth))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllCustomers_Admin() throws Exception {
        UsernamePasswordAuthenticationToken adminAuth = new UsernamePasswordAuthenticationToken("1", null,
                Collections.singletonList(new SimpleGrantedAuthority("ADMIN")));
        when(service.getAllCustomers()).thenReturn(Collections.singletonList(testResponse));

        mockMvc.perform(get("/customer/list").principal(adminAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetAllCustomers_Forbidden() throws Exception {
        mockMvc.perform(get("/customer/list").principal(auth))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateCustomer() throws Exception {
        when(service.updateCustomer(any(CustomerRequestDTO.class), anyLong())).thenReturn(testResponse);

        mockMvc.perform(put("/customer/update/1")
                .principal(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testDeleteCustomer() throws Exception {
        mockMvc.perform(delete("/customer/delete/1").principal(auth))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer Deleted Successfully"));
    }

    @Test
    void testDeleteCustomer_Unauthorized() throws Exception {
        mockMvc.perform(delete("/customer/delete/2").principal(auth))
                .andExpect(status().isForbidden());
    }
}
