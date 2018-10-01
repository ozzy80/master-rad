package com.kikkar.schedule.impl;

import java.net.DatagramPacket;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.kikkar.global.ClockSingleton;
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
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private ClockSingleton clockSingleton;
	private SharingBufferSingleton sharingBufferSingleton;
	private ConnectionManager connectionManager;
	private DatagramPacket reciveDatagramPacket;
	private UploadScheduler uploadScheduler;
	private List<Pair<String, Integer>> notInterestList;
	private int lastVideoNumberSent;
	private int lastControlMessageId;

	private long WAIT_MILLISECOND = 200;

	@Override
	public void startDownload() {
		new Thread(() -> {
			try {
				connectionManager.start(reciveDatagramPacket);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	@Override
	public Pair<String, PacketWrapper> getNextPacket() {
		Pair<String, PacketWrapper> packetPair = connectionManager.getWaitingPackets();
		return packetPair;
	}

	@Override
	public void processPacket(Pair<String, PacketWrapper> packetPair) {
		if (packetPair == null) {
			return;
		} else if (packetPair.getRight().hasHaveMessage()) {
			HaveMessage haveMessage = packetPair.getRight().getHaveMessage();
			if (sharingBufferSingleton.isVideoPresent(haveMessage.getVideoNum())) {
				uploadScheduler.sendNotInterested(packetPair);
			}
		} else if (packetPair.getRight().hasControlMessage()) {
			ControlMessage controlMessage = packetPair.getRight().getControlMessage();
			long currentTime = clockSingleton.getcurrentTimeMilliseconds();
			boolean properTime = controlMessage.getTimeInMilliseconds() > currentTime - 500
					&& controlMessage.getTimeInMilliseconds() < currentTime + 500;
			if (controlMessage.getMessageId() > lastControlMessageId && properTime) {
				sharingBufferSingleton.setMinVideoNum(controlMessage.getCurrentDisplayedVideoNum());
				lastControlMessageId = controlMessage.getMessageId();
				uploadScheduler.sendControlMessage(controlMessage);
			}
		} else if (packetPair.getRight().hasNotInterestedMessage()) {
			notInterestList.add(new Pair<String, Integer>(packetPair.getLeft(),
					packetPair.getRight().getNotInterestedMessage().getVideoNum()));
		} else if (packetPair.getRight().hasVideoPacket()) {
			VideoPacket video = packetPair.getRight().getVideoPacket();
			uploadScheduler.sendHaveMessage(video.getVideoNum());
			if (sharingBufferSingleton.isVideoPresent(video.getVideoNum())) {
				sharingBufferSingleton.addVideoPacket(video.getVideoNum(), video);
			}
			executor.schedule(() -> {
				List<String> currentVideoNotInterestedIpAddresses = notInterestList.stream()
						.filter(p -> p.getRight().equals(lastVideoNumberSent)).map(Pair::getLeft)
						.collect(Collectors.toList());
				lastVideoNumberSent++;
				int currentVideoNum = lastVideoNumberSent - 1;
				uploadScheduler.sendVideo(currentVideoNum, currentVideoNotInterestedIpAddresses);
				notInterestList.removeIf(p -> p.getRight() < lastVideoNumberSent);
			}, WAIT_MILLISECOND, TimeUnit.MILLISECONDS);
		} else if (packetPair.getRight().hasRequestVideoMessage()) {
			RequestVideoMessage request = packetPair.getRight().getRequestVideoMessage();
			int[] videoNum = request.getVideoNumList().stream().mapToInt(i -> i).toArray();
			uploadScheduler.sendResponseMessage(packetPair, videoNum);
		} else if (packetPair.getRight().hasResponseVideoMessage()) {
			ResponseVideoMessage response = packetPair.getRight().getResponseVideoMessage();
			if (sharingBufferSingleton.isVideoPresent(response.getVideoNum())) {
				VideoPacket.Builder videoBuilder = VideoPacket.newBuilder();
				videoBuilder.setVideoNum(response.getVideoNum()).setChunkNum(response.getChunkNum())
						.setVideo(response.getVideo());
				sharingBufferSingleton.addVideoPacket(response.getVideoNum(), videoBuilder.build());
			}
		}
	}
}
