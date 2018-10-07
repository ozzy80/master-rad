package com.kikkar.dao;

import java.util.List;

import com.kikkar.model.Channel;
import com.kikkar.model.PeerInformation;

public interface PeerInformationDao {

	List<PeerInformation> getPeersList(int limit, byte[] ipAddress, Channel channel);

	void addPeer(PeerInformation peer);

	PeerInformation getLastActivePeer(Channel channel);

	void deletePeer(PeerInformation peerInformation);

	PeerInformation getPeerById(byte[] bytes);

	void deleteDeadPeers(Long elapsedSinceLastMessage);
}
