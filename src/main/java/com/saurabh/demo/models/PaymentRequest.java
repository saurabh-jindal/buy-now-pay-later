package com.saurabh.demo.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
	private Long userId;
	private BigDecimal amount;
}
