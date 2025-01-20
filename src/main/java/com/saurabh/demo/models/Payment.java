package com.saurabh.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
@Data
@AllArgsConstructor
public class Payment {
    private UUID purchaseId;
    private LocalDate paymentDate;
    private BigDecimal paymentAmount;
}
