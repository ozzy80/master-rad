package com.kikkar.network.impl;

import com.kikkar.packet.ControlMessage;
import com.kikkar.packet.HaveMessage;
import com.kikkar.packet.KeepAliveMessage;
import com.kikkar.packet.NotInterestedMessage;
import com.kikkar.packet.PacketWrapper;
import com.kikkar.packet.PingMessage;
import com.kikkar.packet.PongMessage;
import com.kikkar.packet.RequestMessage;
import com.kikkar.packet.RequestVideoMessage;
import com.kikkar.packet.ResponseMessage;
import com.kikkar.packet.ResponseVideoMessage;
import com.kikkar.packet.TerminatedMessage;
import com.kikkar.packet.VideoPacket;

public class MessageWrapper {

	private static PacketWrapper.Builder setupPacketWrapper(PeerInformation peerInformation) {
		PacketWrapper.Builder wrap = PacketWrapper.newBuilder();
		wrap.setPacketId(peerInformation.getLastSentPacketNumber());
		peerInformation.incrementLastSentPacketNumber();

		return wrap;
	}

	public static PacketWrapper wrapMessage(ControlMessage message, PeerInformation peerInformation) {
		PacketWrapper.Builder wrap = setupPacketWrapper(peerInformation);
		wrap.setControlMessage(message);

		return wrap.build();
	}

	public static PacketWrapper wrapMessage(HaveMessage message, PeerInformation peerInformation) {
		PacketWrapper.Builder wrap = setupPacketWrapper(peerInformation);
		wrap.setHaveMessage(message);

		return wrap.build();
	}

	public static PacketWrapper wrapMessage(KeepAliveMessage message, PeerInformation peerInformation) {
		PacketWrapper.Builder wrap = setupPacketWrapper(peerInformation);
		wrap.setKeepAliveMessage(message);

		return wrap.build();
	}

	public static PacketWrapper wrapMessage(NotInterestedMessage message, PeerInformation peerInformation) {
		PacketWrapper.Builder wrap = setupPacketWrapper(peerInformation);
		wrap.setNotInterestedMessage(message);

		return wrap.build();
	}

	public static PacketWrapper wrapMessage(PingMessage message, PeerInformation peerInformation) {
		PacketWrapper.Builder wrap = setupPacketWrapper(peerInformation);
		wrap.setPingMessage(message);

		return wrap.build();
	}

	public static PacketWrapper wrapMessage(PongMessage message, PeerInformation peerInformation) {
		PacketWrapper.Builder wrap = setupPacketWrapper(peerInformation);
		wrap.setPongMessage(message);

		return wrap.build();
	}

	public static PacketWrapper wrapMessage(RequestMessage message, PeerInformation peerInformation) {
		PacketWrapper.Builder wrap = setupPacketWrapper(peerInformation);
		wrap.setRequestMessage(message);

		return wrap.build();
	}

	public static PacketWrapper wrapMessage(RequestVideoMessage message, PeerInformation peerInformation) {
		PacketWrapper.Builder wrap = setupPacketWrapper(peerInformation);
		wrap.setRequestVideoMessage(message);

		return wrap.build();
	}

	public static PacketWrapper wrapMessage(ResponseMessage message, PeerInformation peerInformation) {
		PacketWrapper.Builder wrap = setupPacketWrapper(peerInformation);
		wrap.setResponseMessage(message);

		return wrap.build();
	}

	public static PacketWrapper wrapMessage(ResponseVideoMessage message, PeerInformation peerInformation) {
		PacketWrapper.Builder wrap = setupPacketWrapper(peerInformation);
		wrap.setResponseVideoMessage(message);

		return wrap.build();
	}

	public static PacketWrapper wrapMessage(TerminatedMessage message, PeerInformation peerInformation) {
		PacketWrapper.Builder wrap = setupPacketWrapper(peerInformation);
		wrap.setTerminatedMessage(message);

		return wrap.build();
	}

	public static PacketWrapper wrapMessage(VideoPacket message, PeerInformation peerInformation) {
		PacketWrapper.Builder wrap = setupPacketWrapper(peerInformation);
		wrap.setVideoPacket(message);

		return wrap.build();
	}

}
