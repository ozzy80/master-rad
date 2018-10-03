package com.kikkar.global;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.google.protobuf.ByteString;
import com.kikkar.packet.VideoPacket;

class SharingBufferSingletonTest {

	private SharingBufferSingleton sharingBufferSingleton;
	private int MAX_ELEMENT_NUMBER = 6_000;
	private ByteString video;

	@BeforeEach
	void setup() {
		sharingBufferSingleton = SharingBufferSingleton.getInstance();
		sharingBufferSingleton.setMinVideoNum(0);
		sharingBufferSingleton.setMAX_ELEMENT_NUMBER(MAX_ELEMENT_NUMBER);
		sharingBufferSingleton.setVideoArray(new VideoPacket[MAX_ELEMENT_NUMBER]);
		video = ByteString.copyFrom(new String(
				"csdiqj68ze787eu1h2flnb2n80mscgk3aerhoog7oqgxis2vrmm07acdc1jzcqahoj8mr07crrognje0lzz6ento06g8pc1fg7na9usdlm44ru\recz1pvuo2m8cq4g64ri9\r\n"
						+ "eggud9ccvy5t61q3txpb\rshrok61mjttccoreev81lyry7syutamjm0yoqqv6wkks87p096rqn54tgcg9eq8ji4jhtn2pnzzm04g39uc3nz53k2d2io1w9i3rit9cuikqfh"
						+ "4mksh23l3v90y4zbbb4ns0vigxrhd3kwqqzbm6yxzhnvdzkcurvtqjwt55f1yzv1nfeyg843uqenaom2ifpqp5oadfihs61jajfh0wl7pscbvogeq9ikeuxpxjzntx0k25"
						+ "exum6h7s9gxma251u9z7r8noz9hjjyci8czw0vdyhf4rym0glrvp8lk3mjok2i91t454egin1vjzrx58nffyr9yzvwegidkparn7wr64pi6jtxm0hzfazw50e395p1u3cc\r\n"
						+ "70sehu92kf06n5qwx211c29rtm4qkdtrqu3uty72q263bxlsizk6fjbbvmvb6ey9viqikvezb1zzimlhnsqglijipf4olig4n36hll5nmjeyfj92aqkzvci7se72tirp09\r\n"
						+ "fmoodgi6zmmgf8gc14uw0g6jro9mc103kyd3rxqj2z7j2o6cudeo4a1nhuk9fbi7cee75yd1gemh14sy2aoyzcf3ucwbumixlyligir8nxsff4\r\n7qg5vc11trww0fa9955d\r\n"
						+ "1trr5zyf60tliq36be13x2kew63t6ow0yakswtduq87qjusi0l8ns0irmkocj3watfzzq20dm3wdmfjq1avxdwbxf07ym1t9ljxis60f6ob5nuqiwdg6wudihbfjxmon9n0fmo6gumyq"
						+ "acb8b4i7aji3c6jm6y5q0csjeyqixvhy97wwo8wd50fp7n7kacoth18ver4mjt7g00fm34ml5xb7okox")
								.getBytes());
	}

	@Test
	void testGetInstance_checkIsSharingBufferSingleton() {
		SharingBufferSingleton sharingBufferNew = SharingBufferSingleton.getInstance();

		assertEquals(sharingBufferNew, sharingBufferSingleton);
	}

	@ParameterizedTest
	@ValueSource(strings = { "0", "3", "25", "458721", "3216215" })
	void testAddVideoPacket_checkPacketAdd(int videoNum) {
		int chunkNum = 29654;
		VideoPacket videoPack = VideoPacket.newBuilder().setVideoNum(videoNum).setChunkNum(chunkNum).setVideo(video)
				.build();

		sharingBufferSingleton.addVideoPacket(videoNum, videoPack);

		assertEquals(videoPack, sharingBufferSingleton.getVideoPacket(videoNum));
		long notNullElements = Arrays.asList(sharingBufferSingleton.getVideoArray()).stream().filter(v -> v != null)
				.count();
		assertEquals(1, notNullElements);
	}

	@Test
	void testAddVideoPacket_checkNegativIndexException() {
		int videoNum = -485563;
		int chunkNum = 29654;
		VideoPacket videoPack = VideoPacket.newBuilder().setVideoNum(videoNum).setChunkNum(chunkNum).setVideo(video)
				.build();

		assertThrows(ArrayIndexOutOfBoundsException.class,
				() -> sharingBufferSingleton.addVideoPacket(videoNum, videoPack));
	}

	@ParameterizedTest
	@ValueSource(strings = { "1", "3", "25", "458721", "3216215" })
	void testIsVideoPresent_checkPresentVideo(int videoNum) {
		int chunkNum = 29654;
		VideoPacket videoPack = VideoPacket.newBuilder().setVideoNum(videoNum).setChunkNum(chunkNum).setVideo(video)
				.build();
		sharingBufferSingleton.addVideoPacket(videoNum, videoPack);

		assertTrue(sharingBufferSingleton.isVideoPresent(videoNum));
		assertFalse(sharingBufferSingleton.isVideoPresent(videoNum + 1));
		assertFalse(sharingBufferSingleton.isVideoPresent(videoNum - 1));
	}

	@Test
	void testIsVideoPresent_checkNotPresentVideo() {
		int videoNum = 1;
		int chunkNum = 29654;
		VideoPacket videoPack = VideoPacket.newBuilder().setVideoNum(videoNum).setChunkNum(chunkNum).setVideo(video)
				.build();
		sharingBufferSingleton.addVideoPacket(videoNum, videoPack);
		videoNum += 6000;

		assertFalse(sharingBufferSingleton.isVideoPresent(videoNum));
		assertFalse(sharingBufferSingleton.isVideoPresent(videoNum + 50));
	}

	@Test
	void testIsVideoPresent_checkNegativIndexException() {
		int videoNum = -485563;

		assertThrows(ArrayIndexOutOfBoundsException.class, () -> sharingBufferSingleton.isVideoPresent(videoNum));
	}

	@ParameterizedTest
	@ValueSource(strings = { "1", "3", "25", "458721", "3216215" })
	void testGetVideoPacket_checkVideoSave(int videoNum) {
		int chunkNum = 3215;
		VideoPacket videoPack = VideoPacket.newBuilder().setVideoNum(videoNum).setChunkNum(chunkNum).setVideo(video)
				.build();

		sharingBufferSingleton.addVideoPacket(videoNum, videoPack);

		assertEquals(videoPack, sharingBufferSingleton.getVideoPacket(videoNum));
	}

	@Test
	void testGetVideoPacket_checkNegativIndexException() {
		int videoNum = -485563;

		assertThrows(ArrayIndexOutOfBoundsException.class, () -> sharingBufferSingleton.getVideoPacket(videoNum));
	}

	private void setVideo(int startPosition, int numberOfIteration) {
		int chunkNum = numberOfIteration - startPosition;
		for (int videoNum = startPosition; videoNum < numberOfIteration; videoNum++) {
			VideoPacket videoPack = VideoPacket.newBuilder().setVideoNum(videoNum).setChunkNum(chunkNum--)
					.setVideo(video).build();
			sharingBufferSingleton.addVideoPacket(videoNum, videoPack);
		}

	}

	private byte[] repeat(byte[] array, int times) {
		byte[] repeated = new byte[times * array.length];
		for (int dest = 0; dest < repeated.length; dest += array.length) {
			System.arraycopy(array, 0, repeated, dest, array.length);
		}
		return repeated;
	}

	@Test
	void testSaveVideoPack_checkDefaultBehaviour() throws IOException {
		setVideo(0, 15);
		setVideo(15, 22);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		sharingBufferSingleton.saveVideoPack(baos);

		assertArrayEquals(repeat(video.toByteArray(), 15), baos.toByteArray());
		long onlySecondChunkExist = Arrays.asList(sharingBufferSingleton.getVideoArray()).stream()
				.filter(v -> v != null).count();
		assertEquals(8, onlySecondChunkExist);
	}
	
	@Test
	void testSaveVideoPack_checkInfiniteLoopEnd() throws IOException {
		setVideo(0, 10);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		sharingBufferSingleton.saveVideoPack(baos);

		assertTrue(true);
	}
	
	@Test
	void testGetNumberOfBufferedVideoContent_checkAllIsMissing() {
		sharingBufferSingleton.setVideoArray(new VideoPacket[10]);
		VideoPacket video = VideoPacket.newBuilder().setVideoNum(2).build();
		sharingBufferSingleton.addVideoPacket(2, video);
		
		assertEquals(1, sharingBufferSingleton.getNumberOfBufferedVideoContent());
	}

}
