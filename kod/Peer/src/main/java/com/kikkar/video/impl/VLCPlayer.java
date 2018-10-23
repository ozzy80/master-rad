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
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "/usr/bin/vlc");
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
		if(!isVideoPlaying()) {
			mediaPlayer.playMedia(mediaPath);			
		}
	}

	public int getCurrentPlayTime() {
		return (int) mediaPlayer.getTime();
	}

	public void synchronizeVideo(int sourceVideoTime) {
		int timeDiff = sourceVideoTime/2;

		if(timeDiff < -500) {
			setVideoSpeed(2f, -timeDiff);
		} else if (timeDiff > 500) {
			setVideoSpeed(0.5f, timeDiff);
		} 
	}
	
	public boolean isVideoPlaying() {
		return mediaPlayer.getMediaPlayerState().equals(libvlc_state_t.libvlc_Playing);
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
	
	public void setPlayTime(long time) {
		mediaPlayer.setTime(time);
	}

}
