package com.jung.beat.main;

import com.jung.framework.impl.GLGame;
import com.jung.framework.intf.Music;
import com.jung.framework.util.Animation;
import com.jung.framework.util.Font;
import com.jung.framework.util.Texture;
import com.jung.framework.util.TextureRegion;

public class Assets {
	public static Texture texture;
	public static TextureRegion logo, mainmenu, ready, gameClear, pause, level, rating,
	spike, box, pit, save, arrowButton, exitButton, settingsButton, numbers,
	redRect, yellowRect, greenRect, blueRect, navyRect, grayRect,
	playerBlue, playerGreen, playerYellow, playerRed, playerPink, playerDefault,
	boxBlue, boxGreen, boxYellow, boxRed, boxPink, boxDefault;
	
	
	public static Font font;
	public static Animation line;

	public static Music lvlOneBgm, lvlTwoBgm, lvlThreeBgm, lvlFourBgm;

	public static void load(GLGame game) {
		texture = new Texture(game, "atlas.png");
		logo = new TextureRegion(texture, 0, 0, 590, 400);
		mainmenu = new TextureRegion(texture, 0, 420, 200, 190);
		pause = new TextureRegion(texture, 250, 420, 314, 200);
		level = new TextureRegion(texture, 620, 420, 240, 80);
		gameClear = new TextureRegion(texture, 0, 640, 580, 80);
		rating = new TextureRegion(texture, 0, 730, 580, 60);
		ready = new TextureRegion(texture, 605, 640, 290, 130);
		numbers = new TextureRegion(texture, 600, 0, 420, 53);
		
		spike = new TextureRegion(texture, 900, 250, 60, 60);
		box = new TextureRegion(texture, 950, 50, 60, 60);
		save = new TextureRegion(texture, 880, 120, 120, 120);
		pit = new TextureRegion(texture, 600, 380, 400, 6);
		
		arrowButton = new TextureRegion(texture, 600, 200, 100, 130);
		exitButton = new TextureRegion(texture, 750, 220, 90, 90);
		settingsButton = new TextureRegion(texture, 605, 800, 100, 100);
		font = new Font(texture, 0, 800, 10, 60, 66);
		
		redRect = new TextureRegion(texture, 900, 400, 60, 60);
		yellowRect = new TextureRegion(texture, 900, 500, 60, 60);
		greenRect = new TextureRegion(texture, 900, 600, 60, 60);
		blueRect = new TextureRegion(texture, 900, 700, 60, 60);
		navyRect = new TextureRegion(texture, 900, 800, 60, 60);
		grayRect = new TextureRegion(texture, 900, 900, 60, 60);
		
		playerBlue = new TextureRegion(texture, 0, 890, 60, 60);
		playerGreen = new TextureRegion(texture, 100, 890, 60, 60);
		playerYellow = new TextureRegion(texture,200, 890, 60, 60);
		playerRed = new TextureRegion(texture, 300, 890, 60, 60);
		playerPink = new TextureRegion(texture, 400, 890, 60, 60);
		
		boxBlue = new TextureRegion(texture, 0, 960, 60, 60);
		boxGreen = new TextureRegion(texture, 100, 960, 60, 60);
		boxYellow = new TextureRegion(texture, 200, 960, 60, 60);
		boxRed = new TextureRegion(texture, 300, 960, 60, 60);
		boxPink = new TextureRegion(texture, 400, 960, 60, 60);
		
		playerDefault = playerBlue;
		boxDefault = boxBlue;
		Settings.load(game.getFileIO());
				
		
		line = new Animation(3000, new TextureRegion(texture, 600, 340, 400, 5),
								 new TextureRegion(texture, 600, 350, 400, 5),
								 new TextureRegion(texture, 600, 360, 400, 5),
								 new TextureRegion(texture, 600, 370, 400, 5));
		
		lvlOneBgm = game.getAudio().newMusic("adven.mp3");
		lvlTwoBgm = game.getAudio().newMusic("mission.mp3");
		lvlThreeBgm = game.getAudio().newMusic("what.mp3");
		lvlFourBgm = game.getAudio().newMusic("love.mp3");
	}

	public static void reload() {
		texture.reload();
	}

}
