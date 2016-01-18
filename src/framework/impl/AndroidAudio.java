package com.jung.framework.impl;

import java.io.IOException;

import com.jung.framework.intf.Audio;
import com.jung.framework.intf.Music;
import com.jung.framework.intf.Sound;
import com.jung.framework.util.AndroidMusic;
import com.jung.framework.util.AndroidSound;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;



public class AndroidAudio implements Audio {
    AssetManager assets;
    SoundPool soundPool;

    public AndroidAudio(Activity activity) {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.assets = activity.getAssets();
        this.soundPool = buildSoundPool();
    }

    public Music newMusic(String filename) {
        try {
            AssetFileDescriptor assetDescriptor = assets.openFd(filename);
            return new AndroidMusic(assetDescriptor);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load music '" + filename + "'");
        }
    }
    
    public Sound newSound(String filename) {
        try {
            AssetFileDescriptor assetDescriptor = assets.openFd(filename);
            int soundId = soundPool.load(assetDescriptor, 0);
            return new AndroidSound(soundPool, soundId);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load sound '" + filename + "'");
        }
    }
    
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private SoundPool buildSoundPool() {
    	SoundPool soundPool;
    	
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                  .setUsage(AudioAttributes.USAGE_GAME)
                                  .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                  .build();
                                  
          soundPool = new SoundPool.Builder()
                      .setMaxStreams(25)
                      .setAudioAttributes(audioAttributes)
                      .build();
       } else {
          soundPool = new SoundPool(25, AudioManager.STREAM_MUSIC, 0);
       }
       return soundPool;
    }
}