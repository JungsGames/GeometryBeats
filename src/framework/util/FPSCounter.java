package com.jung.framework.util;

import android.util.Log;

public class FPSCounter {
	public static long startTime = System.nanoTime();
	public static int frames = 0;

	public static void logFrame() {
		frames++;
		if (System.nanoTime() - startTime >= 1000000000) {
			Log.d("Game", "fps: " + frames);
			frames = 0;
			startTime = System.nanoTime();
		}
	}
}
