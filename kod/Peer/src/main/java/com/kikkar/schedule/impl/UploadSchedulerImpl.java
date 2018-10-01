package com.kikkar.schedule.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.kikkar.global.SharingBufferSingleton;
import com.kikkar.network.ConnectionManager;
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
	
	private int MAX_ELEMENT_NUMBER = sharingBufferSingleton.getMAX_ELEMENT_NUMBER();
	
	@Override
	public void sendControlMessage(ControlMessage message) {
		PacketWrapper.Builder wrap = PacketWrapper.newBuilder();
		wrap.setControlMessage(message);

		connectionManager.sendAll(wrap, new ArrayList<>());
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

		connectionManager.sendAll(wrap, new ArrayList<>());
	}

	@Override
	public void sendVideo(int currentVideoNum, List<String> currentVideoNotInterestedIpAddresses) {
		VideoPacket video = sharingBufferSingleton.getVideoPacket(currentVideoNum);
		PacketWrapper.Builder wrap = PacketWrapper.newBuilder().setVideoPacket(video);

		connectionManager.sendAll(wrap, currentVideoNotInterestedIpAddresses);
	}

	@Override
	public void sendResponseMessage(Pair<String, PacketWrapper> packetPair, int[] videoNum) {
		VideoPacket[] videoPack = new VideoPacket[videoNum.length];
		for (int i = 0; i < videoPack.length; i++) {
			videoPack[i] = sharingBufferSingleton.getVideoPacket(videoNum[i]);
		}

		Stream.of(videoPack).forEach(v -> {
			ResponseVideoMessage.Builder responseBuilder = ResponseVideoMessage.newBuilder();
			responseBuilder.setVideoNum(v.getVideoNum()).setChunkNum(v.getChunkNum()).setVideo(v.getVideo());
			PacketWrapper.Builder wrap = PacketWrapper.newBuilder().setResponseVideoMessage(responseBuilder.build());

			connectionManager.sendOne(wrap, packetPair.getLeft());
		});
	}

	public void getMissingVideoNum() {
		int[] videoNum = getMissingVideos();
		RequestVideoMessage.Builder request = RequestVideoMessage.newBuilder();
		request.setMessageId(0);
		
		for (int i = 0; i < videoNum.length; i++) {
			request.setVideoNum(i, videoNum[i]);			
		}
		
		PacketWrapper.Builder wrap = PacketWrapper.newBuilder().setRequestVideoMessage(request.build());
		
		connectionManager.sendAll(wrap, new ArrayList<>());
	}

	private int[] getMissingVideos() {
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
			
			if(iterationNUm > MAX_ELEMENT_NUMBER) {
				break;
			}
			
			currentPos = (currentPos + 1) % MAX_ELEMENT_NUMBER;
			iterationNUm++;
		}

		return videoNum.stream().mapToInt(i -> i).toArray();
	}

}
