package com.saurabh.demo.service;

import com.fasterxml.jackson.datatype.jdk8.OptionalDoubleSerializer;
import com.saurabh.demo.models.User;
import com.saurabh.demo.models.UserBalance;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Getter
public class UserService {
    private final Map<Long, User> users = new HashMap<>();
	private long userIdCounter = 1;

	public User registerUser(User user) {
		user.setId(userIdCounter++);
		user.setAvailableCredit(user.getCreditLimit());
		users.put(user.getId(), user);
		return user;
	}

	public UserBalance getUserBalance(Long userId) {
		User user = users.get(userId);
		if (user == null) {
			throw new IllegalArgumentException("User not found");
		}

		return new UserBalance(user.getAvailableCredit(), user.getCreditLimit());
	}
}
