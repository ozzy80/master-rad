package com.kikkar.video.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.google.protobuf.ByteString;
import com.kikkar.global.ClockSingleton;
import com.kikkar.global.Constants;
import com.kikkar.global.SharingBufferSingleton;
import com.kikkar.packet.VideoPacket;
import com.kikkar.schedule.UploadScheduler;
import com.kikkar.video.SourceVideoLoader;

public class SourceVideoLoaderImpl implements SourceVideoLoader {

	private int videoBufferSize = 1450;
	private int videoNum;
	private UploadScheduler uploadScheduler;
	private ClockSingleton clock = ClockSingleton.getInstance();
	private SharingBufferSingleton sharingBufferSingleton = SharingBufferSingleton.getInstance();

	@Override
	public void loadVideo(String inputVideoPath) {
		int i = 0;
		long startTime;
		while (true) {
			File videoFile = new File(inputVideoPath + "izlaz" + i++ + ".mov");
			try (InputStream is = new FileInputStream(videoFile)) {
				startTime = clock.getcurrentTimeMilliseconds();
				int chunkNum = (int) videoFile.length() / videoBufferSize;
				readChunk(is, chunkNum);
				try {
					Thread.sleep(
							Constants.VIDEO_DURATION_SECOND * 100 / (clock.getcurrentTimeMilliseconds() - startTime));
				} catch (InterruptedException e) {
					System.err.println(e.getMessage());
				}
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public void readChunk(InputStream is, int chunkNum) throws IOException {
		byte[] buffer = new byte[videoBufferSize];
		boolean firstFrame = true;
		while (is.read(buffer) > 0) {
			VideoPacket.Builder video = VideoPacket.newBuilder();
			video.setVideoNum(videoNum);
			video.setChunkNum(chunkNum--);
			video.setFirstFrame(firstFrame);
			video.setVideo(ByteString.copyFrom(buffer));
			sharingBufferSingleton.addVideoPacket(videoNum, video.build());

			uploadScheduler.sendVideo(videoNum, new ArrayList<>());
			firstFrame = false;
			videoNum = ++videoNum < 0 ? 0 : videoNum++;
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

}
