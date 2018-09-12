package com.kikkar.Peer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Instant;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import com.kikkar.global.Clock;

public class App {

	public static void main(String[] args) throws InterruptedException {
		NTPUDPClient client = new NTPUDPClient();
		client.setDefaultTimeout(10000);
		Clock clock = Clock.getInstance();

		try {
			client.open();
			TimeInfo info;
			try {
				info = client.getTime(InetAddress.getByName("2.europe.pool.ntp.org"));
				info.computeDetails(); // compute offset/delay if not already done
				Long offsetValue = info.getOffset();
				System.out.println(offsetValue);
				clock.setOffsetValue(offsetValue);
			} catch (UnknownHostException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} finally {
			client.close();
		}

		Thread.sleep(10000);
		System.out.println(Instant.ofEpochMilli(clock.getcurrentTimeMilliseconds()));
		System.out.println(Instant.ofEpochMilli(System.currentTimeMillis()));
	}
}
