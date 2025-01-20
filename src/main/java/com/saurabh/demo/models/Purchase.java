package com.saurabh.demo.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
@Data
public class Purchase {
    private UUID id;
	private BigDecimal amount;
	private LocalDate date;
	private EMIPlan emiPlan;
	private BigDecimal remainingAmount;

    public Purchase(UUID id, BigDecimal amount, LocalDate date, EMIPlan emiPlan) {
		this.id = id;
		this.amount = amount;
		this.date = date;
		this.emiPlan = emiPlan;
		this.remainingAmount = emiPlan != null ? emiPlan.calculateTotalWithInterest(amount) : amount;
	}
}
