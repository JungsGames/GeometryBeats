package com.jung.beat.screen;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import com.jung.beat.main.Assets;
import com.jung.framework.impl.GLGame;
import com.jung.framework.impl.GLScreen;
import com.jung.framework.intf.BannerListener;
import com.jung.framework.intf.Game;
import com.jung.framework.intf.Input.TouchEvent;
import com.jung.framework.util.Animation;
import com.jung.framework.util.Camera2D;
import com.jung.framework.util.Rectangle;
import com.jung.framework.util.SpriteBatcher;
import com.jung.framework.util.TextureRegion;
import com.jung.framework.util.Vector2;

public class MainMenuScreen extends GLScreen {
	BannerListener bannerListener;

	Camera2D guiCam;
	SpriteBatcher batcher;
	Rectangle playBounds;
	Rectangle rateBounds;
	Vector2 touchPoint;
	
	TextureRegion keyFrame;
	private int tickTime;

	public MainMenuScreen(Game game) {
		super(game);
		bannerListener = (BannerListener) game;
		bannerListener.showBanner(false);
		tickTime = 0;

		guiCam = new Camera2D(glGraphics, 1280, 800);
		batcher = new SpriteBatcher(glGraphics, 100);
		// The origin is in the bottom-left
		playBounds = new Rectangle(640 - 100, 145, 200, 80);
		rateBounds = new Rectangle(640 - 100, 20, 200, 80);
		touchPoint = new Vector2();
	}

	@Override
	public void update() {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				touchPoint.set(event.x, event.y);
				guiCam.touchToWorld(touchPoint);

				if (inBounds(playBounds, touchPoint)) {
					game.setScreen(new LevelScreen(game));
					return;
				}
				// if (inBounds(event, 540, 400 + 100, 212, 70)) {
				// game.setScreen(new HelpScreen(game));
				// return;
				// }
				if (inBounds(rateBounds, touchPoint)) {
					Uri uri = Uri.parse("market://details?id="
							+ ((GLGame) game).getPackageName());
					Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
					try {
						((GLGame) game).startActivity(goToMarket);
					} catch (ActivityNotFoundException e) {
						((GLGame) game)
								.startActivity(new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("http://play.google.com/store/apps/details?id="
												+ ((GLGame) game)
														.getPackageName())));
					}
				}
			}
		}
	}

	@Override
	public void present() {
		tickTime += 17;
		GL10 gl = glGraphics.getGL();
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		guiCam.setViewportAndMatrices();

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		batcher.beginBatch(Assets.texture);
		keyFrame = Assets.line.getKeyFrame(tickTime, Animation.ANIMATION_LOOPING);
		batcher.drawSprite(keyFrame, 0+640, 235+2, 1280, 5);
		batcher.drawSprite(Assets.logo, 640, 525, 590, 400); // 540
		batcher.drawSprite(Assets.mainmenu, 640, 120, 200, 190); // 145
		batcher.endBatch();

		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

	@Override
	public String toString() {
		return "MainMenuScreen";
	}

}
