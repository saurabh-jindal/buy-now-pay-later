package com.saurabh.demo.controller;

import com.saurabh.demo.models.*;
import com.saurabh.demo.service.PurchaseService;
import com.saurabh.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
class BuyNowPayLaterController {
    @Autowired
	private UserService userService;

    @Autowired
	private PurchaseService purchaseService;

	@PostMapping("/register")
	public ResponseEntity<User> registerUser(@RequestBody User user) {
		User registeredUser = userService.registerUser(user);
		return ResponseEntity.ok(registeredUser);
	}

	@PostMapping("/purchase")
	public ResponseEntity<String> recordPurchase(@RequestBody PurchaseRequest request) {
		String response = purchaseService.recordPurchase(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/payment")
	public ResponseEntity<String> recordPayment(@RequestBody PaymentRequest request) {
		String response = purchaseService.recordPayment(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/user/{userId}/balance")
	public ResponseEntity<UserBalance> getUserBalance(@PathVariable Long userId) {
		UserBalance balanceResponse = userService.getUserBalance(userId);
		return ResponseEntity.ok(balanceResponse);
	}

	@GetMapping("/reports/outstanding")
	public ResponseEntity<List<OutstandingReport>> getOutstandingReports(@RequestParam Optional<LocalDate> startDate,
																		 @RequestParam Optional<LocalDate> endDate,
																		 @RequestParam Optional<List<Long>> userIds,
																		 @RequestParam Optional<BigDecimal> minAmount,
																		 @RequestParam Optional<BigDecimal> maxAmount) {
		List<OutstandingReport> reports = purchaseService.getOutstandingReports(startDate, endDate, userIds, minAmount, maxAmount);
		return ResponseEntity.ok(reports);
	}

	@GetMapping("/reports/common-payment-plans")
	public ResponseEntity<Map<String, Long>> getMostCommonPaymentPlans() {
		Map<String, Long> response = purchaseService.getMostCommonPaymentPlans();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/user/{userId}/repayment-history")
	public ResponseEntity<List<RepaymentHistory>> getRepaymentHistory(@PathVariable Long userId) {
		List<RepaymentHistory> repaymentHistories = purchaseService.getRepaymentHistory(userId);
		return ResponseEntity.ok(repaymentHistories);
	}

	@GetMapping("/user/{userId}/emi-outstanding")
	public ResponseEntity<List<EMIOutstanding>> getEmiWiseOutstandingBalance(@PathVariable Long userId) {
		List<EMIOutstanding> emiOutstanding = purchaseService.getEmiWiseOutstandingBalance(userId);
		return ResponseEntity.ok(emiOutstanding);
	}
}