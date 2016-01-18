package com.jung.framework.intf;

public interface Music {
    public void play();

    public void stop();

    public void pause();
    
    public void seekTo (int msec);
    
    public int getCurrentPosition();
    
    public int getDuration();

    public void setLooping(boolean looping);

    public void setVolume(float volume);

    public boolean isPlaying();

    public boolean isStopped();

    public boolean isLooping();

    public void dispose();
}
