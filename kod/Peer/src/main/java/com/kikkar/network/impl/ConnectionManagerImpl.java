package com.kikkar.network.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import com.kikkar.network.ConnectionManager;
import com.kikkar.network.PeerConnector;
import com.kikkar.packet.ConnectionType;
import com.kikkar.packet.KeepAliveMessage;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.PingMessage;
import com.kikkar.packet.PongMessage;
import com.kikkar.packet.RequestMessage;
import com.kikkar.packet.ResponseMessage;
import com.kikkar.packet.TerminatedMessage;
import com.kikkar.packet.TerminatedReason;

public class ConnectionManagerImpl implements ConnectionManager {
	private PeerConnector peerConnector;
	private DatagramSocket socket;
	private short clubNum;
	private List<PeerInformation> peerList;

	@Override
	public DatagramPacket createPingPacket(PeerInformation peerInformation, ConnectionType connectionType)
			throws IOException {
		PingMessage pingMessage = peerConnector.createPingMessage(peerInformation, clubNum, connectionType);
		PacketWrapper packet = MessageWrapper.wrapMessage(pingMessage, peerInformation);
		DatagramPacket datagramPacket = peerConnector.createSendDatagramPacket(packet, peerInformation);

		return datagramPacket;
	}

	@Override
	public DatagramPacket createPongMessage(PeerInformation peerInformation, int bufferVideoNum, PingMessage ping)
			throws IOException {
		int uploadLinkNum = (int) peerList.stream().filter(s -> s.getPeerStatus().equals(PeerStatus.UPLOAD_CONNECTION))
				.count();
		int downloadLinkNum = (int) peerList.stream()
				.filter(s -> s.getPeerStatus().equals(PeerStatus.DOWNLOAD_CONNECTION)).count();

		PongMessage pongMessage = peerConnector.createPongMessage(uploadLinkNum, downloadLinkNum, bufferVideoNum, ping);
		PacketWrapper packet = MessageWrapper.wrapMessage(pongMessage, peerInformation);
		DatagramPacket datagramPacket = peerConnector.createSendDatagramPacket(packet, peerInformation);

		return datagramPacket;
	}
	
	@Override
	public DatagramPacket createRequestMessage(PeerInformation peerInformation, ConnectionType connectionType)
			throws IOException {
		
		RequestMessage requestMessage = peerConnector.createRequestMessage(peerInformation, clubNum, connectionType);
		PacketWrapper packet = MessageWrapper.wrapMessage(requestMessage, peerInformation);
		DatagramPacket datagramPacket = peerConnector.createSendDatagramPacket(packet, peerInformation);
		
		return datagramPacket;
	}

	@Override
	public DatagramPacket createResponseMessage(PeerInformation peerInformation, RequestMessage requestMessage) throws IOException {
		ResponseMessage responseMessage = peerConnector.createResponseMessage(requestMessage);
		PacketWrapper packet = MessageWrapper.wrapMessage(responseMessage, peerInformation);
		DatagramPacket datagramPacket = peerConnector.createSendDatagramPacket(packet, peerInformation);
		
		return datagramPacket;
	}
	
	@Override
	public DatagramPacket createTerminatedMessage(PeerInformation peerInformation, TerminatedReason terminatedReason) throws IOException {
		TerminatedMessage terminatedMessage = peerConnector.createTerminateConnectionMessage(peerInformation, terminatedReason);
		PacketWrapper packet = MessageWrapper.wrapMessage(terminatedMessage, peerInformation);
		DatagramPacket datagramPacket = peerConnector.createSendDatagramPacket(packet, peerInformation);
		
		return datagramPacket;		
	}
	
	@Override
	public DatagramPacket createKeepAliveMessage(PeerInformation peerInformation) throws IOException {
		KeepAliveMessage keepAliveMessage = peerConnector.createKeepAliveMessage();
		PacketWrapper packet = MessageWrapper.wrapMessage(keepAliveMessage, peerInformation);
		DatagramPacket datagramPacket = peerConnector.createSendDatagramPacket(packet, peerInformation);
		
		return datagramPacket;				
	}
	
	
	
	
	
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
