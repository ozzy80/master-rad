package com.kikkar.network.impl;

import java.util.Arrays;

import com.kikkar.global.ClockSingleton;

public class PeerInformation {
	private byte[] ipAddress;
	private Integer portNumber;
	private Short clubNumber;
	private PeerStatus peerStatus;
	private long lastReceivedMessageTimeMilliseconds;
	private long lastSentMessageTimeMilliseconds;
	private int lastSentPacketNumber;
	private int lastReceivedPacketNumber;
	private short unorderPacketNumber;
	private short pingMessageNumber;
	private short requestMessageNumber;

	public PeerInformation() {
		this.peerStatus = PeerStatus.NOT_CONTACTED;
		this.lastReceivedMessageTimeMilliseconds = ClockSingleton.getInstance().getcurrentTimeMilliseconds();
		lastReceivedPacketNumber = -1;
		
	}

	public PeerInformation(byte[] ipAddress, Integer portNumber, Short clubNumber) {
		this();
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

	public PeerStatus getPeerStatus() {
		return peerStatus;
	}

	public void setPeerStatus(PeerStatus peerStatus) {
		this.peerStatus = peerStatus;
	}

	public long getLastReceivedMessageTimeMilliseconds() {
		return lastReceivedMessageTimeMilliseconds;
	}

	public void setLastReceivedMessageTimeMilliseconds(long lastReceivedMessageTimeMilliseconds) {
		this.lastReceivedMessageTimeMilliseconds = lastReceivedMessageTimeMilliseconds;
	}

	public int getLastSentPacketNumber() {
		return lastSentPacketNumber;
	}

	public void setLastSentPacketNumber(int lastSentPacketNumber) {
		this.lastSentPacketNumber = lastSentPacketNumber;
	}

	public int getLastReceivedPacketNumber() {
		return lastReceivedPacketNumber;
	}

	public void setLastReceivedPacketNumber(int lastReceivedPacketNumber) {
		this.lastReceivedPacketNumber = lastReceivedPacketNumber;
	}

	public short getUnorderPacketNumber() {
		return unorderPacketNumber;
	}

	public void setUnorderPacketNumber(short unorderPacketNumber) {
		this.unorderPacketNumber = unorderPacketNumber;
	}

	public short getPingMessageNumber() {
		return pingMessageNumber;
	}

	public void setPingMessageNumber(short pingMessageNumber) {
		this.pingMessageNumber = pingMessageNumber;
	}

	public void incrementPingMessageNumber() {
		pingMessageNumber++;
	}

	public short getRequestMessageNumber() {
		return requestMessageNumber;
	}

	public void setRequestMessageNumber(short requestMessageNumber) {
		this.requestMessageNumber = requestMessageNumber;
	}

	public void incrementRequestMessageNumber() {
		requestMessageNumber++;
	}

	public void incrementLastSentPacketNumber() {
		lastSentPacketNumber++;
	}

	public void incrementUnorderPacketNumber() {
		unorderPacketNumber++;
	}

	public void decrementUnorderPacketNumber() {
		if (unorderPacketNumber > 0) {
			unorderPacketNumber--;
		}
	}

	public long getLastSentMessageTimeMilliseconds() {
		return lastSentMessageTimeMilliseconds;
	}

	public void setLastSentMessageTimeMilliseconds(long lastSentMessageTimeMilliseconds) {
		this.lastSentMessageTimeMilliseconds = lastSentMessageTimeMilliseconds;
	}

	public void resetCounters() {
		lastReceivedPacketNumber = 0;
		lastSentPacketNumber = 0;
		unorderPacketNumber = 0;
		pingMessageNumber = 0;
		requestMessageNumber = 0;
	}

}
