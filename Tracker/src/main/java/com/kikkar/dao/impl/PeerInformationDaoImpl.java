package com.kikkar.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kikkar.dao.PeerInformationDao;
import com.kikkar.model.Channel;
import com.kikkar.model.PeerInformation;
import com.kikkar.model.TopChannel;

@Repository
@Transactional
public class PeerInformationDaoImpl implements PeerInformationDao {

	@Autowired
	private SessionFactory sessionFactory;

}
