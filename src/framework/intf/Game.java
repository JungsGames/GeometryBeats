package com.jung.framework.intf;

import com.jung.framework.impl.GLGraphics;

public interface Game {
    public Input getInput();

    public FileIO getFileIO();

    public GLGraphics getGLGraphics();

    public Audio getAudio();

    public void setScreen(Screen screen);

    public Screen getCurrentScreen();

    public Screen getStartScreen();
    
}