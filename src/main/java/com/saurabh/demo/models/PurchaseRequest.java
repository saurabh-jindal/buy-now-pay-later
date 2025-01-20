package com.saurabh.demo.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseRequest {
    private Long userId;
	private BigDecimal amount;
	private EMIPlan emiPlan;
}
