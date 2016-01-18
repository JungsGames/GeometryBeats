package com.jung.framework.intf;

public abstract class Screen {
    protected final Game game;

    public Screen(Game game) {
        this.game = game;
    }

    public abstract void update();

    public abstract void present();

    public abstract void pause();

    public abstract void resume();

    public abstract void dispose();
}
