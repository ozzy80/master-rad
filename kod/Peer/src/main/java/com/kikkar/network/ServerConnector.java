package com.kikkar.network;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.net.ntp.NTPUDPClient;

import com.kikkar.global.PeerInformation;
import com.kikkar.network.impl.Channel;

public interface ServerConnector {

	Channel loadJson(String Json);

	URL createConnectURL(SpeedTest speedTest) throws MalformedURLException;

	Short connectToServer(URL url) throws IOException;

	void synchronizeTime(NTPUDPClient client, String ntpServer) throws UnknownHostException;

	URL createPeerInfoURL(Short token, Integer port) throws MalformedURLException;

	List<PeerInformation> getPeerInfoList(URL url) throws IOException;

	void sendStayAliveMessage() throws MalformedURLException, IOException;

	void sendLeaveMessage() throws IOException;

	public void schedulerAliveAndNTPMessage(int repeatInterval);
	
	void setChannel(Channel channel);

	Channel getChannel();
}
