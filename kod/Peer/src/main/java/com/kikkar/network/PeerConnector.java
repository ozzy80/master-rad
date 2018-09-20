package com.kikkar.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.kikkar.network.impl.PeerInformation;
import com.kikkar.packet.ConnectionType;
import com.kikkar.packet.KeepAliveMessage;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.PingMessage;
import com.kikkar.packet.PongMessage;
import com.kikkar.packet.RequestMessage;
import com.kikkar.packet.ResponseMessage;
import com.kikkar.packet.TerminatedMessage;
import com.kikkar.packet.TerminatedReason;

public interface PeerConnector {
	PingMessage createPingMessage(PeerInformation peerInformation, short personalClubNum, ConnectionType connectionType);

	PongMessage createPongMessage(int uploadLinkNum, int downloadLinkNum, int bufferVideoNum, PingMessage ping);

	RequestMessage createRequestMessage(PeerInformation peerInformation, short personalClubNum, ConnectionType connectionType);
	
	ResponseMessage createResponseMessage(RequestMessage requestMessage);

	public DatagramPacket createSendDatagramPacket(PacketWrapper packet, PeerInformation peerInformation) throws IOException;
	
	void send(DatagramPacket sendDataPacket, DatagramSocket socket) throws IOException;
	
	public DatagramPacket createReciveDatagramPacket(int byteBufferSize) throws IOException;
	
	PacketWrapper recive(DatagramSocket socket, DatagramPacket recivePacket) throws IOException;

	TerminatedMessage createTerminateConnectionMessage(PeerInformation peerInformation, TerminatedReason terminatedReason);

	KeepAliveMessage createKeepAliveMessage();
}
