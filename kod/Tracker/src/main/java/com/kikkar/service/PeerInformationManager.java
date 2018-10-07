package com.kikkar.service;

import java.util.List;

import com.kikkar.model.PeerInformation;

public interface PeerInformationManager {

	List<PeerInformation> getPeersList(int limit, Long channelId, String ip);

	void addPeer(PeerInformation peer);

	public Short getLastChannelClubNumber(Long channelId);

	void deletePeer(String ipAddress);

	void stayAlive(String ipAddress);

	PeerInformation getPeerById(String ip);
}
