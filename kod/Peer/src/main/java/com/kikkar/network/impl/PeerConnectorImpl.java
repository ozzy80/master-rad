package com.kikkar.network.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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

public class PeerConnectorImpl implements PeerConnector {

	@Override
	public DatagramPacket createPingMessage(PeerInformation peerInformation, short personalClubNum,
			ConnectionType connectionType) throws IOException {
		PingMessage.Builder ping = PingMessage.newBuilder();
		ping.setClubNumber(personalClubNum);
		ping.setPingId(peerInformation.getPingMessageNumber());
		ping.setConnectionType(connectionType);
		peerInformation.incrementPingMessageNumber();
		
		PacketWrapper packet = MessageWrapper.wrapMessage(ping.build(), peerInformation);
		DatagramPacket datagramPacket = createSendDatagramPacket(packet, peerInformation);

		return datagramPacket;
	}

	@Override
	public DatagramPacket createPongMessage(PeerInformation peerInformation, int uploadLinkNum, int downloadLinkNum, int bufferVideoNum, PingMessage ping) throws IOException {
		PongMessage.Builder pong = PongMessage.newBuilder();
		pong.setResponsePingId(ping.getPingId());
		pong.setDownloadLinkNum(downloadLinkNum);
		pong.setUploadLinkNum(uploadLinkNum);
		pong.setBufferVideoNum(bufferVideoNum);
		
		PacketWrapper packet = MessageWrapper.wrapMessage(pong.build(), peerInformation);
		DatagramPacket datagramPacket = createSendDatagramPacket(packet, peerInformation);

		return datagramPacket;
	}

	@Override
	public DatagramPacket createRequestMessage(PeerInformation peerInformation, short personalClubNum, ConnectionType connectionType) throws IOException {
		RequestMessage.Builder request = RequestMessage.newBuilder();
		request.setRequestId(peerInformation.getRequestMessageNumber());
		request.setClubNumber(personalClubNum);
		request.setConnectionType(connectionType);
		peerInformation.incrementRequestMessageNumber();
		
		PacketWrapper packet = MessageWrapper.wrapMessage(request.build(), peerInformation);
		DatagramPacket datagramPacket = createSendDatagramPacket(packet, peerInformation);

		return datagramPacket;
	}

	@Override
	public DatagramPacket createResponseMessage(PeerInformation peerInformation, RequestMessage requestMessage) throws IOException {
		ResponseMessage.Builder response = ResponseMessage.newBuilder();
		response.setResponseRequestId(requestMessage.getRequestId());

		PacketWrapper packet = MessageWrapper.wrapMessage(response.build(), peerInformation);
		DatagramPacket datagramPacket = createSendDatagramPacket(packet, peerInformation);

		return datagramPacket;
	}

	private DatagramPacket createSendDatagramPacket(PacketWrapper packet, PeerInformation peerInformation) throws IOException {
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		packet.writeTo(byteOutStream);
		byte[] sendData = byteOutStream.toByteArray();
		InetAddress serverAddress = InetAddress.getByName(new String(peerInformation.getIpAddress()));

		DatagramPacket sendDataPacket = new DatagramPacket(sendData, sendData.length, serverAddress,
				peerInformation.getPortNumber());

		return sendDataPacket;
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
	public DatagramPacket createTerminateConnectionMessage(PeerInformation peerInformation, TerminatedReason terminatedReason) throws IOException {
		TerminatedMessage.Builder terminate = TerminatedMessage.newBuilder();
		terminate.setTerminatedId(peerInformation.getLastSentPacketNumber());
		terminate.setTerminatedReason(terminatedReason);

		PacketWrapper packet = MessageWrapper.wrapMessage(terminate.build(), peerInformation);
		DatagramPacket datagramPacket = createSendDatagramPacket(packet, peerInformation);

		return datagramPacket;
	}

	@Override
	public DatagramPacket createKeepAliveMessage(PeerInformation peerInformation) throws IOException {
		KeepAliveMessage.Builder alive = KeepAliveMessage.newBuilder();
		alive.setMessageId(0);

		PacketWrapper packet = MessageWrapper.wrapMessage(alive.build(), peerInformation);
		DatagramPacket datagramPacket = createSendDatagramPacket(packet, peerInformation);

		return datagramPacket;
	}

}
