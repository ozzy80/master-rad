package com.kikkar.Peer;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.quartz.SchedulerException;

import com.kikkar.network.impl.Channel;
import com.kikkar.network.impl.PeerInformation;
import com.kikkar.network.impl.ServerConnectorImpl;
import com.kikkar.network.impl.SpeedTestImpl;

import fr.bmartel.speedtest.SpeedTestSocket;

public class App {

	public static void main(String[] args) throws InterruptedException, SchedulerException {
		/*
		 * Server connector part
		 */
		/*
		ServerConnectorImpl serverConnector = new ServerConnectorImpl();
		Channel channel = serverConnector.loadJson(
				"{\"channelId\":1,\"chunkSize\":1500,\"bitrate\":1500,\"name\":\"BBC\",\"description\":null,\"ipAddress\":\"http://192.168.0.171:8080/Tracker\"}");

		try {
			String baseURL = channel.getIpAddress() + "/connect/initial/" + channel.getChannelId();
			Map<String, String> parameters = serverConnector.createConnectionParamerets(new SpeedTestImpl(new SpeedTestSocket()));
			URL url = serverConnector.createURL(baseURL, parameters);
			
			
			Short token = serverConnector.connectToServer(url);
			serverConnector.synchronizeTime(new NTPUDPClient(), "0.pool.ntp.org");
			
			
			baseURL = channel.getIpAddress() + "/connect/list/" + channel.getChannelId();
			Map<String, String> parameters2 = new HashMap<>();
			parameters2.put("token", token.toString());
			parameters2.put("port", new Integer(1234).toString());

			url = serverConnector.createURL(baseURL, parameters2);
			List<PeerInformation> peerInformations = serverConnector.getPeerInfoList(url);
			peerInformations.stream().map(s -> new String(s.getIpAddress(), StandardCharsets.UTF_8)).forEach(System.out::println);

			//url = new URL(channel.getIpAddress() + "/connect/stayAlive");
			//serverConnector.sendStayAliveMessage(url);
			
			//url = new URL(channel.getIpAddress() + "/connect/leave");
			//serverConnector.sendLeaveMessage();
			
			serverConnector.schedulerAliveAndNTPMessage(10);
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		*/
	}
}
