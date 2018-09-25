package com.kikkar.network.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.kikkar.packet.ConnectionType;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.PongMessage;

public class DummyObjectCreator {

	public static List<PeerInformation> createDummyPeers(int uploadLinkNum, int downloadLinkNum, int otherLinkNum) {
		List<PeerInformation> peerInformations = new ArrayList<>();
		PeerInformation peerInformation = null;
		String ip = null;
		int portNum = 54321;

		for (int i = 0; i < downloadLinkNum; i++) {
			ip = "192.168.0." + (i + 1);
			peerInformation = new PeerInformation(ip.getBytes(), portNum, (short) (i % 6));
			peerInformation.setPeerStatus(PeerStatus.DOWNLOAD_CONNECTION);
			peerInformations.add(peerInformation);
		}

		for (int i = downloadLinkNum; i < uploadLinkNum + downloadLinkNum; i++) {
			ip = "192.168.0." + (i + 1);
			peerInformation = new PeerInformation(ip.getBytes(), portNum, (short) (i % 6));
			peerInformation.setPeerStatus(PeerStatus.UPLOAD_CONNECTION);
			peerInformations.add(peerInformation);
		}

		for (int i = uploadLinkNum + downloadLinkNum; i < uploadLinkNum + downloadLinkNum + otherLinkNum; i++) {
			ip = "192.168.0." + (i + otherLinkNum);
			peerInformation = new PeerInformation(ip.getBytes(), portNum, (short) (i % 6));
			peerInformation.setPeerStatus(PeerStatus.NOT_CONTACTED);
			peerInformations.add(peerInformation);

			ip = "192.168.0." + (i + 2 * otherLinkNum);
			peerInformation = new PeerInformation(ip.getBytes(), portNum, (short) (i % 6));
			peerInformation.setPeerStatus(PeerStatus.PONG_WAIT_DOWNLOAD);
			peerInformations.add(peerInformation);

			ip = "192.168.0." + (i + 3 * otherLinkNum);
			peerInformation = new PeerInformation(ip.getBytes(), portNum, (short) (i % 6));
			peerInformation.setPeerStatus(PeerStatus.PONG_WAIT_UPLOAD);
			peerInformations.add(peerInformation);

			ip = "192.168.0." + (i + 4 * otherLinkNum);
			peerInformation = new PeerInformation(ip.getBytes(), portNum, (short) (i % 6));
			peerInformation.setPeerStatus(PeerStatus.RESPONSE_WAIT_DOWNLOAD);
			peerInformations.add(peerInformation);

			ip = "192.168.0." + (i + 5 * otherLinkNum);
			peerInformation = new PeerInformation(ip.getBytes(), portNum, (short) (i % 6));
			peerInformation.setPeerStatus(PeerStatus.RESPONSE_WAIT_UPLOAD);
			peerInformations.add(peerInformation);
		}
		return peerInformations;
	}

	public static Map<String, PongMessage> createDummyPongMessageMap(List<PeerInformation> peerInformations, PeerConnectorImpl peerConnectorImpl) throws IOException { 
		Map<String, PongMessage> result = new HashMap<>(); 
	 
		List<Integer> uploadLinkNum = new ArrayList<>(); 
		List<Integer> downloadLinkNum = new ArrayList<>(); 
		List<Integer> bufferVideoNum = new ArrayList<>(); 
		for (int i = 0; i < peerInformations.size(); i++) {
			uploadLinkNum.add(i % 4); 
			downloadLinkNum.add(i % 4); 
			bufferVideoNum.add((i %20) + 10); 
		} 
		
		Collections.shuffle(uploadLinkNum, new Random(1));
		Collections.shuffle(downloadLinkNum, new Random(1));
		Collections.shuffle(bufferVideoNum, new Random(1));
	 
		DatagramPacket packet;
		DatagramPacket pong;
		for (int i = 0; i < peerInformations.size() - 1; i += 2) { 
			packet = peerConnectorImpl.createPingMessage(peerInformations.get(0), peerInformations.get(0).getClubNumber(), ConnectionType.DOWNLOAD); 
			pong = peerConnectorImpl.createPongMessage(peerInformations.get(i), uploadLinkNum.get(i), downloadLinkNum.get(i), bufferVideoNum.get(i), PacketWrapper.parseFrom(packet.getData()).getPingMessage()); 
			result.put(new String(peerInformations.get(i).getIpAddress()), PacketWrapper.parseFrom(pong.getData()).getPongMessage());
	 
			packet = peerConnectorImpl.createPingMessage(peerInformations.get(0), peerInformations.get(0).getClubNumber(), ConnectionType.UPLOAD); 
			pong = peerConnectorImpl.createPongMessage(peerInformations.get(i + 1), uploadLinkNum.get(i + 1), downloadLinkNum.get(i + 1), bufferVideoNum.get(i + 1), PacketWrapper.parseFrom(packet.getData()).getPingMessage());
			result.put(new String(peerInformations.get(i + 1).getIpAddress()), PacketWrapper.parseFrom(pong.getData()).getPongMessage());
		}
		
		return result; 
	}
}
