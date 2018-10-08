package com.kikkar.schedule.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.kikkar.global.Constants;
import com.kikkar.global.SharingBufferSingleton;
import com.kikkar.network.ConnectionManager;
import com.kikkar.network.impl.PeerStatus;
import com.kikkar.packet.ControlMessage;
import com.kikkar.packet.HaveMessage;
import com.kikkar.packet.NotInterestedMessage;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.Pair;
import com.kikkar.packet.RequestVideoMessage;
import com.kikkar.packet.ResponseVideoMessage;
import com.kikkar.packet.VideoPacket;
import com.kikkar.schedule.UploadScheduler;

public class UploadSchedulerImpl implements UploadScheduler {
	private ConnectionManager connectionManager;
	private SharingBufferSingleton sharingBufferSingleton;
	private ScheduledExecutorService executor;

	public UploadSchedulerImpl() {
		sharingBufferSingleton = SharingBufferSingleton.getInstance();
		executor = Executors.newSingleThreadScheduledExecutor();
	}
	
	public UploadSchedulerImpl(ConnectionManager connectionManager) {
		this();
		this.connectionManager = connectionManager;
	}
	
	@Override
	public void sendControlMessage(ControlMessage message) {
		PacketWrapper.Builder wrap = PacketWrapper.newBuilder();
		wrap.setControlMessage(message);

		connectionManager.sendAll(wrap, new ArrayList<>(), PeerStatus.DOWNLOAD_CONNECTION);
	}

	@Override
	public void sendNotInterested(Pair<String, PacketWrapper> haveMessagePair) {
		HaveMessage haveMessage = haveMessagePair.getRight().getHaveMessage();
		NotInterestedMessage notInterested = NotInterestedMessage.newBuilder().setVideoNum(haveMessage.getVideoNum())
				.build();
		PacketWrapper.Builder wrap = PacketWrapper.newBuilder().setNotInterestedMessage(notInterested);

		connectionManager.sendOne(wrap, haveMessagePair.getLeft());
	}

	@Override
	public void sendHaveMessage(int videoNum) {
		HaveMessage haveMessage = HaveMessage.newBuilder().setVideoNum(videoNum).build();
		PacketWrapper.Builder wrap = PacketWrapper.newBuilder().setHaveMessage(haveMessage);

		connectionManager.sendToClub(wrap, PeerStatus.DOWNLOAD_CONNECTION, -1);
	}

	@Override
	public void sendVideo(int currentVideoNum, List<String> currentVideoNotInterestedIpAddresses) {
		if (sharingBufferSingleton.isVideoPresent(currentVideoNum)) {
			VideoPacket video = sharingBufferSingleton.getVideoPacket(currentVideoNum);
			int videoBelongClub = video.getVideoNum() % Constants.NUMBER_OF_CLUB;
			if(connectionManager.getPeerConnector().getThisPeer().getClubNumber() == videoBelongClub) {
				PacketWrapper.Builder wrap = PacketWrapper.newBuilder().setVideoPacket(video);
				connectionManager.sendAll(wrap, currentVideoNotInterestedIpAddresses, PeerStatus.DOWNLOAD_CONNECTION);
			}
		}
	}

	@Override
	public void sendResponseMessage(Pair<String, PacketWrapper> packetPair, int[] videoNum) {
		VideoPacket[] videoPack = new VideoPacket[videoNum.length];
		for (int i = 0; i < videoPack.length; i++) {
			if (sharingBufferSingleton.isVideoPresent(i)) {
				videoPack[i] = sharingBufferSingleton.getVideoPacket(videoNum[i]);
			}
		}

		Stream.of(videoPack).filter(v -> v != null).forEach(v -> {
			ResponseVideoMessage.Builder responseBuilder = ResponseVideoMessage.newBuilder();
			responseBuilder.setVideoNum(v.getVideoNum()).setChunkNum(v.getChunkNum()).setVideo(v.getVideo());
			PacketWrapper.Builder wrap = PacketWrapper.newBuilder().setResponseVideoMessage(responseBuilder.build());

			connectionManager.sendOne(wrap, packetPair.getLeft());
		});
	}

	public void scheduleCollectMissingVideo() {
		executor.scheduleAtFixedRate(() -> getMissingVideoNum(), Constants.INITIA_MISSING_VIDEO_COLLECT_DELAY_SECOND,
				Constants.VIDEO_DURATION_SECOND / 2, TimeUnit.SECONDS);
	}

	public void getMissingVideoNum() {
		List<Integer> videoNum = getMissingVideos();
		RequestVideoMessage.Builder request = RequestVideoMessage.newBuilder().setMessageId(0).addAllVideoNum(videoNum);

		PacketWrapper.Builder wrap = PacketWrapper.newBuilder().setRequestVideoMessage(request.build());

		connectionManager.sendAll(wrap, new ArrayList<>(), PeerStatus.UPLOAD_CONNECTION);
	}

	private List<Integer> getMissingVideos() {
		int currentPos = sharingBufferSingleton.getMinVideoNum();
		List<Integer> videoNum = new ArrayList<>();
		int previousChunkNum = -1;

		int iterationNUm = 0;
		while (true) {
			VideoPacket video = sharingBufferSingleton.getVideoPacket(sharingBufferSingleton.getMinVideoNum());
			if (video == null) {
				videoNum.add(currentPos);
			} else if (previousChunkNum >= 0 && video.getChunkNum() > previousChunkNum) {
				break;
			} else {
				previousChunkNum = video.getChunkNum();
			}

			if (iterationNUm > Constants.BUFFER_SIZE || videoNum.size() > Constants.MAX_REUQEST_VIDEO_SIZE) {
				break;
			}

			currentPos = (currentPos + 1) % Constants.BUFFER_SIZE;
			iterationNUm++;
		}

		return videoNum;
	}

	@Override
	public void sendControlMessage(int currentVideoNum) {
	}
	
	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public void setConnectionManager(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	public SharingBufferSingleton getSharingBufferSingleton() {
		return sharingBufferSingleton;
	}

	public void setSharingBufferSingleton(SharingBufferSingleton sharingBufferSingleton) {
		this.sharingBufferSingleton = sharingBufferSingleton;
	}


}
