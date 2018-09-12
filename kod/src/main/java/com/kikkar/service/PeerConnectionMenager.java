package com.kikkar.service;

public interface PeerConnectionMenager {
	
	public boolean checkIsProtocolVersionSupported(String protocolVersion);
	
	public boolean checkIsPeerHasEnoughResources(Long channelId, Long downloadSpeed, Long uploadSpeed);
	
}
