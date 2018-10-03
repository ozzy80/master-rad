package com.kikkar.global;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.kikkar.packet.VideoPacket;

public class SharingBufferSingleton {

	private int MAX_ELEMENT_NUMBER = 6_000; // 6s je max 4_000 delova + jos malo preko
	private int VIDEO_DURATION_SECOND = 6;
	private int INITIA_VIDEO_DELAY_SECOND = 8;
	//private String videoFile = "output.mxf";
	private String videoFile = "output.mov";

	private OutputStream os;
	private VideoPacket[] videoArray = new VideoPacket[MAX_ELEMENT_NUMBER];
	private int minVideoNum;
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private static SharingBufferSingleton firstInstance;

	private SharingBufferSingleton() {
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
		videoArray[i % MAX_ELEMENT_NUMBER] = videoContent;
	}

	public boolean isVideoPresent(int i) {
		if (i < 0) {
			throw new ArrayIndexOutOfBoundsException(i);
		}
		if (videoArray[i % MAX_ELEMENT_NUMBER] == null || videoArray[i % MAX_ELEMENT_NUMBER].getVideoNum() != i) {
			return false;
		}
		return true;
	}

	public VideoPacket getVideoPacket(int i) {
		if (i < 0) {
			throw new ArrayIndexOutOfBoundsException(i);
		}
		return videoArray[i % MAX_ELEMENT_NUMBER];
	}

	public void saveVideoPack(OutputStream os) throws IOException {
		int previousChunkNum = videoArray[minVideoNum].getChunkNum();
		int i = 0;
		while (true) {
			processMissingChunk(minVideoNum);

			if(checkExitCondition(i, previousChunkNum)) {
				break;
			}

			byte[] video = videoArray[minVideoNum].getVideo().toByteArray();
			os.write(video, 0, video.length);

			cleanPreviousValue();
			previousChunkNum = videoArray[minVideoNum].getChunkNum();
			minVideoNum = (minVideoNum + 1) % MAX_ELEMENT_NUMBER;
			i++;
		}
	}

	private boolean checkExitCondition(int i, int previousChunkNum) {
		if (videoArray[minVideoNum] == null) {
			return true;
		} else if (videoArray[minVideoNum].getChunkNum() > previousChunkNum) {
			return true;
		} else if (i > MAX_ELEMENT_NUMBER) {
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
			}
		}
	}

	private int getPreviousIndex() {
		int previousVideoNum = minVideoNum - 1 < 0 ? MAX_ELEMENT_NUMBER - 1 : minVideoNum - 1;
		if (videoArray[previousVideoNum] == null) {
			return -1;
		}
		return previousVideoNum;
	}
	
	public int getNumberOfBufferedVideoContent() {
		int start = minVideoNum;
		int presentVideoNum = 0;
		for (int i = 0; i < videoArray.length; i++) {
			if(isVideoPresent(start)) {
				presentVideoNum++;
			}
			start = (start + 1) % MAX_ELEMENT_NUMBER;
		}
		return presentVideoNum;
	}

	public void synchronizeVideoPlayTime(long controlMessageTime) {
		// TODO pomeri video player ako treba da se slaze sa kontrolnom porukom
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + MAX_ELEMENT_NUMBER;
		result = prime * result + VIDEO_DURATION_SECOND;
		result = prime * result + minVideoNum;
		result = prime * result + Arrays.hashCode(videoArray);
		result = prime * result + ((videoFile == null) ? 0 : videoFile.hashCode());
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
		if (MAX_ELEMENT_NUMBER != other.MAX_ELEMENT_NUMBER)
			return false;
		if (VIDEO_DURATION_SECOND != other.VIDEO_DURATION_SECOND)
			return false;
		if (minVideoNum != other.minVideoNum)
			return false;
		if (!Arrays.equals(videoArray, other.videoArray))
			return false;
		if (videoFile == null) {
			if (other.videoFile != null)
				return false;
		} else if (!videoFile.equals(other.videoFile))
			return false;
		return true;
	}

	private void automaticIncrement() {
		scheduler.scheduleAtFixedRate(() -> {
			OutputStream os = null;
			try {
				os = new FileOutputStream(new File(videoFile), true);
				if (videoArray[minVideoNum] != null) {
					saveVideoPack(os);
				}
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
			} catch (IOException e) {
				System.err.println(e.getMessage());
			} finally {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}, INITIA_VIDEO_DELAY_SECOND, VIDEO_DURATION_SECOND, TimeUnit.SECONDS);
	}

	public int getMAX_ELEMENT_NUMBER() {
		return MAX_ELEMENT_NUMBER;
	}

	public void setMAX_ELEMENT_NUMBER(int MAX_ELEMENT_NUMBER) {
		this.MAX_ELEMENT_NUMBER = MAX_ELEMENT_NUMBER;
	}

	public int getVIDEO_DURATION_SECOND() {
		return VIDEO_DURATION_SECOND;
	}

	public void setVIDEO_DURATION_SECOND(int vIDEO_DURATION_SECOND) {
		VIDEO_DURATION_SECOND = vIDEO_DURATION_SECOND;
	}

	public int getINITIA_VIDEO_DELAY_SECOND() {
		return INITIA_VIDEO_DELAY_SECOND;
	}

	public void setINITIA_VIDEO_DELAY_SECOND(int iNITIA_VIDEO_DELAY_SECOND) {
		INITIA_VIDEO_DELAY_SECOND = iNITIA_VIDEO_DELAY_SECOND;
	}

	public String getVideoFile() {
		return videoFile;
	}

	public void setVideoFile(String videoFile) {
		this.videoFile = videoFile;
	}

	public OutputStream getOs() {
		return os;
	}

	public void setOs(OutputStream os) {
		this.os = os;
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
		this.minVideoNum = minVideoNum % MAX_ELEMENT_NUMBER;
	}

}
