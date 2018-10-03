package com.kikkar.schedule;

import java.util.List;

import com.kikkar.packet.ControlMessage;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.Pair;

public interface UploadScheduler {

	void sendControlMessage(ControlMessage message);

	void sendNotInterested(Pair<String, PacketWrapper> haveMessagePair);

	void sendHaveMessage(int videoNum);

	void sendVideo(int currentVideoNum, List<String> currentVideoNotInterestedIpAddresses);

	void sendResponseMessage(Pair<String, PacketWrapper> packetPair, int[] videoNum);

	void getMissingVideoNum();
	
	void scheduleCollectMissingVideo();
}
