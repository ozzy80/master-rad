package com.kikkar.network.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.http.HTTPException;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.kikkar.network.SpeedTest;

import javafx.beans.binding.SetExpression;

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

	@ParameterizedTest(name = "{index} => download={0}, upload={1}")
	@CsvSource({ "2500, 1800", "2400, 2400", "3800, 5400", "0, 240", "0, 0", "25000, 0" })
	void testCreateConnectionParamerets_checkParametersWithDifferentSpeed(Long download, Long upload)
			throws MalformedURLException {
		Map<String, String> parametersExpected = new HashMap<>();
		parametersExpected.put("protocol", "P2Pv.1");
		parametersExpected.put("download", download.toString());
		parametersExpected.put("upload", upload.toString());

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
		Map<String, String> parametersActual = serverConnectorImpl.createConnectionParamerets(speedTest);

		assertEquals(parametersExpected, parametersActual);
	}

	@Test
	void testCreateURL_checkURL() throws MalformedURLException {
		URL urlExpected = new URL(
				"http://192.168.0.15/Tracker/connect/initial/1?protocol=P2Pv.1&download=2500&upload=2500");

		String baseURL = "http://192.168.0.15/Tracker/connect/initial/1";
		Map<String, String> parameters = new HashMap<>();
		parameters.put("protocol", "P2Pv.1");
		parameters.put("download", "2500");
		parameters.put("upload", "2500");
		URL urlActual = serverConnectorImpl.createURL(baseURL, parameters);

		assertEquals(urlExpected, urlActual);
	}

	@Test
	void testConnectToServer_checkSuccessfulResponse() throws IOException {
		Short tokenExpected = 15;

		final HttpURLConnection connect = Mockito.mock(HttpURLConnection.class);
		String testJSON = "15";
		URLStreamHandler stubURLStreamHandler = new URLStreamHandler() {
			@Override
			protected URLConnection openConnection(URL u) throws IOException {
				return connect;
			}
		};

		Mockito.when(connect.getResponseCode()).thenReturn(202);
		Mockito.when(connect.getInputStream()).thenReturn(new ByteArrayInputStream(testJSON.getBytes()));
		URL url = new URL(null,
				"http://192.168.0.15/Tracker/connect/initial/1?protocol=P2Pv.1&download=2500&upload=2500",
				stubURLStreamHandler);
		Short tokenActual = serverConnectorImpl.connectToServer(url);

		assertEquals(tokenExpected, tokenActual);
	}

	@ParameterizedTest
	@ValueSource(ints = { 404, 505, 509 })
	void testConnectToServer_checkFailedResponse(int responseCode) throws IOException {
		final HttpURLConnection connect = Mockito.mock(HttpURLConnection.class);
		String testJSON = "15";
		URLStreamHandler stubURLStreamHandler = new URLStreamHandler() {
			@Override
			protected URLConnection openConnection(URL u) throws IOException {
				return connect;
			}
		};

		Mockito.when(connect.getResponseCode()).thenReturn(responseCode);
		Mockito.when(connect.getInputStream()).thenReturn(new ByteArrayInputStream(testJSON.getBytes()));
		URL url = new URL(null,
				"http://192.168.0.15/Tracker/connect/initial/1?protocol=P2Pv.1&download=2500&upload=2500",
				stubURLStreamHandler);

		assertThrows(HTTPException.class, () -> {
			serverConnectorImpl.connectToServer(url);
		});
	}

	@Test
	void testGetPeerInfoList_checkSuccessfulResponse() throws IOException {
		List<PeerInformation> peerInformationExpected = new ArrayList<>();
		peerInformationExpected.add(new PeerInformation("192.168.0.2".getBytes(), 52315, (short) 0));
		peerInformationExpected.add(new PeerInformation("192.168.0.3".getBytes(), 56514, (short) 1));
		peerInformationExpected.add(new PeerInformation("192.168.0.4".getBytes(), 3215, (short) 2));

		final HttpURLConnection connect = Mockito.mock(HttpURLConnection.class);
		String responseJson = new GsonBuilder().create().toJson(peerInformationExpected);
		URLStreamHandler stubURLStreamHandler = new URLStreamHandler() {
			@Override
			protected URLConnection openConnection(URL u) throws IOException {
				return connect;
			}
		};

		Mockito.when(connect.getResponseCode()).thenReturn(200);
		Mockito.when(connect.getInputStream()).thenReturn(new ByteArrayInputStream(responseJson.getBytes()));
		URL url = new URL(null, "http://192.168.0.15/Tracker/connect/list/1?protocol=P2Pv.1&download=2500&upload=2500",
				stubURLStreamHandler);
		List<PeerInformation> peerInformationActual = serverConnectorImpl.getPeerInfoList(url);

		assertEquals(peerInformationExpected, peerInformationActual);
	}

	@ParameterizedTest
	@ValueSource(ints = {400, 412, 404, 505, 509 })
	void testGetPeerInfoList_checkFailedResponse(int responseCode) throws IOException {
		final HttpURLConnection connect = Mockito.mock(HttpURLConnection.class);
		String responseJson = "1";
		URLStreamHandler stubURLStreamHandler = new URLStreamHandler() {
			@Override
			protected URLConnection openConnection(URL u) throws IOException {
				return connect;
			}
		};

		Mockito.when(connect.getResponseCode()).thenReturn(responseCode);
		Mockito.when(connect.getInputStream()).thenReturn(new ByteArrayInputStream(responseJson.getBytes()));
		URL url = new URL(null,
				"http://192.168.0.15/Tracker/connect/initial/1?protocol=P2Pv.1&download=2500&upload=2500",
				stubURLStreamHandler);

		assertThrows(HTTPException.class, () -> {
			serverConnectorImpl.getPeerInfoList(url);
		});
		
	}

	@ParameterizedTest
	@ValueSource(ints = {400, 412, 404, 505})
	void testSendStayAliveMessage_checkFailedResponse(int responseCode) throws IOException {
		final HttpURLConnection connect = Mockito.mock(HttpURLConnection.class);
		String responseJSON = "Goodbye";
		URLStreamHandler stubURLStreamHandler = new URLStreamHandler() {
			@Override
			protected URLConnection openConnection(URL u) throws IOException {
				return connect;
			}
		};

		Mockito.when(connect.getResponseCode()).thenReturn(responseCode);
		Mockito.when(connect.getInputStream()).thenReturn(new ByteArrayInputStream(responseJSON.getBytes()));
		URL url = new URL(null,
				"http://192.168.0.15/Tracker/connect/stayAlive",
				stubURLStreamHandler);

		assertThrows(HTTPException.class, () -> {
			serverConnectorImpl.sendStayAliveMessage(url);
		});
	}
	
	@ParameterizedTest
	@ValueSource(ints = {400, 412, 404, 505})
	void testSendLeaveMessage_checkFailedResponse(int responseCode) throws IOException {
		final HttpURLConnection connect = Mockito.mock(HttpURLConnection.class);
		String responseJSON = "Goodbye";
		URLStreamHandler stubURLStreamHandler = new URLStreamHandler() {
			@Override
			protected URLConnection openConnection(URL u) throws IOException {
				return connect;
			}
		};

		Mockito.when(connect.getResponseCode()).thenReturn(responseCode);
		Mockito.when(connect.getInputStream()).thenReturn(new ByteArrayInputStream(responseJSON.getBytes()));
		URL url = new URL(null,
				"http://192.168.0.15/Tracker/connect/leave",
				stubURLStreamHandler);

		assertThrows(HTTPException.class, () -> {
			serverConnectorImpl.sendLeaveMessage(url);
		});
	}
}
