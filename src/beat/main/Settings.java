package com.jung.beat.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.jung.framework.intf.FileIO;

public class Settings {
	public static int playerColor = 0;
	
	public static final int BLUE = 1;
	public static final int GREEN = 2;
	public static final int YELLOW = 3;
	public static final int RED = 4;
	public static final int PINK = 5;

	public static void load(FileIO files) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(
					files.readFile("geometry")));
			playerColor = Integer.parseInt(in.readLine());
			if (playerColor == BLUE) {
				Assets.playerDefault = Assets.playerBlue;
				Assets.boxDefault = Assets.boxBlue;
			} else if (playerColor == GREEN) {
				Assets.playerDefault = Assets.playerGreen;
				Assets.boxDefault = Assets.boxGreen;
			} else if (playerColor == YELLOW) {
				Assets.playerDefault = Assets.playerYellow;
				Assets.boxDefault = Assets.boxYellow;
			} else if (playerColor == RED) {
				Assets.playerDefault = Assets.playerRed;
				Assets.boxDefault = Assets.boxRed;
			} else if (playerColor == PINK) {
				Assets.playerDefault = Assets.playerPink;
				Assets.boxDefault = Assets.boxPink;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
		}
	}

	public static void save(FileIO files) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					files.writeFile("geometry")));
			out.write(Integer.toString(playerColor));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
