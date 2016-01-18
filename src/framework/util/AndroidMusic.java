package com.jung.framework.util;

import java.io.IOException;

import com.jung.framework.intf.Music;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;


public class AndroidMusic implements Music, OnCompletionListener {
    MediaPlayer mediaPlayer;
    boolean isPrepared = false;

    public AndroidMusic(AssetFileDescriptor assetDescriptor) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(assetDescriptor.getFileDescriptor(),
                    assetDescriptor.getStartOffset(),
                    assetDescriptor.getLength());
            mediaPlayer.prepare();
            isPrepared = true;
            mediaPlayer.setOnCompletionListener(this);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't load music");
        }
    }
    
    public void play() {
        if (mediaPlayer.isPlaying())
            return;
        try {
            synchronized (this) {
                if (!isPrepared){
                	mediaPlayer.prepare();
                	isPrepared = true;
                }
                mediaPlayer.start();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void pause() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }
    
    public void stop() {
        mediaPlayer.stop();
        synchronized (this) {
            isPrepared = false;
        }
    }

    public void setLooping(boolean isLooping) {
        mediaPlayer.setLooping(isLooping);
    }

    public void setVolume(float volume) {
        mediaPlayer.setVolume(volume, volume);
    }
    
    public boolean isLooping() {
        return mediaPlayer.isLooping();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public boolean isStopped() {
        return !isPrepared;
    }
    

    public void onCompletion(MediaPlayer player) {
        synchronized (this) {
            isPrepared = false;
        }
    }
    
    public void dispose() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
        mediaPlayer.release();
    }

	@Override
	public void seekTo(int msec) {
		mediaPlayer.seekTo(msec);
	}

	@Override
	public int getCurrentPosition() {
		return mediaPlayer.getCurrentPosition();
		
	}

	@Override
	public int getDuration() {
		return mediaPlayer.getDuration();	
	}
}
