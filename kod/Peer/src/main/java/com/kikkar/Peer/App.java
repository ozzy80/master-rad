package com.kikkar.Peer;

import java.io.FileNotFoundException;
import java.net.DatagramPacket;

import com.kikkar.global.Constants;
import com.kikkar.network.ConnectionManager;
import com.kikkar.network.impl.ConnectionManagerImpl;
import com.kikkar.network.impl.ConnectionManagerSourceImpl;
import com.kikkar.schedule.DownloadScheduler;
import com.kikkar.schedule.UploadScheduler;
import com.kikkar.schedule.impl.DownloadSchedulerImpl;
import com.kikkar.schedule.impl.DownloadSchedulerSourceImpl;
import com.kikkar.schedule.impl.UploadSchedulerImpl;
import com.kikkar.schedule.impl.UploadSchedulerSourceImpl;
import com.kikkar.video.SourceVideoLoader;
import com.kikkar.video.impl.SourceVideoLoaderImpl;

public class App {

	public static void main(String[] args) throws Exception {
		// final PrintStream pst = new PrintStream("error.txt");
		// Constants.setErrorPrintIntoFile(pst);

		// Obican parnjak
		if (args[0].equals("Peer")) {
			System.out.println("OBICAN PARNJAK");
			ConnectionManager connectionManager = new ConnectionManagerImpl();
			String rawJson = "{\"channelId\":1,\"chunkSize\":1500,\"bitrate\":1500,\"name\":\"BBC\",\"description\":null,\"ipAddress\":\"http://192.168.0.170:8080/Tracker\"}";
			connectionManager.loadJson(rawJson);
			// connectionManager.start();

			UploadScheduler uploadScheduler = new UploadSchedulerImpl(connectionManager);
			DownloadScheduler downloadScheduler = new DownloadSchedulerImpl(connectionManager, uploadScheduler);
			downloadScheduler.startDownload();
			uploadScheduler.scheduleCollectMissingVideo();
			while (true) {
				downloadScheduler.processPacket(downloadScheduler.getNextPacket());
			}
		}

		// Izvor
		if (args[0].equals("Source")) {
			System.out.println("IZVOR");
			ConnectionManager connectionManager = new ConnectionManagerSourceImpl();
			String rawJson = "{\"channelId\":1,\"chunkSize\":1500,\"bitrate\":1500,\"name\":\"BBC\",\"description\":null,\"ipAddress\":\"http://192.168.0.170:8080/Tracker\"}";
			byte[] reciveData = new byte[Constants.DATAGRAM_PACKET_SIZE];
			DatagramPacket reciveDatagramPacket = new DatagramPacket(reciveData, reciveData.length);
			connectionManager.loadJson(rawJson);
			// connectionManager.start();

			UploadScheduler uploadScheduler = new UploadSchedulerSourceImpl(connectionManager);
			DownloadScheduler downloadScheduler = new DownloadSchedulerSourceImpl(connectionManager,
					reciveDatagramPacket, uploadScheduler);
			SourceVideoLoader sourceVideoLoader = new SourceVideoLoaderImpl(uploadScheduler);


			downloadScheduler.startDownload();
			new Thread(() -> {
				try {
					sourceVideoLoader.loadVideo("./video/source", "./video/play");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}).start();
			while (true) {
				downloadScheduler.processPacket(downloadScheduler.getNextPacket());
			}
		}

	}
}
