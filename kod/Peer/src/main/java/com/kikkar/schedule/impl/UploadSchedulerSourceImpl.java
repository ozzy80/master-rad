package com.kikkar.schedule.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.kikkar.global.ClockSingleton;
import com.kikkar.global.Constants;
import com.kikkar.global.SharingBufferSingleton;
import com.kikkar.network.impl.PeerStatus;
import com.kikkar.packet.ControlMessage;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.VideoPacket;

public class UploadSchedulerSourceImpl extends UploadSchedulerImpl {

	private int currentVideoNum;
	private int controlMessageId;
	private ClockSingleton clock = ClockSingleton.getInstance();
	private SharingBufferSingleton sharingBufferSingleton = SharingBufferSingleton.getInstance();
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	@Override
	public void sendVideo(int currentVideoNum, List<String> currentVideoNotInterestedIpAddresses) {
		VideoPacket video = sharingBufferSingleton.getVideoPacket(currentVideoNum);
		PacketWrapper.Builder wrap = PacketWrapper.newBuilder().setVideoPacket(video);
		int clubNum = currentVideoNum % Constants.NUMBER_OF_CLUB;
		super.getConnectionManager().sendToClub(wrap, PeerStatus.UPLOAD_CONNECTION, clubNum);
		
		if(video.getFirstFrame()) {
			sendControlMessage();
		}
		this.currentVideoNum = currentVideoNum;
	}
	
	private void sendControlMessage() {
		ControlMessage.Builder controlMessage = ControlMessage.newBuilder();
		controlMessage.setMessageId(controlMessageId++);
		controlMessage.setCurrentChunkVideoNum(currentVideoNum);
		//controlMessage.setPlayerElapsedTime();
		controlMessage.setTimeInMilliseconds(clock.getcurrentTimeMilliseconds());
		PacketWrapper.Builder controlWrap = PacketWrapper.newBuilder().setControlMessage(controlMessage);
		
		super.getConnectionManager().sendAll(controlWrap, new ArrayList<>(), PeerStatus.UPLOAD_CONNECTION);	
	}

	public int getCurrentVideoNum() {
		return currentVideoNum;
	}

	public void setCurrentVideoNum(int currentVideoNum) {
		this.currentVideoNum = currentVideoNum;
	}

	public int getControlMessageId() {
		return controlMessageId;
	}

	public void setControlMessageId(int controlMessageId) {
		this.controlMessageId = controlMessageId;
	}

}
