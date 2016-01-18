package com.jung.framework.util;

public class Rectangle {
	public final Vector2 lowerLeft;
	public float width, height;
	
	public Rectangle(float x, float y, float width, float height) {
		this.lowerLeft = new Vector2(x,y);
		this.width = width;
		this.height = height;
	}
	
	public void set(float x, float y, float width, float height) {
		lowerLeft.set(x, y);
		this.width = width;
		this.height = height;
		
	}
	
	public static boolean intersects(Rectangle r1, Rectangle r2) {
		if (r1.lowerLeft.x < r2.lowerLeft.x + r2.width &&
				r1.lowerLeft.x + r1.width > r2.lowerLeft.x && 
				r1.lowerLeft.y < r2.lowerLeft.y + r2.height &&
				r1.lowerLeft.y + r1.height > r2.lowerLeft.y)
			return true;
		else
			return false;
	}
}
