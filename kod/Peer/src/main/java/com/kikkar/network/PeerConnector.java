package com.kikkar.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.kikkar.network.impl.PeerInformation;
import com.kikkar.packet.ConnectionType;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.PingMessage;
import com.kikkar.packet.RequestMessage;
import com.kikkar.packet.TerminatedReason;

public interface PeerConnector {
	DatagramPacket createPingMessage(PeerInformation peerInformation, short personalClubNum,
			ConnectionType connectionType) throws IOException;

	DatagramPacket createPongMessage(PeerInformation peerInformation, int uploadLinkNum, int downloadLinkNum,
			int bufferVideoNum, PingMessage ping) throws IOException;

	DatagramPacket createRequestMessage(PeerInformation peerInformation, short personalClubNum,
			ConnectionType connectionType) throws IOException;

	DatagramPacket createResponseMessage(PeerInformation peerInformation, RequestMessage requestMessage)
			throws IOException;

	void send(DatagramPacket sendDataPacket, DatagramSocket socket) throws IOException;

	public DatagramPacket createReciveDatagramPacket(int byteBufferSize) throws IOException;

	PacketWrapper recive(DatagramSocket socket, DatagramPacket recivePacket) throws IOException;

	DatagramPacket createTerminateConnectionMessage(PeerInformation peerInformation,
			TerminatedReason terminatedReason) throws IOException ;

	DatagramPacket createKeepAliveMessage(PeerInformation peerInformation) throws IOException;
}
