package com.kikkar.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kikkar.dao.ChannelDao;
import com.kikkar.model.Channel;
import com.kikkar.model.TopChannel;

@Repository
@Transactional
public class ChannelDaoImpl implements ChannelDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void addChannel(Channel channel) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(channel);
		session.flush();
	}

	@Override
	public Channel getChannelByID(Long channelId) {
		Session session = sessionFactory.getCurrentSession();
		Channel channel = session.get(Channel.class, channelId);
		session.flush();

		return channel;
	}

	@Override
	public void deleteChannel(Channel channel) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(channel);
		session.flush();
	}

	@Override
	public List<Channel> getAllChannels() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from Channel");
		List<Channel> channelList = query.list();
		session.flush();

		return channelList;
	}

	@Override
	public List<Channel> getChannelsByPopularity(int limit) {
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(TopChannel.class);
		criteria.setProjection(Projections.property("channel"));
		criteria.addOrder(Order.desc("viewsNumber"));
		criteria.setMaxResults(limit);

		List<Channel> topChannelList = criteria.list();
		session.flush();

		return topChannelList;
	}

}
