package com.kikkar.network.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.net.ntp.NTPUDPClient;

import com.kikkar.global.ClockSingleton;
import com.kikkar.network.ConnectionManager;
import com.kikkar.network.PeerConnector;
import com.kikkar.network.ServerConnector;
import com.kikkar.network.SpeedTest;
import com.kikkar.packet.ConnectionType;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.Pair;
import com.kikkar.packet.PongMessage;
import com.kikkar.packet.TerminatedReason;

import fr.bmartel.speedtest.SpeedTestSocket;

public class ConnectionManagerImpl implements ConnectionManager {
	private ServerConnector serverConnector;
	private PeerConnector peerConnector;
	private DatagramSocket socket;
	private Channel channel;
	private List<PeerInformation> peerList;
	private Map<String, PongMessage> pongMessageMap;
	private ClockSingleton clock;
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private BlockingQueue<Pair<String, PacketWrapper>> packetsForHigherLevel;

	private int NUMBER_OF_CLUB = 6;
	private long WAIT_SECOND = 1;
	private long WAIT_NEIGHBOUR_PACKETS_MILLISECOND = 1000;
	private short MAX_NUMBER_OF_UNORDER_PACKET = 15;

	@Override
	public void loadJson(String rawJson) {
		channel = serverConnector.loadJson(rawJson);
	}

	@Override
	public void start(DatagramPacket reciveDatagramPacket) throws Exception {
		Short token = initialServerConnection(new SpeedTestImpl(new SpeedTestSocket()));
		serverConnector.synchronizeTime(new NTPUDPClient(), "0.pool.ntp.org");
		clock = ClockSingleton.getInstance();
		peerList = getServersPeerInformations(token);
		setClubNum(peerList);
		serverConnector.schedulerAliveAndNTPMessage(10);

		new Thread(() -> {
			try {
				peerConnector.startRecivePacketLoop(socket, reciveDatagramPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();

		Pair<String, PacketWrapper> packetPair = null;
		while (true) {
			if (!hasNotContactedPeer()) {
				contactServerForMorePeers(new SpeedTestImpl(new SpeedTestSocket()), System.err);
			}
			congestionControl();
			keepAliveUploadConnection();
			maintainClubsConnection();

			packetPair = peerConnector.getPacketsWaitingForProcessing();

			if (packetPair == null) {
				System.err.println("null packet");
			} else if (!checkPackageNumberASCOrder(packetPair)) {
				continue;
			} else if (packetPair.getRight().hasPingMessage()) {
				PeerInformation peer = getPeer(packetPair.getLeft());
				peerConnector.sendPongMessage(peerList, peer, packetPair.getRight().getPingMessage(), socket);
			} else if (packetPair.getRight().hasPongMessage()) {
				pongMessageMap.put(packetPair.getLeft(), packetPair.getRight().getPongMessage());
			} else if (packetPair.getRight().hasRequestMessage()) {
				PeerInformation peer = getPeer(packetPair.getLeft());
				peerConnector.sendResponseMessage(peer, packetPair.getRight(), socket);
				peer.setLastSentMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds());
				updateLastTimeReciveMessage(packetPair);
			} else if (packetPair.getRight().hasResponseMessage()) {
				PeerInformation peer = getPeer(packetPair.getLeft());
				assumePeerAreConnected(peer);
				updateLastTimeReciveMessage(packetPair);
			} else if (packetPair.getRight().hasKeepAliveMessage()) {
				updateLastTimeReciveMessage(packetPair);
			} else if (packetPair.getRight().hasTerminatedMessage()) {
				removeConnection(packetPair);
			} else {
				// (dodaj vreme slanja ako odgovaram da imam)
				packetsForHigherLevel.add(packetPair); // hasControlMessage, hasHaveMessage, hasNotInterestedMessage,
														// hasRequestVideoMessage, hasResponseVideoMessage,
														// hasVideoPacket
				updateLastTimeReciveMessage(packetPair);
			}
		}
	}

	private Short initialServerConnection(SpeedTest speedTest) throws MalformedURLException, Exception {
		String baseConnectionURL = channel.getIpAddress() + "/connect/initial/" + channel.getChannelId();
		Map<String, String> parameters = serverConnector.createConnectionParamerets(speedTest);
		URL url = serverConnector.createURL(baseConnectionURL, parameters);

		return serverConnector.connectToServer(url);
	}

	private List<PeerInformation> getServersPeerInformations(Short token) throws IOException {
		String baseURL = channel.getIpAddress() + "/connect/list/" + channel.getChannelId();
		Map<String, String> parameters = new HashMap<>();
		parameters.put("token", token.toString());
		parameters.put("port", peerConnector.getThisPeer().getPortNumber().toString());

		URL url = serverConnector.createURL(baseURL, parameters);
		return serverConnector.getPeerInfoList(url);
	}

	private void setClubNum(List<PeerInformation> peers) {
		peerConnector.setThisPeer(peerList.get(peerList.size() - 1));
		peerList.remove(peerList.size() - 1);
	}

	private boolean hasNotContactedPeer() {
		return peerList.stream().anyMatch(p -> p.getPeerStatus().equals(PeerStatus.NOT_CONTACTED));
	}

	@Override
	public void contactServerForMorePeers(SpeedTest speedTest, OutputStream errorOutput) {
		Short token;
		try {
			token = initialServerConnection(speedTest);
			List<PeerInformation> newPeerList = getServersPeerInformations(token);
			setClubNum(newPeerList);
			for (PeerInformation p : newPeerList) {
				if (!peerList.contains(p))
					peerList.add(p);
			}
		} catch (Exception e) {
			try {
				errorOutput.write(e.getMessage().getBytes());
			} catch (IOException e1) {
			}
		}
	}

	@Override
	public void congestionControl() {
		List<PeerInformation> connectedPeers = getConnectedPeers(peerList);

		deleteSlowPeer(connectedPeers);
		deleteUnorderPacketNum(connectedPeers);
	}

	private List<PeerInformation> getConnectedPeers(List<PeerInformation> peerList) {
		return peerList.stream().filter(p -> p.getPeerStatus().equals(PeerStatus.DOWNLOAD_CONNECTION)
				|| p.getPeerStatus().equals(PeerStatus.UPLOAD_CONNECTION)).collect(Collectors.toList());
	}

	private void deleteSlowPeer(List<PeerInformation> connectedPeers) {
		List<PeerInformation> deadPeers = connectedPeers.stream()
				.filter(p -> p.getLastReceivedMessageTimeMilliseconds() + WAIT_NEIGHBOUR_PACKETS_MILLISECOND < clock
						.getcurrentTimeMilliseconds()
						|| p.getLastSentMessageTimeMilliseconds() + WAIT_NEIGHBOUR_PACKETS_MILLISECOND < clock
								.getcurrentTimeMilliseconds())
				.collect(Collectors.toList());

		terminateConnections(deadPeers, TerminatedReason.DEAD_PEER);
	}

	private void deleteUnorderPacketNum(List<PeerInformation> connectedPeers) {
		List<PeerInformation> unorderPeers = connectedPeers.stream()
				.filter(p -> p.getUnorderPacketNumber() > MAX_NUMBER_OF_UNORDER_PACKET).collect(Collectors.toList());

		terminateConnections(unorderPeers, TerminatedReason.PACKET_NUMBER_DISORDER);
	}

	@Override
	public void terminateConnections(List<PeerInformation> peerInformations, TerminatedReason terminatedReason) {
		List<PeerInformation> peers = getConnectedPeers(peerInformations);
		peers.stream().forEach(p -> {
			DatagramPacket packet;
			try {
				packet = peerConnector.createTerminateConnectionMessage(p, terminatedReason);
				peerConnector.send(packet, socket);
				p.setPeerStatus(PeerStatus.NOT_CONTACTED);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		});
	}

	@Override
	public void keepAliveUploadConnection() {
		List<PeerInformation> connectedPeers = getConnectedPeers(peerList);
		connectedPeers.stream().filter(p -> p.getLastSentMessageTimeMilliseconds()
				+ WAIT_NEIGHBOUR_PACKETS_MILLISECOND / 2 < clock.getcurrentTimeMilliseconds()).forEach(p -> {
					peerConnector.sendKeepAliveMessage(p, socket);
					p.setLastSentMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds());
				});
	}

	public void maintainClubsConnection() {
		short belongClubNum = peerConnector.getThisPeer().getClubNumber();
		int downloadConnectionNum;
		int uploadConnectionNum;
		for (int i = 0; i < NUMBER_OF_CLUB; i++) {
			downloadConnectionNum = peerDownloadConnectionNum(i);
			uploadConnectionNum = peerUploadConnectionNum(i);
			if (i == belongClubNum) {
				maintainInnerClubConnection(downloadConnectionNum, ConnectionType.DOWNLOAD);
				maintainInnerClubConnection(uploadConnectionNum, ConnectionType.UPLOAD);
			} else {
				maintainOuterClubConnectedPeers(downloadConnectionNum, i);
			}
		}
	}

	private int peerDownloadConnectionNum(int clubNum) {
		return (int) peerList.stream().filter(p -> p.getClubNumber() == clubNum)
				.filter(p -> p.getPeerStatus().equals(PeerStatus.DOWNLOAD_CONNECTION)).count();
	}

	private int peerUploadConnectionNum(int clubNum) {
		return (int) peerList.stream().filter(p -> p.getClubNumber() == clubNum)
				.filter(p -> p.getPeerStatus().equals(PeerStatus.UPLOAD_CONNECTION)).count();
	}

	private void maintainInnerClubConnection(int connectionNum, ConnectionType connectionType) {
		short clubNum = peerConnector.getThisPeer().getClubNumber();
		if (connectionNum < 2) {
			exchangePingPongMessage((short) (2 - connectionNum), connectionType, clubNum);
		} else if (connectionNum > 3) {
			PeerStatus peerStatus = connectionType.equals(ConnectionType.DOWNLOAD) ? PeerStatus.DOWNLOAD_CONNECTION
					: PeerStatus.UPLOAD_CONNECTION;
			for (int i = 3; i < connectionNum; i++) {
				rejectOldestConnections(TerminatedReason.NEW_CONNECTION, peerStatus, clubNum);
			}
		}
	}

	private void maintainOuterClubConnectedPeers(int connectionNum, int clubNum) {
		if (connectionNum == 0) {
			exchangePingPongMessage((short) 1, ConnectionType.DOWNLOAD, clubNum);
		} else if (connectionNum > 1) {
			for (int i = 1; i < connectionNum; i++) {
				rejectOldestConnections(TerminatedReason.NEW_CONNECTION, PeerStatus.DOWNLOAD_CONNECTION, clubNum);
			}
		}
	}

	private void exchangePingPongMessage(short wantedConnections, ConnectionType connectionType, int clubNum) {
		peerConnector.sendPingMessages(peerList, connectionType, socket);

		if (connectionType.equals(ConnectionType.DOWNLOAD)) {
			executor.schedule(() -> {
				sendRequestMessage(wantedConnections, PeerStatus.PONG_WAIT_DOWNLOAD, connectionType, clubNum);
				deleteNotReplyPeers(clubNum);
				// pongMessageMap.clear();
			}, WAIT_SECOND, TimeUnit.SECONDS);
		} else {
			executor.schedule(() -> {
				sendRequestMessage(wantedConnections, PeerStatus.PONG_WAIT_UPLOAD, connectionType, clubNum);
				deleteNotReplyPeers(clubNum);
				// pongMessageMap.clear();
			}, WAIT_SECOND, TimeUnit.SECONDS);
		}
	}

	public void sendRequestMessage(short wantedConnections, PeerStatus peerStatus, ConnectionType connectedType,
			int clubNum) {
		List<PeerInformation> bestFitPeers = takePeersForConnection(wantedConnections, peerStatus, (short) clubNum);
		peerConnector.sendRequestMessage(bestFitPeers, socket, connectedType, System.err);
		bestFitPeers.stream().forEach(p -> p.setLastSentMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds()));
	}

	private List<PeerInformation> takePeersForConnection(short wantedConnections, PeerStatus peerStatus,
			short clubNum) {
		Map<String, PongMessage> pongMessage = getPeerIpAddressAndCorespondingPongMessage(peerStatus, clubNum);

		List<PongMessage> firstClassLimitPongMessage;
		if (peerStatus.equals(PeerStatus.PONG_WAIT_DOWNLOAD)) {
			firstClassLimitPongMessage = pongMessage.entrySet().stream().map(p -> p.getValue())
					.sorted(Comparator.comparing(PongMessage::getBufferVideoNum)
							.thenComparing(PongMessage::getDownloadLinkNum).reversed())
					.limit(wantedConnections).collect(Collectors.toList());
		} else {
			firstClassLimitPongMessage = pongMessage.entrySet().stream().map(p -> p.getValue())
					.sorted(Comparator.comparing(PongMessage::getBufferVideoNum)
							.thenComparing(PongMessage::getUploadLinkNum).reversed())
					.limit(wantedConnections).collect(Collectors.toList());
		}

		List<PeerInformation> wantedPeers = getCorrespondingPeers(firstClassLimitPongMessage);
		resetOtherPeerStatus(wantedPeers, peerStatus, clubNum);
		return wantedPeers;
	}

	private void deleteNotReplyPeers(int clubNum) {
		Set<String> replyIpAddress = pongMessageMap.keySet();
		peerList.removeIf(p -> p.getPeerStatus().equals(PeerStatus.NOT_CONTACTED) && p.getClubNumber() == clubNum
				&& !replyIpAddress.contains(new String(p.getIpAddress())));
	}

	private Map<String, PongMessage> getPeerIpAddressAndCorespondingPongMessage(PeerStatus peerStatus, short clubNum) {
		List<String> ipAddresses = peerList.stream().filter(p -> p.getClubNumber() == clubNum)
				.filter(p -> p.getPeerStatus().equals(peerStatus)).map(PeerInformation::getIpAddress).map(String::new)
				.collect(Collectors.toList());

		Map<String, PongMessage> appropriatePongMessage = pongMessageMap.entrySet().stream()
				.filter(e -> ipAddresses.contains(e.getKey()))
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

		return appropriatePongMessage;
	}

	private List<PeerInformation> getCorrespondingPeers(List<PongMessage> firstClassLimitPongMessage) {
		List<String> limitIpAddresses = pongMessageMap.entrySet().stream()
				.filter(entry -> firstClassLimitPongMessage.contains(entry.getValue())).map(p -> p.getKey())
				.collect(Collectors.toList());

		List<PeerInformation> wantedPeers = peerList.stream()
				.filter(p -> limitIpAddresses.contains(new String(p.getIpAddress()))).collect(Collectors.toList());

		return wantedPeers;
	}

	private void resetOtherPeerStatus(List<PeerInformation> wantedPeers, PeerStatus peerStatus, short clubNum) {
		peerList.stream().filter(p -> !wantedPeers.contains(p)).filter(p -> p.getPeerStatus().equals(peerStatus))
				.filter(p -> p.getClubNumber() == clubNum).forEach(p -> p.setPeerStatus(PeerStatus.NOT_CONTACTED));
	}

	private void rejectOldestConnections(TerminatedReason terminatedReason, PeerStatus peerStatus, int clubNum) {
		PeerInformation peerInformation = peerList.stream().filter(p -> p.getPeerStatus().equals(peerStatus))
				.filter(p -> p.getClubNumber() == clubNum)
				.sorted(Comparator.comparing(PeerInformation::getLastReceivedMessageTimeMilliseconds).reversed())
				.findFirst().get();
		List<PeerInformation> peer = new ArrayList<>();
		peer.add(peerInformation);

		terminateConnections(peer, TerminatedReason.NEW_CONNECTION);
	}

	public boolean checkPackageNumberASCOrder(Pair<String, PacketWrapper> packetPair) {
		PeerInformation peer = getPeer(packetPair.getLeft());
		int receivedPacketNumber = packetPair.getRight().getPacketId();
		if (peer.getLastReceivedPacketNumber() + 1 == receivedPacketNumber) {
			peer.setLastReceivedPacketNumber(receivedPacketNumber);
			peer.decrementUnorderPacketNumber();
			return true;
		} else if (peer.getLastReceivedPacketNumber() > receivedPacketNumber) {
			peer.setLastReceivedPacketNumber(receivedPacketNumber);
			peer.incrementUnorderPacketNumber();
			return true;
		} else {
			peer.incrementUnorderPacketNumber();
			return false;
		}
	}

	public void assumePeerAreConnected(PeerInformation peer) {
		peerList.stream().filter(p -> p.equals(peer)).forEach(p -> {
			if (p.getPeerStatus().equals(PeerStatus.RESPONSE_WAIT_DOWNLOAD)) {
				p.setPeerStatus(PeerStatus.DOWNLOAD_CONNECTION);
			} else {
				p.setPeerStatus(PeerStatus.UPLOAD_CONNECTION);
			}
		});
	}

	public void updateLastTimeReciveMessage(Pair<String, PacketWrapper> packetPair) {
		PeerInformation peer = getPeer(packetPair.getLeft());
		peer.setLastReceivedMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds());
	}

	private PeerInformation getPeer(String ipAddress) {
		PeerInformation peerInformation = peerList.stream().filter(p -> new String(p.getIpAddress()).equals(ipAddress))
				.findFirst().get();
		return peerInformation;
	}

	public void removeConnection(Pair<String, PacketWrapper> packetPair) {
		PeerInformation peerInformation = peerList.stream()
				.filter(p -> new String(p.getIpAddress()).equals(packetPair.getLeft())).findFirst().get();
		peerList.remove(peerInformation);
	}

	public void send(DatagramPacket packet, OutputStream outError) {
		try {
			peerConnector.send(packet, socket);
		} catch (IOException e) {
			try {
				outError.write(e.getMessage().getBytes());
			} catch (IOException e1) {
			}
		}
	}

	public void shutDownConnectionManager() {
		executor.shutdown();
	}

	public ServerConnector getServerConnector() {
		return serverConnector;
	}

	public void setServerConnector(ServerConnector serverConnector) {
		this.serverConnector = serverConnector;
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

	public List<PeerInformation> getPeerList() {
		return peerList;
	}

	public void setPeerList(List<PeerInformation> peerList) {
		this.peerList = peerList;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Map<String, PongMessage> getPongMessageMap() {
		return pongMessageMap;
	}

	public void setPongMessageMap(Map<String, PongMessage> pongMessageMap) {
		this.pongMessageMap = pongMessageMap;
	}

	public ClockSingleton getClock() {
		return clock;
	}

	public void setClock(ClockSingleton clock) {
		this.clock = clock;
	}

	public void setWAIT_SECOND(long wAIT_SECOND) {
		WAIT_SECOND = wAIT_SECOND;
	}

	public Pair<String, PacketWrapper> getWaitingPackets() throws InterruptedException {
		return packetsForHigherLevel.take();
	}

}
