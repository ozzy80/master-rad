package com.kikkar.Peer;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.kikkar.global.PeerInformation;
import com.kikkar.network.impl.Channel;
import com.kikkar.network.impl.ServerConnectorImpl;
import com.kikkar.network.impl.SpeedTestIMpl;

import fr.bmartel.speedtest.SpeedTestSocket;

public class App {

	public static void main(String[] args) throws InterruptedException, SchedulerException {
		ServerConnectorImpl serverConnector = new ServerConnectorImpl();
		Channel channel = serverConnector.loadJson(
				"{\"channelId\":1,\"chunkSize\":1500,\"bitrate\":1500,\"name\":\"BBC\",\"description\":null,\"ipAddress\":\"http://192.168.0.171:8080/Tracker\"}");

		try {
			URL url = serverConnector.createConnectURL(new SpeedTestIMpl(new SpeedTestSocket()));
			Short token = serverConnector.connectToServer(url);
			System.out.println(token);
			serverConnector.synchronizeTime(new NTPUDPClient(), "0.pool.ntp.org");
			
			url = serverConnector.createPeerInfoURL(token, 55000);
			List<PeerInformation> peerInformations = serverConnector.getPeerInfoList(url);
			peerInformations.stream().map(s -> new String(s.getIpAddress(), StandardCharsets.UTF_8)).forEach(System.out::println);

			//serverConnector.sendStayAliveMessage();
			//serverConnector.sendLeaveMessage();
			
			serverConnector.schedulerAliveAndNTPMessage(5);
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}
}
