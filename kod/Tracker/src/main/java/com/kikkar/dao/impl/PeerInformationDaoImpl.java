package com.kikkar.dao.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kikkar.dao.PeerInformationDao;
import com.kikkar.model.Channel;
import com.kikkar.model.PeerInformation;

@Repository
@Transactional
public class PeerInformationDaoImpl implements PeerInformationDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<PeerInformation> getPeersList(int limit, byte[] ipAddress, Channel channel) {
		Session session = sessionFactory.getCurrentSession();

		Query query = session
				.createQuery("from PeerInformation p where p.channel = :channel and p.clubNumber = :clubNumber and ipAddress <> :ipAddress")
				.setParameter("channel", channel).setParameter("ipAddress", ipAddress);

		List<PeerInformation> peerInformationList = query.setParameter("clubNumber", (short) 0).list();
		for (int i = 1; i < 5; i++) {
			peerInformationList.addAll(query.setParameter("clubNumber", (short) i).list());
		}
		session.flush();

		return peerInformationList;
	}

	@Override
	public void addPeer(PeerInformation peer) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(peer);
		session.flush();
	}

	@Override
	public PeerInformation getLastActivePeer(Channel channel) {
		Session session = sessionFactory.getCurrentSession();

		PeerInformation peerInformation = (PeerInformation) session.createCriteria(PeerInformation.class)
				.addOrder(Order.desc("lastActiveMessage")).setMaxResults(1).uniqueResult();

		return peerInformation;
	}

	@Override
	public void deletePeer(PeerInformation peerInformation) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(peerInformation);
		session.flush();
	}

	@Override
	public PeerInformation getPeerById(byte[] ipAddress) {
		Session session = sessionFactory.getCurrentSession();
		PeerInformation peerInformation = session.get(PeerInformation.class, ipAddress);
		session.flush();

		return peerInformation;
	}

	@Override
	public void deleteDeadPeers(Long elapsedMinutesSinceLastMessage) {
		Date in = new Date();
		LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
		ldt = ldt.minusMinutes(elapsedMinutesSinceLastMessage);
		Date utilDate = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("delete from PeerInformation where lastActiveMessage < :msgDate");
		query.setTimestamp("msgDate", utilDate);
		query.executeUpdate();
		session.flush();
	}

}
