package com.example.demo.controller;

import com.example.demo.dto.LoanResponseDTO;
import com.example.demo.entity.LoanType;
import com.example.demo.service.LoanService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService service;

    @Test
    @WithMockUser
    void testGetLoanStatus() throws Exception {

        LoanResponseDTO response = new LoanResponseDTO(
                1L,
                1001L,
                LocalDate.now(),
                50000.0,
                "ABCDE1234F",
                12,
                8.5,
                "PENDING",
                LoanType.BIKE_LOAN
        );

        when(service.getLoanStatus(1L)).thenReturn(response);

        mockMvc.perform(get("/loan/status/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser
    void testGetAllLoans() throws Exception {

        LoanResponseDTO response = new LoanResponseDTO(
                1L,
                1001L,
                LocalDate.now(),
                50000.0,
                "ABCDE1234F",
                12,
                8.5,
                "PENDING",
                LoanType.BIKE_LOAN
        );

        when(service.getAllLoans()).thenReturn(List.of(response));

        mockMvc.perform(get("/loan/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }
}