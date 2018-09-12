package com.kikkar.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kikkar.dao.ChannelDao;
import com.kikkar.dao.PeerInformationDao;
import com.kikkar.model.Channel;
import com.kikkar.model.PeerInformation;
import com.kikkar.service.PeerInformationManager;

@Service
public class PeerInformationManagerImpl implements PeerInformationManager {

	@Autowired
	private PeerInformationDao peerInformationDao;

	@Autowired
	private ChannelDao channelDao;

	@Override
	public List<PeerInformation> getPeersList(int limit, Long channelId) {
		Channel channel = channelDao.getChannelByID(channelId);
		return peerInformationDao.getPeersList(limit, channel);
	}

	@Override
	public void addPeer(PeerInformation peer) {
		peerInformationDao.addPeer(peer);
	}

	@Override
	public Short getLastChannelClubNumber(Long channelId) {
		Channel channel = channelDao.getChannelByID(channelId);
		PeerInformation peerInformation = peerInformationDao.getLastActivePeer(channel);
		if (peerInformation == null) {
			return (short) 0;
		}
		return peerInformation.getClubNumber();
	}

	@Override
	public void deletePeer(String ipAddress) {
		PeerInformation peerInformation = peerInformationDao.getPeerById(ipAddress.getBytes());
		peerInformationDao.deletePeer(peerInformation);
	}

	@Override
	public void stayAlive(String ipAddress) {
		PeerInformation peerInformation = peerInformationDao.getPeerById(ipAddress.getBytes());
		peerInformation.setLastActiveMessage(new Date());
		peerInformationDao.addPeer(peerInformation);
	}

	// TODO Odkomentarisi kad se povezu parnjaci
	@Scheduled(fixedRate = 600000)
	private void helloWorld() {
		System.out.println("aaaaaaaaa" + new Date());
		// peerInformationDao.deleteDeadPeers(10l);
	}
}
