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
	public @ResponseBody ResponseEntity<Object> initialConnect(
			@RequestParam(value = "protocol", required = true) String protocolVersion,
			@RequestParam(value = "download", required = true) Long downloadSpeed,
			@RequestParam(value = "upload", required = true) Long uploadSpeed,
			@PathVariable(value = "channelId") Long channelId, HttpServletRequest request) {

		boolean supportedProtocolVersion = peerConnectionMenager.checkIsProtocolVersionSupported(protocolVersion);
		if (!supportedProtocolVersion) {
			return ResponseEntity.status(HttpStatus.HTTP_VERSION_NOT_SUPPORTED)
					.body("P2P protocol version not supported");
		}

		boolean hasEnoughResources = peerConnectionMenager.checkIsPeerHasEnoughResources(channelId, downloadSpeed,
				uploadSpeed);
		if (!hasEnoughResources) {
			return ResponseEntity.status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
					.body("Peer has lower download/upload than it is required");
		}

		Short token = (short) ThreadLocalRandom.current().nextInt(Short.MIN_VALUE, Short.MAX_VALUE);
		String ip = peerConnectionMenager.getClientIp(request);
		System.out.println("*****************************" + ip);
		tokenManager.addToken(token, ip);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(token);
	}

	@RequestMapping(value = "/list/{channelId}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Object> getPeersList(
			@RequestParam(value = "token", required = true) Short peerToken,
			@RequestParam(value = "port", required = true) Integer peerPort,
			@PathVariable(value = "channelId") Long channelId, HttpServletRequest request) {

		String ip = peerConnectionMenager.getClientIp(request);
		Token token = tokenManager.getTokenByIpAddress(ip);
		if (token == null || !token.getToken().equals(peerToken)) {
			return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Token is not valid");
		}

		List<PeerInformation> peerList = peerInformationManager.getPeersList(10, channelId, ip);
		PeerInformation currentPeer = peerInformationManager.getPeerById(ip);
		Short clubNumber;
		if (currentPeer == null) {
			clubNumber = (short) ((peerInformationManager.getLastChannelClubNumber(channelId) + 1) % 6);
		} else {
			clubNumber = currentPeer.getClubNumber();
		}
		Channel channel = channelManager.getChannelByID(channelId);
		currentPeer = new PeerInformation(token.getIpAddress(), peerPort, clubNumber, new Date(), channel);
		peerInformationManager.addPeer(currentPeer);
		peerList.add(currentPeer);

		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(peerList);

		return ResponseEntity.status(HttpStatus.OK).body(json.toString());
	}

	@RequestMapping(value = "/leave", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Object> leaveChannel(HttpServletRequest request) {
		String ip = peerConnectionMenager.getClientIp(request);
		peerInformationManager.deletePeer(ip);

		Token token = tokenManager.getTokenByIpAddress(ip);
		tokenManager.deleteToken(token);

		return ResponseEntity.status(HttpStatus.OK).body("Goodbye");
	}

	@RequestMapping(value = "/stayAlive", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Object> stayAliveMessage(HttpServletRequest request) {
		String ip = peerConnectionMenager.getClientIp(request);
		peerInformationManager.stayAlive(ip);

		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

}
