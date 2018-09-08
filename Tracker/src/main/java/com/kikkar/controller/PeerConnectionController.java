package com.kikkar.controller;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kikkar.model.Channel;
import com.kikkar.model.PeerInformation;
import com.kikkar.model.Token;
import com.kikkar.service.ChannelManager;
import com.kikkar.service.PeerConnectionMenager;
import com.kikkar.service.PeerInformationManager;
import com.kikkar.service.TokenManager;

@RestController
@RequestMapping(value = "/connect")
public class PeerConnectionController {

	@Autowired
	private PeerConnectionMenager peerConnectionMenager;

	@Autowired
	@Qualifier("peerInformationManagerImpl")
	private PeerInformationManager peerInformationManager;

	@Autowired
	private TokenManager tokenManager;

	@Autowired
	private ChannelManager channelManager;

	@RequestMapping(value = "/initial/{channelId}", method = RequestMethod.GET)
	public ResponseEntity<Object> initialConnect(
			@RequestParam(value = "protocol", required = true) String protocolVersion,
			@RequestParam(value = "download", required = true) Long downloadSpeed,
			@RequestParam(value = "upload", required = true) Long uploadSpeed,
			@PathVariable(value = "channelId") Long channelId, HttpServletRequest request) {

		boolean supportedProtocolVersion = peerConnectionMenager.checkIsProtocolVersionSupported(protocolVersion);
		if (!supportedProtocolVersion) {
			return new ResponseEntity<>("P2P protocol version not supported", HttpStatus.HTTP_VERSION_NOT_SUPPORTED);
		}

		boolean hasEnoughResources = peerConnectionMenager.checkIsPeerHasEnoughResources(channelId, downloadSpeed,
				uploadSpeed);
		if (!hasEnoughResources) {
			return new ResponseEntity<>("Peer has lower download/upload than it is required",
					HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
		}

		Short token = (short) ThreadLocalRandom.current().nextInt(Short.MIN_VALUE, Short.MAX_VALUE);
		tokenManager.addToken(token, request);

		return new ResponseEntity<>(token, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/list/{channelId}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Object> getPeersList(
			@RequestParam(value = "token", required = true) Short token,
			@RequestParam(value = "ipAddress", required = true) String ipAddress,
			@RequestParam(value = "port", required = true) Integer port,
			@PathVariable(value = "channelId") Long channelId, HttpServletRequest request) {

		Token tokenObj = tokenManager.getTokenByIpAddress(request);
		if (tokenObj == null || !tokenObj.getToken().equals(token)) {
			return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Token is not valid");
		}
		tokenManager.deleteToken(tokenObj);

		List<PeerInformation> peerInformationList = peerInformationManager.getPeersList(3, channelId);
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		StringBuffer json = new StringBuffer();
		for (PeerInformation peerInformation : peerInformationList) {
			json.append(gson.toJson(peerInformation));
		}

		Short lastPeerClub = peerInformationManager.getLastChannelClubNumber(channelId);
		PeerInformation peerInformation = new PeerInformation();
		Channel channel = channelManager.getChannelByID(channelId);
		peerInformation.setChannel(channel);
		peerInformation.setClubNumber((short) (lastPeerClub + 1));
		peerInformation.setIpAddress(ipAddress.getBytes());
		peerInformation.setLastActiveMessage(new Date());
		peerInformation.setPortNumber(port);
		peerInformationManager.addPeer(peerInformation);

		json.append(gson.toJson(peerInformation));

		return ResponseEntity.status(HttpStatus.OK).body(json.toString());
	}

	@RequestMapping(value = "/leave", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Object> leaveChannel(
			@RequestParam(value = "ipAddress", required = true) String ipAddress) {
		peerInformationManager.deletePeer(ipAddress);
		return ResponseEntity.status(HttpStatus.OK).body("Goodbye");
	}
	
	@RequestMapping(value = "/stayAlive", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Object> stayAliveMessage(
			@RequestParam(value = "ipAddress", required = true) String ipAddress) {
		peerInformationManager.stayAlive(ipAddress);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
	
}
