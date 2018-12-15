package com.kikkar.network.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.http.HTTPException;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import com.google.gson.Gson;
import com.kikkar.global.ClockSingleton;
import com.kikkar.network.ServerConnector;
import com.kikkar.network.SpeedTest;

public class ServerConnectorImpl implements ServerConnector {
	private Channel channel;
	private ScheduledExecutorService executor;
	private String lastModified;
	
	public ServerConnectorImpl() {
		executor = Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	public Channel loadJson(String rawJson) {
		channel = new Gson().fromJson(rawJson, Channel.class);
		return channel;
	}

	@Override
	public Map<String, String> createConnectionParamerets(SpeedTest speedTest) throws MalformedURLException {
		Long downloadSpeed = speedTest.downloadSpeedTest(5000, 1000);
		Long uploadSpeed = speedTest.uploadSpeedTest(5000, 1000);

		Map<String, String> parameters = new HashMap<>();
		parameters.put("protocol", "P2Pv.1");
		parameters.put("download", downloadSpeed.toString());
		parameters.put("upload", uploadSpeed.toString());

		return parameters;
	}

	@Override
	public URL createURL(String baseURL, Map<String, String> parameters) throws MalformedURLException {
		String url = baseURL + getParamsString(parameters);
		return new URL(url);
	}

	private HttpURLConnection getURLConnection(URL url) throws IOException {
		HttpURLConnection connect = (HttpURLConnection) url.openConnection();
		connect.setRequestMethod("GET");
		connect.setDoOutput(true);
		connect.setDoInput(true);
		connect.setRequestProperty("User-Agent", "Mozilla/5.0");
		connect.setRequestProperty("Accept-Charset", "UTF-8");
		if(lastModified != null) {
			connect.setRequestProperty("If-Unmodified-Since", lastModified);
		}
		connect.setConnectTimeout(5000);
		connect.setReadTimeout(5000);

		return connect;
	}

	@Override
	public Short connectToServer(URL url) throws IOException {
		HttpURLConnection connect = getURLConnection(url);

		int status = connect.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();

		if (status == 202) {
			Short token = (short) Integer.parseInt(content.toString());
			return token;
		} else {
			throw new HTTPException(status);
		}
	}

	@Override
	public void synchronizeTime(NTPUDPClient client, String ntpServer) throws UnknownHostException {
		client.setDefaultTimeout(10000);
		ClockSingleton clock = ClockSingleton.getInstance();

		try {
			client.open();
			TimeInfo info;
			try {
				info = client.getTime(InetAddress.getByName(ntpServer));
				info.computeDetails();
				clock.setOffsetValue(info.getOffset());
			} catch (UnknownHostException e) {
				throw e;
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		} catch (SocketException e) {
			System.err.println(e.getMessage());
		} finally {
			client.close();
		}
	}

	public List<PeerInformation> getPeerInfoList(URL url) throws IOException {
		HttpURLConnection connect = getURLConnection(url);
		int status = connect.getResponseCode();
		lastModified = connect.getHeaderField("Last-Modified");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();

		if (status == 200) {
			PeerInformation[] peerInformation = new Gson().fromJson(content.toString(), PeerInformation[].class);
			return Arrays.asList(peerInformation);
		} else {
			throw new HTTPException(status);
		}
	}

	public void sendStayAliveMessage(URL url) throws MalformedURLException, IOException {
		HttpURLConnection connect = getURLConnection(url);

		int status = connect.getResponseCode();
		if (status != 200) {
			throw new HTTPException(status);
		}
	}

	public void sendLeaveMessage(URL url) throws IOException {
		HttpURLConnection connect = getURLConnection(url);

		int status = connect.getResponseCode();
		if (status == 200) {
			System.out.println("Goodbye");
		} else {
			throw new HTTPException(status);
		}
	}

	private String getParamsString(Map<String, String> params) {
		StringBuilder result = new StringBuilder();
		result.append("?");
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
				result.append("=");
				result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
				result.append("&");
			}
		} catch (UnsupportedEncodingException e) {
			System.err.println(e.getMessage());
		}

		String resultString = result.toString();
		return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
	}

	@Override
	public Channel getChannel() {
		return channel;
	}

	@Override
	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public void schedulerAliveAndNTPMessage(int repeatIntervalSecond) {
		executor.scheduleAtFixedRate(() -> {
			try {
				URL url = new URL(getChannel().getIpAddress() + "/connect/stayAlive");
				sendStayAliveMessage(url);
				synchronizeTime(new NTPUDPClient(), "0.pool.ntp.org");		
				System.out.println("Pozvao");
			} catch (MalformedURLException e) {
				System.err.println(e.getMessage());
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}, repeatIntervalSecond, repeatIntervalSecond, TimeUnit.SECONDS);
	}

}
