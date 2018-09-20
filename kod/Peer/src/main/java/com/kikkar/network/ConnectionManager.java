package com.kikkar.network;

import java.io.IOException;
import java.net.DatagramPacket;

import com.kikkar.network.impl.PeerInformation;
import com.kikkar.packet.ConnectionType;
import com.kikkar.packet.PingMessage;
import com.kikkar.packet.RequestMessage;
import com.kikkar.packet.TerminatedReason;

public interface ConnectionManager {
	
	DatagramPacket createPingPacket(PeerInformation peerInformation, ConnectionType connectionType) throws IOException;

	DatagramPacket createPongMessage(PeerInformation peerInformation, int bufferVideoNum, PingMessage ping) throws IOException;
	
	DatagramPacket createRequestMessage(PeerInformation peerInformation, ConnectionType connectionType) throws IOException;
	
	DatagramPacket createResponseMessage(PeerInformation peerInformation, RequestMessage requestMessage) throws IOException;
	
	DatagramPacket createTerminatedMessage(PeerInformation peerInformation, TerminatedReason terminatedReason) throws IOException;
	
	DatagramPacket createKeepAliveMessage(PeerInformation peerInformation) throws IOException;
}
