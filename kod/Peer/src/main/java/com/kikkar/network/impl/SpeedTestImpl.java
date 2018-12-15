package com.kikkar.network.impl;

import com.kikkar.network.SpeedTest;

import fr.bmartel.speedtest.SpeedTestSocket;

public class SpeedTestImpl implements SpeedTest {

	private static Long bitePerSecond;
	private SpeedTestSocket speedTestSocket;

	public SpeedTestImpl(SpeedTestSocket speedTestSocket) {
		bitePerSecond = 0l;
		this.speedTestSocket = speedTestSocket;
	}

	@Override
	public Long downloadSpeedTest(int measureTime, int additionallyWaitForDataMilliseconds) {
		runSpeedTest(true, measureTime);
		try {
			Thread.sleep(measureTime + additionallyWaitForDataMilliseconds);
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
		return bitePerSecond;
	}

	@Override
	public Long uploadSpeedTest(int measureTimeMilliseconds, int additionallyWaitForDataMilliseconds) {
		runSpeedTest(false, measureTimeMilliseconds);
		try {
			Thread.sleep(measureTimeMilliseconds + additionallyWaitForDataMilliseconds);
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
		return bitePerSecond;
	}

	private void runSpeedTest(boolean testDownloadSpeed, int measureTime) {
		bitePerSecond = 123456789l;
	}

}
