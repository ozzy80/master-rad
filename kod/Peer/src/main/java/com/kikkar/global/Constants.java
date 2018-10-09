package com.kikkar.global;

import java.io.PrintStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public final class Constants {
	private Constants() {
	}

	public static final int BUFFER_SIZE = 10_000;

	public static final int VIDEO_DURATION_SECOND = 6;

	public static final int NUMBER_OF_CLUB = 6;

	public static final int WAIT_NEIGHBOUR_PACKETS_MILLISECOND = 5000;

	public static final short MAX_NUMBER_OF_UNORDER_PACKET = 60;
	
	public static final short MAX_NUMBER_OF_LATE_PACKET = 50;

	public static final int MAX_NUMBER_OF_WAIT_PACKET = 150;

	public static final int MAX_REUQEST_VIDEO_SIZE = 20;

	public static final int INITIA_MISSING_VIDEO_COLLECT_DELAY_SECOND = 5;

	public static final int DATA_WAIT_SECOND = 1;
	
	public static final int DATAGRAM_PACKET_SIZE = 1500;

	public static final String OUTPUT_VIDEO_FILE_PATH = "./video/play";

	public static void setErrorPrintIntoFile(PrintStream pst) {
		System.setErr(pst);
	}
	
	public static String getLocalIp() {
		try(final DatagramSocket socket = new DatagramSocket()){
			  socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			  return socket.getLocalAddress().getHostAddress();
		} catch (UnknownHostException e) {
			System.err.println(e.getMessage());
		} catch (SocketException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}
}
