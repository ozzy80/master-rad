package com.kikkar.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kikkar.model.Channel;

public interface ChannelManager {
	void addChannel(Channel channel);

	Channel getChannelByID(Long channelId);

	void deleteChannel(Channel channel);

	List<Channel> getAllChannels();

	List<Channel> getChannelsByPopularity(int limit);

	boolean savePicture(String channelName, MultipartFile file) throws IOException;
}
