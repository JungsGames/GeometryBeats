package com.jung.framework.util;

public class Animation {
	public static final int ANIMATION_LOOPING = 0;
	public static final int ANIMATION_NONLOOPING = 1;
	final TextureRegion[] keyFrames;
	final int frameDuration;

	public Animation(int frameDuration, TextureRegion... keyFrames) {
		this.frameDuration = frameDuration;
		this.keyFrames = keyFrames;
	}

	public TextureRegion getKeyFrame(int stateTime, int mode) {
		int frameNumber = stateTime / frameDuration;
		if (mode == ANIMATION_NONLOOPING) {
			frameNumber = Math.min(keyFrames.length - 1, frameNumber);
		} else {
			frameNumber = frameNumber % keyFrames.length;
		}
		return keyFrames[frameNumber];
	}

}
