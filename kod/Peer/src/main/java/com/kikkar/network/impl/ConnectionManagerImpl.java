package com.kikkar.network.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.net.ntp.NTPUDPClient;

import com.kikkar.global.ClockSingleton;
import com.kikkar.global.Constants;
import com.kikkar.network.ConnectionManager;
import com.kikkar.network.PeerConnector;
import com.kikkar.network.ServerConnector;
import com.kikkar.network.SpeedTest;
import com.kikkar.packet.ConnectionType;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.Pair;
import com.kikkar.packet.PingMessage;
import com.kikkar.packet.PongMessage;
import com.kikkar.packet.TerminatedReason;

import fr.bmartel.speedtest.SpeedTestSocket;

public class ConnectionManagerImpl implements ConnectionManager {
	private int dataWaitSecond;
	private ServerConnector serverConnector;
	private PeerConnector peerConnector;
	private DatagramSocket socket;
	private Channel channel;
	private List<PeerInformation> peerList;
	private Map<String, PongMessage> pongMessageMap;
	private ClockSingleton clock;
	private ScheduledExecutorService executor;
	private BlockingQueue<Pair<String, PacketWrapper>> packetsForHigherLevel;
	private Short token;

	public ConnectionManagerImpl() {
		peerList = new ArrayList<>();
		dataWaitSecond = Constants.DATA_WAIT_SECOND;
		serverConnector = new ServerConnectorImpl();
		peerConnector = new PeerConnectorImpl();
		clock = ClockSingleton.getInstance();
		packetsForHigherLevel = new ArrayBlockingQueue<>(Constants.MAX_NUMBER_OF_WAIT_PACKET);
		try {
			socket = new DatagramSocket();
			PeerInformation thisPeer = new PeerInformation(Constants.getLocalIp().getBytes(), socket.getLocalPort(),
					(short) 0);
			peerConnector.setThisPeer(thisPeer);
			pongMessageMap = new HashMap<>();
			executor = Executors.newSingleThreadScheduledExecutor();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadJson(String rawJson) {
		channel = serverConnector.loadJson(rawJson);
	}

	@Override
	public void start() throws Exception {
		serverConnect();
		
		new Thread(() -> {
			try {
				peerConnector.startRecivePacketLoop(socket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();

		Pair<String, PacketWrapper> packetPair = null;
		while (true) {
			// Zakomentarisano jer pri testiranju ima jako malo parnjaka pa preterano opterecuje server
			/*if (!hasNotContactedPeer()) {
				contactServerForMorePeers();
			}*/
			congestionControl();
			keepAliveUploadConnection();
			maintainClubsConnection();

			packetPair = peerConnector.getPacketsWaitingForProcessing();

			System.out.println("Paket od " + packetPair.getLeft());
			System.out.println(packetPair.getRight());
			System.out.println("Download kon: " + peerList.stream().filter(p -> p.getPeerStatus().equals(PeerStatus.DOWNLOAD_CONNECTION)).count());
			System.out.println("Upload kon: " + peerList.stream().filter(p -> p.getPeerStatus().equals(PeerStatus.UPLOAD_CONNECTION)).count());

			if (packetPair == null) {
				System.err.println("null packet");
			} else if (!checkPackageNumberASCOrder(packetPair)) {
				continue;
			} else {
				processPacket(packetPair);
			}
		}
	}
	
	private void serverConnect() throws MalformedURLException, Exception {
		token = initialServerConnection(new SpeedTestImpl(new SpeedTestSocket()));
		serverConnector.synchronizeTime(new NTPUDPClient(), "0.pool.ntp.org");
		clock = ClockSingleton.getInstance();
		peerList = getServersPeerInformations(token);
		setClubNum(peerList);
		serverConnector.schedulerAliveAndNTPMessage(900);		
	}

	public void processPacket(Pair<String, PacketWrapper> packetPair) {
		updateLastTimeReciveMessage(packetPair);
		updateLastReceiveMessageNum(packetPair);

		if (packetPair.getRight().hasPingMessage()) {
			PeerInformation peer = processPingMessage(packetPair);
			peerConnector.sendPongMessage(peerList, peer, packetPair.getRight().getPingMessage(), socket);
		} else if (packetPair.getRight().hasPongMessage()) {
			pongMessageMap.put(packetPair.getLeft(), packetPair.getRight().getPongMessage());
		} else if (packetPair.getRight().hasRequestMessage()) {
			PeerInformation peer = getPeer(packetPair.getLeft());
			if (peer != null) {
				peerConnector.sendResponseMessage(peer, packetPair.getRight(), socket);
				updateLastTimeSentMessage(peer);
			}
		} else if (packetPair.getRight().hasResponseMessage()) {
			PeerInformation peer = getPeer(packetPair.getLeft());
			if (peer != null) {
				assumePeerAreConnected(peer);
			}
		} else if (packetPair.getRight().hasTerminatedMessage()) {
			removeConnection(packetPair);
		} else if (packetPair.getRight().hasKeepAliveMessage()) {
			// All is done upper with updateLastTimeReciveMessage()
		} else {
			packetsForHigherLevel.add(packetPair);
		}
	}

	protected PeerInformation processPingMessage(Pair<String, PacketWrapper> packetPair) {
		PingMessage ping = packetPair.getRight().getPingMessage();
		PeerInformation peer = getPeer(packetPair.getLeft());
		if (peer == null) {
			peer = new PeerInformation(packetPair.getLeft().getBytes(), ping.getPortNumber(),
					(short) ping.getClubNumber());
			List<PeerInformation> modifiable = new ArrayList<>(peerList);
			modifiable.add(peer);
			peerList = Collections.unmodifiableList(modifiable);
		} else {
			peer.setPortNumber(ping.getPortNumber());
			peer.setClubNumber(peer.getClubNumber());
		}
		return peer;
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
		List<PeerInformation> modifiable = new ArrayList<>(peerList);
		modifiable.remove(peerList.size() - 1);
		peerList = Collections.unmodifiableList(modifiable);
	}

	private boolean hasNotContactedPeer() {
		return peerList.stream().anyMatch(p -> p.getPeerStatus().equals(PeerStatus.NOT_CONTACTED));
	}

	@Override
	public void contactServerForMorePeers() {
		try {
			List<PeerInformation> newPeerList = getServersPeerInformations(token);
			newPeerList.remove(newPeerList.size() - 1);
			boolean notExist = true;
			for (PeerInformation p1 : newPeerList) {
				for (PeerInformation p2 : peerList) {
					if (Arrays.equals(p1.getIpAddress(), p2.getIpAddress())) {
						notExist = false;
						p2.setClubNumber(p1.getClubNumber());
						p2.setPortNumber(p1.getPortNumber());
						break;
					}
				}
				if (notExist) {
					List<PeerInformation> modifiable = new ArrayList<>(peerList);
					modifiable.add(p1);
					peerList = Collections.unmodifiableList(modifiable);
				}
				notExist = true;
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
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
				.filter(p -> p.getLastReceivedMessageTimeMilliseconds()
						+ Constants.WAIT_NEIGHBOUR_PACKETS_MILLISECOND < clock.getcurrentTimeMilliseconds())
				.collect(Collectors.toList());

		terminateConnections(deadPeers, TerminatedReason.DEAD_PEER);
	}

	private void deleteUnorderPacketNum(List<PeerInformation> connectedPeers) {
		List<PeerInformation> unorderPeers = connectedPeers.stream()
				.filter(p -> p.getUnorderPacketNumber() > Constants.MAX_NUMBER_OF_UNORDER_PACKET)
				.collect(Collectors.toList());

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
		connectedPeers.stream()
				.filter(p -> p.getLastSentMessageTimeMilliseconds()
						+ Constants.WAIT_NEIGHBOUR_PACKETS_MILLISECOND / 2 < clock.getcurrentTimeMilliseconds())
				.forEach(p -> {
					peerConnector.sendKeepAliveMessage(p, socket);
					updateLastTimeSentMessage(p);
				});
	}

	public void maintainClubsConnection() {
		short belongClubNum = peerConnector.getThisPeer().getClubNumber();
		int downloadConnectionNum;
		int uploadConnectionNum;
		for (int i = 0; i < Constants.NUMBER_OF_CLUB; i++) {
			uploadConnectionNum = peerConnectionNum(i, PeerStatus.UPLOAD_CONNECTION);
			downloadConnectionNum = peerConnectionNum(i, PeerStatus.DOWNLOAD_CONNECTION);
			if (i == belongClubNum) {
				maintainInnerClubConnection(uploadConnectionNum, ConnectionType.UPLOAD, (short) i);
				maintainInnerClubConnection(downloadConnectionNum, ConnectionType.DOWNLOAD, (short) i);
			} else {
				maintainOuterClubConnectedPeers(downloadConnectionNum, i);
			}
			executor.schedule(() -> deleteOldPongMessages(pongMessageMap.keySet()), dataWaitSecond + 1,
					TimeUnit.SECONDS);
		}
	}

	protected int peerConnectionNum(int clubNum, PeerStatus peerStatus) {
		return (int) peerList.stream().filter(p -> p.getClubNumber() == clubNum)
				.filter(p -> p.getPeerStatus().equals(peerStatus)).count();
	}

	protected void maintainInnerClubConnection(int connectionNum, ConnectionType connectionType, short clubNum) {
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

	protected void maintainOuterClubConnectedPeers(int connectionNum, int clubNum) {
		if (connectionNum == 0) {
			exchangePingPongMessage((short) 1, ConnectionType.DOWNLOAD, clubNum);
		} else if (connectionNum > 1) {
			for (int i = 1; i < connectionNum; i++) {
				rejectOldestConnections(TerminatedReason.NEW_CONNECTION, PeerStatus.DOWNLOAD_CONNECTION, clubNum);
			}
		}
	}

	private void exchangePingPongMessage(short wantedConnections, ConnectionType connectionType, int clubNum) {
		peerConnector.sendPingMessages(peerList, connectionType, socket, (short) clubNum);

		if (connectionType.equals(ConnectionType.DOWNLOAD)) {
			executor.schedule(() -> {
				sendRequestMessage(wantedConnections, PeerStatus.PONG_WAIT_DOWNLOAD, connectionType, clubNum);
				deleteNotReplyPeers(pongMessageMap.keySet(), clubNum);
			}, dataWaitSecond, TimeUnit.SECONDS);
		} else {
			executor.schedule(() -> {
				sendRequestMessage(wantedConnections, PeerStatus.PONG_WAIT_UPLOAD, connectionType, clubNum);
				deleteNotReplyPeers(pongMessageMap.keySet(), clubNum);
			}, dataWaitSecond, TimeUnit.SECONDS);
		}
	}

	public void sendRequestMessage(short wantedConnections, PeerStatus peerStatus, ConnectionType connectedType,
			int clubNum) {
		List<PeerInformation> bestFitPeers = takePeersForConnection(wantedConnections, peerStatus, (short) clubNum);
		peerConnector.sendRequestMessage(bestFitPeers, socket, connectedType);
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

	private void deleteNotReplyPeers(Set<String> replyIpAddress, int clubNum) {
		peerList.removeIf(p -> p.getClubNumber() == clubNum && p.getPeerStatus().equals(PeerStatus.NOT_CONTACTED)
				&& !replyIpAddress.contains(new String(p.getIpAddress())));
	}

	protected void deleteOldPongMessages(Set<String> replyIpAddress) {
		peerList.stream().filter(p -> replyIpAddress.contains(new String(p.getIpAddress())))
				.forEach(p -> pongMessageMap.remove(new String(p.getIpAddress())));
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
		Optional<PeerInformation> peerInformation = peerList.stream().filter(p -> p.getPeerStatus().equals(peerStatus))
				.filter(p -> p.getClubNumber() == clubNum)
				.sorted(Comparator.comparing(PeerInformation::getLastReceivedMessageTimeMilliseconds)).findFirst();

		if (peerInformation.isPresent()) {
			List<PeerInformation> peer = new ArrayList<>();
			peer.add(peerInformation.get());
			terminateConnections(peer, TerminatedReason.NEW_CONNECTION);
		}

	}

	public boolean checkPackageNumberASCOrder(Pair<String, PacketWrapper> packetPair) {
		PeerInformation peer = getPeer(packetPair.getLeft());
		if (peer != null) {
			int receivedPacketNumber = packetPair.getRight().getPacketId();
			if (peer.getLastReceivedPacketNumber() + 1 == receivedPacketNumber) {
				peer.decrementUnorderPacketNumber();
				return true;
			} else if (peer.getLastReceivedPacketNumber() < receivedPacketNumber) {
				peer.incrementUnorderPacketNumber();
				return true;
			} else {
				peer.incrementUnorderPacketNumber();
				return false;
			}
		}
		return true;
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
		if (peer != null) {
			peer.setLastReceivedMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds());
		}
	}

	public void updateLastReceiveMessageNum(Pair<String, PacketWrapper> packetPair) {
		PeerInformation peer = getPeer(packetPair.getLeft());
		if (peer != null) {
			peer.setLastReceivedPacketNumber(packetPair.getRight().getPacketId());
		}
	}

	protected void updateLastTimeSentMessage(PeerInformation peer) {
		peer.setLastSentMessageTimeMilliseconds(clock.getcurrentTimeMilliseconds());
	}

	protected PeerInformation getPeer(String ipAddress) {
		return peerList.stream().filter(p -> new String(p.getIpAddress()).equals(ipAddress)).findFirst().orElse(null);
	}

	public void removeConnection(Pair<String, PacketWrapper> packetPair) {
		Optional<PeerInformation> peerInformation = peerList.stream()
				.filter(p -> new String(p.getIpAddress()).equals(packetPair.getLeft())).findFirst();

		if (peerInformation.isPresent()) {
			ArrayList<PeerInformation> modified = new ArrayList<>(peerList);
			modified.remove(peerInformation.get());
			peerList = Collections.unmodifiableList(modified);
		}
	}

	public void send(DatagramPacket packet) {
		try {
			peerConnector.send(packet, socket);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void sendAll(PacketWrapper.Builder wrap, List<String> uninterestedPeerIp, PeerStatus peerStatus) {
		int currentClubNum = peerConnector.getThisPeer().getClubNumber();
		List<PeerInformation> connectedPeer = getConnectedPeerByClub(currentClubNum, peerStatus);
		Stream<PeerInformation> peerStream = connectedPeer.stream();
		if (uninterestedPeerIp.size() != 0) {
			peerStream = peerStream.filter(p -> !uninterestedPeerIp.contains(new String(p.getIpAddress())));
		}
		peerStream.forEach(p -> sendWrap(p, wrap));

		for (int i = 0; i < Constants.NUMBER_OF_CLUB; i++) {
			if (currentClubNum != i) {
				connectedPeer = getConnectedPeerByClub(i, peerStatus);
				peerStream = connectedPeer.stream();
				if (uninterestedPeerIp.size() != 0) {
					peerStream = peerStream.filter(p -> !uninterestedPeerIp.contains(new String(p.getIpAddress())));
				}
				peerStream.forEach(p -> sendWrap(p, wrap));
			}
		}
	}

	@Override
	public void sendToClub(PacketWrapper.Builder wrap, PeerStatus peerStatus, int clubNum) {
		if (clubNum == -1) {
			clubNum = peerConnector.getThisPeer().getClubNumber();
		}
		List<PeerInformation> connectedPeer = getConnectedPeerByClub(clubNum, peerStatus);

		if (connectedPeer.size() == 0) {
			Optional<PeerInformation> peer = peerList.stream().filter(p -> p.getPeerStatus().equals(peerStatus))
					.findFirst();

			if (peer.isPresent()) {
				connectedPeer.add(peer.get());
			}
		}

		connectedPeer.stream().forEach(p -> sendWrap(p, wrap));
	}

	protected List<PeerInformation> getConnectedPeerByClub(int clubNum, PeerStatus peerStatus) {
		return peerList.stream().filter(p -> p.getPeerStatus().equals(peerStatus))
				.filter(p -> p.getClubNumber() == clubNum).collect(Collectors.toList());
	}

	@Override
	public void sendOne(PacketWrapper.Builder wrap, String IpAddress) {
		PeerInformation peer = getPeer(IpAddress);
		if (peer != null) {
			sendWrap(peer, wrap);
		}
	}

	private void sendWrap(PeerInformation peer, PacketWrapper.Builder wrap) {
		DatagramPacket packet;
		try {
			wrap.setPacketId(peer.getLastSentPacketNumber());
			peer.incrementLastSentPacketNumber();
			packet = MessageWrapper.createSendDatagramPacket(wrap.build(), peer);
			send(packet);
			updateLastTimeSentMessage(peer);
		} catch (IOException e) {
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

	@Override
	public Pair<String, PacketWrapper> getWaitingPackets() {
		try {
			return packetsForHigherLevel.take();
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	public BlockingQueue<Pair<String, PacketWrapper>> getPacketsForHigherLevel() {
		return packetsForHigherLevel;
	}

	public void setPacketsForHigherLevel(BlockingQueue<Pair<String, PacketWrapper>> packetsForHigherLevel) {
		this.packetsForHigherLevel = packetsForHigherLevel;
	}

	public void addPacketsForHigherLevel(Pair<String, PacketWrapper> packetsForHigherLevel) {
		this.packetsForHigherLevel.add(packetsForHigherLevel);
	}
	
	public Short getToken() {
		return token;
	}

	public void setToken(Short token) {
		this.token = token;
	}

	public void setDataWaitSecond(int dataWaitSecond) {
		this.dataWaitSecond = dataWaitSecond;
	}

}
