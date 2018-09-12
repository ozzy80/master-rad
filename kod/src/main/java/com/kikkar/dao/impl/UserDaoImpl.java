package com.kikkar.dao.impl;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kikkar.dao.UserDao;
import com.kikkar.model.User;

@Repository
@Transactional
public class UserDaoImpl implements UserDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void addUser(User user) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(user);
		session.flush();
	}

	@Override
	public User getUserbyUsername(String username) {
		Session session = sessionFactory.getCurrentSession();
		User user = session.get(User.class, username);
		session.flush();
		
		return user;
	}

}
