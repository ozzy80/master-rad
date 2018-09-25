package com.kikkar.network.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.protobuf.InvalidProtocolBufferException;
import com.kikkar.packet.ConnectionType;
import com.kikkar.packet.KeepAliveMessage;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.Pair;
import com.kikkar.packet.PingMessage;
import com.kikkar.packet.PongMessage;
import com.kikkar.packet.RequestMessage;
import com.kikkar.packet.ResponseMessage;
import com.kikkar.packet.TerminatedMessage;
import com.kikkar.packet.TerminatedReason;

class PeerConnectorImplTest {

	private PeerConnectorImpl peerConnectorImpl;
	private byte[] ipAddress = "192.168.0.2".getBytes();
	private short clubNum = 0;
	private int portNum = 54321;
	private PeerInformation peerInformation;

	@BeforeEach
	void setup() {
		peerConnectorImpl = new PeerConnectorImpl();
		peerInformation = new PeerInformation(ipAddress, portNum, clubNum);
		peerConnectorImpl.setThisPeer(new PeerInformation("172.168.0.1".getBytes(), portNum, clubNum));
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
	void testCreatePingMessage_checkDefaultBehaviour() throws InvalidProtocolBufferException, IOException {
		short pingNum = 0;
		ConnectionType connectionType = ConnectionType.BOTH;

		PingMessage ping = PingMessage.newBuilder().setClubNumber(clubNum).setPingId(pingNum)
				.setConnectionType(connectionType).build();
		PacketWrapper packet = MessageWrapper.wrapMessage(ping, peerInformation);
		resetCounters(peerInformation);

		assertPacket(packet, peerConnectorImpl.createPingMessage(peerInformation, clubNum, connectionType));
	}

	private static Stream<Arguments> createDifferentShortNumberWithBoundaries() {
		return Stream.of(Arguments.of((short) 0, (short) 1), Arguments.of((short) -505, (short) -504),
				Arguments.of((short) 30811, (short) 30812), Arguments.of(Short.MAX_VALUE, Short.MIN_VALUE),
				Arguments.of(Short.MIN_VALUE, (short) (Short.MIN_VALUE + 1)));
	}

	@ParameterizedTest
	@MethodSource("createDifferentShortNumberWithBoundaries")
	void testCreatePingMessage_checkPingMessageNumberRotation(short pingNum, short pingNumExpected) throws IOException {
		ConnectionType connectionType = ConnectionType.BOTH;

		PeerInformation peerInformation = new PeerInformation(ipAddress, portNum, clubNum);
		peerInformation.setPingMessageNumber(pingNum);
		peerConnectorImpl.createPingMessage(peerInformation, clubNum, connectionType);

		assertEquals(pingNumExpected, peerInformation.getPingMessageNumber());
	}

	@Test
	void testCreatePongMessage_checkDefaultBehaviour() throws InvalidProtocolBufferException, IOException {
		int uploadLinkNum = 3;
		int downloadLinkNum = 2;
		int bufferVideoNum = 34;
		int pingId = 0;

		PongMessage pongExpected = PongMessage.newBuilder().setBufferVideoNum(bufferVideoNum)
				.setDownloadLinkNum(downloadLinkNum).setUploadLinkNum(uploadLinkNum).setResponsePingId(pingId).build();
		PacketWrapper packet = MessageWrapper.wrapMessage(pongExpected, peerInformation);
		resetCounters(peerInformation);
		PingMessage ping = PingMessage.newBuilder().setPingId(pingId).build();

		assertPacket(packet, peerConnectorImpl.createPongMessage(peerInformation, uploadLinkNum, downloadLinkNum,
				bufferVideoNum, ping));
	}

	void testCreateRequestMessage_checkDefaultBehaviour() throws InvalidProtocolBufferException, IOException {
		short requestNum = 0;
		ConnectionType connectionType = ConnectionType.DOWNLOAD;

		RequestMessage request = RequestMessage.newBuilder().setClubNumber(clubNum).setRequestId(requestNum)
				.setConnectionType(connectionType).build();
		PacketWrapper packet = MessageWrapper.wrapMessage(request, peerInformation);
		resetCounters(peerInformation);

		assertPacket(packet, peerConnectorImpl.createRequestMessage(peerInformation, clubNum, connectionType));
	}

	@ParameterizedTest
	@MethodSource("createDifferentShortNumberWithBoundaries")
	void testCreateRequestMessage_checkRequestMessageNumberRotation(short requestNum, short requestNumExpected)
			throws IOException {
		ConnectionType connectionType = ConnectionType.DOWNLOAD;

		PeerInformation peerInformation = new PeerInformation(ipAddress, portNum, clubNum);
		peerInformation.setRequestMessageNumber(requestNum);
		peerConnectorImpl.createRequestMessage(peerInformation, clubNum, connectionType);

		assertEquals(requestNumExpected, peerInformation.getRequestMessageNumber());
	}

	@Test
	void testCreateResponseMessage_checkDefaultBehaviour() throws InvalidProtocolBufferException, IOException {
		int requestId = 0;

		ResponseMessage response = ResponseMessage.newBuilder().setResponseRequestId(requestId).build();
		RequestMessage request = RequestMessage.newBuilder().setRequestId(requestId).build();
		PacketWrapper packet = MessageWrapper.wrapMessage(response, peerInformation);
		resetCounters(peerInformation);

		assertPacket(packet, peerConnectorImpl.createResponseMessage(peerInformation, request));
	}

	@Test
	void testSend_checkIsPacketSent() throws IOException {
		ConnectionType connectionType = ConnectionType.DOWNLOAD;
		DatagramSocket mockSocket = Mockito.mock(DatagramSocket.class);

		DatagramPacket packet = peerConnectorImpl.createRequestMessage(peerInformation, clubNum, connectionType);
		peerConnectorImpl.send(packet, mockSocket);

		Mockito.verify(mockSocket).send(packet);
	}

	@Test
	void testCreateReciveDatagramPacket_checkDefaultBehaviour() throws IOException {
		int byteBufferSize = 1024;
		byte[] receiveData = new byte[byteBufferSize];

		DatagramPacket recivePacketExpected = new DatagramPacket(receiveData, receiveData.length);
		DatagramPacket recivePacketActual = peerConnectorImpl.createReciveDatagramPacket(byteBufferSize);

		assertEquals(recivePacketExpected.getLength(), recivePacketActual.getLength());
	}

	@Test
	void testRecive_checkDefaultBehaviour() throws IOException {
		int byteBufferSize = 1024;
		int packetId = 0;
		DatagramSocket mockSocket = Mockito.mock(DatagramSocket.class);
		DatagramPacket receivePacket = peerConnectorImpl.createReciveDatagramPacket(byteBufferSize);

		PacketWrapper packetWrapperSent = PacketWrapper.newBuilder().setPacketId(packetId).build();
		Mockito.doAnswer(new Answer<DatagramPacket>() {
			public DatagramPacket answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				((DatagramPacket) args[0]).setData(packetWrapperSent.toByteArray());
				return null;
			}
		}).when(mockSocket).receive(receivePacket);

		PacketWrapper packetWrapperRecived = peerConnectorImpl.recive(mockSocket, receivePacket);
		Mockito.verify(mockSocket).receive(receivePacket);

		assertEquals(packetWrapperSent, packetWrapperRecived);
	}

	@Test
	void testCreateTerminateConnectionMessage_checkDefaultBehaviour()
			throws InvalidProtocolBufferException, IOException {
		TerminatedReason terminatedReason = TerminatedReason.BLOCK_TIMEOUT;

		TerminatedMessage terminatedMessage = TerminatedMessage.newBuilder().setTerminatedId(0)
				.setTerminatedReason(terminatedReason).build();
		PacketWrapper packet = MessageWrapper.wrapMessage(terminatedMessage, peerInformation);
		resetCounters(peerInformation);

		assertPacket(packet, peerConnectorImpl.createTerminateConnectionMessage(peerInformation, terminatedReason));
	}

	@Test
	void testCreateKeepAliveMessage() throws InvalidProtocolBufferException, IOException {
		KeepAliveMessage keepAlive = KeepAliveMessage.newBuilder().setMessageId(0).build();
		PacketWrapper packet = MessageWrapper.wrapMessage(keepAlive, peerInformation);
		resetCounters(peerInformation);

		assertPacket(packet, peerConnectorImpl.createKeepAliveMessage(peerInformation));
	}

	@ParameterizedTest
	@EnumSource(value = ConnectionType.class,
			names = {"DOWNLOAD", "UPLOAD"})
	void testSendRequestMessage_checkChangingPeerStatus(ConnectionType connectionType) throws SocketException {
		int expectedNum = 3 + 1 + 2*5;
		List<PeerInformation> peerInformations = DummyObjectCreator.createDummyPeers(3, 1, 2);
		DatagramSocket socket = new DatagramSocket();
		
		peerConnectorImpl.sendRequestMessage(peerInformations, socket, connectionType, System.err);
		
		if(connectionType.equals(ConnectionType.DOWNLOAD)) {
			assertEquals(expectedNum, peerInformations.stream().filter(p -> p.getPeerStatus().equals(PeerStatus.RESPONSE_WAIT_DOWNLOAD)).count());
		}
		if(connectionType.equals(ConnectionType.UPLOAD)) {
			assertEquals(expectedNum, peerInformations.stream().filter(p -> p.getPeerStatus().equals(PeerStatus.RESPONSE_WAIT_UPLOAD)).count());
		}
	}
	
	@ParameterizedTest
	@EnumSource(value = ConnectionType.class,
			names = {"DOWNLOAD", "UPLOAD"})
	void testSendPingMessages_checkChangingPeerStatus(ConnectionType connectionType) throws SocketException {
		 int expectedNum = 6 + 6;
		 List<PeerInformation> peerInformations = DummyObjectCreator.createDummyPeers(3, 1, 6);
		 DatagramSocket socket = new DatagramSocket();
		 
		 peerConnectorImpl.sendPingMessages(peerInformations, connectionType, socket);
		 
	    if(connectionType.equals(ConnectionType.DOWNLOAD)) {
			assertEquals(expectedNum, peerInformations.stream().filter(p -> p.getPeerStatus().equals(PeerStatus.PONG_WAIT_DOWNLOAD)).count());
		}
		if(connectionType.equals(ConnectionType.UPLOAD)) {
			assertEquals(expectedNum, peerInformations.stream().filter(p -> p.getPeerStatus().equals(PeerStatus.PONG_WAIT_UPLOAD)).count());
		}
	}
	
	@ParameterizedTest
	@EnumSource(value = ConnectionType.class,
			names = {"DOWNLOAD", "UPLOAD"})
	void testSendResponseMessage_checkChangingPeerStatus(ConnectionType connectionType) throws SocketException {
		 PeerInformation peer = new PeerInformation(ipAddress, portNum, clubNum);
		 RequestMessage request = RequestMessage.newBuilder().setConnectionType(connectionType).build();
		 PacketWrapper packet = MessageWrapper.wrapMessage(request, peer);
		 DatagramSocket socket = new DatagramSocket();
		 
		 peerConnectorImpl.sendResponseMessage(peer, packet, socket);
		 
	    if(connectionType.equals(ConnectionType.DOWNLOAD)) {
			assertEquals(PeerStatus.DOWNLOAD_CONNECTION, peer.getPeerStatus());
		}
		if(connectionType.equals(ConnectionType.UPLOAD)) {
			assertEquals(PeerStatus.UPLOAD_CONNECTION, peer.getPeerStatus());
		}
	}
	
}
