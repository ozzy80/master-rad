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
			ip = "http://" + getServerIpAddress() + ":8080/Tracker";
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

			String currentDir = System.getProperty("user.dir");
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(
					currentDir + "\\src\\main\\webapp\\WEB-INF\\resources\\img\\channel", channelName + extension)));
			outputStream.write(file.getBytes());
			outputStream.flush();
			outputStream.close();

			return true;
		} else {
			return false;
		}
	}

}
