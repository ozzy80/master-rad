package com.kikkar.global;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import com.kikkar.packet.ControlMessage;
import com.kikkar.packet.VideoPacket;

public class SharingBufferSingleton {
	private ClockSingleton clock;
	private long sourcePlayerPastTime;

	private VideoPacket[] videoArray;
	private int minVideoNum;
	private static SharingBufferSingleton firstInstance;

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
		if (videoArray[i % Constants.BUFFER_SIZE] == null || videoArray[i % Constants.BUFFER_SIZE].getVideoNum() != i) {
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

	public void saveVideoPack(OutputStream os) throws IOException {
		if (!isHeadAtChunkStart()) {
			return;
		}

		int previousChunkNum = videoArray[minVideoNum].getChunkNum();
		int i = 0;
		while (true) {
			if (previousChunkNum > 0) {
				processMissingChunk(minVideoNum);
			}

			if (checkExitCondition(i, previousChunkNum)) {
				break;
			}

			byte[] video = videoArray[minVideoNum].getVideo().toByteArray();
			os.write(video, 0, video.length);

			cleanPreviousValue();
			previousChunkNum = videoArray[minVideoNum].getChunkNum();
			minVideoNum = (minVideoNum + 1) % Constants.BUFFER_SIZE;
			i++;
		}
	}

	private boolean checkExitCondition(int i, int previousChunkNum) {
		if (videoArray[minVideoNum] == null) {
			return true;
		} else if (videoArray[minVideoNum].getChunkNum() > previousChunkNum) {
			return true;
		} else if (i > Constants.BUFFER_SIZE) {
			return true;
		}
		return false;
	}

	private void cleanPreviousValue() {
		int previousVideoNum = getPreviousIndex();
		if (previousVideoNum >= 0) {
			videoArray[previousVideoNum] = null;
		}
	}

	private void processMissingChunk(int videoNum) {
		if (videoArray[videoNum] == null) {
			int previousVideoNum = getPreviousIndex();
			if (previousVideoNum >= 0) {
				videoArray[videoNum] = videoArray[previousVideoNum];
				videoArray[previousVideoNum] = null;
			}
		}
	}

	private int getPreviousIndex() {
		int previousVideoNum = minVideoNum - 1 < 0 ? Constants.BUFFER_SIZE - 1 : minVideoNum - 1;
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

	public void synchronizeVideoPlayTime(ControlMessage controlMessage) {
		long currentPlayerDelay = clock.getcurrentTimeMilliseconds() - controlMessage.getTimeInMilliseconds();
		// if() Proveri da li je plejer pusten
		// ubrzava/usporava/ne dira se plejer da stigne do prethodno dobijene vrednosti
		// else
		sourcePlayerPastTime = controlMessage.getPlayerElapsedTime();
		// pokreni plejer
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

	public void saveVideoPackIntoFile() {
		try (OutputStream os = new FileOutputStream(new File(Constants.OUTPUT_VIDEO_FILE_PATH), true)) {
			saveVideoPack(os);
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

	public long getSourcePlayerPastTime() {
		return sourcePlayerPastTime;
	}

	public void setSourcePlayerPastTime(long sourcePlayerPastTime) {
		this.sourcePlayerPastTime = sourcePlayerPastTime;
	}

}
