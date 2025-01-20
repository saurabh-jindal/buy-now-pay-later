package com.saurabh.demo.models;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class EMIPlan {
    private int months;
	private BigDecimal interestRate;

	public BigDecimal calculateTotalWithInterest(BigDecimal principal) {
		return principal.add(principal.multiply(interestRate).multiply(BigDecimal.valueOf(months / 12.0)));
	}
}
