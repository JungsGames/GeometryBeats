package com.jung.beat.screen;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;


import com.jung.beat.main.Assets;
import com.jung.framework.impl.GLScreen;
import com.jung.framework.intf.Game;
import com.jung.framework.intf.Input.TouchEvent;
import com.jung.framework.util.Animation;
import com.jung.framework.util.Camera2D;
import com.jung.framework.util.Rectangle;
import com.jung.framework.util.SpriteBatcher;
import com.jung.framework.util.TextureRegion;
import com.jung.framework.util.Vector2;

public class LevelScreen extends GLScreen {
//	BannerListener bannerListener;

	private String line = "1 2 3 4 5";
	
	Camera2D guiCam;
	SpriteBatcher batcher;
	Vector2 touchPoint;
	Rectangle lvlOneBounds, lvlTwoBounds, lvlThreeBounds, lvlFourBounds;
	Rectangle backBounds, settingsBounds;
	
	TextureRegion keyFrame;
	private int tickTime;

	public LevelScreen(Game game) {
		super(game);
//		bannerListener = (BannerListener) game;
//		bannerListener.showBanner(true);
		tickTime = 3000;
		
		guiCam = new Camera2D(glGraphics, 1280, 800);
		batcher = new SpriteBatcher(glGraphics, 100);
		lvlOneBounds = new Rectangle(260, 325, 100, 100);
		lvlTwoBounds = new Rectangle(420, 325, 100, 100);
		lvlThreeBounds = new Rectangle(580, 325, 100, 100);
		lvlFourBounds = new Rectangle(740, 325, 100, 100);
		backBounds = new Rectangle(0, 5, 100, 100);
		settingsBounds = new Rectangle(1160, 5, 100, 100);
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
				if (inBounds(lvlOneBounds, touchPoint)) {
					game.setScreen(new LevelOneScreen(game));
//					bannerListener.showBanner(false);
					return;
				}
				
				if (inBounds(lvlTwoBounds, touchPoint)) {
					game.setScreen(new LevelTwoScreen(game));
//					bannerListener.showBanner(false);
					return;
				}
				
				if (inBounds(lvlThreeBounds, touchPoint)) {
					game.setScreen(new LevelThreeScreen(game));
//					bannerListener.showBanner(false);
					return;
				}
				
				if (inBounds(lvlFourBounds, touchPoint)) {
					game.setScreen(new LevelFourScreen(game));
//					bannerListener.showBanner(false);
					return;
				}
				
//				if (inBounds(lvlFourBounds, touchPoint)) {
//					game.setScreen(new LevelThreeScreen(game));
//					bannerListener.showBanner(false);
//					return;
//				}
				
				if (inBounds(settingsBounds, touchPoint)) {
					game.setScreen(new SettingScreen(game));
					return;
				}
				
				if (inBounds(backBounds, touchPoint)) {
					game.setScreen(new MainMenuScreen(game));
					return;
				}
//				if (inBounds(event, 540, 660, 200, 80)) {
//					Uri uri = Uri.parse("market://details?id="
//							+ ((AndroidGame) game).getPackageName());
//					Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//					try {
//						((AndroidGame) game).startActivity(goToMarket);
//					} catch (ActivityNotFoundException e) {
//						((AndroidGame) game)
//								.startActivity(new Intent(
//										Intent.ACTION_VIEW,
//										Uri.parse("http://play.google.com/store/apps/details?id="
//												+ ((AndroidGame) game)
//														.getPackageName())));
//					}
//				}
			
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
		batcher.drawSprite(Assets.level, 520+120, 560+40, 240, 80); // 360-200
		batcher.drawSprite(Assets.arrowButton, 0+50, 5+50, 100, 100); // 655-80
		batcher.drawSprite(Assets.settingsButton, 1180+40, 10+40, 80, 80);

		
//		batcher.drawSprite(Assets.blueRect, lvlOneBounds.lowerLeft.x+(lvlOneBounds.width/2), lvlOneBounds.lowerLeft.y + (lvlOneBounds.height/2), lvlOneBounds.width, lvlOneBounds.height); 
//		batcher.drawSprite(Assets.blueRect, lvlTwoBounds.lowerLeft.x+(lvlTwoBounds.width/2), lvlTwoBounds.lowerLeft.y + (lvlTwoBounds.height/2), lvlTwoBounds.width, lvlOneBounds.height); 
//		batcher.drawSprite(Assets.blueRect, lvlThreeBounds.lowerLeft.x+(lvlThreeBounds.width/2), lvlThreeBounds.lowerLeft.y + (lvlThreeBounds.height/2), lvlThreeBounds.width, lvlOneBounds.height); 
//		batcher.drawSprite(Assets.blueRect, lvlFourBounds.lowerLeft.x+(lvlFourBounds.width/2), lvlFourBounds.lowerLeft.y + (lvlFourBounds.height/2), lvlFourBounds.width, lvlOneBounds.height); 
		
		Assets.font.drawText(batcher, line, 310, 375);
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
		return "LevelScreen";
	}

}
