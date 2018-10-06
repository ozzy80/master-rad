package com.kikkar.network.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.kikkar.global.Constants;
import com.kikkar.global.SharingBufferSingleton;
import com.kikkar.network.PeerConnector;
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

public class PeerConnectorImpl implements PeerConnector {

	private BlockingQueue<Pair<String, PacketWrapper>> packetsWaitingForProcessing;
	private PeerInformation thisPeer;
	private SharingBufferSingleton sharingBufferSingleton;

	public PeerConnectorImpl() {
		packetsWaitingForProcessing = new ArrayBlockingQueue<>(Constants.MAX_NUMBER_OF_WAIT_PACKET);
		sharingBufferSingleton = SharingBufferSingleton.getInstance();
	}

	@Override
	public DatagramPacket createPingMessage(PeerInformation peerInformation, ConnectionType connectionType)
			throws IOException {
		PingMessage.Builder ping = PingMessage.newBuilder();
		ping.setClubNumber(thisPeer.getClubNumber());
		ping.setPingId(peerInformation.getPingMessageNumber());
		ping.setConnectionType(connectionType);
		peerInformation.incrementPingMessageNumber();

		PacketWrapper packet = MessageWrapper.wrapMessage(ping.build(), peerInformation);
		DatagramPacket datagramPacket = MessageWrapper.createSendDatagramPacket(packet, peerInformation);

		return datagramPacket;
	}

	@Override
	public DatagramPacket createPongMessage(PeerInformation peerInformation, int uploadLinkNum, int downloadLinkNum,
			int bufferVideoNum, PingMessage ping) throws IOException {
		PongMessage.Builder pong = PongMessage.newBuilder();
		pong.setResponsePingId(ping.getPingId());
		pong.setDownloadLinkNum(downloadLinkNum);
		pong.setUploadLinkNum(uploadLinkNum);
		pong.setBufferVideoNum(bufferVideoNum);

		PacketWrapper packet = MessageWrapper.wrapMessage(pong.build(), peerInformation);
		DatagramPacket datagramPacket = MessageWrapper.createSendDatagramPacket(packet, peerInformation);

		return datagramPacket;
	}

	@Override
	public DatagramPacket createRequestMessage(PeerInformation peerInformation, ConnectionType connectionType)
			throws IOException {
		RequestMessage.Builder request = RequestMessage.newBuilder();
		request.setRequestId(peerInformation.getRequestMessageNumber());
		request.setClubNumber(thisPeer.getClubNumber());
		request.setConnectionType(connectionType);
		peerInformation.incrementRequestMessageNumber();

		PacketWrapper packet = MessageWrapper.wrapMessage(request.build(), peerInformation);
		DatagramPacket datagramPacket = MessageWrapper.createSendDatagramPacket(packet, peerInformation);

		return datagramPacket;
	}

	@Override
	public DatagramPacket createResponseMessage(PeerInformation peerInformation, RequestMessage requestMessage)
			throws IOException {
		ResponseMessage.Builder response = ResponseMessage.newBuilder();
		response.setResponseRequestId(requestMessage.getRequestId());

		PacketWrapper packet = MessageWrapper.wrapMessage(response.build(), peerInformation);
		DatagramPacket datagramPacket = MessageWrapper.createSendDatagramPacket(packet, peerInformation);

		return datagramPacket;
	}

	@Override
	public void send(DatagramPacket sendDataPacket, DatagramSocket socket) throws IOException {
		socket.send(sendDataPacket);
	}

	@Override
	public DatagramPacket createReciveDatagramPacket(int byteBufferSize) throws IOException {
		byte[] receiveData = new byte[byteBufferSize];
		DatagramPacket recivePacket = new DatagramPacket(receiveData, receiveData.length);

		return recivePacket;
	}

	@Override
	public PacketWrapper recive(DatagramSocket socket, DatagramPacket recivePacket) throws IOException {
		socket.receive(recivePacket);
		byte[] data = new byte[recivePacket.getLength()];
		System.arraycopy(recivePacket.getData(), recivePacket.getOffset(), data, 0, recivePacket.getLength());

		return PacketWrapper.parseFrom(data);
	}

	@Override
	public DatagramPacket createTerminateConnectionMessage(PeerInformation peerInformation,
			TerminatedReason terminatedReason) throws IOException {
		TerminatedMessage.Builder terminate = TerminatedMessage.newBuilder();
		terminate.setTerminatedId(peerInformation.getLastSentPacketNumber());
		terminate.setTerminatedReason(terminatedReason);

		PacketWrapper packet = MessageWrapper.wrapMessage(terminate.build(), peerInformation);
		DatagramPacket datagramPacket = MessageWrapper.createSendDatagramPacket(packet, peerInformation);

		return datagramPacket;
	}

	@Override
	public DatagramPacket createKeepAliveMessage(PeerInformation peerInformation) throws IOException {
		KeepAliveMessage.Builder alive = KeepAliveMessage.newBuilder();
		alive.setMessageId(0);

		PacketWrapper packet = MessageWrapper.wrapMessage(alive.build(), peerInformation);
		DatagramPacket datagramPacket = MessageWrapper.createSendDatagramPacket(packet, peerInformation);

		return datagramPacket;
	}

	@Override
	public void startRecivePacketLoop(DatagramSocket socket) throws IOException {
		while (true) {
			DatagramPacket recivePacket = new DatagramPacket(new byte[Constants.BUFFER_SIZE], Constants.BUFFER_SIZE);
			PacketWrapper packet = recive(socket, recivePacket);
			packetsWaitingForProcessing
					.add(new Pair<String, PacketWrapper>(recivePacket.getAddress().getHostAddress(), packet));
		}
	}

	@Override
	public Pair<String, PacketWrapper> getPacketsWaitingForProcessing() {
		try {
			return packetsWaitingForProcessing.take();
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	@Override
	public void setPacketsWaitingForProcessing(BlockingQueue<Pair<String, PacketWrapper>> packetsWaitingForProcessing) {
		this.packetsWaitingForProcessing = packetsWaitingForProcessing;
	}

	@Override
	public void sendRequestMessage(List<PeerInformation> neighbourPeers, DatagramSocket socket,
			ConnectionType connectionType) {
		neighbourPeers.stream().forEach(peer -> {
			try {
				DatagramPacket packet = createRequestMessage(peer, connectionType);
				send(packet, socket);
				if (connectionType.equals(ConnectionType.DOWNLOAD)) {
					peer.setPeerStatus(PeerStatus.RESPONSE_WAIT_DOWNLOAD);
				} else {
					peer.setPeerStatus(PeerStatus.RESPONSE_WAIT_UPLOAD);
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		});
	}

	@Override
	public void sendPingMessages(List<PeerInformation> neighbourPeers, ConnectionType connectionType,
			DatagramSocket socket) {
		neighbourPeers.stream().filter(peer -> peer.getPeerStatus().equals(PeerStatus.NOT_CONTACTED)).forEach(peer -> {
			try {
				DatagramPacket packet = createPingMessage(peer, connectionType);
				send(packet, socket);
				if (connectionType.equals(ConnectionType.DOWNLOAD)) {
					peer.setPeerStatus(PeerStatus.PONG_WAIT_DOWNLOAD);
				} else {
					peer.setPeerStatus(PeerStatus.PONG_WAIT_UPLOAD);
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		});
	}

	@Override
	public void sendPongMessage(List<PeerInformation> neighbourPeers, PeerInformation peer, PingMessage pingMessage,
			DatagramSocket socket) {
		int uploadLinkNum = (int) neighbourPeers.stream()
				.filter(p -> p.getPeerStatus().equals(PeerStatus.UPLOAD_CONNECTION)).count();
		int downloadLinkNum = (int) neighbourPeers.stream()
				.filter(p -> p.getPeerStatus().equals(PeerStatus.DOWNLOAD_CONNECTION)).count();
		int bufferVideoNum = sharingBufferSingleton.getNumberOfBufferedVideoContent();
		try {
			DatagramPacket packet = createPongMessage(peer, uploadLinkNum, downloadLinkNum, bufferVideoNum,
					pingMessage);
			send(packet, socket);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void sendResponseMessage(PeerInformation peer, PacketWrapper packet, DatagramSocket socket) {
		try {
			DatagramPacket datagramPacket = createResponseMessage(peer, packet.getRequestMessage());
			send(datagramPacket, socket);

			if (packet.getRequestMessage().getConnectionType().equals(ConnectionType.DOWNLOAD)) {
				peer.setPeerStatus(PeerStatus.DOWNLOAD_CONNECTION);
			} else {
				peer.setPeerStatus(PeerStatus.UPLOAD_CONNECTION);
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void sendKeepAliveMessage(PeerInformation peer, DatagramSocket socket) {
		try {
			DatagramPacket datagramPacket = createKeepAliveMessage(peer);
			send(datagramPacket, socket);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public PeerInformation getThisPeer() {
		return thisPeer;
	}

	public void setThisPeer(PeerInformation thisPeer) {
		this.thisPeer = thisPeer;
	}

}
