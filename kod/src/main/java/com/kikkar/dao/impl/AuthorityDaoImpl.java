package com.kikkar.dao.impl;

import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kikkar.dao.AuthorityDao;
import com.kikkar.model.Authorities;

@Repository
@Transactional
public class AuthorityDaoImpl implements AuthorityDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void updateUserRole(Authorities authority) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(authority);
		session.flush();
	}

	@Override
	public Authorities getAuthorityByUsername(String username) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from Authorities where username = :username");
		query.setParameter("username", username);
		Authorities authority = (Authorities) query.uniqueResult();
		session.flush();

		return authority;
	}

}
