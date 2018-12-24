package com.kikkar.video.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.kikkar.global.Constants;
import com.kikkar.global.SharingBufferSingleton;
import com.kikkar.network.impl.ConnectionManagerImpl;
import com.kikkar.network.impl.ConnectionManagerSourceImpl;
import com.kikkar.network.impl.DummyObjectCreator;
import com.kikkar.network.impl.PeerConnectorImpl;
import com.kikkar.network.impl.PeerInformation;
import com.kikkar.schedule.impl.UploadSchedulerSourceImpl;

class SourceVideoLoaderImplTest {

	private SourceVideoLoaderImpl sourceVideoLoaderImpl;
	private SharingBufferSingleton sharingBufferSingleton = SharingBufferSingleton.getInstance();
	private byte[] file = new String(
			"csdiqj68ze787eu1h2flnb2n80mscgk3aerhoog7oqgxis2vrmm07acdc1jzcqahoj8mr07crrognje0lzz6ento06g8pc1fg7na9usdlm44ru\recz1pvuo2m8cq4g64ri9\r\n"
					+ "eggud9ccvy5t61q3txpb\rshrok61mjttccoreev81lyry7syutamjm0yoqqv6wkks87p096rqn54tgcg9eq8ji4jhtn2pnzzm04g39uc3nz53k2d2io1w9i3rit9cuikqfh"
					+ "4mksh23l3v90y4zbbb4ns0vigxrhd3kwqqzbm6yxzhnvdzkcurvtqjwt55f1yzv1nfeyg843uqenaom2ifpqp5oadfihs61jajfh0wl7pscbvogeq9ikeuxpxjzntx0k25"
					+ "exum6h7s9gxma251u9z7r8noz9hjjyci8czw0vdyhf4rym0glrvp8lk3mjok2i91t454egin1vjzrx58nffyr9yzvwegidkparn7wr64pi6jtxm0hzfazw50e395p1u3cc\r\n"
					+ "70sehu92kf06n5qwx211c29rtm4qkdtrqu3uty72q263bxlsizk6fjbbvmvb6ey9viqikvezb1zzimlhnsqglijipf4olig4n36hll5nmjeyfj92aqkzvci7se72tirp09\r\n"
					+ "fmoodgi6zmmgf8gc14uw0g6jro9mc103kyd3rxqj2z7j2o6cudeo4a1nhuk9fbi7cee75yd1gemh14sy2aoyzcf3ucwbumixlyligir8nxsff4\r\n7qg5vc11trww0fa9955d\r\n"
					+ "1trr5zyf60tliq36be13x2kew63t6ow0yakswtduq87qjusi0l8ns0irmkocj3watfzzq20dm3wdmfjq1avxdwbxf07ym1t9ljxis60f6ob5nuqiwdg6wudihbfjxmon9n0fmo6gumyq"
					+ "acb8b4i7aji3c6jm6y5q0csjeyqixvhy97wwo8wd50fp7n7kacoth18ver4mjt7g00fm34ml5xb7okoxssa5d1asaqixvhy97wwo8wd50fp7n7kacoth18ver4mjt7g00fm34ml5xb7ok"
					+ "acb8b4i7aji3c6jm6y5q0csjeyqixvhy97wwo8wd50fp7n7kacoth18ver4mjt7g00fm34ml5xb7okoxssa5d1asaqixvhy97wwo8wd50fp7n7kacoth18ver4mjt7oxssa5d1asaacb8b4i7aji3c6jm6y5q0csjeyqixvhy97wwo8wd50fp7n7kacoth18ver4mjt7g00fm34ml5xb7okoxssa5d1asaqixvhy97wwo8wd50fp7n7kacoth18ver4mjt7g00fm34ml5xb7ok"
					+ "acb8b4i7aji3c6jm6y5q0csjeyqixvhy97wwo8wd50fp7n7kacoth18ver4mjt7g00fm34ml5ddsxb7okoxssac5n7d1asaqixv").getBytes();

	@BeforeEach
	void setup() throws SocketException {
		sourceVideoLoaderImpl = new SourceVideoLoaderImpl();
		sourceVideoLoaderImpl.setVideoDutarionMillisecond(1000);
		UploadSchedulerSourceImpl uploadScheduler = new UploadSchedulerSourceImpl();
		ConnectionManagerSourceImpl connectionManagerImpl = new ConnectionManagerSourceImpl();
		connectionManagerImpl.setPeerList(DummyObjectCreator.createDummyPeers(12, 12, 24));
		connectionManagerImpl.setSocket(new DatagramSocket());
		PeerConnectorImpl peerConnector = new PeerConnectorImpl();
		peerConnector.setThisPeer(new PeerInformation("192.168.0.5".getBytes(), 523, (short) 2));
		connectionManagerImpl.setPeerConnector(peerConnector);
		uploadScheduler.setConnectionManager(connectionManagerImpl);
		sourceVideoLoaderImpl.setUploadScheduler(uploadScheduler);
	}

	private byte[] repeat(byte[] array, int times) {
		byte[] repeated = new byte[times * array.length];
		for (int dest = 0; dest < repeated.length; dest += array.length) {
			System.arraycopy(array, 0, repeated, dest, array.length);
		}
		return repeated;
	}

	@Test
	void testReadChunk_checkFirstFrameChange() throws IOException {
		int size = 5;
		InputStream is = new ByteArrayInputStream(repeat(file, size));

		sourceVideoLoaderImpl.readChunk(is, 10);

		assertTrue(sharingBufferSingleton.getVideoPacket(0).getFirstFrame());
		assertFalse(sharingBufferSingleton.getVideoPacket(1).getFirstFrame());
	}

	@Test
	void testReadChunk_checkChunkNumberChange() throws IOException {
		int size = 5;
		InputStream is = new ByteArrayInputStream(repeat(file, size));

		sourceVideoLoaderImpl.readChunk(is, 10);

		assertEquals(10, sharingBufferSingleton.getVideoPacket(0).getChunkNum());
		assertEquals(9, sharingBufferSingleton.getVideoPacket(1).getChunkNum());
	}

	@Test
	void testReadChunk_checkVideoNumberChange() throws IOException {
		int size = 5;
		InputStream is = new ByteArrayInputStream(repeat(file, size));

		sourceVideoLoaderImpl.readChunk(is, 10);

		assertEquals(0, sharingBufferSingleton.getVideoPacket(0).getVideoNum());
		assertEquals(1, sharingBufferSingleton.getVideoPacket(1).getVideoNum());
	}

	@ParameterizedTest(name = "{index} => videoNum={0}, chunkNum={1}")
	@CsvSource({ "0, 1", "0, 30", "3800, 5400", "541231, 240", "12542, 3584", "0, 0" })
	void testAddVideoToBuffer_checkVideoNumAndChunkNumChange(int videoNum, int chunkNum) {
		sourceVideoLoaderImpl.setVideoNum(videoNum);

		sourceVideoLoaderImpl.addVideoToBuffer(file, false, chunkNum);

		assertEquals(videoNum, sharingBufferSingleton.getVideoPacket(videoNum).getVideoNum());
		assertEquals(chunkNum, sharingBufferSingleton.getVideoPacket(videoNum).getChunkNum());
		assertArrayEquals(file, sharingBufferSingleton.getVideoPacket(videoNum).getVideo().toByteArray());
	}

}
