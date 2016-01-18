package com.jung.beat.screen;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.jung.beat.main.Assets;
import com.jung.beat.main.Settings;
import com.jung.framework.impl.GLScreen;
import com.jung.framework.intf.Game;
import com.jung.framework.intf.Input.TouchEvent;
import com.jung.framework.util.Animation;
import com.jung.framework.util.Camera2D;
import com.jung.framework.util.Rectangle;
import com.jung.framework.util.SpriteBatcher;
import com.jung.framework.util.TextureRegion;
import com.jung.framework.util.Vector2;

public class SettingScreen extends GLScreen {
	String lines[] = new String[5];
//	BannerListener bannerListener;
	
	Camera2D guiCam;
	SpriteBatcher batcher;
	Vector2 touchPoint;
	Rectangle blueBounds, greenBounds, yellowBounds, redBounds, pinkBounds;
	
	TextureRegion keyFrame;
	private int tickTime;

	public SettingScreen(Game game) {
		super(game);
//		bannerListener = (BannerListener) game;
//		bannerListener.showBanner(false);
		tickTime = 6000;
		
		guiCam = new Camera2D(glGraphics, 1280, 800);
		batcher = new SpriteBatcher(glGraphics, 100);
		blueBounds = new Rectangle(260, 325, 100, 100);
		greenBounds = new Rectangle(420, 325, 100, 100);
		yellowBounds = new Rectangle(580, 325, 100, 100);
		redBounds = new Rectangle(740, 325, 100, 100);
		pinkBounds = new Rectangle(900, 325, 100, 100);
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
				if (inBounds(blueBounds, touchPoint)) {
					Assets.playerDefault = Assets.playerBlue;
					Assets.boxDefault = Assets.boxBlue;
					Settings.playerColor = Settings.BLUE;
					Settings.save(game.getFileIO());
//					bannerListener.showBanner(true);
					game.setScreen(new LevelScreen(game));
					return;
				}
				
				if (inBounds(greenBounds, touchPoint)) {
					Assets.playerDefault = Assets.playerGreen;
					Assets.boxDefault = Assets.boxGreen;
					Settings.playerColor = Settings.GREEN;
					Settings.save(game.getFileIO());
//					bannerListener.showBanner(true);
					game.setScreen(new LevelScreen(game));
					return;
				}
				
				if (inBounds(yellowBounds, touchPoint)) {
					Assets.playerDefault = Assets.playerYellow;
					Assets.boxDefault = Assets.boxYellow;
					Settings.playerColor = Settings.YELLOW;
					Settings.save(game.getFileIO());
//					bannerListener.showBanner(true);
					game.setScreen(new LevelScreen(game));
					return;
				}
				
				if (inBounds(redBounds, touchPoint)) {
					Assets.playerDefault = Assets.playerRed;
					Assets.boxDefault = Assets.boxRed;
					Settings.playerColor = Settings.RED;
					Settings.save(game.getFileIO());
//					bannerListener.showBanner(true);
					game.setScreen(new LevelScreen(game));
					return;
				}
				
				if (inBounds(pinkBounds, touchPoint)) {
					Assets.playerDefault = Assets.playerPink;
					Assets.boxDefault = Assets.boxPink;
					Settings.playerColor = Settings.PINK;
					Settings.save(game.getFileIO());
//					bannerListener.showBanner(true);
					game.setScreen(new LevelScreen(game));
					return;
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
		
		batcher.drawSprite(Assets.playerBlue, blueBounds.lowerLeft.x+(blueBounds.width/2), blueBounds.lowerLeft.y + (blueBounds.height/2), blueBounds.width, blueBounds.height); 
		batcher.drawSprite(Assets.playerGreen, greenBounds.lowerLeft.x+(greenBounds.width/2), greenBounds.lowerLeft.y + (greenBounds.height/2), greenBounds.width, greenBounds.height); 
		batcher.drawSprite(Assets.playerYellow, yellowBounds.lowerLeft.x+(yellowBounds.width/2), yellowBounds.lowerLeft.y + (yellowBounds.height/2), yellowBounds.width, yellowBounds.height); 
		batcher.drawSprite(Assets.playerRed, redBounds.lowerLeft.x+(redBounds.width/2), redBounds.lowerLeft.y + (redBounds.height/2), redBounds.width, redBounds.height); 
		batcher.drawSprite(Assets.playerPink, pinkBounds.lowerLeft.x+(pinkBounds.width/2), pinkBounds.lowerLeft.y + (pinkBounds.height/2), pinkBounds.width, pinkBounds.height); 
		
		batcher.endBatch();

		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	

}
