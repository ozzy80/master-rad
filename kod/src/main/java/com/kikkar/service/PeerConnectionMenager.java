package com.kikkar.service;

import javax.servlet.http.HttpServletRequest;

public interface PeerConnectionMenager {

	boolean checkIsProtocolVersionSupported(String protocolVersion);

	boolean checkIsPeerHasEnoughResources(Long channelId, Long downloadSpeed, Long uploadSpeed);

	String getClientIp(HttpServletRequest request);
}
