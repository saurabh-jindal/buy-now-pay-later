package com.saurabh.demo.service;

import com.saurabh.demo.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PurchaseService {
	@Autowired
	private UserService userService;

	private final Map<Long, List<Payment>> purchasePayments = new HashMap<>();
	private final Map<Long, List<Purchase>> userPurchases = new HashMap<>();

	public String recordPurchase(PurchaseRequest request) {
		User user = userService.getUsers().get(request.getUserId());
		if (user == null) {
			return "User not found";
		}

		BigDecimal totalCost = request.getEmiPlan() != null
				? request.getEmiPlan().calculateTotalWithInterest(request.getAmount())
				: request.getAmount();

		if (user.getAvailableCredit().compareTo(totalCost) < 0) {
			return "Insufficient credit limit";
		}

		user.setAvailableCredit(user.getAvailableCredit().subtract(request.getAmount()));
		List<Purchase> purchases = userPurchases.getOrDefault(request.getUserId(), new ArrayList<>());
		Purchase purchase = new Purchase(UUID.randomUUID(), request.getAmount(), LocalDate.now(), request.getEmiPlan());
		purchases.add(purchase);
		userPurchases.put(request.getUserId(), purchases);
		return "Purchase recorded successfully";
	}

	public String recordPayment(PaymentRequest request) {
		User user = userService.getUsers().get(request.getUserId());
		if (user == null) {
			return "User not found";
		}

		List<Purchase> purchases = userPurchases.getOrDefault(request.getUserId(), new ArrayList<>());
		BigDecimal remainingPayment = request.getAmount();
		List<Payment> paymentLog = new ArrayList<>();

		for (Purchase purchase : purchases) {
			if (purchase.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0 && remainingPayment.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal paymentTowardsThisPurchase = remainingPayment.min(purchase.getRemainingAmount());
				purchase.setRemainingAmount(purchase.getRemainingAmount().subtract(paymentTowardsThisPurchase));
				remainingPayment = remainingPayment.subtract(paymentTowardsThisPurchase);

				// Log this payment
				paymentLog.add(new Payment(purchase.getId(), LocalDate.now(), paymentTowardsThisPurchase));
			}

			// Check for overdue penalties
			if (purchase.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0 &&
					purchase.getDate().plusMonths(purchase.getEmiPlan() != null ? purchase.getEmiPlan().getMonths() : 1).isBefore(LocalDate.now())) {
				BigDecimal penalty = calculatePenalty(purchase.getRemainingAmount());
				purchase.setRemainingAmount(purchase.getRemainingAmount().add(penalty));
			}
		}

		user.setAvailableCredit(user.getAvailableCredit().add(request.getAmount().subtract(remainingPayment)));

		// Add payments to the user's payment log
		purchasePayments.computeIfAbsent(request.getUserId(), k -> new ArrayList<>()).addAll(paymentLog);

		return "Payment recorded successfully";
	}


	public List<OutstandingReport> getOutstandingReports(Optional<LocalDate> startDate, Optional<LocalDate> endDate, Optional<List<Long>> userIds, Optional<BigDecimal> minAmount, Optional<BigDecimal> maxAmount) {
		List<OutstandingReport> reports = new ArrayList<>();

		for (Map.Entry<Long, List<Purchase>> entry : userPurchases.entrySet()) {
			Long userId = entry.getKey();
			if (userIds.isPresent() && !userIds.get().contains(userId)) {
				continue;
			}

			for (Purchase purchase : entry.getValue()) {
				if (startDate.isPresent() && purchase.getDate().isBefore(startDate.get())) {
					continue;
				}
				if (endDate.isPresent() && purchase.getDate().isAfter(endDate.get())) {
					continue;
				}
				if (minAmount.isPresent() && purchase.getRemainingAmount().compareTo(minAmount.get()) < 0) {
					continue;
				}
				if (maxAmount.isPresent() && purchase.getRemainingAmount().compareTo(maxAmount.get()) > 0) {
					continue;
				}

				reports.add(new OutstandingReport(userId, purchase));
			}
		}

		return reports;
	}

	private BigDecimal calculatePenalty(BigDecimal overdueAmount) {
		BigDecimal penaltyRate = BigDecimal.valueOf(0.02); // 2% penalty rate
		return overdueAmount.multiply(penaltyRate);
	}

	public Map<String, Long> getMostCommonPaymentPlans() {
		return userPurchases.values().stream()
				.flatMap(List::stream)
				.filter(purchase -> purchase.getEmiPlan() != null)
				.collect(Collectors.groupingBy(
						purchase -> purchase.getEmiPlan().getMonths() + " months at " + purchase.getEmiPlan().getInterestRate() + " interest",
						Collectors.counting()
				));
	}

	public List<RepaymentHistory> getRepaymentHistory(Long userId) {
		List<Purchase> purchases = userPurchases.getOrDefault(userId, new ArrayList<>());
		List<Payment> payments = purchasePayments.getOrDefault(userId, new ArrayList<>());

		return purchases.stream()
				.map(purchase -> {
					List<Payment> purchasePayments = payments.stream()
							.filter(payment -> payment.getPurchaseId().equals(purchase.getId()))
							.collect(Collectors.toList());
					return new RepaymentHistory(
							purchase.getId(),
							purchase.getDate(),
							purchase.getAmount(),
							purchasePayments,
							purchase.getRemainingAmount()
					);
				})
				.collect(Collectors.toList());
	}


	public List<EMIOutstanding> getEmiWiseOutstandingBalance(Long userId) {
		List<Purchase> purchases = userPurchases.getOrDefault(userId, new ArrayList<>());
		return purchases.stream()
				.filter(purchase -> purchase.getEmiPlan() != null)
				.map(purchase -> new EMIOutstanding(purchase.getId(), purchase.getEmiPlan().getMonths(), purchase.getRemainingAmount()))
				.collect(Collectors.toList());
	}
}
