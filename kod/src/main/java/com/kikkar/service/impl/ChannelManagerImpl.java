package com.kikkar.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kikkar.dao.ChannelDao;
import com.kikkar.model.Channel;
import com.kikkar.service.ChannelManager;

import de.javawi.jstun.attribute.MessageAttributeParsingException;
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

	public String getServerIpAddress() throws UtilityException, IOException, MessageAttributeParsingException {
		/*
		 * MessageHeader sendMH = new
		 * MessageHeader(MessageHeader.MessageHeaderType.BindingRequest); ChangeRequest
		 * changeRequest = new ChangeRequest();
		 * sendMH.addMessageAttribute(changeRequest);
		 * 
		 * byte[] data = sendMH.getBytes();
		 * 
		 * DatagramSocket s = new DatagramSocket(); DatagramPacket p = new
		 * DatagramPacket(data, data.length, InetAddress.getByName("stun.l.google.com"),
		 * 19302); s.setReuseAddress(true); s.send(p);
		 * 
		 * DatagramPacket rp; rp = new DatagramPacket(new byte[32], 32); s.receive(rp);
		 * 
		 * MessageHeader receiveMH = new
		 * MessageHeader(MessageHeader.MessageHeaderType.BindingResponse);
		 * receiveMH.parseAttributes(rp.getData()); MappedAddress ma = (MappedAddress)
		 * receiveMH.getMessageAttribute(MessageAttributeType.MappedAddress);
		 * 
		 * return ma.getAddress().toString();
		 */
		return InetAddress.getLocalHost().getHostAddress();
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

	@Override
	public boolean savePicture(String channelName, MultipartFile file) throws IOException {
		if (!file.getOriginalFilename().isEmpty()) {
			int i = file.getOriginalFilename().lastIndexOf('.');
			String extension = null;
			if (i > 0) {
				extension = file.getOriginalFilename().substring(i);
			}

			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(
					"C:\\Users\\Ozzy\\git\\master-rad\\Tracker\\src\\main\\webapp\\WEB-INF\\resources\\img\\channel",
					channelName + extension)));
			outputStream.write(file.getBytes());
			outputStream.flush();
			outputStream.close();

			return true;
		} else {
			return false;
		}

	}

}
