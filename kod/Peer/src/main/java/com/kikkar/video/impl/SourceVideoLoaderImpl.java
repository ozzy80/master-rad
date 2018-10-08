package com.kikkar.video.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.google.protobuf.ByteString;
import com.kikkar.global.ClockSingleton;
import com.kikkar.global.Constants;
import com.kikkar.global.SharingBufferSingleton;
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

	public SourceVideoLoaderImpl() {
		videoBufferSize = 1450;
		videoDutarionMillisecond = Constants.VIDEO_DURATION_SECOND * 1000;
		clock = ClockSingleton.getInstance();
		sharingBufferSingleton = SharingBufferSingleton.getInstance();
	}
	
	public SourceVideoLoaderImpl(UploadScheduler uploadScheduler) {
		this();
		this.uploadScheduler = uploadScheduler;
	}
	
	@Override
	public void loadVideo(String inputVideoPath, String outputVideoPath) throws FileNotFoundException {
		int i = 0;
		// TODO skini posle true
		OutputStream os = new FileOutputStream(new File(outputVideoPath + "/output.mov"), true);
		while (true) {
			File videoFile = new File(inputVideoPath + "/izlaz" + i++ + ".mov");
			System.out.println("USAOOOO");
			try (InputStream is = new FileInputStream(videoFile)) {
				int chunkNum = (int) Math.ceil(videoFile.length() / videoBufferSize) - 1;
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
			if(passTime < videoDutarionMillisecond) {
				Thread.sleep(videoDutarionMillisecond - passTime);
			}
			uploadScheduler.sendControlMessage(videoNum);
			sharingBufferSingleton.saveVideoPack(os);
			// TODO sinhronizuj video plejer
			// Neka python prebaci u .mxf i poveze sa prethodnim		
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
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
