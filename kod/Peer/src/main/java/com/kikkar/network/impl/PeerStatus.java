package com.kikkar.network.impl;

public enum PeerStatus {
	NOT_CONTACTED,
	PONG_WAIT,
	PING_PONG_EXCHANGE,
	RESPONSE_WAIT,
	UPLOAD_CONNECTION,
	DOWNLOAD_CONNECTION
}
