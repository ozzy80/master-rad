package com.kikkar.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "peer_information")
public class PeerInformation {

	@Id
	@NotNull
	@Column(name = "ip_address")
	private byte[] ipAddress;
	
	@NotNull
	@Size(min = 0, max = 65535)
	@Column(name = "port_number")
	private Integer portNumber;

	@NotNull
	@Size(min = 0, max = 255)
	@Column(name = "club_number")
	private Short clubNumber;

	@NotNull
	@Column(name = "last_active_message", columnDefinition="DATETIME")
	private Date lastActiveMessage;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name="channel_id")
	private Channel channel;

	public byte[] getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(byte[] ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Integer getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(Integer portNumber) {
		this.portNumber = portNumber;
	}

	public Short getClubNumber() {
		return clubNumber;
	}

	public void setClubNumber(Short clubNumber) {
		this.clubNumber = clubNumber;
	}

	public Date getLastActiveMessage() {
		return lastActiveMessage;
	}

	public void setLastActiveMessage(Date lastActiveMessage) {
		this.lastActiveMessage = lastActiveMessage;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

}
