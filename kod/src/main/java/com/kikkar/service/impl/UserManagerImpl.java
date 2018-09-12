package com.kikkar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kikkar.dao.AuthorityDao;
import com.kikkar.dao.UserDao;
import com.kikkar.model.Authorities;
import com.kikkar.model.User;
import com.kikkar.service.UserManager;

@Service
public class UserManagerImpl implements UserManager {

	@Autowired
	private UserDao userDao;

	@Autowired
	private AuthorityDao authorityDao;

	@Override
	public void addUser(User user) {
		Authorities authority = new Authorities();
		authority.setAuthority("ROLE_USER");
		authority.setUser(user);
		user.setEnabled(true);
		userDao.addUser(user);
		authorityDao.updateUserRole(authority);
	}

	@Override
	public User getUserByUsername(String username) {
		User user = userDao.getUserbyUsername(username);
		return user;
	}

}
