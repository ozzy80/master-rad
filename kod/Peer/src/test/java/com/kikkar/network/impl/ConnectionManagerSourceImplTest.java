package com.kikkar.network.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import com.google.protobuf.InvalidProtocolBufferException;
import com.kikkar.global.ClockSingleton;
import com.kikkar.global.Constants;
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
import com.kikkar.packet.VideoPacket;

class ConnectionManagerSourceImplTest {

	private DatagramSocket socket = Mockito.mock(DatagramSocket.class);
	private PeerConnectorImpl peerConnectorImpl;
	private ConnectionManagerSourceImpl connectionManagerImpl;
	private short clubNum = 0;
	private ClockSingleton clock = ClockSingleton.getInstance();

	@BeforeEach
	void setup() {
		peerConnectorImpl = new PeerConnectorImpl();
		connectionManagerImpl = new ConnectionManagerSourceImpl();
		connectionManagerImpl.setPeerConnector(peerConnectorImpl);
		connectionManagerImpl.setSocket(socket);
		connectionManagerImpl.setDataWaitSecond(0);
		connectionManagerImpl.setThreadWaitSecond(0);
		Channel channel = new Channel();
		channel.setChannelId(1l);
		channel.setIpAddress("http://192.168.0.171:8080/Tracker");
		connectionManagerImpl.setChannel(channel);
		peerConnectorImpl.setThisPeer(new PeerInformation("192.168.0.1".getBytes(), 51234, clubNum));
	}

	private static Stream<Arguments> createMaintainClubParameters() {
		return Stream.of(Arguments.of((short) 2, (short) 0, (short) 0), Arguments.of((short) 1, (short) 6, (short) 1),
				Arguments.of((short) 0, 6 * 2, (short) 2), Arguments.of((short) 0, (short) 6 * 4, (short) 3));
	}

	private void testMaintainClubsConnection_setup(int uploadConnNUm) throws IOException {
		List<PeerInformation> peerListActual = DummyObjectCreator.createDummyPeers(uploadConnNUm, 0, 6 * 3);
		peerListActual.stream()
				.filter(p -> p.getPeerStatus().equals(PeerStatus.RESPONSE_WAIT_DOWNLOAD)
						|| p.getPeerStatus().equals(PeerStatus.RESPONSE_WAIT_UPLOAD)
						|| p.getPeerStatus().equals(PeerStatus.PONG_WAIT_DOWNLOAD)
						|| p.getPeerStatus().equals(PeerStatus.PONG_WAIT_UPLOAD))
				.forEach(p -> p.setPeerStatus(PeerStatus.NOT_CONTACTED));
		List<PeerInformation> notConnectedPeerList = peerListActual.stream()
				.filter(p -> p.getPeerStatus().equals(PeerStatus.NOT_CONTACTED)).collect(Collectors.toList());
		Map<String, PongMessage> mapPongMessage = DummyObjectCreator.createDummyPongMessageMap(notConnectedPeerList,
				peerConnectorImpl);
		connectionManagerImpl.setPongMessageMap(mapPongMessage);

		peerListActual.add(new PeerInformation("192.168.0.114".getBytes(), 5721, clubNum));
		peerListActual.add(new PeerInformation("192.168.0.115".getBytes(), 5721, clubNum));
		connectionManagerImpl.setPeerList(peerListActual);
	}

	private long getConnectionCreatedNumber(List<PeerInformation> peerList, short clubNum) {
		return peerList.stream().filter(p -> p.getClubNumber() == clubNum)
				.filter(p -> p.getPeerStatus().equals(PeerStatus.RESPONSE_WAIT_UPLOAD)).count();
	}

	private short getUploadNum(List<PeerInformation> peerListActual, int clubNum) {
		return (short) peerListActual.stream()
				.filter(p -> p.getPeerStatus().equals(PeerStatus.UPLOAD_CONNECTION) && p.getClubNumber() == clubNum)
				.count();
	}

	@Test
	void testMaintainClubsConnection_checkDeleteNoActivePeer() throws IOException, InterruptedException {
		testMaintainClubsConnection_setup(0);
		List<PeerInformation> peerListExpected = DummyObjectCreator.createDummyPeers(0, 0, 6 * 3);

		connectionManagerImpl.maintainClubsConnection();

		Thread.sleep(300);
		assertEquals(peerListExpected.size(), connectionManagerImpl.getPeerList().size());
	}

	@Test
	void testMaintainClubsConnection_checkCleanPongMessageAfterFinishConnection() throws IOException {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(0, 0, 6);
		Map<String, PongMessage> mapPongMessage = DummyObjectCreator.createDummyPongMessageMap(peerList,
				peerConnectorImpl);
		connectionManagerImpl.setPeerList(peerList);
		connectionManagerImpl.setPongMessageMap(mapPongMessage);

		connectionManagerImpl.maintainClubsConnection();
		try {
			Thread.sleep(1100);
		} catch (Exception e) {
		}
		assertEquals(0, connectionManagerImpl.getPongMessageMap().size());
	}

	@Test
	void testProcessPacket_checkPongMessage() throws InvalidProtocolBufferException, IOException {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(2, 0, 6);
		PeerInformation peer = peerList.get(1);
		connectionManagerImpl.setPeerList(peerList);
		connectionManagerImpl.setPongMessageMap(new HashMap<>());
		PacketWrapper wrap = PacketWrapper.parseFrom(peerConnectorImpl
				.createPongMessage(peer, 1, 1, 50, PingMessage.newBuilder().setPingId(0).build()).getData());
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);

		connectionManagerImpl.processPacket(packetPair);

		assertEquals(1, connectionManagerImpl.getPongMessageMap().size());
		assertEquals(0, peer.getUnorderPacketNumber());
	}

	@Test
	void testProcessPacket_checkRequestMessageUpload() throws InvalidProtocolBufferException, IOException {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(0, 0, 6);
		PeerInformation peer = peerList.get(4);
		connectionManagerImpl.setPeerList(peerList);
		connectionManagerImpl.setPongMessageMap(new HashMap<>());
		PacketWrapper wrap = PacketWrapper
				.parseFrom(peerConnectorImpl.createRequestMessage(peer, ConnectionType.UPLOAD).getData());
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);

		connectionManagerImpl.processPacket(packetPair);

		assertEquals(PeerStatus.RESPONSE_WAIT_UPLOAD, peer.getPeerStatus());
		assertTrue(peer.getLastReceivedMessageTimeMilliseconds() > 1000);
		assertEquals(1, peer.getRequestMessageNumber());
		assertEquals(1, peer.getLastSentPacketNumber());
		assertEquals(0, peer.getUnorderPacketNumber());
	}

	@Test
	void testProcessPacket_checkRequestMessageDownload() throws InvalidProtocolBufferException, IOException {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(0, 0, 6);
		PeerInformation peer = peerList.get(3);
		connectionManagerImpl.setPeerList(peerList);
		connectionManagerImpl.setPongMessageMap(new HashMap<>());
		PacketWrapper wrap = PacketWrapper
				.parseFrom(peerConnectorImpl.createRequestMessage(peer, ConnectionType.DOWNLOAD).getData());
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);

		connectionManagerImpl.processPacket(packetPair);

		assertEquals(PeerStatus.UPLOAD_CONNECTION, peer.getPeerStatus());
	}

	@Test
	void testProcessPacket_checkResponseMessage() throws InvalidProtocolBufferException, IOException {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(0, 0, 6);
		PeerInformation peer = peerList.get(4);
		connectionManagerImpl.setPeerList(peerList);
		PacketWrapper wrap = PacketWrapper.parseFrom(peerConnectorImpl
				.createResponseMessage(peer,
						RequestMessage.newBuilder().setRequestId(0).setConnectionType(ConnectionType.UPLOAD).build())
				.getData());
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);

		connectionManagerImpl.processPacket(packetPair);

		assertEquals(PeerStatus.UPLOAD_CONNECTION, peer.getPeerStatus());
		assertTrue(peer.getLastReceivedMessageTimeMilliseconds() > 1000);
		assertEquals(1, peer.getLastSentPacketNumber());
		assertEquals(0, peer.getUnorderPacketNumber());
	}

	@Test
	void testProcessPacket_checkKeepAliveMessage() throws InvalidProtocolBufferException, IOException {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(0, 0, 6);
		PeerInformation peer = peerList.get(4);
		connectionManagerImpl.setPeerList(peerList);

		PacketWrapper wrap = PacketWrapper.parseFrom(peerConnectorImpl.createKeepAliveMessage(peer).getData());
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);

		connectionManagerImpl.processPacket(packetPair);

		assertTrue(peer.getLastReceivedMessageTimeMilliseconds() > 1000);
		assertEquals(0, peer.getLastReceivedPacketNumber());
		assertEquals(0, peer.getUnorderPacketNumber());
	}

	@Test
	void testProcessPacket_checkTerminatedMessage() throws InvalidProtocolBufferException, IOException {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(0, 0, 6);
		PeerInformation peer = peerList.get(4);
		connectionManagerImpl.setPeerList(peerList);

		PacketWrapper wrap = PacketWrapper.parseFrom(
				peerConnectorImpl.createTerminateConnectionMessage(peer, TerminatedReason.DEAD_PEER).getData());
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);

		connectionManagerImpl.processPacket(packetPair);

		assertFalse(connectionManagerImpl.getPeerList().contains(peer));
		assertEquals(0, peer.getUnorderPacketNumber());
	}

	@Test
	void testProcessPacket_checkPacketsForHigherLevel() throws InvalidProtocolBufferException, IOException {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(0, 0, 6);
		PeerInformation peer = peerList.get(4);
		connectionManagerImpl.setPeerList(peerList);

		PacketWrapper wrap = MessageWrapper.wrapMessage(VideoPacket.newBuilder().setVideoNum(1).build(), peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);

		connectionManagerImpl.setPacketsForHigherLevel(new ArrayBlockingQueue<>(30));
		connectionManagerImpl.processPacket(packetPair);

		assertEquals(1, connectionManagerImpl.getPacketsForHigherLevel().size());
		assertEquals(0, peer.getUnorderPacketNumber());
	}

	@Test
	void testProcessPacket_checkLastReciveMessageUpdate() {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(2, 2, 0);
		connectionManagerImpl.setPeerList(peerList);
		MessageWrapper.wrapMessage(KeepAliveMessage.newBuilder().build(), peerList.get(2));
		PacketWrapper packetWrap = MessageWrapper.wrapMessage(KeepAliveMessage.newBuilder().build(), peerList.get(2));
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(
				new String(peerList.get(2).getIpAddress()), packetWrap);

		connectionManagerImpl.processPacket(packetPair);

		assertTrue(peerList.get(2).getLastReceivedMessageTimeMilliseconds() > 10000);
		assertEquals(1, peerList.get(2).getLastReceivedPacketNumber());
	}

	@Test
	void testProcessPacket_checkCorrectnessOfPongMapUpdate() {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(2, 2, 0);
		connectionManagerImpl.setPeerList(peerList);
		PongMessage pong = PongMessage.newBuilder().setBufferVideoNum(5).build();
		PacketWrapper packetWrap = MessageWrapper.wrapMessage(pong, peerList.get(2));
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(
				new String(peerList.get(2).getIpAddress()), packetWrap);
		connectionManagerImpl.setPongMessageMap(new HashMap<>());

		connectionManagerImpl.processPacket(packetPair);

		assertEquals(pong, connectionManagerImpl.getPongMessageMap().get(new String(peerList.get(2).getIpAddress())));
	}

	@Test
	void testProcessPacket_checkRequestMessageProcess() {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(0, 0, 1);
		connectionManagerImpl.setPeerList(peerList);
		PeerInformation peer = peerList.get(3);
		RequestMessage request = RequestMessage.newBuilder().setConnectionType(ConnectionType.UPLOAD).build();
		PacketWrapper packetWrap = MessageWrapper.wrapMessage(request, peer);
		peer.setLastSentMessageTimeMilliseconds(0);
		peer.setLastSentPacketNumber(0);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()),
				packetWrap);

		connectionManagerImpl.processPacket(packetPair);

		assertEquals(0, peer.getRequestMessageNumber());
		assertEquals(PeerStatus.RESPONSE_WAIT_DOWNLOAD, peer.getPeerStatus());
	}

	@Test
	void testProcessPacket_checkResponseMessageProcess() {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(0, 0, 1);
		connectionManagerImpl.setPeerList(peerList);
		PeerInformation peer = peerList.get(3);
		peer.setPeerStatus(PeerStatus.RESPONSE_WAIT_UPLOAD);
		ResponseMessage request = ResponseMessage.newBuilder().setResponseRequestId(1).build();
		PacketWrapper packetWrap = MessageWrapper.wrapMessage(request, peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()),
				packetWrap);

		connectionManagerImpl.processPacket(packetPair);

		assertEquals(PeerStatus.UPLOAD_CONNECTION, peer.getPeerStatus());
	}

	@Test
	void testProcessPacket_checkTerminatedMessageProcess() {
		List<PeerInformation> peerListExpected = DummyObjectCreator.createDummyPeers(3, 3, 0);
		peerListExpected.remove(3);
		List<PeerInformation> peerListActual = DummyObjectCreator.createDummyPeers(3, 3, 0);
		connectionManagerImpl.setPeerList(peerListActual);
		PeerInformation peer = peerListActual.get(3);
		TerminatedMessage terminated = TerminatedMessage.newBuilder()
				.setTerminatedReason(TerminatedReason.BLOCK_TIMEOUT).build();
		PacketWrapper packetWrap = MessageWrapper.wrapMessage(terminated, peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()),
				packetWrap);

		connectionManagerImpl.processPacket(packetPair);

		assertEquals(peerListExpected, connectionManagerImpl.getPeerList());
	}

	@Test
	void testProcessPacket_checkPacketForDownload() {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(3, 3, 0);
		connectionManagerImpl.setPeerList(peerList);
		PeerInformation peer = peerList.get(3);
		VideoPacket video = VideoPacket.newBuilder().build();
		PacketWrapper packetWrap = MessageWrapper.wrapMessage(video, peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()),
				packetWrap);
		connectionManagerImpl.processPacket(packetPair);

		assertEquals(1, connectionManagerImpl.getPacketsForHigherLevel().size());
		assertEquals(packetPair, connectionManagerImpl.getWaitingPackets());
	}

}
