package com.kikkar.video.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.protobuf.ByteString;
import com.kikkar.global.ClockSingleton;
import com.kikkar.global.Constants;
import com.kikkar.global.SharingBufferSingleton;
import com.kikkar.packet.Pair;
import com.kikkar.packet.VideoPacket;
import com.kikkar.schedule.UploadScheduler;
import com.kikkar.video.SourceVideoLoader;

public class SourceVideoLoaderImpl implements SourceVideoLoader {

	private int videoBufferSize;
	private int videoNum;
	private int videoDutarionMillisecond;
	private UploadScheduler uploadScheduler;
	private ClockSingleton clock;
	private SharingBufferSingleton sharingBufferSingleton;
	private ScheduledExecutorService executor;

	public SourceVideoLoaderImpl() {
		videoBufferSize = 1430;
		videoDutarionMillisecond = Constants.VIDEO_DURATION_SECOND * 1000;
		clock = ClockSingleton.getInstance();
		sharingBufferSingleton = SharingBufferSingleton.getInstance();
		executor = Executors.newSingleThreadScheduledExecutor();
	}

	public SourceVideoLoaderImpl(UploadScheduler uploadScheduler) {
		this();
		this.uploadScheduler = uploadScheduler;
	}

	@Override
	public void loadVideo(String inputVideoPath, String outputVideoPath) throws FileNotFoundException {
		int i = 0;
		while (true) {
			File inputVideoFile = new File(inputVideoPath + "/izlaz" + i + ".mov");
			File outputVideoFile = new File(outputVideoPath + "/output" + i++ + ".mov");
			try (InputStream is = new FileInputStream(inputVideoFile);
					OutputStream os = new FileOutputStream(outputVideoFile);) {
				int chunkNum = (int) Math.ceil(inputVideoFile.length() / videoBufferSize);
				iterateOverFiles(is, os, chunkNum);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public void iterateOverFiles(InputStream is, OutputStream os, int chunkNum) {
		long startTime = clock.getcurrentTimeMilliseconds();
		try {
			readChunk(is, chunkNum);
			long passTime = clock.getcurrentTimeMilliseconds() - startTime;
			if (passTime < videoDutarionMillisecond) {
				Thread.sleep(videoDutarionMillisecond - passTime);
			}
			notifySwarm(videoNum, os);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
	}

	private void notifySwarm(int videoNum, OutputStream os) {
		executor.schedule(() -> {
			uploadScheduler.sendControlMessage(videoNum);		
		}, Constants.VIDEO_DURATION_SECOND, TimeUnit.SECONDS);
	}

	public void readChunk(InputStream is, int chunkNum) throws IOException {
		byte[] buffer = new byte[videoBufferSize];
		int waitMilliseconds = (videoDutarionMillisecond - 1000) / chunkNum;
		sharingBufferSingleton.setMinVideoNum(videoNum);
		boolean isFirstChunk = true;
		System.out.println("Chunk readed");
		while (is.read(buffer) > 0) {
			addVideoToBuffer(buffer, isFirstChunk, chunkNum--);

			try {
				Thread.sleep(waitMilliseconds);
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}

			uploadScheduler.sendVideo(videoNum, new ArrayList<>());
			incrementVideoNum();
			isFirstChunk = false;
		}
	}

	public void addVideoToBuffer(byte[] buffer, boolean isFirstFrame, int chunkNum) {
		VideoPacket.Builder video = VideoPacket.newBuilder();
		video.setVideoNum(videoNum);
		video.setChunkNum(chunkNum);
		video.setFirstFrame(isFirstFrame);
		video.setVideo(ByteString.copyFrom(buffer));
		sharingBufferSingleton.addVideoPacket(videoNum, video.build());
	}

	private void incrementVideoNum() {
		videoNum++;
		if (videoNum < 0) {
			videoNum = 0;
		}
	}

	public int getVideoBufferSize() {
		return videoBufferSize;
	}

	public void setVideoBufferSize(int videoBufferSize) {
		this.videoBufferSize = videoBufferSize;
	}

	public int getVideoNum() {
		return videoNum;
	}

	public void setVideoNum(int videoNum) {
		this.videoNum = videoNum;
	}

	public UploadScheduler getUploadScheduler() {
		return uploadScheduler;
	}

	public void setUploadScheduler(UploadScheduler uploadScheduler) {
		this.uploadScheduler = uploadScheduler;
	}

	public int getVideoDutarionMillisecond() {
		return videoDutarionMillisecond;
	}

	public void setVideoDutarionMillisecond(int videoDutarionMillisecond) {
		this.videoDutarionMillisecond = videoDutarionMillisecond;
	}

}
