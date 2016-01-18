package com.jung.beat.main;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.jung.beat.screen.MainMenuScreen;
import com.jung.framework.impl.GLGame;
import com.jung.framework.intf.Screen;

public class BeatsGame extends GLGame {

	boolean firstTimeCreate = true;

	public Screen getStartScreen() {
		return new MainMenuScreen(this);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);
		if (firstTimeCreate) {
//			Settings.load(getFileIO());
			Assets.load(this);
			firstTimeCreate = false;
		} else {
			Assets.reload();
		}
	}

}
