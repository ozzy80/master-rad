package com.kikkar.service.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kikkar.dao.ChannelDao;
import com.kikkar.model.Channel;
import com.kikkar.service.ChannelManager;

import de.javawi.jstun.attribute.ChangeRequest;
import de.javawi.jstun.attribute.MappedAddress;
import de.javawi.jstun.attribute.MessageAttributeInterface.MessageAttributeType;
import de.javawi.jstun.attribute.MessageAttributeParsingException;
import de.javawi.jstun.header.MessageHeader;
import de.javawi.jstun.util.UtilityException;

@Service
public class ChannelManagerImpl implements ChannelManager {

	@Autowired
	private ChannelDao channelDao;
	
	@Override
	public void addChannel(Channel channel) {
		channelDao.addChannel(channel);
	}

	@Override
	public Channel getChannelByID(Long channelId) {
		Channel channel = channelDao.getChannelByID(channelId);
		
		String ip = null;
		try {
			ip = getServerIpAddress();
		} catch (MessageAttributeParsingException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (UtilityException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	
		channel.setIpAddress(ip);
		return channel;
	}

	public String getServerIpAddress() throws UtilityException, IOException, MessageAttributeParsingException{
	    MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
	    ChangeRequest changeRequest = new ChangeRequest();
	    sendMH.addMessageAttribute(changeRequest);

	    byte[] data = sendMH.getBytes();

	    DatagramSocket s = new DatagramSocket();
	    DatagramPacket p = new DatagramPacket(data, data.length, InetAddress.getByName("stun.l.google.com"), 19302);
	    s.setReuseAddress(true);
	    s.send(p);

	    DatagramPacket rp;
	    rp = new DatagramPacket(new byte[32], 32);
	    s.receive(rp);
	    
	    MessageHeader receiveMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingResponse);
	    receiveMH.parseAttributes(rp.getData());
	    MappedAddress ma = (MappedAddress) receiveMH.getMessageAttribute(MessageAttributeType.MappedAddress);
	    
		return ma.getAddress().toString();
	}
	
	@Override
	public void deleteChannel(Channel channel) {
		channelDao.deleteChannel(channel);
	}

	@Override
	public List<Channel> getAllChannels() {
		List<Channel> channels = channelDao.getAllChannels();
		return channels;
	}

	@Override
	public List<Channel> getChannelsByPopularity(int limit) {
		List<Channel> peerInformationList = channelDao.getChannelsByPopularity(limit);
		return peerInformationList;
	}

}
