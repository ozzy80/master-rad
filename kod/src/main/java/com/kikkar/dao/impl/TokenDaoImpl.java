package com.kikkar.dao.impl;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kikkar.dao.TokenDao;
import com.kikkar.model.Token;

@Repository
@Transactional
public class TokenDaoImpl implements TokenDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void addToken(Token token) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(token);
		session.flush();
	}

	@Override
	public Token getTokenByIpAddress(byte[] ipAddress) {
		Session session = sessionFactory.getCurrentSession();
		Token token = session.get(Token.class, ipAddress);
		session.flush();

		return token;
	}

	@Override
	public void deleteToken(Token token) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(token);
		session.flush();
	}

}
