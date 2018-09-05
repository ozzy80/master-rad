package com.kikkar.service;

import java.util.List;

import com.kikkar.model.Channel;

public interface ChannelManager {
	void addChannel(Channel channel);

	Channel getChannelByID(Long channelId);

	void deleteChannel(Channel channel);

	List<Channel> getAllChannels();

	List<Channel> getChannelsByPopularity(int limit);
}
