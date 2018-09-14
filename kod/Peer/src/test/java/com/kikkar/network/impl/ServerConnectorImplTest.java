package com.kikkar.network.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import com.google.gson.JsonSyntaxException;
import com.kikkar.network.SpeedTest;

class ServerConnectorImplTest {

	private ServerConnectorImpl serverConnectorImpl;

	@BeforeEach
	void setup() {
		serverConnectorImpl = new ServerConnectorImpl();
	}

	@Test
	void testLoadJson_checkIsChannelProperlySet() {
		Channel channelExpected = new Channel();
		channelExpected.setBitrate(1500l);
		channelExpected.setChannelId(1l);
		channelExpected.setChunkSize(1500);
		channelExpected.setDescription(null);
		channelExpected.setIpAddress("http://192.168.0.171:8080/Tracker");
		channelExpected.setName("BBC");

		String json = "{\"channelId\":1,\"chunkSize\":1500,\"bitrate\":1500,\"name\":\"BBC\",\"description\":null,\"ipAddress\":\"http://192.168.0.171:8080/Tracker\"}";
		Channel channelActual = serverConnectorImpl.loadJson(json);

		assertEquals(channelExpected, channelActual);
	}

	@Test
	void testLoadJson_checkJsonWithMissingFields() {
		Channel channelExpected = new Channel();
		channelExpected.setChannelId(1l);
		channelExpected.setChunkSize(1500);
		channelExpected.setIpAddress("http://192.168.0.171:8080/Tracker");

		String json = "{\"channelId\":1,\"chunkSize\":1500,\"description\":null,\"ipAddress\":\"http://192.168.0.171:8080/Tracker\"}";
		Channel channelActual = serverConnectorImpl.loadJson(json);

		assertEquals(channelExpected, channelActual);
	}

	@Test
	void testLoadJson_checkWithBadJson() {
		String json = "{\"channelId\":1chunkSize\":1500,\"description\":null,\"ipAddress\":\"http://192.168.0.171:8080/Tracker\"}";

		assertThrows(JsonSyntaxException.class, () -> {
			serverConnectorImpl.loadJson(json);
		});
	}

	@ParameterizedTest(name = "{index} => ipAddress={0}, channelId={1}")
	@CsvSource({ "http://192.168.0.171:8080/Tracker, 1", "http://192.168.0.171:8080/Tracker, 2400",
			"http://localhost//Tracker, 540000", "https://www.google.rs/, 445632" })
	void testCreateConnectURL_checkURLWithDifferentChannels(String ipAddress, long channelId) throws MalformedURLException {
		URL urlExpected = new URL(
				ipAddress + "/connect/initial/" + channelId + "?protocol=P2Pv.1&download=2500&upload=2500");

		Channel channel = new Channel();
		channel.setChannelId(channelId);
		channel.setIpAddress(ipAddress);
		SpeedTest speedTest = new SpeedTest() {
			@Override
			public Long uploadSpeedTest(int measureTimeMilliseconds, int additionallyWaitForDataMilliseconds) {
				return 2500l;
			}

			@Override
			public Long downloadSpeedTest(int measureTimeMilliseconds, int additionallyWaitForDataMilliseconds) {
				return 2500l;
			}
		};

		serverConnectorImpl.setChannel(channel);
		URL urlActual = serverConnectorImpl.createConnectURL(speedTest);

		assertEquals(urlExpected, urlActual);
	}

	@ParameterizedTest(name = "{index} => download={0}, upload={1}")
	@CsvSource({ "2500, 1800", "2400, 2400", "3800, 5400" })
	void testCreateURL_checkURLWithDifferentSpeed(long download, long upload) throws MalformedURLException {
		URL urlExpected = new URL("http://192.168.0.171:8080/Tracker/connect/initial/1?protocol=P2Pv.1&download="
				+ download + "&upload=" + upload);

		Channel channel = new Channel();
		channel.setBitrate(1500l);
		channel.setChannelId(1l);
		channel.setChunkSize(1500);
		channel.setDescription("BBC BBC BBC BBC BBC BBC BBC BBC BBC BBC BBC BBC BBC BBC BBC BBC");
		channel.setIpAddress("http://192.168.0.171:8080/Tracker");
		channel.setName("BBC");
		SpeedTest speedTest = new SpeedTest() {
			@Override
			public Long uploadSpeedTest(int measureTimeMilliseconds, int additionallyWaitForDataMilliseconds) {
				return upload;
			}

			@Override
			public Long downloadSpeedTest(int measureTimeMilliseconds, int additionallyWaitForDataMilliseconds) {
				return download;
			}
		};

		serverConnectorImpl.setChannel(channel);
		URL urlActual = serverConnectorImpl.createConnectURL(speedTest);

		assertEquals(urlExpected, urlActual);
	}

	// Mock ima bag i ne moze da se testira
/*	@Test
	void testConnectToServer() throws IOException {
		URL url = Mockito.mock(URL.class);
		HttpURLConnection connect = Mockito.mock(HttpURLConnection.class);
		String testJSON = "1234";

		Mockito.when(url.openConnection()).thenReturn(connect);
		Mockito.when(connect.getResponseCode()).thenReturn(202);
		Mockito.when(connect.getInputStream()).thenReturn(new ByteArrayInputStream(testJSON.getBytes()));
	}
*/

}
