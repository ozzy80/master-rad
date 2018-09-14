package com.kikkar.global;

public class PeerInformation {

	private byte[] ipAddress;

	private Integer portNumber;

	private Short clubNumber;

	public PeerInformation() { }

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

}
