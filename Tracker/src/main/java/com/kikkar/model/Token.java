package com.kikkar.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "peer_token")
public class Token {

	@Id
	@NotNull
	@Column(name = "ip_address")
	private byte[] ipAddress;
	
	@NotNull
	@Column(name = "token")
	private Short token;

	public byte[] getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(byte[] ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Short getToken() {
		return token;
	}

	public void setToken(Short token) {
		this.token = token;
	}

}
