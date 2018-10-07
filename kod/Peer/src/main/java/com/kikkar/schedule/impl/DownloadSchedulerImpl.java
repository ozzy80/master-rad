package com.kikkar.schedule.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.kikkar.global.SharingBufferSingleton;
import com.kikkar.network.ConnectionManager;
import com.kikkar.packet.ControlMessage;
import com.kikkar.packet.HaveMessage;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.Pair;
import com.kikkar.packet.RequestVideoMessage;
import com.kikkar.packet.ResponseVideoMessage;
import com.kikkar.packet.VideoPacket;
import com.kikkar.schedule.DownloadScheduler;
import com.kikkar.schedule.UploadScheduler;

public class DownloadSchedulerImpl implements DownloadScheduler {
	private ScheduledExecutorService executor;
	private SharingBufferSingleton sharingBufferSingleton;
	private ConnectionManager connectionManager;
	private UploadScheduler uploadScheduler;
	private List<Pair<String, Integer>> notInterestList;
	private int lastVideoNumberSent;
	private int lastControlMessageId;

	private long WAIT_MILLISECOND = 300;

	public DownloadSchedulerImpl() {
		executor = Executors.newSingleThreadScheduledExecutor();
		sharingBufferSingleton = SharingBufferSingleton.getInstance();
		notInterestList = new ArrayList<>();
		lastControlMessageId = -1;
	}

	public DownloadSchedulerImpl(ConnectionManager connectionManager, UploadScheduler uploadScheduler) {
		this();
		this.connectionManager = connectionManager;
		this.uploadScheduler = uploadScheduler;
	}

	@Override
	public void startDownload() {
		new Thread(() -> {
			try {
				connectionManager.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	@Override
	public Pair<String, PacketWrapper> getNextPacket() {
		return connectionManager.getWaitingPackets();
	}

	@Override
	public void processPacket(Pair<String, PacketWrapper> packetPair) {
		if (packetPair == null) {
			return;
		} else if (packetPair.getRight().hasHaveMessage()) {
			System.out.println("Have poruka");
			HaveMessage haveMessage = packetPair.getRight().getHaveMessage();
			if (sharingBufferSingleton.isVideoPresent(haveMessage.getVideoNum())) {
				uploadScheduler.sendNotInterested(packetPair);
			}
		} else if (packetPair.getRight().hasControlMessage()) {
			System.out.println("Control poruka");
			ControlMessage controlMessage = packetPair.getRight().getControlMessage();
			if (lastControlMessageId < controlMessage.getMessageId()) {
				processControlMessage(packetPair.getRight().getControlMessage());
				lastControlMessageId = controlMessage.getMessageId();
			}
		} else if (packetPair.getRight().hasNotInterestedMessage()) {
			System.out.println("Not interested poruka");
			notInterestList.add(new Pair<String, Integer>(packetPair.getLeft(),
					packetPair.getRight().getNotInterestedMessage().getVideoNum()));
		} else if (packetPair.getRight().hasVideoPacket()) {
			System.out.println("video poruka");
			VideoPacket video = packetPair.getRight().getVideoPacket();
			if (!sharingBufferSingleton.isVideoPresent(video.getVideoNum())) {
				uploadScheduler.sendHaveMessage(video.getVideoNum());
				sharingBufferSingleton.addVideoPacket(video.getVideoNum(), video);
				sendVideoOther();
			}
		} else if (packetPair.getRight().hasRequestVideoMessage()) {
			System.out.println("request poruka");
			RequestVideoMessage request = packetPair.getRight().getRequestVideoMessage();
			int[] videoNum = request.getVideoNumList().stream().mapToInt(i -> i).toArray();
			uploadScheduler.sendResponseMessage(packetPair, videoNum);
		} else if (packetPair.getRight().hasResponseVideoMessage()) {
			System.out.println("response poruka");
			ResponseVideoMessage response = packetPair.getRight().getResponseVideoMessage();
			if (!sharingBufferSingleton.isVideoPresent(response.getVideoNum())) {
				VideoPacket.Builder videoBuilder = VideoPacket.newBuilder();
				videoBuilder.setVideoNum(response.getVideoNum()).setChunkNum(response.getChunkNum())
						.setVideo(response.getVideo());
				sharingBufferSingleton.addVideoPacket(response.getVideoNum(), videoBuilder.build());
			}
		}
	}

	private void processControlMessage(ControlMessage controlMessage) {
		uploadScheduler.sendControlMessage(controlMessage);

		if (sharingBufferSingleton.isHeadAtChunkStart()) {
			sharingBufferSingleton.saveVideoPackIntoFile();
			sharingBufferSingleton.synchronizeVideoPlayTime(controlMessage);
		}
		sharingBufferSingleton.setMinVideoNum(controlMessage.getCurrentChunkVideoNum());
	}

	private void sendVideoOther() {
		executor.schedule(() -> {
			List<String> currentVideoNotInterestedIpAddresses = notInterestList.stream()
					.filter(p -> p.getRight().equals(lastVideoNumberSent)).map(Pair::getLeft)
					.collect(Collectors.toList());
			lastVideoNumberSent++;
			int currentVideoNum = lastVideoNumberSent - 1;
			notInterestList.removeIf(p -> p.getRight() < lastVideoNumberSent);
			uploadScheduler.sendVideo(currentVideoNum, currentVideoNotInterestedIpAddresses);
		}, WAIT_MILLISECOND, TimeUnit.MILLISECONDS);
	}

	public SharingBufferSingleton getSharingBufferSingleton() {
		return sharingBufferSingleton;
	}

	public void setSharingBufferSingleton(SharingBufferSingleton sharingBufferSingleton) {
		this.sharingBufferSingleton = sharingBufferSingleton;
	}

	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public void setConnectionManager(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	public UploadScheduler getUploadScheduler() {
		return uploadScheduler;
	}

	public void setUploadScheduler(UploadScheduler uploadScheduler) {
		this.uploadScheduler = uploadScheduler;
	}

	public List<Pair<String, Integer>> getNotInterestList() {
		return notInterestList;
	}

	public void setNotInterestList(List<Pair<String, Integer>> notInterestList) {
		this.notInterestList = notInterestList;
	}

	public int getLastVideoNumberSent() {
		return lastVideoNumberSent;
	}

	public void setLastVideoNumberSent(int lastVideoNumberSent) {
		this.lastVideoNumberSent = lastVideoNumberSent;
	}

	public int getLastControlMessageId() {
		return lastControlMessageId;
	}

	public void setLastControlMessageId(int lastControlMessageId) {
		this.lastControlMessageId = lastControlMessageId;
	}

	public long getWAIT_MILLISECOND() {
		return WAIT_MILLISECOND;
	}

	public void setWAIT_MILLISECOND(long wAIT_MILLISECOND) {
		WAIT_MILLISECOND = wAIT_MILLISECOND;
	}

}
