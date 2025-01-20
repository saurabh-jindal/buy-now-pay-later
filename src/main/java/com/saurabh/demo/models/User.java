package com.saurabh.demo.models;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class User {
    private Long id;
	private String name;
	private BigDecimal creditLimit;
	private BigDecimal availableCredit;
}
