package com.kikkar.network;

import java.io.OutputStream;
import java.net.DatagramPacket;
import java.util.List;

import com.kikkar.network.impl.PeerInformation;
import com.kikkar.network.impl.PeerStatus;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.Pair;
import com.kikkar.packet.TerminatedReason;

public interface ConnectionManager {

	void loadJson(String rawJson);

	void start(DatagramPacket reciveDatagramPacket) throws Exception;

	void contactServerForMorePeers(OutputStream errorOutput);

	void congestionControl();

	void terminateConnections(List<PeerInformation> peerInformations, TerminatedReason terminatedReason);

	void keepAliveUploadConnection();

	Pair<String, PacketWrapper> getWaitingPackets();

	void sendAll(PacketWrapper.Builder wrap, List<String> uninterestedPeerIp, PeerStatus peerStatus);

	void sendOne(PacketWrapper.Builder wrap, String IpAddress);
}
