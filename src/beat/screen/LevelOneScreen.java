package com.jung.beat.screen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import com.jung.beat.main.Assets;
import com.jung.framework.impl.GLScreen;
import com.jung.framework.intf.BannerListener;
import com.jung.framework.intf.Game;
import com.jung.framework.intf.Input.KeyEvent;
import com.jung.framework.intf.Input.TouchEvent;
import com.jung.framework.intf.InterstitialListener;
import com.jung.framework.util.Animation;
import com.jung.framework.util.Camera2D;
import com.jung.framework.util.Rectangle;
import com.jung.framework.util.SpriteBatcher;
import com.jung.framework.util.TextureRegion;
import com.jung.framework.util.Vector2;

public class LevelOneScreen extends GLScreen {
	enum GameState {
		Ready, Running, Pause, GameClear
	}

	InterstitialListener interstitialListener;
	BannerListener bannerListener;

	GameState state = GameState.Ready;

	// object vars
	public static final int VELOCITY_X = 10; // Determines the speed of objects
	private static final int OBJECT_POS_X = 2400; // Determines objects' initial position
	private int playerX = 300; // Determines player's initial position
	
	private int startX; // Measures how far a player has gone
	private float startY; // Measures how high a player has reached
	
	private int boxX[] = new int[150];
	private int cloneBoxX[] = new int[150];
	private int saveBoxX[];
	private int boxY[] = new int[150];

	private int spikeX[] = new int[150];
	private int cloneSpikeX[] = new int[150];
	private int saveSpikeX[];
	private int spikeY[] = new int[150];

	private int pitPos[];
	private int clonePitPos[];
	private int savePitPos[];
	private int pitLen[];

	private int saveX[] = new int[10]; // coordinates of save rectangles
	private int cloneSaveX[] = new int[10];
	private int saveSaveX[]; // require for reset()
	private boolean saved[] = new boolean[10];

	private boolean boxOnScreen[] = new boolean[150];
	private boolean spkOnScreen[] = new boolean[150];
	private boolean pitOnScreen[];
	private boolean saveOnScreen[] = new boolean[10];

	private int lineY;
	private Rectangle pl = new Rectangle(0, 0, 0, 0);
	private Rectangle bx = new Rectangle(0, 0, 0, 0);
	private Rectangle sv = new Rectangle(0, 0, 0, 0);
	private Rectangle sp = new Rectangle(0, 0, 0, 0);
	private Rectangle sp2 = new Rectangle(0, 0, 0, 0);

	// game vars
	private int boxes;
	private int spikes;
	private int pits;
	private int saves;
	private boolean jump, land;
	private float velo;
	private int savePos;
	private int currentMusicPos;

	// special effect vars
	private int tickTime = 0; // accumulate deltaTime for color change

	private int attempts;
	private int angle; // for player rotation
	private String level = "level1.map";

	// OpenGL vars
	Camera2D guiCam;
	Vector2 touchPoint;
	SpriteBatcher batcher;
	Rectangle resumeBounds, quitBounds, exitBounds;
	TextureRegion keyFrame;
	private Random rand;
	


	public LevelOneScreen(Game game) {
		super(game);
		
		rand = new Random();
		guiCam = new Camera2D(glGraphics, 1280, 800);
		touchPoint = new Vector2();
		batcher = new SpriteBatcher(glGraphics, 1000);
		try {
			reset();
			loadMap(level);
		} catch (IOException e) {
			e.printStackTrace();
		}

		resumeBounds = new Rectangle(483, 450, 316, 90);
		quitBounds = new Rectangle(483, 330, 316, 90);
		exitBounds = new Rectangle(0, 690, 110, 110);
	}

	private void loadMap(String filename) throws IOException {
		ArrayList<String> lines = new ArrayList<String>();
		int mapWidth = 0;
		int mapHeight = 0;
		int numPit = 0;
		int count = 0;

		BufferedReader reader = new BufferedReader(new InputStreamReader(game
				.getFileIO().readAsset(filename)));

		while (true) {
			String line = reader.readLine();
			// no more lines to read
			if (line == null) {
				reader.close();
				break;
			}
			if (!line.startsWith(" ")) {

				if (count == 0) {
					
					numPit = Integer.parseInt(line.split(" ")[0]);
					pitPos = new int[numPit];
					clonePitPos = new int[numPit];
					pitLen = new int[numPit];
					pitOnScreen = new boolean[numPit];
					pits = numPit;
					count++;
				} else {
					// sets pit positions
					pitPos[count - 1] = OBJECT_POS_X + (Integer.parseInt(line
							.split(" ")[0]) - 1) * 60;
					clonePitPos[count - 1] = OBJECT_POS_X + (Integer.parseInt(line
							.split(" ")[0]) - 1) * 60;
					pitLen[count - 1] = Integer.parseInt(line.split(" ")[1]) * 60;

					count++;
				}
			} else {
				lines.add(line);
				mapWidth = Math.max(mapWidth, line.length());
			}
		}
		mapHeight = lines.size();

		for (int j = 0; j < mapHeight; j++) {
			String line = lines.get(j);
			for (int i = 0; i < mapWidth; i++) {

				if (i < line.length()) {
					if (line.charAt(i) == '1') {

						// starting pos + i * spacing factor
						boxX[boxes] = OBJECT_POS_X + i * 60;
						cloneBoxX[boxes] = OBJECT_POS_X + i * 60;
						boxY[boxes] = 240 + (mapHeight - j - 1) * 60;
						boxes++;
					} else if (line.charAt(i) == '2') {

						spikeX[spikes] = OBJECT_POS_X + i * 60;
						cloneSpikeX[spikes] = OBJECT_POS_X + i * 60;
						spikeY[spikes] = 240 + (mapHeight - j - 1) * 60;
						spikes++;
					} else if (line.charAt(i) == '3') {

						saveX[saves] = OBJECT_POS_X + i * 60;
						cloneSaveX[saves] = OBJECT_POS_X + i * 60;
						saves++;
					}
				}
			}
		}
	}

	@Override
	public void update() {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		List<KeyEvent> keyEvents = game.getInput().getKeyEvents();

		if (state == GameState.Ready)
			updateReady(keyEvents, touchEvents);
		if (state == GameState.Running)
			// Music starts
			updateRunning(keyEvents, touchEvents);
		if (state == GameState.Pause)
			// Music pauses
			updatePaused(keyEvents, touchEvents);
		if (state == GameState.GameClear)
			// Music pauses
			updateGameClear(keyEvents, touchEvents);
	}

	private void updateReady(List<KeyEvent> keyEvents,
			List<TouchEvent> touchEvents) {

		int len = keyEvents.size();
		for (int i = 0; i < len; i++) {
			KeyEvent keyEvent = keyEvents.get(i);
			if (keyEvent.keyCode == android.view.KeyEvent.KEYCODE_BACK) {
				Assets.lvlOneBgm.setLooping(true);
				bannerListener = (BannerListener) game;
				bannerListener.showBanner(true);
				state = GameState.Pause;
			}
		}
		if (touchEvents.size() > 0) {
			Assets.lvlOneBgm.play();
			Assets.lvlOneBgm.setLooping(true);
			state = GameState.Running;
		}

	}

	private void updateRunning(List<KeyEvent> keyEvents,
			List<TouchEvent> touchEvents) {

		int len = touchEvents.size();
		// Check for touchEvent
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);

			if (event.type == TouchEvent.TOUCH_DOWN) {
				jump = true;

			} else if (event.type == TouchEvent.TOUCH_UP) {
				jump = false;
			}
		}

		// Check for completion
		if (startX > 10700) {
			Assets.lvlOneBgm.pause();
			jump = false; // Stop line color change when displaying gameClear
							// screen
			state = GameState.GameClear;
		}

		// Set box visibility
		for (int b = 0; b < boxes; b++) {
			// Update box position
			boxX[b] -= VELOCITY_X;
			if (boxX[b] > -60 && boxX[b] < 1280) {
				boxOnScreen[b] = true;
			} else {
				boxOnScreen[b] = false;
			}
		}

		// Set spike visibility
		for (int s = 0; s < spikes; s++) {
			// update spike position
			spikeX[s] -= VELOCITY_X;
			if (spikeX[s] > -60 && spikeX[s] < 1280) {
				spkOnScreen[s] = true;
			} else {
				spkOnScreen[s] = false;
			}
		}

		// Set pit visibility
		for (int p = 0; p < pits; p++) {
			// Update pit position
			pitPos[p] -= VELOCITY_X;
			if (pitPos[p] + pitLen[p] > 0
					&& pitPos[p] < 1280) {
				pitOnScreen[p] = true;
			} else {
				pitOnScreen[p] = false;
			}
		}

		// Set save visibility
		for (int i = 0; i < saves; i++) {
			saveX[i] -= VELOCITY_X;
			if (saveX[i] > -120 && saveX[i] < 1280) {
				saveOnScreen[i] = true;
			} else {
				saveOnScreen[i] = false;
			}
		}

		updatePlayer();

		int keyLength = keyEvents.size();
		for (int i = 0; i < keyLength; i++) {
			KeyEvent keyEvent = keyEvents.get(i);
			if (keyEvent.keyCode == android.view.KeyEvent.KEYCODE_BACK) {
				bannerListener = (BannerListener) game;
				bannerListener.showBanner(true);
				Assets.lvlOneBgm.pause();
				state = GameState.Pause;
			}
		}
	}

	private void updatePaused(List<KeyEvent> keyEvents,
			List<TouchEvent> touchEvents) {

		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				touchPoint.set(event.x, event.y);
				guiCam.touchToWorld(touchPoint);
				if (inBounds(resumeBounds, touchPoint)) {
					jump = false;
					state = GameState.Running;
					Assets.lvlOneBgm.play();
					bannerListener = (BannerListener) game;
					bannerListener.showBanner(false);
					return;
				}
				if (inBounds(quitBounds, touchPoint)) {
					Assets.lvlOneBgm.seekTo(0);
					bannerListener = (BannerListener) game;
					bannerListener.showBanner(false);
					if (rand.nextInt(10) == 0) {
						interstitialListener = (InterstitialListener) game;
						interstitialListener.showInterstitial();
					}
					game.setScreen(new LevelScreen(game));
					return;

				}
			}
		}
	}

	private void updateGameClear(List<KeyEvent> keyEvents,
			List<TouchEvent> touchEvents) {

		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				touchPoint.set(event.x, event.y);
				guiCam.touchToWorld(touchPoint);
				if (inBounds(exitBounds, touchPoint)) {
					Assets.lvlOneBgm.seekTo(0);
					if (rand.nextInt(10) == 0) {
						interstitialListener = (InterstitialListener) game;
						interstitialListener.showInterstitial();
					}
					game.setScreen(new LevelScreen(game));
					return;
				}
			}
		}
	}

	private void updatePlayer() {
		startX += 2;
		// Log.d("Game", "" + (int) startX);

		if (!land) {
			// gravity
			velo -= 1.2;
			angle -= 22;
			startY += velo;
		}

		pl.set(playerX, startY, 60, 60);

		if (startY <= 240) {
			land = true;
			startY = 240;
		} else {
			land = false;
		}
//		if (saveX[4] < playerX+60)
//	    checkCollision();

		checkCollision();

		if (jump) {
			if (land) {
				land = false;
				angle = 0;
				velo = 16.9f;

			}
		}

	}

	/**
	 * Check if the player collide with any objects.
	 * 
	 * @param delta
	 *            time taken for processing update and present methods
	 */
	private void checkCollision() {

		// Check for pit collision
		for (int p = 0; p < pits; p++) {
			if (pitOnScreen[p] && pitPos[p] < playerX+70) {
				if (startY == 240 && pitPos[p] < playerX
						&& pitPos[p] + pitLen[p] > playerX) {
					reset();
					return;
				}
			}
		}

		// Check for box collision
		for (int b = 0; b < boxes; b++) {
			if (boxOnScreen[b] && boxX[b] < playerX+70) {
				bx.set(boxX[b], boxY[b], 60, 60);

				if (Rectangle.intersects(pl, bx)) {
					// boxX[b] > 200 - playerWidth && boxX[b] < 200 + playWidth
					if (startY > boxY[b] && boxX[b] > playerX-60 && boxX[b] < playerX+60) {
						land = true;
						startY = boxY[b] + 59; // box height - 1
						velo = 0;
						break;
					} else {
						reset();
						return;
					}
				}
			}
		}

		// Check for spike collision
		for (int s = 0; s < spikes; s++) {
			if (spkOnScreen[s] && spikeX[s] < playerX+70) {
				sp.set(spikeX[s]+4, spikeY[s]+1, 52, 1); // horizontal, Change spikeX ONLY if needed
				                                         // 3px offset each side for collision detail
				sp2.set(spikeX[s] + 30, spikeY[s], 1, 56); // vertical, Change spikeY ONLY if needed
				if (Rectangle.intersects(pl, sp) || Rectangle.intersects(pl, sp2)) {
					reset();
					return;
				}
			}
		}

		// Check for save collision
		for (int i = 0; i < saves; i++) {
			if (saveOnScreen[i] && saveX[i] < playerX+70) {
				sv.set(saveX[i], 240, 120, 300); // NOT THE ACTUAL
															// DRAWING

				if (Rectangle.intersects(pl, sv) && !saved[i]) {
					saved[i] = true;
					savePos = startX;
					currentMusicPos = Assets.lvlOneBgm.getCurrentPosition();
					saveBoxX = boxX.clone();
					saveSpikeX = spikeX.clone();
					savePitPos = pitPos.clone();
					saveSaveX = saveX.clone();
				}
			}
		}
	}

	private void reset() {
		land = true;

		// Implicitly define count variables here
		// boxes = 0;
		// spikes = 0;
		// pits = 0;
		// saves = 0;

		// Starts from the beginning if player never enter a save zone;
		// Otherwise start from the save zone

		if (savePos > 0) {
			Assets.lvlOneBgm.pause();
			Assets.lvlOneBgm.seekTo(currentMusicPos);
			
			boxX = saveBoxX.clone();
			spikeX = saveSpikeX.clone();
			pitPos = savePitPos.clone();
			saveX = saveSaveX.clone();
			startX = savePos;
			
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Assets.lvlOneBgm.play();
		} else {
			// reset() is invoked more than one time
			if (pitPos != null) {
				Assets.lvlOneBgm.pause();
				Assets.lvlOneBgm.seekTo(0);
				// Clone each array instead of loadMap() on every reset
				boxX = cloneBoxX.clone();
				spikeX = cloneSpikeX.clone();
				pitPos = clonePitPos.clone();
				saveX = cloneSaveX.clone();
				
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				Assets.lvlOneBgm.play();
			}
			startX = 0;
		}
		startY = 240;
		lineY = 235;
		attempts++;
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
		drawWorld();
		
		if (state == GameState.Ready)
			drawReadyUI();
		if (state == GameState.Running)
			drawRunningUI();
		if (state == GameState.Pause)
			drawPauseUI();
		if (state == GameState.GameClear)
			drawGameClearUI();
		batcher.endBatch();
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
	}

	private void drawReadyUI() {
		batcher.drawSprite(Assets.ready, 480+143, 575-61, 295, 130);
	}

	private void drawRunningUI() {
		GL10 gl = glGraphics.getGL();
		drawSave(gl);
		drawPit(gl);
		drawBox(gl);
		drawSpike(gl);
		drawPlayer(gl);
	}

	private void drawPauseUI() {		
//		batcher.drawSprite(Assets.blueRect, 
//				resumeBounds.lowerLeft.x+(resumeBounds.width/2), 
//				resumeBounds.lowerLeft.y+(resumeBounds.height/2), 
//				resumeBounds.width, resumeBounds.height);
//		batcher.drawSprite(Assets.blueRect, 
//				quitBounds.lowerLeft.x+(quitBounds.width/2), 
//				quitBounds.lowerLeft.y+(quitBounds.height/2), 
//				quitBounds.width, quitBounds.height);
		batcher.drawSprite(Assets.pause, 483+157, 535-100, 314, 200);
	}

	private void drawGameClearUI() {
		batcher.drawSprite(Assets.gameClear, 355+290, 540-40, 580, 80);
		batcher.drawSprite(Assets.rating, 360+290, 210-30, 580, 60);
		batcher.drawSprite(Assets.exitButton, 5+45, 795-45, 90, 90);
		
		batcher.drawSprite(Assets.grayRect, 410+30, 100-30, 60, 60);
		batcher.drawSprite(Assets.grayRect, 510+30, 100-30, 60, 60);
		batcher.drawSprite(Assets.grayRect, 610+30, 100-30, 60, 60);
		batcher.drawSprite(Assets.grayRect, 710+30, 100-30, 60, 60);
		batcher.drawSprite(Assets.grayRect, 810+30, 100-30, 60, 60);

		if (attempts <= 25) {
			batcher.drawSprite(Assets.redRect, 410+30, 100-30, 60, 60);
		}
		if (attempts <= 20) {
			batcher.drawSprite(Assets.yellowRect, 510+30, 100-30, 60, 60);
		}
		if (attempts <= 15) {
			batcher.drawSprite(Assets.greenRect, 610+30, 100-30, 60, 60);
		}
		if (attempts <= 7) {
			batcher.drawSprite(Assets.blueRect, 710+30, 100-30, 60, 60);
		}
		if (attempts <= 3) {
			batcher.drawSprite(Assets.navyRect, 810+30, 100-30, 60, 60);
		}
		
	}

	private void drawWorld() {
		keyFrame = Assets.line.getKeyFrame(tickTime, Animation.ANIMATION_LOOPING);
		batcher.drawSprite(keyFrame, 0+640, lineY+2, 1280, 5);
	}

	private void drawSave(GL10 gl) {

		for (int i = 0; i < saves; i++) {
			if (saveOnScreen[i]) {
				if (Rectangle.intersects(pl, sv)) {
					batcher.drawSprite(Assets.save, saveX[i]+55, 238, 120, 120);
					batcher.drawSprite(keyFrame, saveX[i]+55, lineY+2, 120, 5);
				} else {
					batcher.drawSprite(Assets.save, saveX[i]+55, 238, 110, 110);
					batcher.drawSprite(keyFrame, saveX[i]+55, lineY+2, 110, 5);
				}
//				batcher.drawSprite(Assets.blueRect, sv.lowerLeft.x+50, sv.lowerLeft.y+150, sv.width, sv.height);
//				g.fillCircle(saveX[i] + 50, 512, 50, Color.GREEN);
//				g.fillRect(saveX[i], lineY, 101, 5, color);

			}
		}
	}

	private void drawPlayer(GL10 gl) {
		if (!land) {
			batcher.drawSprite(Assets.playerDefault, playerX+30, startY+30, 60, 60, angle);
//			batcher.drawSprite(Assets.navyRect,pl.lowerLeft.x+23, pl.lowerLeft.y+23, 46, 46);
		} else {
			batcher.drawSprite(Assets.playerDefault, playerX+30, startY+30, 60, 60);
//			batcher.drawSprite(Assets.navyRect,pl.lowerLeft.x+25, pl.lowerLeft.y+25, 48, 48);

		}
	}

	private void drawSpike(GL10 gl) {
		for (int i = 0; i < spikes; i++) {
			if (spkOnScreen[i]) {
				batcher.drawSprite(Assets.spike, spikeX[i] + 30, spikeY[i]+30, 60, 60);
//				batcher.drawSprite(Assets.blueRect,sp.lowerLeft.x+23, sp.lowerLeft.y, 42, 1);
//				batcher.drawSprite(Assets.blueRect,sp2.lowerLeft.x, sp.lowerLeft.y+23, 1, 44);
			}
		}
	}

	private void drawBox(GL10 gl) {
		for (int i = 0; i < boxes; i++) {
			if (boxOnScreen[i]) {
				batcher.drawSprite(Assets.box, boxX[i] + 30, boxY[i] + 30, 60, 60);
//				if (startY == boxY[i] + 53 && boxX[i] > playerX - 54 && boxX[i] < playerX + 54) 
//					batcher.drawSprite(Assets.navyRect, bx.lowerLeft.x+27, bx.lowerLeft.y+27, 54, 54);
				if (startY >= boxY[i] + 59 && boxX[i] > playerX - 60 && boxX[i] < playerX) {
					for (int j = 240; j <= boxY[i] + 60; j += 60)
						batcher.drawSprite(Assets.boxDefault, boxX[i]+30, boxY[i] + 30, 60, 60);
			    }
		    }
	    }
	}

	private void drawPit(GL10 gl) {
		for (int i = 0; i < pits; i++) {
			if (pitOnScreen[i]) {
				batcher.drawSprite(Assets.pit, pitPos[i]+(pitLen[i]/2), lineY+2, pitLen[i], 6);
			}
		}
	}

	@Override
	public void pause() {
		if (state == GameState.Running) {
			if (Assets.lvlOneBgm.isPlaying())
				Assets.lvlOneBgm.pause();
			bannerListener = (BannerListener) game;
			bannerListener.showBanner(true);
			state = GameState.Pause;
		}
	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

	@Override
	public String toString() {
		return "GameScreen";
	}

}
