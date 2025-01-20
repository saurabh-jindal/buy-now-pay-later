package com.saurabh.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class RepaymentHistory {
    private UUID purchaseId;
    private LocalDate purchaseDate;
    private BigDecimal originalAmount;
    private List<Payment> payments;
    private BigDecimal remainingAmount;
}
