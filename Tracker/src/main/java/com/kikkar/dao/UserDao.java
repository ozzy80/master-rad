package com.kikkar.dao;

import com.kikkar.model.User;

public interface UserDao {

	void addUser(User user);

	User getUserbyUsername(String username);

}
