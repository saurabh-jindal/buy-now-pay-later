package com.saurabh.demo.models;

import lombok.Data;

@Data
public class OutstandingReport {
	private Long userId;
	private Purchase purchase;

	public OutstandingReport(Long userId, Purchase purchase) {
		this.userId = userId;
		this.purchase = purchase;
	}
}
