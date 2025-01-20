package com.saurabh.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;
@Data
@AllArgsConstructor
public class EMIOutstanding {
    private UUID purchaseId;
    private int emiMonths;
    private BigDecimal remainingAmount;
}
