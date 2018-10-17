package com.kikkar.video.impl;

import java.awt.Canvas;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.internal.libvlc_state_t;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class VLCPlayer {
	private EmbeddedMediaPlayer mediaPlayer;
	private String mediaPath;

	public VLCPlayer() {
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files\\VideoLAN\\VLC");
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
	}
	
	public void setupVLCPlayer(Canvas canvas) {
		List<String> vlcArgs = new ArrayList<String>();
		vlcArgs.add("--no-plugins-cache");
		vlcArgs.add("--no-video-title-show");
		vlcArgs.add("--no-snapshot-preview");

		MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));

		mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
		mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(canvas));
	}

	public void playVideo() {
		mediaPlayer.playMedia(mediaPath);
	}

	public int getCurrentPlayTime() {
		return (int) mediaPlayer.getTime();
	}

	public void synchronizeVideo(int sourceVideoTime) {
		int timeDiff = (int) (getCurrentPlayTime() - sourceVideoTime);

		if(timeDiff < 500) {
			return;
		} else if (timeDiff < 2000) {
			setVideoSpeed(1.5f, (int) Math.round(timeDiff * 1.5));
		} else {
			setVideoSpeed(2f, timeDiff);
		} 
	}
	
	public boolean isVideoPlaying() {
		return mediaPlayer.getMediaPlayerState().equals(libvlc_state_t.libvlc_Ended);
	}

	private void setVideoSpeed(float rate, int changeDurationMilliseconds) {
		mediaPlayer.setRate(rate);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				mediaPlayer.setRate(1);
			}
		}, changeDurationMilliseconds);
	}

	public String getMediaPath() {
		return mediaPath;
	}

	public void setMediaPath(String mediaPath) {
		this.mediaPath = mediaPath;
	}

}