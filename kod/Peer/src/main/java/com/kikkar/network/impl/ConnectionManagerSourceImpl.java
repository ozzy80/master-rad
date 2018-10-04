package com.kikkar.network.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.kikkar.global.Constants;
import com.kikkar.packet.ConnectionType;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.Pair;

public class ConnectionManagerSourceImpl extends ConnectionManagerImpl {
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private int threadWaitSecond = Constants.DATA_WAIT_SECOND;

	@Override
	public void maintainClubsConnection() {
		int uploadConnectionNum;
		for (int i = 0; i < Constants.NUMBER_OF_CLUB; i++) {
			uploadConnectionNum = super.peerConnectionNum(i, PeerStatus.UPLOAD_CONNECTION);
			super.maintainInnerClubConnection(uploadConnectionNum, ConnectionType.UPLOAD, (short) i);
		}
		executor.schedule(() -> super.deleteOldPongMessages(super.getPongMessageMap().keySet()), threadWaitSecond + 1,
				TimeUnit.SECONDS);
	}

	@Override
	public void processPacket(Pair<String, PacketWrapper> packetPair) {
		super.updateLastTimeReciveMessage(packetPair);
		super.updateLastReceiveMessageNum(packetPair);

		if (packetPair.getRight().hasPingMessage()) {
			if (packetPair.getRight().getPingMessage().getConnectionType().equals(ConnectionType.UPLOAD)) {
				PeerInformation peer = getPeer(packetPair.getLeft());
				if (peer != null) {
					super.getPeerConnector().sendPongMessage(super.getPeerList(), peer,
							packetPair.getRight().getPingMessage(), super.getSocket());
				}
			}
		} else if (packetPair.getRight().hasPongMessage()) {
			super.getPongMessageMap().put(packetPair.getLeft(), packetPair.getRight().getPongMessage());
		} else if (packetPair.getRight().hasRequestMessage()) {
			if (packetPair.getRight().getRequestMessage().getConnectionType().equals(ConnectionType.UPLOAD)) {
				PeerInformation peer = getPeer(packetPair.getLeft());
				if (peer != null) {
					super.getPeerConnector().sendResponseMessage(peer, packetPair.getRight(), super.getSocket());
					super.updateLastTimeSentMessage(peer);
				}
			}
		} else if (packetPair.getRight().hasResponseMessage()) {
			PeerInformation peer = getPeer(packetPair.getLeft());
			if (peer != null) {
				assumePeerAreConnected(peer);
			}
		} else if (packetPair.getRight().hasTerminatedMessage()) {
			removeConnection(packetPair);
		} else if (packetPair.getRight().hasKeepAliveMessage()) {
			// All is done upper with updateLastTimeReciveMessage()
		} else {
			super.getPacketsForHigherLevel().add(packetPair);
		}
	}

	public void setThreadWaitSecond(int threadWaitSecond) {
		this.threadWaitSecond = threadWaitSecond;
	}

}
