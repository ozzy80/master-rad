package com.kikkar.schedule.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kikkar.global.ClockSingleton;
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
	private int MAX_ELEMENT_NUMBER = 6_000; 

	@BeforeEach
	void setup() throws SocketException {
		connectionManagerImpl = new ConnectionManagerImpl();
		uploadSchedulerImpl = new UploadSchedulerImpl();
		uploadSchedulerImpl.setConnectionManager(connectionManagerImpl);
		connectionManagerImpl.setPeerConnector(new PeerConnectorImpl());
		connectionManagerImpl.setClock(ClockSingleton.getInstance());
		connectionManagerImpl.setSocket(new DatagramSocket());
		sharingBufferSingleton = SharingBufferSingleton.getInstance();
		sharingBufferSingleton.setVideoArray(new VideoPacket[MAX_ELEMENT_NUMBER]);
	}

	@Test
	void testGetMissingVideoNum_checkDefaultBehaviour() {
		List<PeerInformation> peerListActual = DummyObjectCreator.createDummyPeers(2, 2, 4);
		connectionManagerImpl.setPeerList(peerListActual);

		uploadSchedulerImpl.getMissingVideoNum();

		assertTrue(peerListActual.get(2).getLastSentMessageTimeMilliseconds() > 1000);
		assertEquals(1, peerListActual.get(2).getLastSentPacketNumber());
		assertTrue(peerListActual.get(3).getLastSentMessageTimeMilliseconds() > 1000);
		assertEquals(1, peerListActual.get(3).getLastSentPacketNumber());
	}

}
