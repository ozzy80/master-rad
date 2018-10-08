package com.kikkar.schedule.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kikkar.global.Constants;
import com.kikkar.global.SharingBufferSingleton;
import com.kikkar.network.impl.ConnectionManagerImpl;
import com.kikkar.network.impl.DummyObjectCreator;
import com.kikkar.network.impl.MessageWrapper;
import com.kikkar.network.impl.PeerConnectorImpl;
import com.kikkar.network.impl.PeerInformation;
import com.kikkar.packet.ControlMessage;
import com.kikkar.packet.HaveMessage;
import com.kikkar.packet.NotInterestedMessage;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.Pair;
import com.kikkar.packet.RequestVideoMessage;
import com.kikkar.packet.ResponseVideoMessage;
import com.kikkar.packet.VideoPacket;

class DownloadSchedulerImplTest {

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
		PeerConnectorImpl peerConnectorImpl = new PeerConnectorImpl();
		peerConnectorImpl.setThisPeer(new PeerInformation("192.168.0.54".getBytes(), 5721, (short) 0));
		connectionManagerImpl.setPeerConnector(peerConnectorImpl);
		connectionManagerImpl.setSocket(new DatagramSocket());
		sharingBufferSingleton = SharingBufferSingleton.getInstance();
		downloadSchedulerImpl.setSharingBufferSingleton(sharingBufferSingleton);
		downloadSchedulerImpl.setUploadScheduler(uploadSchedulerImpl);
		downloadSchedulerImpl.setNotInterestList(new ArrayList<>());
		sharingBufferSingleton.setVideoArray(new VideoPacket[300]);
		sharingBufferSingleton.setMinVideoNum(0);
	}

	@Test
	void testGetNextPacket_checkDefaultBehaviour() {
		PeerInformation peer = new PeerInformation("192.168.0.54".getBytes(), 5721, (short) 0);
		PacketWrapper wrap = MessageWrapper.wrapMessage(VideoPacket.newBuilder().setVideoNum(1).build(), peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);
		BlockingQueue<Pair<String, PacketWrapper>> queue = new ArrayBlockingQueue<>(30);
		queue.add(packetPair);
		connectionManagerImpl.setPacketsForHigherLevel(queue);

		assertEquals(packetPair, downloadSchedulerImpl.getNextPacket());
	}

	@Test
	void testProcessPacket_checkHaveMessagePresentVideoCase() {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(2, 3, 4);
		connectionManagerImpl.setPeerList(peerList);
		PeerInformation peer = peerList.get(2);
		VideoPacket video = VideoPacket.newBuilder().setVideoNum(2).setChunkNum(21).build();
		sharingBufferSingleton.addVideoPacket(2, video);
		PacketWrapper wrap = MessageWrapper.wrapMessage(HaveMessage.newBuilder().setVideoNum(2).build(), peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);
		connectionManagerImpl.setPeerList(peerList);
		downloadSchedulerImpl.processPacket(packetPair);

		assertEquals(2, peer.getLastSentPacketNumber());
		assertTrue(peer.getLastSentMessageTimeMilliseconds() > 1000);
	}

	@Test
	void testProcessPacket_checkHaveMessageNotPresentVideoCase() {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(2, 3, 4);
		connectionManagerImpl.setPeerList(peerList);
		PeerInformation peer = peerList.get(2);
		PacketWrapper wrap = MessageWrapper.wrapMessage(HaveMessage.newBuilder().setVideoNum(221).build(), peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);
		connectionManagerImpl.setPeerList(peerList);
		downloadSchedulerImpl.processPacket(packetPair);

		assertEquals(1, peer.getLastSentPacketNumber());
	}

	@Test
	void testProcessPacket_checkCorrectControlMessage() {
		int messageId = 5;
		int displayedVideoNum = 123545;
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(2, 3, 4);
		connectionManagerImpl.setPeerList(peerList);
		PeerInformation peer = peerList.get(2);
		ControlMessage control = ControlMessage.newBuilder().setCurrentChunkVideoNum(displayedVideoNum)
				.setMessageId(messageId).setTimeInMilliseconds(156423).build();
		PacketWrapper wrap = MessageWrapper.wrapMessage(control, peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);
		connectionManagerImpl.setPeerList(peerList);
		downloadSchedulerImpl.processPacket(packetPair);

		assertEquals(5, downloadSchedulerImpl.getLastControlMessageId());
		assertEquals(displayedVideoNum % Constants.BUFFER_SIZE, sharingBufferSingleton.getMinVideoNum());
		assertEquals(2, peer.getLastSentPacketNumber());
		assertTrue(peer.getLastSentMessageTimeMilliseconds() > 1000);
	}

	@Test
	void testProcessPacket_checkDelayedControlMessage() {
		int messageId = 5;
		int previousMessageId = 6;
		int displayedVideoNum = 123545;
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(2, 3, 4);
		connectionManagerImpl.setPeerList(peerList);
		PeerInformation peer = peerList.get(2);
		ControlMessage control = ControlMessage.newBuilder().setCurrentChunkVideoNum(displayedVideoNum)
				.setMessageId(messageId).setTimeInMilliseconds(156423).build();
		PacketWrapper wrap = MessageWrapper.wrapMessage(control, peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);
		connectionManagerImpl.setPeerList(peerList);
		downloadSchedulerImpl.setLastControlMessageId(previousMessageId);
		downloadSchedulerImpl.processPacket(packetPair);

		assertEquals(previousMessageId, downloadSchedulerImpl.getLastControlMessageId());
		assertEquals(0, sharingBufferSingleton.getMinVideoNum());
		assertEquals(1, peer.getLastSentPacketNumber());
		assertEquals(0, peer.getLastSentMessageTimeMilliseconds());
	}

	@Test
	void testProcessPacket_checkNotInterestedMessage() {
		Integer videoNum = 5;
		String ipAddress = "192.168.0.54";
		PeerInformation peer = new PeerInformation(ipAddress.getBytes(), 5721, (short) 5);
		NotInterestedMessage notInterestedMessage = NotInterestedMessage.newBuilder().setVideoNum(videoNum).build();
		PacketWrapper wrap = MessageWrapper.wrapMessage(notInterestedMessage, peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);

		downloadSchedulerImpl.processPacket(packetPair);

		assertEquals(ipAddress, downloadSchedulerImpl.getNotInterestList().get(0).getLeft());
		assertEquals(videoNum, downloadSchedulerImpl.getNotInterestList().get(0).getRight());
	}

	@Test
	void testProcessPacket_checkVideoPacketInClub() {
		int videoNum = 6;
		int chunkNum = 123545;
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(12, 12, 4);
		connectionManagerImpl.setPeerList(peerList);
		PeerInformation peer = peerList.get(5);
		VideoPacket video = VideoPacket.newBuilder().setVideoNum(videoNum).setChunkNum(chunkNum).build();
		PacketWrapper wrap = MessageWrapper.wrapMessage(video, peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);
		downloadSchedulerImpl.setWAIT_MILLISECOND(0);
		downloadSchedulerImpl.setLastVideoNumberSent(6);

		downloadSchedulerImpl.processPacket(packetPair);

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			return;
		}

		assertEquals(video, sharingBufferSingleton.getVideoPacket(videoNum));
		assertEquals(2, peer.getLastSentPacketNumber());
		assertTrue(peer.getLastSentMessageTimeMilliseconds() > 1000);
		assertEquals(7, downloadSchedulerImpl.getLastVideoNumberSent());
	}
	
	@Test
	void testProcessPacket_checkVideoPacketOutCub() {
		int videoNum = 5;
		int chunkNum = 123545;
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(12, 12, 4);
		connectionManagerImpl.setPeerList(peerList);
		PeerInformation peer = peerList.get(5);
		VideoPacket video = VideoPacket.newBuilder().setVideoNum(videoNum).setChunkNum(chunkNum).build();
		PacketWrapper wrap = MessageWrapper.wrapMessage(video, peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);
		downloadSchedulerImpl.setWAIT_MILLISECOND(0);
		downloadSchedulerImpl.setLastVideoNumberSent(5);

		downloadSchedulerImpl.processPacket(packetPair);

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			return;
		}

		assertEquals(video, sharingBufferSingleton.getVideoPacket(videoNum));
		assertEquals(1, peer.getLastSentPacketNumber());
		assertEquals(0, peer.getLastSentMessageTimeMilliseconds());
		assertEquals(6, downloadSchedulerImpl.getLastVideoNumberSent());
	}

	@Test
	void testProcessPacket_checkVideoPacketFreeNotInterestList() {
		int videoNum = 5;
		int chunkNum = 123545;
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(2, 3, 4);
		connectionManagerImpl.setPeerList(peerList);
		PeerInformation peer = peerList.get(2);
		VideoPacket video = VideoPacket.newBuilder().setVideoNum(videoNum).setChunkNum(chunkNum).build();
		PacketWrapper wrap = MessageWrapper.wrapMessage(video, peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);
		downloadSchedulerImpl.setWAIT_MILLISECOND(0);
		List<Pair<String, Integer>> notInterestList = new ArrayList<>();
		notInterestList.add(new Pair<String, Integer>("192.168.0.1", 110));
		notInterestList.add(new Pair<String, Integer>("192.168.0.2", 111));
		downloadSchedulerImpl.setNotInterestList(notInterestList);
		downloadSchedulerImpl.setLastVideoNumberSent(200);
		downloadSchedulerImpl.setWAIT_MILLISECOND(0);

		downloadSchedulerImpl.processPacket(packetPair);

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}

		assertEquals(0, notInterestList.size());
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

	@Test
	void testProcessPacket_checkResponseVideoMessage() {
		VideoPacket videoExpected = VideoPacket.newBuilder().setChunkNum(0).setVideoNum(0).build();
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(2, 3, 4);
		PeerInformation peer = peerList.get(2);
		ResponseVideoMessage responseVideoMessage = ResponseVideoMessage.newBuilder().setVideoNum(0).setChunkNum(0)
				.build();
		PacketWrapper wrap = MessageWrapper.wrapMessage(responseVideoMessage, peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);

		downloadSchedulerImpl.processPacket(packetPair);

		assertEquals(videoExpected, sharingBufferSingleton.getVideoPacket(0));
	}

	@Test
	void testProcessControlMessage_checkUnkownHead() {
		int videoNum = 15;
		int messageId = 0;
		int playerElapsedTime = 5624;
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(2, 3, 4);
		PeerInformation peer = peerList.get(2);
		connectionManagerImpl.setPeerList(peerList);
		ControlMessage responseVideoMessage = ControlMessage.newBuilder().setCurrentChunkVideoNum(videoNum)
				.setMessageId(messageId).setPlayerElapsedTime(playerElapsedTime).setTimeInMilliseconds(12458l).build();
		PacketWrapper wrap = MessageWrapper.wrapMessage(responseVideoMessage, peer);
		Pair<String, PacketWrapper> packetPair = new Pair<String, PacketWrapper>(new String(peer.getIpAddress()), wrap);

		downloadSchedulerImpl.processPacket(packetPair);

		assertEquals(videoNum, sharingBufferSingleton.getMinVideoNum());
	}
}
