package com.kikkar.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.google.gson.annotations.Expose;

@Entity
@Table(name = "peer_information")
public class PeerInformation {

	@Expose
	@Id
	@NotNull
	@Column(name = "ip_address")
	private byte[] ipAddress;
	
	@Expose
	@NotNull
	@Min(0)
	@Max(65535)
	@Column(name = "port_number")
	private Integer portNumber;

	@Expose
	@NotNull
	@Min(0)
	@Max(255)
	@Column(name = "club_number")
	private Short clubNumber;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_active_message")
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
