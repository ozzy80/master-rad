package com.kikkar.network.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import com.google.protobuf.InvalidProtocolBufferException;
import com.kikkar.global.ClockSingleton;
import com.kikkar.network.ServerConnector;
import com.kikkar.network.SpeedTest;
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

class ConnectionManagerImplTest {

	private DatagramSocket socket = Mockito.mock(DatagramSocket.class);
	private PeerConnectorImpl peerConnectorImpl;
	private ConnectionManagerImpl connectionManagerImpl;
	private short clubNum = 0;
	private ClockSingleton clock = ClockSingleton.getInstance();

	@BeforeEach
	void setup() {
		peerConnectorImpl = new PeerConnectorImpl();
		connectionManagerImpl = new ConnectionManagerImpl();
		connectionManagerImpl.setClock(clock);
		connectionManagerImpl.setPeerConnector(peerConnectorImpl);
		connectionManagerImpl.setSocket(socket);
		Channel channel = new Channel();
		channel.setChannelId(1l);
		channel.setIpAddress("http://192.168.0.171:8080/Tracker");
		connectionManagerImpl.setChannel(channel);
		peerConnectorImpl.setThisPeer(new PeerInformation("192.168.0.1".getBytes(), 51234, clubNum));
	}

	@Test
	void testLoadJson_checkDefaultBehaviour() {
		Channel channelExpected = new Channel();
		channelExpected.setBitrate(1500l);
		channelExpected.setChannelId(1l);
		channelExpected.setChunkSize(1500);
		channelExpected.setDescription(null);
		channelExpected.setIpAddress("http://192.168.0.171:8080/Tracker");
		channelExpected.setName("BBC");
		connectionManagerImpl.setServerConnector(new ServerConnectorImpl());
		String json = "{\"channelId\":1,\"chunkSize\":1500,\"bitrate\":1500,\"name\":\"BBC\",\"description\":null,\"ipAddress\":\"http://192.168.0.171:8080/Tracker\"}";

		connectionManagerImpl.loadJson(json);

		assertEquals(channelExpected, connectionManagerImpl.getChannel());
	}

	@Test
	void testContactServerForMorePeers_checkDefaultBehaviour() throws IOException {
		int newPeerNum = 2;
		short token = 15;
		int numOfDownloadPeers = 3;
		int numOfUploadPeers = 0;
		int numOfOtherPeers = 0;
		ServerConnector serverConnector = stubServerConnector(numOfDownloadPeers, numOfUploadPeers + newPeerNum + 1,
				numOfOtherPeers);
		List<PeerInformation> oldPeerList = DummyObjectCreator.createDummyPeers(numOfDownloadPeers, numOfUploadPeers,
				numOfOtherPeers);
		List<PeerInformation> newPeerList = DummyObjectCreator.createDummyPeers(numOfDownloadPeers, numOfUploadPeers,
				numOfOtherPeers);
		connectionManagerImpl.setServerConnector(serverConnector);
		connectionManagerImpl.setPeerList(newPeerList);
		connectionManagerImpl.setToken(token);

		connectionManagerImpl.contactServerForMorePeers(System.err);

		assertEquals(oldPeerList.size() + newPeerNum, newPeerList.size());
	}

	private ServerConnector stubServerConnector(int numOfDownloadPeers, int numOfUploadPeers, int numOfOtherPeers) {
		return new ServerConnector() {
			@Override
			public void synchronizeTime(NTPUDPClient client, String ntpServer) throws UnknownHostException {
			}

			@Override
			public void setChannel(Channel channel) {
			}

			@Override
			public void sendStayAliveMessage(URL url) throws MalformedURLException, IOException {
			}

			@Override
			public void sendLeaveMessage(URL url) throws IOException {
			}

			@Override
			public void schedulerAliveAndNTPMessage(int repeatInterval) {
			}

			@Override
			public Channel loadJson(String Json) {
				return null;
			}

			@Override
			public List<PeerInformation> getPeerInfoList(URL url) throws IOException {
				return DummyObjectCreator.createDummyPeers(numOfDownloadPeers, numOfUploadPeers, numOfOtherPeers);
			}

			@Override
			public Channel getChannel() {
				return null;
			}

			@Override
			public URL createURL(String baseURL, Map<String, String> parameters) throws MalformedURLException {
				return null;
			}

			@Override
			public Map<String, String> createConnectionParamerets(SpeedTest speedTest) throws MalformedURLException {
				return null;
			}

			@Override
			public Short connectToServer(URL url) throws IOException {
				return 15;
			}
		};

	}

	@Test
	void testCongestionControl_checkNoCongestion() {
		List<PeerInformation> peerListExpected = DummyObjectCreator.createDummyPeers(2, 3, 4);
		List<PeerInformation> peerListActual = DummyObjectCreator.createDummyPeers(2, 3, 4);
		connectionManagerImpl.setPeerList(peerListActual);
		peerListActual.stream()
				.forEach(p -> p.setLastReceivedMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds() + 5000));

		connectionManagerImpl.congestionControl();

		assertEquals(peerListExpected, peerListActual);
	}

	@ParameterizedTest
	@EnumSource(value = PeerStatus.class, names = { "DOWNLOAD_CONNECTION", "UPLOAD_CONNECTION" })
	void testCongestionControl_checkWaitOverlongMessage(PeerStatus peerStatus) {
		List<PeerInformation> peerListExpected = DummyObjectCreator.createDummyPeers(2, 3, 4);
		List<PeerInformation> peerListActual = DummyObjectCreator.createDummyPeers(2, 3, 4);
		peerListActual.stream()
				.forEach(p -> p.setLastReceivedMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds() + 5000));

		PeerInformation p1 = new PeerInformation("192.168.0.54".getBytes(), 5721, clubNum);
		p1.setLastReceivedMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds() - 3000);
		p1.setPeerStatus(peerStatus);
		PeerInformation p2 = new PeerInformation("192.168.0.54".getBytes(), 5721, clubNum);
		p2.setLastReceivedMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds() - 1010);
		p2.setPeerStatus(peerStatus);
		peerListActual.add(p2);
		connectionManagerImpl.setPeerList(peerListActual);

		List<PeerInformation> connectedPeers = getUploadDownloadPeers(peerListActual);
		connectedPeers.get(4).setLastReceivedMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds() - 700);
		connectionManagerImpl.congestionControl();

		assertEquals(getUploadDownloadPeers(peerListExpected).size(), getUploadDownloadPeers(peerListActual).size());
	}

	private List<PeerInformation> getUploadDownloadPeers(List<PeerInformation> peers) {
		return peers.stream().filter(p -> p.getPeerStatus().equals(PeerStatus.DOWNLOAD_CONNECTION)
				|| p.getPeerStatus().equals(PeerStatus.UPLOAD_CONNECTION)).collect(Collectors.toList());
	}

	@ParameterizedTest
	@EnumSource(value = PeerStatus.class, names = { "DOWNLOAD_CONNECTION", "UPLOAD_CONNECTION" })
	void testCongestionControl_checkUnorderMessage(PeerStatus peerStatus) {
		List<PeerInformation> peerListExpected = DummyObjectCreator.createDummyPeers(2, 3, 4);
		List<PeerInformation> peerListActual = DummyObjectCreator.createDummyPeers(2, 3, 4);
		long currentTime = clock.getcurrentTimeMilliseconds() + 500;
		peerListActual.stream().forEach(p -> p.setLastReceivedMessageTimeMilliseconds(currentTime));
		peerListExpected.stream().forEach(p -> p.setLastReceivedMessageTimeMilliseconds(currentTime));

		PeerInformation p1 = new PeerInformation("192.168.0.54".getBytes(), 5721, clubNum);
		p1.setUnorderPacketNumber((short) 16);
		p1.setLastReceivedMessageTimeMilliseconds(currentTime);
		p1.setPeerStatus(peerStatus);
		peerListActual.add(p1);
		connectionManagerImpl.setPeerList(peerListActual);

		peerListActual.get(0).setUnorderPacketNumber((short) 2);
		peerListExpected.get(0).setUnorderPacketNumber((short) 2);
		peerListActual.get(3).setUnorderPacketNumber((short) 4);
		peerListExpected.get(3).setUnorderPacketNumber((short) 4);
		connectionManagerImpl.congestionControl();

		assertEquals(getUploadDownloadPeers(peerListExpected), getUploadDownloadPeers(peerListActual));
	}

	@ParameterizedTest
	@EnumSource(value = TerminatedReason.class, names = { "LEAVE_PROGRAM", "BLOCK_TIMEOUT", "PACKET_NUMBER_DISORDER",
			"DEAD_PEER", "NEW_CONNECTION" })
	void testTerminateConnections__checkDefaultBehaviour(TerminatedReason terminatedReason) {
		int numberOfNotConnectedExpected = 4 + 2 + 3;
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(2, 3, 4);

		connectionManagerImpl.terminateConnections(peerList, terminatedReason);

		assertEquals(numberOfNotConnectedExpected,
				peerList.stream().filter(p -> p.getPeerStatus().equals(PeerStatus.NOT_CONTACTED)).count());
	}

	@Test
	void testKeepAliveUploadConnection_checkKeepAliveMessageZeroSendTime() {
		List<PeerInformation> peerListActual = DummyObjectCreator.createDummyPeers(2, 3, 4);
		connectionManagerImpl.setPeerList(peerListActual);

		connectionManagerImpl.keepAliveUploadConnection();

		long numberOfZeroTime = getUploadDownloadPeers(peerListActual).stream()
				.filter(p -> p.getLastSentMessageTimeMilliseconds() == 0).count();
		assertEquals(0, numberOfZeroTime);
	}

	@Test
	void testKeepAliveUploadConnection_checkKeepAliveMessageSendTime() {
		List<PeerInformation> peerListActual = DummyObjectCreator.createDummyPeers(2, 3, 4);
		connectionManagerImpl.setPeerList(peerListActual);
		long goToFuture = clock.getcurrentTimeMilliseconds() + 5000;
		peerListActual.get(0).setLastSentMessageTimeMilliseconds(goToFuture);
		peerListActual.get(3).setLastSentMessageTimeMilliseconds(goToFuture);

		connectionManagerImpl.keepAliveUploadConnection();

		long numberOfChanges = getUploadDownloadPeers(peerListActual).stream().filter(
				p -> p.getLastSentMessageTimeMilliseconds() < goToFuture && p.getLastSentMessageTimeMilliseconds() != 0)
				.count();
		assertEquals(3, numberOfChanges);
	}

	private void testSendRequestMessage_setup(PeerStatus peerStatus) throws IOException {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(0, 0, 6 * 6);
		connectionManagerImpl.setPeerList(peerList);
		List<PeerInformation> pongPeerList = peerList.stream().filter(p -> p.getPeerStatus().equals(peerStatus))
				.collect(Collectors.toList());
		Map<String, PongMessage> pongMessageMap = DummyObjectCreator.createDummyPongMessageMap(pongPeerList,
				peerConnectorImpl);
		connectionManagerImpl.setPongMessageMap(pongMessageMap);
	}

	@ParameterizedTest
	@MethodSource("createDifferentRequestParameters")
	void testSendRequestMessage_checkConnectionRequest(PeerStatus peerStatus, PeerStatus returnStatus,
			short wantedConnections, short clubNum) throws IOException {
		testSendRequestMessage_setup(peerStatus);
		List<PeerInformation> peerList = connectionManagerImpl.getPeerList();
		ConnectionType connectionType = peerStatus.equals(PeerStatus.PONG_WAIT_DOWNLOAD) ? ConnectionType.DOWNLOAD
				: ConnectionType.UPLOAD;

		connectionManagerImpl.sendRequestMessage(wantedConnections, peerStatus, connectionType, clubNum);
		List<PeerInformation> addPeers = peerList.stream()
				.filter(p -> p.getPeerStatus().equals(returnStatus) && p.getLastSentMessageTimeMilliseconds() != 0)
				.collect(Collectors.toList());

		assertEquals(6 + 6 - wantedConnections,
				peerList.stream()
						.filter(p -> p.getPeerStatus().equals(PeerStatus.NOT_CONTACTED) && p.getClubNumber() == clubNum)
						.count());
		assertEquals(0, peerList.stream()
				.filter(p -> p.getPeerStatus().equals(peerStatus) && p.getClubNumber() == clubNum).count());
		assertEquals(wantedConnections, addPeers.size());
		if (wantedConnections == 1) {
			if (peerStatus.equals(PeerStatus.PONG_WAIT_UPLOAD)) {
				assertEquals("192.168.0.120", new String(addPeers.get(0).getIpAddress()));
			} else {
				assertEquals("192.168.0.84", new String(addPeers.get(0).getIpAddress()));
			}
		} else if (wantedConnections == 2) {
			if (peerStatus.equals(PeerStatus.PONG_WAIT_UPLOAD)) {
				assertEquals("192.168.0.120", new String(addPeers.get(0).getIpAddress()));
				assertEquals("192.168.0.138", new String(addPeers.get(1).getIpAddress()));
			} else {
				assertEquals("192.168.0.78", new String(addPeers.get(0).getIpAddress()));
				assertEquals("192.168.0.84", new String(addPeers.get(1).getIpAddress()));
			}
		}
	}

	private static Stream<Arguments> createDifferentRequestParameters() {
		return Stream.of(
				Arguments.of(PeerStatus.PONG_WAIT_DOWNLOAD, PeerStatus.RESPONSE_WAIT_DOWNLOAD, (short) 0, (short) 0),
				Arguments.of(PeerStatus.PONG_WAIT_UPLOAD, PeerStatus.RESPONSE_WAIT_UPLOAD, (short) 0, (short) 0),
				Arguments.of(PeerStatus.PONG_WAIT_DOWNLOAD, PeerStatus.RESPONSE_WAIT_DOWNLOAD, (short) 1, (short) 0),
				Arguments.of(PeerStatus.PONG_WAIT_UPLOAD, PeerStatus.RESPONSE_WAIT_UPLOAD, (short) 1, (short) 0),
				Arguments.of(PeerStatus.PONG_WAIT_DOWNLOAD, PeerStatus.RESPONSE_WAIT_DOWNLOAD, (short) 2, (short) 0),
				Arguments.of(PeerStatus.PONG_WAIT_UPLOAD, PeerStatus.RESPONSE_WAIT_UPLOAD, (short) 2, (short) 0));
	}

	private void testMaintainClubsConnection_setup(int downloadConnNUm) throws IOException {
		connectionManagerImpl.setThreadWaitSecond(0);
		List<PeerInformation> peerListActual = DummyObjectCreator.createDummyPeers(0, downloadConnNUm, 6 * 3);
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

	@Test
	void testMaintainClubsConnection_checkDeleteNoActivePeer() throws IOException, InterruptedException {
		testMaintainClubsConnection_setup(0);
		List<PeerInformation> peerListExpected = DummyObjectCreator.createDummyPeers(0, 0, 6 * 3);
		List<PeerInformation> peerListActual = connectionManagerImpl.getPeerList();

		connectionManagerImpl.maintainClubsConnection();

		Thread.sleep(300);
		assertEquals(peerListExpected.size(), peerListActual.size());
	}

	@ParameterizedTest
	@MethodSource("createMaintainClubParameters")
	void testMaintainClubsConnection_checkNoConnection(short connInnerClubNum, short connOuterClubNum,
			int downloadConnNUm, short totalConnInClub, short totalConnOuterClub) throws IOException {
		testMaintainClubsConnection_setup(downloadConnNUm);
		List<PeerInformation> peerListExpected = DummyObjectCreator.createDummyPeers(0, downloadConnNUm, 6 * 3);
		List<PeerInformation> peerListActual = connectionManagerImpl.getPeerList();

		connectionManagerImpl.maintainClubsConnection();
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		for (int i = 0; i < 6; i++) {
			long downloadClubConnetion = getConnectionCreatedNumber(peerListActual, (short) i,
					PeerStatus.RESPONSE_WAIT_DOWNLOAD);
			if (i == clubNum) {
				assertEquals(connInnerClubNum, downloadClubConnetion);
				assertEquals(totalConnInClub, getDonwloadNum(peerListActual, i));
			} else {
				assertEquals(connOuterClubNum, downloadClubConnetion);
				assertEquals(totalConnOuterClub, getDonwloadNum(peerListActual, i));
			}
		}
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
			Thread.sleep(3000);
		} catch (Exception e) {
		}
		assertEquals(0, connectionManagerImpl.getPongMessageMap().size());
	}

	private short getDonwloadNum(List<PeerInformation> peerListActual, int clubNum) {
		return (short) peerListActual.stream()
				.filter(p -> p.getPeerStatus().equals(PeerStatus.DOWNLOAD_CONNECTION) && p.getClubNumber() == clubNum)
				.count();
	}

	private long getConnectionCreatedNumber(List<PeerInformation> peerList, short clubNum, PeerStatus peerStatus) {
		return peerList.stream().filter(p -> p.getClubNumber() == clubNum)
				.filter(p -> p.getPeerStatus().equals(peerStatus)).count();
	}

	private static Stream<Arguments> createMaintainClubParameters() {
		return Stream.of(Arguments.of((short) 2, (short) 1, 0, (short) 0, (short) 0),
				Arguments.of((short) 1, (short) 0, 6, (short) 1, (short) 1),
				Arguments.of((short) 0, (short) 0, 6 * 2, (short) 2, (short) 1),
				Arguments.of((short) 0, (short) 0, 6 * 4, (short) 3, (short) 1));
	}

	@ParameterizedTest
	@ValueSource(strings = { "15", "10", "8", "16" })
	void testCheckPackageNumberASCOrder_testUnorderPack(int fromValue) {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(3, 3, 6);
		connectionManagerImpl.setPeerList(peerList);
		PeerInformation peer = peerList.get(0);
		KeepAliveMessage alive = KeepAliveMessage.newBuilder().setMessageId(0).build();

		PacketWrapper packet = MessageWrapper.wrapMessage(alive, peer);
		for (int i = fromValue; i > 8; i--) {
			peer.setLastSentPacketNumber(i);
			packet = MessageWrapper.wrapMessage(alive, peer);
			connectionManagerImpl.checkPackageNumberASCOrder(
					new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), packet));
		}

		assertEquals(fromValue - 8, peer.getUnorderPacketNumber());
	}

	@Test
	void testAssumePeerAreConnected_checkChangePeerStatus() {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(0, 0, 1);
		connectionManagerImpl.setPeerList(peerList);

		connectionManagerImpl.assumePeerAreConnected(peerList.get(3));
		connectionManagerImpl.assumePeerAreConnected(peerList.get(4));

		assertEquals(peerList.get(4).getPeerStatus(), PeerStatus.UPLOAD_CONNECTION);
		assertEquals(peerList.get(3).getPeerStatus(), PeerStatus.DOWNLOAD_CONNECTION);
	}

	@Test
	void testUpdateLastTimeReciveMessage_checkTimeChange() {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(3, 3, 6);
		connectionManagerImpl.setPeerList(peerList);
		PeerInformation peer = peerList.get(0);
		KeepAliveMessage alive = KeepAliveMessage.newBuilder().setMessageId(0).build();
		PacketWrapper packet = MessageWrapper.wrapMessage(alive, peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()),
				packet);

		connectionManagerImpl.updateLastTimeReciveMessage(packetPair);

		assertTrue(peer.getLastReceivedMessageTimeMilliseconds() > 1000);
	}

	@Test
	void testRemoveConnection_checkRemove() {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(3, 3, 6);
		connectionManagerImpl.setPeerList(peerList);
		PeerInformation peer = peerList.get(0);
		KeepAliveMessage alive = KeepAliveMessage.newBuilder().setMessageId(0).build();
		PacketWrapper packet = MessageWrapper.wrapMessage(alive, peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()),
				packet);

		connectionManagerImpl.removeConnection(packetPair);

		assertFalse(peerList.contains(peer));
	}

	@Test
	void testSendAll_checkIncrementMessageNumber() {
		int downloadNum = 3;
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(2, downloadNum, 6);
		connectionManagerImpl.setPeerList(peerList);
		PacketWrapper.Builder wrap = PacketWrapper.newBuilder();

		connectionManagerImpl.sendAll(wrap, new ArrayList<>(), PeerStatus.DOWNLOAD_CONNECTION);

		assertEquals(downloadNum, peerList.stream().filter(p -> p.getLastSentMessageTimeMilliseconds() > 1000).count());
		assertEquals(downloadNum, peerList.stream().filter(p -> p.getLastSentPacketNumber() != 0).count());
	}

	@Test
	void testSendAll_checkSendUninterestedPeer() {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(5, 5, 6);
		connectionManagerImpl.setPeerList(peerList);
		PacketWrapper.Builder wrap = PacketWrapper.newBuilder();

		connectionManagerImpl.sendAll(wrap, peerList.subList(0, 2).stream().map(PeerInformation::getIpAddress)
				.map(String::new).collect(Collectors.toList()), PeerStatus.DOWNLOAD_CONNECTION);

		assertEquals(3, peerList.stream().filter(p -> p.getLastSentMessageTimeMilliseconds() > 1000).count());
		assertEquals(3, peerList.stream().filter(p -> p.getLastSentPacketNumber() != 0).count());
	}

	@Test
	void testSendOne_checkIncrementMessageNumber() {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(3, 3, 6);
		connectionManagerImpl.setPeerList(peerList);
		PacketWrapper.Builder wrap = PacketWrapper.newBuilder();

		connectionManagerImpl.sendOne(wrap, new String(peerList.get(0).getIpAddress()));

		assertTrue(peerList.get(0).getLastSentMessageTimeMilliseconds() > 1000);
		assertTrue(peerList.get(0).getLastSentPacketNumber() == 1);
	}

	@Test
	void testProcessPacket_checkPongMessage() throws InvalidProtocolBufferException, IOException {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(2, 3, 6);
		PeerInformation peer = peerList.get(4);
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
	void testProcessPacket_checkRequestMessage() throws InvalidProtocolBufferException, IOException {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(0, 0, 6);
		PeerInformation peer = peerList.get(4);
		connectionManagerImpl.setPeerList(peerList);
		connectionManagerImpl.setPongMessageMap(new HashMap<>());
		PacketWrapper wrap = PacketWrapper
				.parseFrom(peerConnectorImpl.createRequestMessage(peer, ConnectionType.DOWNLOAD).getData());
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);

		connectionManagerImpl.processPacket(packetPair);

		assertEquals(PeerStatus.DOWNLOAD_CONNECTION, peer.getPeerStatus());
		assertTrue(peer.getLastReceivedMessageTimeMilliseconds() > 1000);
		assertEquals(1, peer.getRequestMessageNumber());
		assertTrue(peer.getLastSentMessageTimeMilliseconds() > 1000);
		assertEquals(2, peer.getLastSentPacketNumber());
		assertEquals(0, peer.getUnorderPacketNumber());
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

		assertFalse(peerList.contains(peer));
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

	@ParameterizedTest
	@EnumSource(value = ConnectionType.class, names = { "UPLOAD", "DOWNLOAD" })
	void testProcessPacket_checkRequestMessageProcess(ConnectionType connectionType) {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(0, 0, 1);
		connectionManagerImpl.setPeerList(peerList);
		PeerInformation peer = peerList.get(3);
		RequestMessage request = RequestMessage.newBuilder().setConnectionType(connectionType).build();
		PacketWrapper packetWrap = MessageWrapper.wrapMessage(request, peer);
		peer.setLastSentMessageTimeMilliseconds(0);
		peer.setLastSentPacketNumber(0);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()),
				packetWrap);

		connectionManagerImpl.processPacket(packetPair);

		assertTrue(peer.getLastSentMessageTimeMilliseconds() > 1000);
		assertEquals(1, peer.getLastSentPacketNumber());
		assertEquals(0, peer.getRequestMessageNumber());

		if (connectionType.equals(ConnectionType.DOWNLOAD)) {
			assertEquals(PeerStatus.DOWNLOAD_CONNECTION, peer.getPeerStatus());
		} else {
			assertEquals(PeerStatus.UPLOAD_CONNECTION, peer.getPeerStatus());
		}
	}

	@ParameterizedTest
	@EnumSource(value = PeerStatus.class, names = { "RESPONSE_WAIT_DOWNLOAD", "RESPONSE_WAIT_UPLOAD" })
	void testProcessPacket_checkResponseMessageProcess(PeerStatus peerStatus) {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(0, 0, 1);
		connectionManagerImpl.setPeerList(peerList);
		PeerInformation peer = peerList.get(3);
		peer.setPeerStatus(peerStatus);
		ResponseMessage request = ResponseMessage.newBuilder().setResponseRequestId(1).build();
		PacketWrapper packetWrap = MessageWrapper.wrapMessage(request, peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()),
				packetWrap);

		connectionManagerImpl.processPacket(packetPair);

		if (peerStatus.equals(PeerStatus.RESPONSE_WAIT_DOWNLOAD)) {
			assertEquals(PeerStatus.DOWNLOAD_CONNECTION, peer.getPeerStatus());
		} else {
			assertEquals(PeerStatus.UPLOAD_CONNECTION, peer.getPeerStatus());
		}
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

		assertEquals(peerListExpected, peerListActual);
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
