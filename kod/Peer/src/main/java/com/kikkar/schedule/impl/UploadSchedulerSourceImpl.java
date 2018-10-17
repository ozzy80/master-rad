package com.kikkar.schedule.impl;

import java.util.ArrayList;
import java.util.List;

import com.kikkar.global.ClockSingleton;
import com.kikkar.global.Constants;
import com.kikkar.global.SharingBufferSingleton;
import com.kikkar.network.ConnectionManager;
import com.kikkar.network.impl.PeerStatus;
import com.kikkar.packet.ControlMessage;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.VideoPacket;

public class UploadSchedulerSourceImpl extends UploadSchedulerImpl {

	private int controlMessageId;
	private ClockSingleton clock;
	private SharingBufferSingleton sharingBufferSingleton;
	
	public UploadSchedulerSourceImpl() {
		super();
		clock = ClockSingleton.getInstance();
		sharingBufferSingleton = SharingBufferSingleton.getInstance();
	}
	
	public UploadSchedulerSourceImpl(ConnectionManager connectionManager) {
		super(connectionManager);
		clock = ClockSingleton.getInstance();
		sharingBufferSingleton = SharingBufferSingleton.getInstance();
	}
	
	@Override
	public void sendVideo(int currentVideoNum, List<String> currentVideoNotInterestedIpAddresses) {
		VideoPacket video = sharingBufferSingleton.getVideoPacket(currentVideoNum);
		PacketWrapper.Builder wrap = PacketWrapper.newBuilder().setVideoPacket(video);
		int clubNum = currentVideoNum % (Constants.NUMBER_OF_CLUB - 1);
		super.getConnectionManager().sendToClub(wrap, PeerStatus.UPLOAD_CONNECTION, clubNum);
	}
	
	@Override
	public void sendControlMessage(int currentVideoNum) {
		ControlMessage.Builder controlMessage = ControlMessage.newBuilder();
		controlMessage.setMessageId(controlMessageId++);
		controlMessage.setCurrentChunkVideoNum(currentVideoNum);
		controlMessage.setPlayerElapsedTime(sharingBufferSingleton.getPlayer().getCurrentPlayTime());
		controlMessage.setTimeInMilliseconds(clock.getcurrentTimeMilliseconds());
		PacketWrapper.Builder controlWrap = PacketWrapper.newBuilder().setControlMessage(controlMessage);
		
		sharingBufferSingleton.synchronizeVideoPlayTime(controlMessage.build());
		super.getConnectionManager().sendAll(controlWrap, new ArrayList<>(), PeerStatus.UPLOAD_CONNECTION);	
	}
	
	@Override
	public void scheduleCollectMissingVideo() {
		return;
	}

	public int getControlMessageId() {
		return controlMessageId;
	}

	public void setControlMessageId(int controlMessageId) {
		this.controlMessageId = controlMessageId;
	}

}
