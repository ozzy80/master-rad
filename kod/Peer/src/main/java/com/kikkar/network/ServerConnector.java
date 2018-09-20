package com.kikkar.network;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ntp.NTPUDPClient;

import com.kikkar.network.impl.Channel;
import com.kikkar.network.impl.PeerInformation;

public interface ServerConnector {

	Channel loadJson(String Json);
	
	public URL createURL(String baseURL, Map<String, String> parameters) throws MalformedURLException;

	public Map<String, String> createConnectionParamerets(SpeedTest speedTest) throws MalformedURLException;
	
	Short connectToServer(URL url) throws IOException;

	void synchronizeTime(NTPUDPClient client, String ntpServer) throws UnknownHostException;

	List<PeerInformation> getPeerInfoList(URL url) throws IOException;

	void sendStayAliveMessage(URL url) throws MalformedURLException, IOException;

	void sendLeaveMessage(URL url) throws IOException;

	public void schedulerAliveAndNTPMessage(int repeatInterval);
	
	void setChannel(Channel channel);

	Channel getChannel();
}
