package com.kikkar.service;

import com.kikkar.model.User;

public interface UserManager {

	void addUser(User user);

	User getUserByUsername(String username);

}
