package com.jung.framework.impl;

import com.jung.framework.intf.Game;
import com.jung.framework.intf.Screen;
import com.jung.framework.util.Rectangle;
import com.jung.framework.util.Vector2;

public abstract class GLScreen extends Screen {
	protected final GLGraphics glGraphics;
	protected final Game glGame;
	
	public GLScreen(Game game) {
		super(game);
		glGame = (GLGame)game;
		glGraphics = glGame.getGLGraphics();
	}
	
	public boolean inBounds(Rectangle r, Vector2 p) {
		return r.lowerLeft.x <= p.x && r.lowerLeft.x + r.width >= p.x
				&& r.lowerLeft.y <= p.y && r.lowerLeft.y + r.height >= p.y;
	}

}
