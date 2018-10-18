package com.kikkar.Peer;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.kikkar.global.Constants;
import com.kikkar.global.SharingBufferSingleton;
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
import com.kikkar.video.impl.VLCPlayer;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class App {

	public static void main(String[] args) throws Exception {
		// final PrintStream pst = new PrintStream("error.txt");
		// Constants.setErrorPrintIntoFile(pst);
		JFrame frame = new JFrame();

		Canvas canvas = new Canvas();
		canvas.setBackground(Color.black);
		JPanel panel = new JPanel();
		canvas.setBounds(100, 400, 800, 400);
		panel.setLayout(new BorderLayout());
		panel.add(canvas, BorderLayout.CENTER);
		panel.setBounds(100, 50, 800, 480);
		frame.add(panel, BorderLayout.NORTH);

		frame.setLocation(100, 100);
		frame.setSize(800, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		JButton openFileButton = new JButton("Load channel");
		openFileButton.setBounds(50,80,65,30);
		openFileButton.setLayout(null);
		openFileButton.setVisible(true);
		frame.add(openFileButton);
		openFileButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				String rawJson = openFile(evt);
				
				Arrays.stream(new File(Constants.VIDEO_PLAY_FILE_PATH ).listFiles()).forEach(File::delete);
				
				VLCPlayer player = new VLCPlayer();
				player.setupVLCPlayer(canvas);
				SharingBufferSingleton.getInstance().setPlayer(player);
				
				if (args[0].equals("Peer")) {
					new Thread(() -> startPeer(rawJson, canvas)).start();
				} else {
					new Thread(() -> startSource(rawJson, canvas)).start();
				}
			}
		});
	}

	private static String openFile(ActionEvent event) {
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Json Files(*.json)", "json");
		fileChooser.setFileFilter(filter);
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			try (Scanner inScanner = new Scanner(selectedFile)) {
				StringBuilder sb = new StringBuilder();
				while (inScanner.hasNext()) {
					sb.append(inScanner.nextLine());
					sb.append("\n");
				}
				return new String(sb);
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
			}
		}
		return null;
	}

	private static void startPeer(String rawJson, Canvas canvas) {
		System.out.println("OBICAN PARNJAK");
		ConnectionManager connectionManager = new ConnectionManagerImpl();
		connectionManager.loadJson(rawJson);

		UploadScheduler uploadScheduler = new UploadSchedulerImpl(connectionManager);
		DownloadScheduler downloadScheduler = new DownloadSchedulerImpl(connectionManager, uploadScheduler);
		downloadScheduler.startDownload();
		// uploadScheduler.scheduleCollectMissingVideo();
		while (true) {
			downloadScheduler.processPacket(downloadScheduler.getNextPacket());
		}
	}

	private static void startSource(String rawJson, Canvas canvas) {
		System.out.println("IZVOR");
		ConnectionManager connectionManager = new ConnectionManagerSourceImpl();
		byte[] reciveData = new byte[Constants.DATAGRAM_PACKET_SIZE];
		DatagramPacket reciveDatagramPacket = new DatagramPacket(reciveData, reciveData.length);
		connectionManager.loadJson(rawJson);

		UploadScheduler uploadScheduler = new UploadSchedulerSourceImpl(connectionManager);
		DownloadScheduler downloadScheduler = new DownloadSchedulerSourceImpl(connectionManager, reciveDatagramPacket,
				uploadScheduler);
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
