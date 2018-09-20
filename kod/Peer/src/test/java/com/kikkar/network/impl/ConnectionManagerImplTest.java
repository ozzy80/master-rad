package com.kikkar.network.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.protobuf.InvalidProtocolBufferException;
import com.kikkar.packet.ConnectionType;
import com.kikkar.packet.KeepAliveMessage;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.PingMessage;
import com.kikkar.packet.PongMessage;
import com.kikkar.packet.RequestMessage;
import com.kikkar.packet.ResponseMessage;
import com.kikkar.packet.TerminatedMessage;
import com.kikkar.packet.TerminatedReason;

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

	void assertPacket(PacketWrapper packet, DatagramPacket datagramPacket) throws InvalidProtocolBufferException {
		assertEquals(packet, PacketWrapper.parseFrom(datagramPacket.getData()));
		assertEquals(new String(ipAddress), datagramPacket.getAddress().getHostAddress());
		assertEquals(portNum, datagramPacket.getPort());		
	}
	
	void resetCounters(PeerInformation peerInformation) {
		peerInformation.setPingMessageNumber((short) 0);
		peerInformation.setRequestMessageNumber((short) 0);
		peerInformation.setLastSentPacketNumber(0);		
	}
	
	@Test
	void testCreatePingMessage_checkDefaultBehaviour() throws IOException {
		ConnectionType connectionType = ConnectionType.BOTH;
		
		PingMessage pingMessage = peerConnectorImpl.createPingMessage(peerInformation, clubNum, connectionType);
		PacketWrapper packet = MessageWrapper.wrapMessage(pingMessage, peerInformation);
		resetCounters(peerInformation);
		DatagramPacket datagramPacket = connectionManagerImpl.createPingPacket(peerInformation, connectionType);
		
		assertPacket(packet, datagramPacket);
	}
	
	@Test
	void testCreatePongMessage_checkDefaultBehaviour() throws IOException {
		int uploadLinkNum = 3;
		int downloadLinkNum = 2;
		int bufferVideoNum = 1024;
		ConnectionType connectionType = ConnectionType.DOWNLOAD;
		connectionManagerImpl.setPeerList(createDummyPeers(uploadLinkNum, downloadLinkNum));
		
		PingMessage pingMessage = peerConnectorImpl.createPingMessage(peerInformation, clubNum, connectionType);
		PongMessage pongMessage = peerConnectorImpl.createPongMessage(uploadLinkNum, downloadLinkNum, bufferVideoNum, pingMessage);
		PacketWrapper packet = MessageWrapper.wrapMessage(pongMessage, peerInformation);
		resetCounters(peerInformation);
		DatagramPacket datagramPacket = connectionManagerImpl.createPongMessage(peerInformation, bufferVideoNum, pingMessage);
		
		assertPacket(packet, datagramPacket);
	}
	
	List<PeerInformation> createDummyPeers(int uploadLinkNum, int downloadLinkNum){
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

	@Test
	void testCreateRequestMessage_checkDefaultBehaviour() throws IOException {
		ConnectionType connectionType = ConnectionType.DOWNLOAD;
		
		RequestMessage requestMessage = peerConnectorImpl.createRequestMessage(peerInformation, clubNum, connectionType);
		PacketWrapper packet = MessageWrapper.wrapMessage(requestMessage, peerInformation);
		resetCounters(peerInformation);
		DatagramPacket datagramPacket = connectionManagerImpl.createRequestMessage(peerInformation, connectionType);
		
		assertPacket(packet, datagramPacket);
	}
	
	@Test
	void testCreateResponseMessage_checkDefaultBehaviour() throws IOException {
		ConnectionType connectionType = ConnectionType.DOWNLOAD;
		
		RequestMessage requestMessage = peerConnectorImpl.createRequestMessage(peerInformation, clubNum, connectionType);
		ResponseMessage responseMessage = peerConnectorImpl.createResponseMessage(requestMessage);
		PacketWrapper packet = MessageWrapper.wrapMessage(responseMessage, peerInformation);
		resetCounters(peerInformation);
		DatagramPacket datagramPacket = connectionManagerImpl.createResponseMessage(peerInformation, requestMessage);
		
		assertPacket(packet, datagramPacket);
	}
	
	@Test
	void testcreateTerminatedMessage_checkDefaultBehaviour() throws IOException {
		TerminatedReason terminatedReason = TerminatedReason.DEAD_PEER;
		
		TerminatedMessage terminatedMessage = peerConnectorImpl.createTerminateConnectionMessage(peerInformation, terminatedReason);
		PacketWrapper packet = MessageWrapper.wrapMessage(terminatedMessage, peerInformation);
		resetCounters(peerInformation);
		DatagramPacket datagramPacket = connectionManagerImpl.createTerminatedMessage(peerInformation, terminatedReason);
		
		assertPacket(packet, datagramPacket);
	}
	
	@Test
	void testCreateKeepAliveMessage_checkDefaultBehaviour() throws IOException {
		KeepAliveMessage keepAliveMessage = peerConnectorImpl.createKeepAliveMessage();
		PacketWrapper packet = MessageWrapper.wrapMessage(keepAliveMessage, peerInformation);
		resetCounters(peerInformation);
		DatagramPacket datagramPacket = connectionManagerImpl.createKeepAliveMessage(peerInformation);
		
		assertPacket(packet, datagramPacket);		
	}
	
}
