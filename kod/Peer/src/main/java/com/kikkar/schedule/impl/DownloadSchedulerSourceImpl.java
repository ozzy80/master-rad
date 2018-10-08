package com.kikkar.schedule.impl;

import java.net.DatagramPacket;

import com.kikkar.network.ConnectionManager;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.Pair;
import com.kikkar.packet.RequestVideoMessage;
import com.kikkar.schedule.UploadScheduler;

public class DownloadSchedulerSourceImpl extends DownloadSchedulerImpl {

	public DownloadSchedulerSourceImpl() {}
	
	public DownloadSchedulerSourceImpl(ConnectionManager connectionManager, DatagramPacket reciveDatagramPacket, UploadScheduler uploadScheduler) {
		super(connectionManager, uploadScheduler);
	}
		
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
