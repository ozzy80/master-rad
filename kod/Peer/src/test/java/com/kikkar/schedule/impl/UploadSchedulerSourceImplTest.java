package com.kikkar.schedule.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.kikkar.global.Constants;
import com.kikkar.global.SharingBufferSingleton;
import com.kikkar.network.impl.ConnectionManagerImpl;
import com.kikkar.network.impl.DummyObjectCreator;
import com.kikkar.network.impl.PeerConnectorImpl;
import com.kikkar.network.impl.PeerInformation;
import com.kikkar.packet.VideoPacket;

public class UploadSchedulerSourceImplTest {

	private UploadSchedulerSourceImpl uploadSchedulerImpl;
	private SharingBufferSingleton sharingBufferSingleton;
	private ConnectionManagerImpl connectionManagerImpl;

	@BeforeEach
	void setup() throws SocketException {
		connectionManagerImpl = new ConnectionManagerImpl();
		uploadSchedulerImpl = new UploadSchedulerSourceImpl();
		uploadSchedulerImpl.setConnectionManager(connectionManagerImpl);
		PeerConnectorImpl peerConnectorImpl = new PeerConnectorImpl();
		peerConnectorImpl.setThisPeer(new PeerInformation("192.168.0.54".getBytes(), 5721, (short) 0));
		connectionManagerImpl.setPeerConnector(peerConnectorImpl);
		connectionManagerImpl.setSocket(new DatagramSocket());
		sharingBufferSingleton = SharingBufferSingleton.getInstance();
		sharingBufferSingleton.setVideoArray(new VideoPacket[Constants.BUFFER_SIZE]);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 15, 132, 1524588, 210 })
	void testSendVideo_checkDefaultBehaviour(int videoNum) {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(2 * 6, 2, 4);
		connectionManagerImpl.setPeerList(peerList);
		VideoPacket video = VideoPacket.newBuilder().setVideoNum(videoNum).build();
		sharingBufferSingleton.addVideoPacket(videoNum, video);

		uploadSchedulerImpl.sendVideo(videoNum, null);

		assertEquals(2, peerList.stream().filter(p -> p.getLastSentMessageTimeMilliseconds() > 1000).count());
		assertEquals(2, peerList.stream().filter(p -> p.getLastSentPacketNumber() != 0).count());
	}

	@Test
	void testSendVideo_checkSendVideoToDefaultClub() {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(2 * 6, 2, 4);
		connectionManagerImpl.setPeerList(peerList);

		for (int videoNum = 0; videoNum < Constants.NUMBER_OF_CLUB; videoNum++) {
			VideoPacket video = VideoPacket.newBuilder().setVideoNum(videoNum).build();
			sharingBufferSingleton.addVideoPacket(videoNum, video);
			uploadSchedulerImpl.sendVideo(videoNum, null);

			assertEquals((videoNum+1) * 2,
					peerList.stream().filter(p -> p.getLastSentMessageTimeMilliseconds() > 1000).count(),
					"Error in club " + videoNum);
			assertEquals((videoNum+1) * 2, peerList.stream().filter(p -> p.getLastSentPacketNumber() != 0).count(),
					"Error in club " + videoNum);
		}
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 15, 132, 1524588, 210 })
	void testSendControlMessage_checkDefaultBehaviour(int videoNum) {
		List<PeerInformation> peerList = DummyObjectCreator.createDummyPeers(2 * 6, 2, 4);
		connectionManagerImpl.setPeerList(peerList);

		uploadSchedulerImpl.sendControlMessage(videoNum);

		assertEquals(12, peerList.stream().filter(p -> p.getLastSentMessageTimeMilliseconds() > 1000).count());
		assertEquals(12, peerList.stream().filter(p -> p.getLastSentPacketNumber() != 0).count());
	}

}
