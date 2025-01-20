package com.saurabh.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UserBalance {
	private BigDecimal availableCredit;
	private BigDecimal creditLimit;
}
