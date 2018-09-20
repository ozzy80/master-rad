package com.kikkar.network.impl;

import com.kikkar.network.SpeedTest;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class SpeedTestImpl implements SpeedTest {

	private static Long bitePerSecond = 0l;
	private SpeedTestSocket speedTestSocket;

	public SpeedTestImpl(SpeedTestSocket speedTestSocket) {
		this.speedTestSocket = speedTestSocket;
	}

	@Override
	public Long downloadSpeedTest(int measureTime, int additionallyWaitForDataMilliseconds) {
		runSpeedTest(true, measureTime);
		try {
			Thread.sleep(measureTime + additionallyWaitForDataMilliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return bitePerSecond;
	}

	@Override
	public Long uploadSpeedTest(int measureTimeMilliseconds, int additionallyWaitForDataMilliseconds) {
		runSpeedTest(false, measureTimeMilliseconds);
		try {
			Thread.sleep(measureTimeMilliseconds + additionallyWaitForDataMilliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return bitePerSecond;
	}

	private void runSpeedTest(boolean testDownloadSpeed, int measureTime) {
		speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {
			@Override
			public void onCompletion(SpeedTestReport report) {
				bitePerSecond = report.getTransferRateBit().longValue();
			}

			@Override
			public void onError(SpeedTestError speedTestError, String errorMessage) {
			}

			@Override
			public void onProgress(float percent, SpeedTestReport report) {
			}
		});

		if (testDownloadSpeed)
			speedTestSocket.startFixedDownload("http://ipv4.ikoula.testdebit.info/100M.iso", measureTime);
		else
			speedTestSocket.startFixedUpload("http://ipv4.ikoula.testdebit.info/", 10000000, measureTime);
	}

}
