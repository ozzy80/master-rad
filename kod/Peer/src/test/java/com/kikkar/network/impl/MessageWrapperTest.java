package com.kikkar.network.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

class MessageWrapperTest {

	private PeerInformation peerInformation;

	@BeforeEach
	void setup() {
		peerInformation = new PeerInformation("192.168.0.2".getBytes(), 54321, (short) 0);
	}

	@ParameterizedTest
	@MethodSource("createDifferentIntegerNumberWithBoundaries")
	void wrapMessageTest_checkSentPacketNumberRotation(int messageId, int messageIdExpected) {
		ControlMessage message = ControlMessage.newBuilder().setMessageId(0).build();
		peerInformation.setLastSentPacketNumber(messageId);
		MessageWrapper.wrapMessage(message, peerInformation);

		assertEquals(messageIdExpected, peerInformation.getLastSentPacketNumber());
	}

	private static Stream<Arguments> createDifferentIntegerNumberWithBoundaries() {
		return Stream.of(Arguments.of(0, 1), 
				Arguments.of(5415, 5416),
				Arguments.of(-5621, -5620),
				Arguments.of(8542, 8543), 
				Arguments.of(-82017, -82016),
				Arguments.of(Integer.MAX_VALUE, Integer.MIN_VALUE),
				Arguments.of(Integer.MIN_VALUE, Integer.MIN_VALUE + 1));
	}

	@Test
	void wrapMessageTest_checkControlMessageWrapper() {
		int messageId = 0;

		ControlMessage message = ControlMessage.newBuilder().build();
		PacketWrapper wrapExpected = PacketWrapper.newBuilder().setControlMessage(message).setPacketId(messageId)
				.build();
		PacketWrapper wrapActual = MessageWrapper.wrapMessage(message, peerInformation);

		assertEquals(wrapExpected, wrapActual);
	}

	@Test
	void wrapMessageTest_checkHaveMessageWrapper() {
		int messageId = 0;

		HaveMessage message = HaveMessage.newBuilder().build();
		PacketWrapper wrapExpected = PacketWrapper.newBuilder().setHaveMessage(message).setPacketId(messageId).build();
		PacketWrapper wrapActual = MessageWrapper.wrapMessage(message, peerInformation);

		assertEquals(wrapExpected, wrapActual);
	}

	@Test
	void wrapMessageTest_checkKeepAliveMessageWrapper() {
		int messageId = 0;

		KeepAliveMessage message = KeepAliveMessage.newBuilder().build();
		PacketWrapper wrapExpected = PacketWrapper.newBuilder().setKeepAliveMessage(message).setPacketId(messageId)
				.build();
		PacketWrapper wrapActual = MessageWrapper.wrapMessage(message, peerInformation);

		assertEquals(wrapExpected, wrapActual);
	}

	@Test
	void wrapMessageTest_checkNotInterestedMessageWrapper() {
		int messageId = 0;

		NotInterestedMessage message = NotInterestedMessage.newBuilder().build();
		PacketWrapper wrapExpected = PacketWrapper.newBuilder().setNotInterestedMessage(message).setPacketId(messageId)
				.build();
		PacketWrapper wrapActual = MessageWrapper.wrapMessage(message, peerInformation);

		assertEquals(wrapExpected, wrapActual);
	}

	@Test
	void wrapMessageTest_checkPingMessageWrapper() {
		int messageId = 0;

		PingMessage message = PingMessage.newBuilder().build();
		PacketWrapper wrapExpected = PacketWrapper.newBuilder().setPingMessage(message).setPacketId(messageId).build();
		PacketWrapper wrapActual = MessageWrapper.wrapMessage(message, peerInformation);

		assertEquals(wrapExpected, wrapActual);
	}

	@Test
	void wrapMessageTest_checkPongMessageWrapper() {
		int messageId = 0;

		PongMessage message = PongMessage.newBuilder().build();
		PacketWrapper wrapExpected = PacketWrapper.newBuilder().setPongMessage(message).setPacketId(messageId).build();
		PacketWrapper wrapActual = MessageWrapper.wrapMessage(message, peerInformation);

		assertEquals(wrapExpected, wrapActual);
	}

	@Test
	void wrapMessageTest_checkRequestMessageWrapper() {
		int messageId = 0;

		RequestMessage message = RequestMessage.newBuilder().build();
		PacketWrapper wrapExpected = PacketWrapper.newBuilder().setRequestMessage(message).setPacketId(messageId)
				.build();
		PacketWrapper wrapActual = MessageWrapper.wrapMessage(message, peerInformation);

		assertEquals(wrapExpected, wrapActual);
	}

	@Test
	void wrapMessageTest_checkRequestVideoMessageWrapper() {
		int messageId = 0;

		RequestVideoMessage message = RequestVideoMessage.newBuilder().build();
		PacketWrapper wrapExpected = PacketWrapper.newBuilder().setRequestVideoMessage(message).setPacketId(messageId)
				.build();
		PacketWrapper wrapActual = MessageWrapper.wrapMessage(message, peerInformation);

		assertEquals(wrapExpected, wrapActual);
	}

	@Test
	void wrapMessageTest_checkResponseMessageWrapper() {
		int messageId = 0;

		ResponseMessage message = ResponseMessage.newBuilder().build();
		PacketWrapper wrapExpected = PacketWrapper.newBuilder().setResponseMessage(message).setPacketId(messageId)
				.build();
		PacketWrapper wrapActual = MessageWrapper.wrapMessage(message, peerInformation);

		assertEquals(wrapExpected, wrapActual);
	}

	@Test
	void wrapMessageTest_checkResponseVideoMessageWrapper() {
		int messageId = 0;

		ResponseVideoMessage message = ResponseVideoMessage.newBuilder().build();
		PacketWrapper wrapExpected = PacketWrapper.newBuilder().setResponseVideoMessage(message).setPacketId(messageId)
				.build();
		PacketWrapper wrapActual = MessageWrapper.wrapMessage(message, peerInformation);

		assertEquals(wrapExpected, wrapActual);
	}

	@Test
	void wrapMessageTest_checkTerminatedMessageWrapper() {
		int messageId = 0;

		TerminatedMessage message = TerminatedMessage.newBuilder().build();
		PacketWrapper wrapExpected = PacketWrapper.newBuilder().setTerminatedMessage(message).setPacketId(messageId)
				.build();
		PacketWrapper wrapActual = MessageWrapper.wrapMessage(message, peerInformation);

		assertEquals(wrapExpected, wrapActual);
	}

	@Test
	void wrapMessageTest_checkVideoPacketWrapper() {
		int messageId = 0;

		VideoPacket message = VideoPacket.newBuilder().build();
		PacketWrapper wrapExpected = PacketWrapper.newBuilder().setVideoPacket(message).setPacketId(messageId).build();
		PacketWrapper wrapActual = MessageWrapper.wrapMessage(message, peerInformation);

		assertEquals(wrapExpected, wrapActual);
	}

}
