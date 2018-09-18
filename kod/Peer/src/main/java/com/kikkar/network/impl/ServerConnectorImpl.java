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

import javax.xml.ws.http.HTTPException;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.google.gson.Gson;
import com.kikkar.global.ClockSingleton;
import com.kikkar.global.PeerInformation;
import com.kikkar.network.ServerConnector;
import com.kikkar.network.SpeedTest;

public class ServerConnectorImpl implements ServerConnector, Job {

	private Channel channel;

	public ServerConnectorImpl() {
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
				System.out.println(e.getMessage());
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
	}

	public List<PeerInformation> getPeerInfoList(URL url) throws IOException {
		HttpURLConnection connect = getURLConnection(url);

		int status = connect.getResponseCode();
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
			e.printStackTrace();
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

	public void schedulerAliveAndNTPMessage(int repeatInterval) {
		try {
			JobDetail j = JobBuilder.newJob(ServerConnectorImpl.class).build();

			Trigger t = TriggerBuilder.newTrigger().withIdentity("CroneTrigger").withSchedule(
					SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(repeatInterval).repeatForever())
					.build();
			Scheduler s = StdSchedulerFactory.getDefaultScheduler();
			s.getContext().put("serverObj", this);
			s.start();
			s.scheduleJob(j, t);

		} catch (SchedulerException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			ServerConnectorImpl obj = (ServerConnectorImpl) context.getScheduler().getContext().get("serverObj");
			URL url = new URL(obj.getChannel().getIpAddress() + "/connect/stayAlive");
			obj.sendStayAliveMessage(url);
			obj.synchronizeTime(new NTPUDPClient(), "0.pool.ntp.org");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
