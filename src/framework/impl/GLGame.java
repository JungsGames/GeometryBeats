package com.jung.framework.impl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.jung.beat.screen.MainMenuScreen;
import com.jung.framework.intf.Audio;
import com.jung.framework.intf.BannerListener;
import com.jung.framework.intf.FileIO;
import com.jung.framework.intf.Game;
import com.jung.framework.intf.Input;
import com.jung.framework.intf.InterstitialListener;
import com.jung.framework.intf.Screen;

public class GLGame extends Activity implements Game, Renderer,
		InterstitialListener, BannerListener {
	enum GLGameState {
		Initialized, Running, Paused, Finished, Idle
	}

	RelativeLayout layout;
	GLSurfaceView glView;
	GLGraphics glGraphics;
	Audio audio;
	Input input;
	FileIO fileIO;
	Screen screen;
	DisplayMetrics displayMetrics;
	GLGameState state = GLGameState.Initialized;
	Object stateChanged = new Object();
	long startTime = SystemClock.uptimeMillis();

	AdView adView;
	InterstitialAd interstitialAd;
	private final int SHOW_ADS = 1;
	private final int HIDE_ADS = 0;
	boolean isVisible = false;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_ADS: {
				adView.setVisibility(View.VISIBLE);
				isVisible = true;
				break;
			}
			case HIDE_ADS: {
				adView.setVisibility(View.GONE);
				isVisible = false;
				break;
			}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		layout = new RelativeLayout(this);
		glView = new GLSurfaceView(this);
		glView.setEGLConfigChooser(8,8,8,8,16,0);
		glView.setRenderer(this);

		glGraphics = new GLGraphics(glView);
		fileIO = new AndroidFileIO(this);
		audio = new AndroidAudio(this);
		input = new AndroidInput(this, glView, 1, 1);
		
		layout.addView(glView);

		adView = new AdView(this);
		AdSize adSize = AdSize.SMART_BANNER;

		double width = displayMetrics.widthPixels;

		if (width > 728) { // > 728 X 90
			adSize = AdSize.LEADERBOARD;
		} else if (width > 468) { // > 468 X 60
			adSize = AdSize.LARGE_BANNER;
		} else { // > 320 X 50
			adSize = AdSize.BANNER;
		}

		adView.setAdSize(adSize);
		adView.setAdUnitId("ca-app-pub-4022879704675666/7704445231");

		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId("ca-app-pub-4022879704675666/9181178439");

		AdRequest adRequest = new AdRequest.Builder().build();

		interstitialAd.loadAd(adRequest);
		adView.loadAd(adRequest);

		interstitialAd.setAdListener(new AdListener() {

			@Override
			public void onAdClosed() {
				AdRequest adRequest = new AdRequest.Builder().build();
				interstitialAd.loadAd(adRequest);
				getCurrentScreen();
			}
		});
		
		RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		adParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		layout.addView(adView, adParams);
		setContentView(layout);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public void onResume() {
		super.onResume();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		glView.onResume();
	}

	@Override
	public void onPause() {
		synchronized (stateChanged) {
			if (isFinishing()) {
				state = GLGameState.Finished;
			}
			else {
				state = GLGameState.Paused;
			}
			while (true) {
				try {
					stateChanged.wait();
					break;
				} catch (InterruptedException e) {
				}
			}
		}

		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		glView.onPause(); // gl Context is being lost
		super.onPause();
		if (isFinishing()) {
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		
	}

	@Override
	public GLGraphics getGLGraphics() {
		return glGraphics;
	}

	@Override
	public Input getInput() {
		return input;
	}

	@Override
	public FileIO getFileIO() {
		return fileIO;
	}

	@Override
	public Audio getAudio() {
		return audio;
	}

	@Override
	public void setScreen(Screen screen) {
		if (screen == null)
			throw new IllegalArgumentException("Screen must not be null");

		this.screen.pause();
		this.screen.dispose();
		screen.resume();
		screen.update();
		this.screen = screen;
	}

	@Override
	public Screen getCurrentScreen() {
		return screen;
	}

	@Override
	public Screen getStartScreen() {
		return null;
	}
// RENDERING THREAD STARTS HERE
	@Override
	public void onDrawFrame(GL10 gl) {
		GLGameState state = null;

		synchronized (stateChanged) {
			state = this.state;
		}

		if (state == GLGameState.Running) {
			long deltaTime = SystemClock.uptimeMillis() - startTime;
			startTime = SystemClock.uptimeMillis();
			// FPSCounter.logFrame();

			screen.update();
			screen.present();
			if (deltaTime < 17) {
				try {
					Thread.sleep(17 - deltaTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

		if (state == GLGameState.Paused) {
			screen.pause();
			synchronized (stateChanged) {
				this.state = GLGameState.Idle;
				stateChanged.notifyAll();
			}
		}
		if (state == GLGameState.Finished) {
			screen.pause();
			screen.dispose();
			synchronized (stateChanged) {
				this.state = GLGameState.Idle;
				stateChanged.notifyAll();
			}
		}

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glGraphics.setGL(gl);

		synchronized (stateChanged) {
			if (state == GLGameState.Initialized)
				screen = getStartScreen();
			state = GLGameState.Running;
			screen.resume();
			startTime = SystemClock.uptimeMillis();
		}
	}
	
// RENDERING THREAD ENDS HERE

	@Override
	public void onBackPressed() {
		if (screen.toString().equals("MainMenuScreen")) {
			// THIS BLOCK WILL BE CALLED IF ABOVE COND IS TRUE, AND WOULD ENABLE
			// BACK BUTTON
			super.onBackPressed();

		} else {
			if (screen.toString().equals("LevelScreen"))// THIS BLOCK WILL NOT
														// DO ANYTHING AND WOULD
														// DISABLE BACK BUTTON
				setScreen(new MainMenuScreen(this));
		}
	}

	@Override
	public void showBanner(boolean show) {
		if (show && !isVisible)
			handler.sendEmptyMessage(SHOW_ADS);
		if (!show)
			handler.sendEmptyMessage(HIDE_ADS);
	}

	@Override
	public void showInterstitial() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				displayInterstitial();
			}
		});
	}

	public void displayInterstitial() {
		if (interstitialAd.isLoaded())
			interstitialAd.show();
	}
}
