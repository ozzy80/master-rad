package com.kikkar.global;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;

import com.kikkar.packet.ControlMessage;
import com.kikkar.packet.VideoPacket;
import com.kikkar.video.impl.VLCPlayer;

public class SharingBufferSingleton {
	private ClockSingleton clock;
	private int sourcePlayerPastTime;

	private VideoPacket[] videoArray;
	private int minVideoNum;
	private static SharingBufferSingleton firstInstance;
	private VLCPlayer player;

	private SharingBufferSingleton() {
		clock = ClockSingleton.getInstance();
		videoArray = new VideoPacket[Constants.BUFFER_SIZE];
	}

	public static SharingBufferSingleton getInstance() {
		if (firstInstance == null) {
			firstInstance = new SharingBufferSingleton();
		}
		return firstInstance;
	}

	public void addVideoPacket(int i, VideoPacket videoContent) {
		if (i < 0) {
			throw new ArrayIndexOutOfBoundsException(i);
		}
		videoArray[i % Constants.BUFFER_SIZE] = videoContent;
	}

	public boolean isVideoPresent(int i) {
		if (i < 0) {
			throw new ArrayIndexOutOfBoundsException(i);
		}
		if (videoArray[i % Constants.BUFFER_SIZE] == null) {
			return false;
		}
		return true;
	}

	public boolean isHeadAtChunkStart() {
		if (isVideoPresent(minVideoNum)) {
			return videoArray[minVideoNum].getFirstFrame();
		}
		return false;
	}

	public VideoPacket getVideoPacket(int i) {
		if (i < 0) {
			throw new ArrayIndexOutOfBoundsException(i);
		}
		return videoArray[i % Constants.BUFFER_SIZE];
	}

	public void saveVideoPack(OutputStream os, int lastControlMessageVideNum) throws IOException {
		if (!isHeadAtChunkStart()) {
			resetOldVideoContent(lastControlMessageVideNum);
			return;
		}

		int currentVideoNum = videoArray[minVideoNum].getVideoNum();
		while (true) {
			if (currentVideoNum == lastControlMessageVideNum) {
				cleanPreviousValue();
				break;
			}

			if (videoArray[minVideoNum] == null) {
				processMissingChunk(minVideoNum);
			}

			byte[] video = videoArray[minVideoNum].getVideo().toByteArray();
			os.write(video, 0, video.length);

			cleanPreviousValue();
			minVideoNum = (minVideoNum + 1) % Constants.BUFFER_SIZE;
			currentVideoNum = (currentVideoNum + 1) < 0 ? 0 : currentVideoNum + 1;
		}
	}

	public void resetOldVideoContent(int lastControlMessageVideNum) {
		int nextMinVideoNumPosition = lastControlMessageVideNum % Constants.BUFFER_SIZE;
		while (true) {
			if (minVideoNum == nextMinVideoNumPosition) {
				cleanPreviousValue();
				break;
			}
			cleanPreviousValue();
			minVideoNum = (minVideoNum + 1) % Constants.BUFFER_SIZE;
		}
	}

	private void cleanPreviousValue() {
		int previousVideoNum = getPreviousIndex();
		if (previousVideoNum >= 0) {
			videoArray[previousVideoNum] = null;
		}
	}

	private void processMissingChunk(int videoNum) {
		int previousVideoNum = getPreviousIndex();
		if (previousVideoNum >= 0) {
			videoArray[videoNum] = videoArray[previousVideoNum];
			videoArray[previousVideoNum] = null;
		}
	}

	private int getPreviousIndex() {
		int previousVideoNum = 0;
		if (minVideoNum == 0) {
			previousVideoNum = Constants.BUFFER_SIZE - 1;
		} else {
			previousVideoNum = minVideoNum - 1;
		}

		if (videoArray[previousVideoNum] == null) {
			return -1;
		}
		return previousVideoNum;
	}

	public int getNumberOfBufferedVideoContent() {
		int start = minVideoNum;
		int presentVideoNum = 0;
		for (int i = 0; i < videoArray.length; i++) {
			if (isVideoPresent(start)) {
				presentVideoNum++;
			}
			start = (start + 1) % Constants.BUFFER_SIZE;
		}
		return presentVideoNum;
	}

	public void synchronizeVideoPlayTime(int currentVideoNum, ControlMessage controlMessage) {
		try {
			System.out.println("Usao " + new Date());

			prepareVideoForPlaying(currentVideoNum);

			int messageDelayTime = (int) (clock.getcurrentTimeMilliseconds() - controlMessage.getTimeInMilliseconds());
			if (player.isVideoPlaying()) {
				System.out.println("Ide");
				player.synchronizeVideo(
						controlMessage.getPlayerElapsedTime() - sourcePlayerPastTime + messageDelayTime);
			} else {
				System.out.println("Ne ide");

				sourcePlayerPastTime = controlMessage.getPlayerElapsedTime();
				player.setMediaPath(Constants.VIDEO_PLAY_FILE_PATH + "/play.mxf");
				player.playVideo();
				player.synchronizeVideo(sourcePlayerPastTime % 6000);
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
	}

	private void prepareVideoForPlaying(int currentChunkVideoNum) throws IOException, InterruptedException {
		String[] argsFFMPEG = new String[] { "ffmpeg", "-i",
				Constants.OUTPUT_VIDEO_FILE_PATH + "/movie" + currentChunkVideoNum + ".mov", "-vcodec", "mpeg2video",
				"-qscale", "1", "-qmin", "1", "-intra", "-ar", "48000", "-y",
				Constants.VIDEO_PLAY_FILE_PATH + "/izlaz-novi.mxf" };
		Process procFFMPEG = new ProcessBuilder(argsFFMPEG).start();
		procFFMPEG.waitFor();

		if (videoNotContainError(procFFMPEG)) {
			File source = new File(Constants.VIDEO_PLAY_FILE_PATH + "/izlaz-novi.mxf");
			File destination = new File(Constants.VIDEO_PLAY_FILE_PATH + "/izlaz.mxf");
			Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

		try (InputStream is = new FileInputStream(new File(Constants.VIDEO_PLAY_FILE_PATH + "/izlaz.mxf"));
				OutputStream os = new FileOutputStream(new File(Constants.VIDEO_PLAY_FILE_PATH + "/play.mxf"), true);) {
			appendFiles(is, os);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		// File source = new File(Constants.VIDEO_PLAY_FILE_PATH + "/play.xmf");
		// Files.write(source.getPath(), contentToAppend.getBytes(),
		// StandardOpenOption.APPEND);
		/*
		 * String[] argsCat = new String[] { "cat", Constants.VIDEO_PLAY_FILE_PATH +
		 * "/izlaz.mxf", ">>", Constants.VIDEO_PLAY_FILE_PATH + "/play.xmf" }; new
		 * ProcessBuilder(argsCat).start().waitFor();
		 */

		System.out.println("Zavrsio " + new Date());
	}

	private void appendFiles(InputStream is, OutputStream os) throws IOException {
		int length;
		byte[] buffer = new byte[4096];
		while ((length = is.read(buffer)) > 0) {
			os.write(buffer, 0, length);
		}
	}

	private boolean videoNotContainError(Process process) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.contains("Error")) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + minVideoNum;
		result = prime * result + Arrays.hashCode(videoArray);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SharingBufferSingleton other = (SharingBufferSingleton) obj;
		if (minVideoNum != other.minVideoNum)
			return false;
		if (!Arrays.equals(videoArray, other.videoArray))
			return false;
		return true;
	}

	public void saveVideoPackIntoFile(int num, int lastControlMessageVideNum) {
		try (OutputStream os = new FileOutputStream(
				new File(Constants.OUTPUT_VIDEO_FILE_PATH + "/movie" + num + ".mov"), true)) {
			saveVideoPack(os, lastControlMessageVideNum);
			os.flush();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public VideoPacket[] getVideoArray() {
		return videoArray;
	}

	public void setVideoArray(VideoPacket[] videoArray) {
		this.videoArray = videoArray;
	}

	public int getMinVideoNum() {
		return minVideoNum;
	}

	public void setMinVideoNum(int minVideoNum) {
		this.minVideoNum = minVideoNum % Constants.BUFFER_SIZE;
	}

	public int getSourcePlayerPastTime() {
		return sourcePlayerPastTime;
	}

	public void setSourcePlayerPastTime(int sourcePlayerPastTime) {
		this.sourcePlayerPastTime = sourcePlayerPastTime;
	}

	public VLCPlayer getPlayer() {
		return player;
	}

	public void setPlayer(VLCPlayer player) {
		this.player = player;
	}

}
