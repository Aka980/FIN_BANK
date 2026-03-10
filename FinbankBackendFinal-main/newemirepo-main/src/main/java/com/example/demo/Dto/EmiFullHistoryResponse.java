package com.example.demo.Dto;

import java.util.List;


import com.example.demo.model.EmiPaymentHistory;
import com.example.demo.model.EmiRecord;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmiFullHistoryResponse {

    private Long accnumber;
    private List<EmiRecord> emiPlans;
}