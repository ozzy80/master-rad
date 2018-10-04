package com.kikkar.schedule.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kikkar.global.ClockSingleton;
import com.kikkar.global.SharingBufferSingleton;
import com.kikkar.network.impl.ConnectionManagerImpl;
import com.kikkar.network.impl.DummyObjectCreator;
import com.kikkar.network.impl.MessageWrapper;
import com.kikkar.network.impl.PeerConnectorImpl;
import com.kikkar.network.impl.PeerInformation;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.Pair;
import com.kikkar.packet.RequestVideoMessage;
import com.kikkar.packet.VideoPacket;

class DownloadSchedulerSourceImplTest {

	private DownloadSchedulerImpl downloadSchedulerImpl;
	private ConnectionManagerImpl connectionManagerImpl;
	private SharingBufferSingleton sharingBufferSingleton;
	private UploadSchedulerImpl uploadSchedulerImpl;

	@BeforeEach
	void setup() throws SocketException {
		connectionManagerImpl = new ConnectionManagerImpl();
		downloadSchedulerImpl = new DownloadSchedulerImpl();
		uploadSchedulerImpl = new UploadSchedulerImpl();
		uploadSchedulerImpl.setConnectionManager(connectionManagerImpl);
		downloadSchedulerImpl.setConnectionManager(connectionManagerImpl);
		connectionManagerImpl.setPeerConnector(new PeerConnectorImpl());
		connectionManagerImpl.setClock(ClockSingleton.getInstance());
		connectionManagerImpl.setSocket(new DatagramSocket());
		sharingBufferSingleton = SharingBufferSingleton.getInstance();
		downloadSchedulerImpl.setSharingBufferSingleton(sharingBufferSingleton);
		downloadSchedulerImpl.setUploadScheduler(uploadSchedulerImpl);
		downloadSchedulerImpl.setNotInterestList(new ArrayList<>());
		sharingBufferSingleton.setVideoArray(new VideoPacket[300]);
		sharingBufferSingleton.setMinVideoNum(0);
	}
	
	@Test
	void testProcessPacket_checkRequestVideoMessageNotPresentVideo() {
		int messageNum = 5;
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(2, 3, 4);
		connectionManagerImpl.setPeerList(peerList);
		PeerInformation peer = peerList.get(1);
		peer.setLastSentMessageTimeMilliseconds(0);
		RequestVideoMessage.Builder requestMessage = RequestVideoMessage.newBuilder();
		requestMessage.setMessageId(messageNum);
		requestMessage.addVideoNum(0);
		requestMessage.addVideoNum(1);
		requestMessage.addVideoNum(2);
		PacketWrapper wrap = MessageWrapper.wrapMessage(requestMessage.build(), peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);

		downloadSchedulerImpl.processPacket(packetPair);

		assertEquals(0, peer.getLastSentMessageTimeMilliseconds());
		assertEquals(0, downloadSchedulerImpl.getLastVideoNumberSent());
	}

	@Test
	void testProcessPacket_checkRequestVideoMessagePresentVideo() {
		int videoNum = 0;
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(2, 3, 4);
		connectionManagerImpl.setPeerList(peerList);
		PeerInformation peer = peerList.get(2);
		VideoPacket video = VideoPacket.newBuilder().setVideoNum(videoNum).build();
		sharingBufferSingleton.addVideoPacket(videoNum, video);
		RequestVideoMessage.Builder requestMessage = RequestVideoMessage.newBuilder();
		requestMessage.setMessageId(2);
		requestMessage.addVideoNum(videoNum);
		requestMessage.addVideoNum(1);
		requestMessage.addVideoNum(2);
		PacketWrapper wrap = MessageWrapper.wrapMessage(requestMessage.build(), peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);

		downloadSchedulerImpl.processPacket(packetPair);

		assertTrue(peer.getLastSentMessageTimeMilliseconds() > 1000);
		assertEquals(2, peer.getLastSentPacketNumber());
	}


}
