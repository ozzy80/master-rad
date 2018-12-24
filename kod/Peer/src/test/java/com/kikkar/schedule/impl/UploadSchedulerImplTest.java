package com.kikkar.schedule.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kikkar.global.ClockSingleton;
import com.kikkar.global.Constants;
import com.kikkar.global.SharingBufferSingleton;
import com.kikkar.network.impl.ConnectionManagerImpl;
import com.kikkar.network.impl.DummyObjectCreator;
import com.kikkar.network.impl.PeerConnectorImpl;
import com.kikkar.network.impl.PeerInformation;
import com.kikkar.packet.VideoPacket;

class UploadSchedulerImplTest {

	private UploadSchedulerImpl uploadSchedulerImpl;
	private SharingBufferSingleton sharingBufferSingleton;
	private ConnectionManagerImpl connectionManagerImpl;

	@BeforeEach
	void setup() throws SocketException {
		connectionManagerImpl = new ConnectionManagerImpl();
		uploadSchedulerImpl = new UploadSchedulerImpl();
		uploadSchedulerImpl.setConnectionManager(connectionManagerImpl);
		PeerConnectorImpl peerConnectorImpl = new PeerConnectorImpl();
		peerConnectorImpl.setThisPeer(new PeerInformation("192.168.0.54".getBytes(), 5721, (short) 0));
		connectionManagerImpl.setPeerConnector(peerConnectorImpl);
		connectionManagerImpl.setSocket(new DatagramSocket());
		sharingBufferSingleton = SharingBufferSingleton.getInstance();
		sharingBufferSingleton.setVideoArray(new VideoPacket[Constants.BUFFER_SIZE]);
	}

	@Test
	void testGetMissingVideoNum_checkDefaultBehaviour() {
		List<PeerInformation> peerListActual = DummyObjectCreator.createDummyPeers(2, 2, 4);
		connectionManagerImpl.setPeerList(peerListActual);

		uploadSchedulerImpl.getMissingVideoNum();

		assertTrue(peerListActual.get(0).getLastSentMessageTimeMilliseconds() > 1000);
		assertEquals(1, peerListActual.get(0).getLastSentPacketNumber());
		assertTrue(peerListActual.get(1).getLastSentMessageTimeMilliseconds() > 1000);
		assertEquals(1, peerListActual.get(1).getLastSentPacketNumber());
	}
	

	@Test
	void testSendHaveMessage_checkInClubSend() {
		List<PeerInformation> peerListActual = DummyObjectCreator.createDummyPeers(2, 2, 4);
		connectionManagerImpl.setPeerList(peerListActual);

		uploadSchedulerImpl.sendHaveMessage(15);

		assertEquals(0, peerListActual.get(1).getLastSentMessageTimeMilliseconds());
		assertEquals(0, peerListActual.get(1).getLastSentPacketNumber());
	}

}
