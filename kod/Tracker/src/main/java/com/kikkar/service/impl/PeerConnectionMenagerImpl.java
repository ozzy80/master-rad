package com.kikkar.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kikkar.dao.ChannelDao;
import com.kikkar.model.Channel;
import com.kikkar.service.PeerConnectionMenager;

@Service
public class PeerConnectionMenagerImpl implements PeerConnectionMenager {

	@Autowired
	private ChannelDao channelDao;

	@Override
	public boolean checkIsProtocolVersionSupported(String protocolVersion) {
		if (protocolVersion.equals("P2Pv.1")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean checkIsPeerHasEnoughResources(Long channelId, Long downloadSpeed, Long uploadSpeed) {
		Channel channel = channelDao.getChannelByID(channelId);
		Long bitrate = channel.getBitrate();
		Long minDownloadSpeed = bitrate + (long) (bitrate * 0.30);
		Long minUploadSpeed = bitrate + (long) (bitrate * 0.35);

		if (minDownloadSpeed > downloadSpeed || minUploadSpeed > uploadSpeed) {
			return false;
		}
		return true;
	}

	public String getClientIp(HttpServletRequest request) {
		String[] IP_HEADER_CANDIDATES = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
				"HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
				"HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR" };

		for (String header : IP_HEADER_CANDIDATES) {
			String ip = request.getHeader(header);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		return request.getRemoteAddr();
	}

}
