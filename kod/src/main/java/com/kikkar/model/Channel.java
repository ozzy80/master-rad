package com.kikkar.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "channel")
public class Channel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "channel_id")
	private Long channelId;

	@NotNull
	@Min(0)
	@Column(name = "chunk_size")
	private Integer chunkSize;

	@NotNull
	@Column(name = "bitrate")
	private Long bitrate;

	@NotNull
	@Column(name = "name", length = 45)
	private String name;

	@Column(name = "description", length = 512)
	private String description;

	@Transient
	private String ipAddress;

	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	public Integer getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(Integer chunkSize) {
		this.chunkSize = chunkSize;
	}

	public Long getBitrate() {
		return bitrate;
	}

	public void setBitrate(Long bitrate) {
		this.bitrate = bitrate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

}
