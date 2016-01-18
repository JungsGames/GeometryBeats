package com.jung.framework.util;

public class Font {
	public final Texture texture;
	public final int glyphWidth;
	public final int glyphHeight;
	public final TextureRegion[] glyphs = new TextureRegion[11];

	public Font(Texture texture, int offsetX, int offsetY, int glyphsPerRow,
			int glyphWidth, int glyphHeight) {
		this.texture = texture;
		this.glyphWidth = glyphWidth;
		this.glyphHeight = glyphHeight;
		int x = offsetX;
		int y = offsetY;
		for (int i = 0; i < 11; i++) {
			glyphs[i] = new TextureRegion(texture, x, y, glyphWidth,
					glyphHeight);
			x += glyphWidth;
			if (x == offsetX + glyphsPerRow * glyphWidth) {
				x = offsetX;
				y += glyphHeight;
			}
		}
	}

	public void drawText(SpriteBatcher batcher, String text, float x, float y) {
		int len = text.length();
		for (int i = 0; i < len; i++) {
//			int c = text.charAt(i) - '0';
			int c = text.charAt(i);
			
			if (c == ' ') {
				x += 100;
				continue;
			}
			
			c = c - '0';
			if (c < 0 || c > glyphs.length - 1)
				continue;

			TextureRegion glyph = glyphs[c];
			batcher.drawSprite(glyph, x, y, glyphWidth, glyphHeight);
			x += glyphWidth;
		}
	}
}
