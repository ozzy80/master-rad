package com.kikkar.schedule.impl;

import com.kikkar.packet.ControlMessage;
import com.kikkar.packet.HaveMessage;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.Pair;
import com.kikkar.packet.RequestVideoMessage;
import com.kikkar.packet.ResponseVideoMessage;
import com.kikkar.packet.VideoPacket;

public class DownloadSchedulerSourceImpl extends DownloadSchedulerImpl {

	@Override
	public void processPacket(Pair<String, PacketWrapper> packetPair) {
		if (packetPair == null) {
			return;
		} else if (packetPair.getRight().hasRequestVideoMessage()) {
			RequestVideoMessage request = packetPair.getRight().getRequestVideoMessage();
			int[] videoNum = request.getVideoNumList().stream().mapToInt(i -> i).toArray();
			super.getUploadScheduler().sendResponseMessage(packetPair, videoNum);
		} 	
	}
}
