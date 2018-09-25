package com.kikkar.network;

import java.io.OutputStream;
import java.net.DatagramPacket;
import java.util.List;

import com.kikkar.network.impl.PeerInformation;
import com.kikkar.packet.TerminatedReason;

public interface ConnectionManager {
	
	void loadJson(String rawJson);
	
	void start(DatagramPacket reciveDatagramPacket) throws Exception;
	
	void contactServerForMorePeers(SpeedTest speedTest, OutputStream errorOutput);
	
	void congestionControl();
	
	void terminateConnections(List<PeerInformation> peerInformations, TerminatedReason terminatedReason);
	
	void keepAliveUploadConnection();
}
