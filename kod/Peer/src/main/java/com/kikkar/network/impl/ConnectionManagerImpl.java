package com.kikkar.network.impl;

import java.net.DatagramSocket;
import java.util.List;

import com.kikkar.network.ConnectionManager;
import com.kikkar.network.PeerConnector;

public class ConnectionManagerImpl implements ConnectionManager {
	private PeerConnector peerConnector;
	private DatagramSocket socket;
	private short clubNum;
	private List<PeerInformation> peerList;

	public PeerConnector getPeerConnector() {
		return peerConnector;
	}

	public void setPeerConnector(PeerConnector peerConnector) {
		this.peerConnector = peerConnector;
	}

	public DatagramSocket getSocket() {
		return socket;
	}

	public void setSocket(DatagramSocket socket) {
		this.socket = socket;
	}

	public short getClubNum() {
		return clubNum;
	}

	public void setClubNum(short clubNum) {
		this.clubNum = clubNum;
	}

	public List<PeerInformation> getPeerList() {
		return peerList;
	}

	public void setPeerList(List<PeerInformation> peerList) {
		this.peerList = peerList;
	}

}
