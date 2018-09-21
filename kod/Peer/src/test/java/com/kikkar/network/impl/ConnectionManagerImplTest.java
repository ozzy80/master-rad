package com.kikkar.network.impl;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

class ConnectionManagerImplTest {

	private DatagramSocket socket = Mockito.mock(DatagramSocket.class);
	private PeerConnectorImpl peerConnectorImpl;
	private ConnectionManagerImpl connectionManagerImpl;
	private PeerInformation peerInformation;
	private byte[] ipAddress = "192.168.0.2".getBytes();
	private int portNum = 54321;
	private short clubNum = 0;

	@BeforeEach
	void setup() {
		peerConnectorImpl = new PeerConnectorImpl();
		connectionManagerImpl = new ConnectionManagerImpl();
		connectionManagerImpl.setPeerConnector(peerConnectorImpl);
		connectionManagerImpl.setSocket(socket);
		peerInformation = new PeerInformation(ipAddress, portNum, clubNum);
	}

	List<PeerInformation> createDummyPeers(int uploadLinkNum, int downloadLinkNum) {
		List<PeerInformation> peerInformations = new ArrayList<>();
		PeerInformation peerInformation = null;

		for (int i = 0; i < downloadLinkNum; i++) {
			String ip = "192.168.0." + (i + 1);
			peerInformation = new PeerInformation(ip.getBytes(), portNum, clubNum);
			peerInformation.setPeerStatus(PeerStatus.DOWNLOAD_CONNECTION);
			peerInformations.add(peerInformation);
		}

		for (int i = downloadLinkNum; i < uploadLinkNum + downloadLinkNum; i++) {
			String ip = "192.168.0." + (i + 1);
			peerInformation = new PeerInformation(ip.getBytes(), portNum, clubNum);
			peerInformation.setPeerStatus(PeerStatus.UPLOAD_CONNECTION);
			peerInformations.add(peerInformation);
		}
		peerInformation = new PeerInformation("192.168.0.1".getBytes(), portNum, clubNum);
		peerInformation.setPeerStatus(PeerStatus.NOT_CONTACTED);
		peerInformations.add(peerInformation);

		peerInformation = new PeerInformation("192.168.0.1".getBytes(), portNum, clubNum);
		peerInformation.setPeerStatus(PeerStatus.PING_PONG_EXCHANGE);
		peerInformations.add(peerInformation);

		peerInformation = new PeerInformation("192.168.0.1".getBytes(), portNum, clubNum);
		peerInformation.setPeerStatus(PeerStatus.PONG_WAIT);
		peerInformations.add(peerInformation);

		peerInformation = new PeerInformation("192.168.0.1".getBytes(), portNum, clubNum);
		peerInformation.setPeerStatus(PeerStatus.RESPONSE_WAIT);
		peerInformations.add(peerInformation);

		return peerInformations;
	}

}
