package com.kikkar.global;

import java.io.PrintStream;

public final class Constants {
	private Constants() {
	}

	public static final int BUFFER_SIZE = 6_000;

	public static final int VIDEO_DURATION_SECOND = 6;

	public static final int NUMBER_OF_CLUB = 6;

	public static final int WAIT_NEIGHBOUR_PACKETS_MILLISECOND = 1000;

	public static final short MAX_NUMBER_OF_UNORDER_PACKET = 15;

	public static final int MAX_NUMBER_OF_WAIT_PACKET = 150;

	public static final String VIDEO_FILE_PATH = "output.mov";

	public static final int MAX_REUQEST_VIDEO_SIZE = 20;

	public static final int INITIA_MISSING_VIDEO_COLLECT_DELAY_SECOND = 5;
	
	public static final int DATA_WAIT_SECOND = 1;
	
	//final PrintStream pst = new PrintStream("error.txt");  
	public static void setErrorPrintIntoFile(PrintStream pst) {
		System.setErr(pst);
	}
}
