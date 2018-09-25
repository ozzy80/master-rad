package com.kikkar.network.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
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

import com.kikkar.global.ClockSingleton;
import com.kikkar.network.ServerConnector;
import com.kikkar.network.SpeedTest;
import com.kikkar.packet.ConnectionType;
import com.kikkar.packet.KeepAliveMessage;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.Pair;
import com.kikkar.packet.PongMessage;

import fr.bmartel.speedtest.SpeedTestSocket;

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
		ServerConnector serverConnector = stubServerConnector(3, 2, 0);
		List<PeerInformation> oldPeerList = DummyObjectCreator.createDummyPeers(3, 0, 0);
		List<PeerInformation> newPeerList = DummyObjectCreator.createDummyPeers(3, 0, 0);
		connectionManagerImpl.setServerConnector(serverConnector);
		connectionManagerImpl.setPeerList(newPeerList);

		connectionManagerImpl.contactServerForMorePeers(new SpeedTestImpl(new SpeedTestSocket()), System.err);

		assertEquals(oldPeerList.size() + 2, newPeerList.size());
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
		peerListActual.stream().forEach(p -> {
			p.setLastReceivedMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds());
			p.setLastSentMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds());
		});

		connectionManagerImpl.congestionControl();

		assertEquals(peerListExpected, peerListActual);
	}

	@ParameterizedTest
	@EnumSource(value = PeerStatus.class, names = { "DOWNLOAD_CONNECTION", "UPLOAD_CONNECTION" })
	void testCongestionControl_checkWaitOverlongMessage(PeerStatus peerStatus) {
		List<PeerInformation> peerListExpected = DummyObjectCreator.createDummyPeers(2, 3, 4);
		List<PeerInformation> peerListActual = DummyObjectCreator.createDummyPeers(2, 3, 4);
		peerListActual.stream().forEach(p -> {
			p.setLastReceivedMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds());
			p.setLastSentMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds());
		});

		PeerInformation p1 = new PeerInformation("192.168.0.54".getBytes(), 5721, clubNum);
		p1.setLastReceivedMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds() - 3000);
		p1.setPeerStatus(peerStatus);
		PeerInformation p2 = new PeerInformation("192.168.0.54".getBytes(), 5721, clubNum);
		p2.setLastReceivedMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds());
		p2.setLastSentMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds() - 4200);
		p2.setPeerStatus(peerStatus);
		peerListActual.add(p2);
		connectionManagerImpl.setPeerList(peerListActual);

		List<PeerInformation> connectedPeers = getUploadDownloadPeers(peerListActual);
		connectedPeers.get(5).setLastReceivedMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds() - 700);
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
		peerListActual.stream().forEach(p -> {
			p.setLastReceivedMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds());
			p.setLastSentMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds());
		});
		peerListExpected.stream().forEach(p -> {
			p.setLastReceivedMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds());
			p.setLastSentMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds());
		});

		PeerInformation p1 = new PeerInformation("192.168.0.54".getBytes(), 5721, clubNum);
		p1.setUnorderPacketNumber((short) 6);
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
		connectionManagerImpl.setWAIT_SECOND(0);
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
}
