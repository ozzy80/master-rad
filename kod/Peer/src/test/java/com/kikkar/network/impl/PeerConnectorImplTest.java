package com.kikkar.network.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.kikkar.packet.ConnectionType;
import com.kikkar.packet.KeepAliveMessage;
import com.kikkar.packet.PacketWrapper;
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
	private Integer portNum = 54321;

	@BeforeEach
	void setup() {
		peerConnectorImpl = new PeerConnectorImpl();
	}

	@Test
	void testCreatePingMessage_checkDefaultBehaviour() {
		short pingNum = 0;
		ConnectionType connectionType = ConnectionType.BOTH;

		PingMessage pingExpected = PingMessage.newBuilder().setClubNumber(clubNum).setPingId(pingNum)
				.setConnectionType(connectionType).build();
		PeerInformation peerInformation = new PeerInformation(ipAddress, portNum, clubNum);

		assertEquals(pingExpected, peerConnectorImpl.createPingMessage(peerInformation, clubNum, connectionType));
	}

	private static Stream<Arguments> createDifferentShortNumberWithBoundaries() {
		return Stream.of(Arguments.of((short) 0, (short) 1), Arguments.of((short) -505, (short) -504),
				Arguments.of((short) 30811, (short) 30812), Arguments.of(Short.MAX_VALUE, Short.MIN_VALUE),
				Arguments.of(Short.MIN_VALUE, (short) (Short.MIN_VALUE + 1)));
	}

	@ParameterizedTest
	@MethodSource("createDifferentShortNumberWithBoundaries")
	void testCreatePingMessage_checkPingMessageNumberRotation(short pingNum, short pingNumExpected) {
		ConnectionType connectionType = ConnectionType.BOTH;

		PeerInformation peerInformation = new PeerInformation(ipAddress, portNum, clubNum);
		peerInformation.setPingMessageNumber(pingNum);
		peerConnectorImpl.createPingMessage(peerInformation, clubNum, connectionType);

		assertEquals(pingNumExpected, peerInformation.getPingMessageNumber());
	}

	@Test
	void testCreatePongMessage_checkDefaultBehaviour() {
		int uploadLinkNum = 3;
		int downloadLinkNum = 2;
		int bufferVideoNum = 34;
		int pingId = 0;

		PongMessage pongExpected = PongMessage.newBuilder().setBufferVideoNum(bufferVideoNum)
				.setDownloadLinkNum(downloadLinkNum).setUploadLinkNum(uploadLinkNum).setResponsePingId(pingId).build();
		PingMessage ping = PingMessage.newBuilder().setPingId(pingId).build();
		PongMessage pongActual = peerConnectorImpl.createPongMessage(uploadLinkNum, downloadLinkNum, bufferVideoNum,
				ping);

		assertEquals(pongExpected, pongActual);
	}

	void testCreateRequestMessage_checkDefaultBehaviour() {
		short requestNum = 0;
		ConnectionType connectionType = ConnectionType.DOWNLOAD;

		RequestMessage requestExpected = RequestMessage.newBuilder().setClubNumber(clubNum).setRequestId(requestNum)
				.setConnectionType(connectionType).build();
		PeerInformation peerInformation = new PeerInformation(ipAddress, portNum, clubNum);
		RequestMessage requestActual = peerConnectorImpl.createRequestMessage(peerInformation, clubNum, connectionType);

		assertEquals(requestExpected, requestActual);
	}

	@ParameterizedTest
	@MethodSource("createDifferentShortNumberWithBoundaries")
	void testCreateRequestMessage_checkRequestMessageNumberRotation(short requestNum, short requestNumExpected) {
		ConnectionType connectionType = ConnectionType.DOWNLOAD;

		PeerInformation peerInformation = new PeerInformation(ipAddress, portNum, clubNum);
		peerInformation.setRequestMessageNumber(requestNum);
		peerConnectorImpl.createRequestMessage(peerInformation, clubNum, connectionType);

		assertEquals(requestNumExpected, peerInformation.getRequestMessageNumber());
	}

	@Test
	void testCreateResponseMessage_checkDefaultBehaviour() {
		int requestId = 0;

		ResponseMessage responseExpected = ResponseMessage.newBuilder().setResponseRequestId(requestId).build();
		RequestMessage request = RequestMessage.newBuilder().setRequestId(requestId).build();
		ResponseMessage responseActual = peerConnectorImpl.createResponseMessage(request);

		assertEquals(responseExpected, responseActual);
	}

	@Test
	void testCreateSendDatagramPacket_checkDefaultBehaviour() throws IOException {
		int packetId = 0;
		PeerInformation peerInformation = new PeerInformation(ipAddress, portNum, clubNum);
		PacketWrapper packet = PacketWrapper.newBuilder().setPacketId(packetId).build();

		DatagramPacket datagramPacket = peerConnectorImpl.createSendDatagramPacket(packet, peerInformation);

		assertEquals(packet, PacketWrapper.parseFrom(datagramPacket.getData()));
		assertEquals(new String(ipAddress), datagramPacket.getAddress().getHostAddress());
		assertEquals(portNum, new Integer(datagramPacket.getPort()));
	}

	@Test
	void testSend_checkIsPacketSent() throws IOException {
		int packetId = 0;
		DatagramSocket mockSocket = Mockito.mock(DatagramSocket.class);
		PeerInformation peerInformation = new PeerInformation(ipAddress, portNum, clubNum);
		PacketWrapper packetWrapper = PacketWrapper.newBuilder().setPacketId(packetId).build();

		DatagramPacket datagramPacket = peerConnectorImpl.createSendDatagramPacket(packetWrapper, peerInformation);
		peerConnectorImpl.send(datagramPacket, mockSocket);

		Mockito.verify(mockSocket).send(datagramPacket);
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
	void testCreateTerminateConnectionMessage_checkDefaultBehaviour() {
		TerminatedReason terminatedReason = TerminatedReason.BLOCK_TIMEOUT;
		PeerInformation peerInformation = new PeerInformation(ipAddress, portNum, clubNum);

		TerminatedMessage terminatedExpected = TerminatedMessage.newBuilder()
				.setTerminatedId(peerInformation.getLastSentPacketNumber()).setTerminatedReason(terminatedReason)
				.build();
		TerminatedMessage terminateActual = peerConnectorImpl.createTerminateConnectionMessage(peerInformation,
				terminatedReason);

		assertEquals(terminatedExpected, terminateActual);
	}

	@Test
	void testCreateKeepAliveMessage() {
		KeepAliveMessage aliveExpected = KeepAliveMessage.newBuilder().setMessageId(0).build();
		KeepAliveMessage aliveActual = peerConnectorImpl.createKeepAliveMessage();

		assertEquals(aliveExpected, aliveActual);
	}

}
