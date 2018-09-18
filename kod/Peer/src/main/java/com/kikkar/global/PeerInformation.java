package com.kikkar.global;

import java.util.Arrays;

public class PeerInformation {

	private byte[] ipAddress;

	private Integer portNumber;

	private Short clubNumber;

	public PeerInformation() { }

	public PeerInformation(byte[] ipAddress, Integer portNumber, Short clubNumber) {
		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
		this.clubNumber = clubNumber;
	}

	public byte[] getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(byte[] ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clubNumber == null) ? 0 : clubNumber.hashCode());
		result = prime * result + Arrays.hashCode(ipAddress);
		result = prime * result + ((portNumber == null) ? 0 : portNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PeerInformation other = (PeerInformation) obj;
		if (clubNumber == null) {
			if (other.clubNumber != null)
				return false;
		} else if (!clubNumber.equals(other.clubNumber))
			return false;
		if (!Arrays.equals(ipAddress, other.ipAddress))
			return false;
		if (portNumber == null) {
			if (other.portNumber != null)
				return false;
		} else if (!portNumber.equals(other.portNumber))
			return false;
		return true;
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
