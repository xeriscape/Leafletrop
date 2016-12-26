package util2d.core;

import util2d.scene.GameScene;
import org.lwjgl.input.Keyboard;

public final class GLOBALS {
	/**
	 * These are "fallback values" to be used when no config file is available / specified,
	 * and this is also how the game is designed. These are hardcoded, since they also serve
	 * as a configuration of last resort. That's also why they're final, to make extra sure.
	 * 
	 * All these fields are private, the stuff that's accessible form outside the class (and
	 * hence may need to change) is public.
	 */
	private static final int standard_char_width = 58, standard_char_height = 124; //Sprite dimensions
	private static final int standard_character_width = 58, standard_character_height = 124; //Character dimensions
	private static final int standard_screen_height = 672, 	standard_screen_width = 1248; //Screen dimensions
	private static final int standard_tile_height = 96, standard_tile_width = 96; //Dimensions of an individual tile (default maps are 7x13)
	private static final int standard_framerate = 60; //Default FPS, this should be 30 or 60
	private static final double standard_transparency_foreground = 0.5; //Partial see-through for objects in front of the player
	private static final int standard_key_left=Keyboard.KEY_A, standard_key_right=Keyboard.KEY_D, standard_key_up=Keyboard.KEY_W, standard_key_down=Keyboard.KEY_S;
	
	
	/**
	 * These are the values actually used elsewhere. By default the game uses the fallback values.
	 */
	private static int assumedCharWidth = standard_char_width, assumedCharHeight = standard_char_height;
	private static int defaultCharacterWidth = standard_character_width, defaultCharacterHeight = standard_character_height;
	private static int framerate = standard_framerate;
	private static int defaultScreenHeight = standard_screen_height, 	defaultScreenWidth = standard_screen_width;
	private static int defaultTileHeight = standard_tile_height, defaultTileWidth = standard_tile_width;
	private static double transparencyForeground = standard_transparency_foreground;
	private static int keyLeft=standard_key_left, keyRight=standard_key_right, keyUp=standard_key_up, keyDown=standard_key_down;

	
	/**
	 * Helper function for converting frames to seconds (by default, divide by 60)
	 * @param numberFrames The number of frames to count
	 * @return The number of seconds it will take to display this many frames
	 */
	public static double framesToSeconds(int numberFrames) {
		return ((double) numberFrames)/((double) framerate);
	}
	
	/**
	 * Helper function for converting seconds to frames (by default, multiply by 60)
	 * @param numberSeconds The number of seconds to measure
	 * @return The amount of frames that go by in the specified time frame (rounded up, so partial frames are counted)
	 */
	public static int secondsToFrames(double numberSeconds) {
		System.err.println(""+((int) Math.ceil((numberSeconds * framerate)))+" frames in "+numberSeconds+" seconds");
		return ((int) Math.ceil((numberSeconds * framerate)));
	}
	
	/**
	 * Some more general game state information: What's the player, what's the current scene
	 */
	private static Renderable playerCharacter;
	private static GameScene currentMap;

	public static int getAssumedCharWidth() {
		return assumedCharWidth;
	}

	public static void setAssumedCharWidth(int assumedCharWidth) {
		if (assumedCharWidth > 0) GLOBALS.assumedCharWidth = assumedCharWidth;
	}

	public static int getAssumedCharHeight() {
		return assumedCharHeight;
	}

	public static void setAssumedCharHeight(int assumedCharHeight) {
		if (assumedCharHeight > 0) GLOBALS.assumedCharHeight = assumedCharHeight;
	}

	public static int getDefaultCharacterWidth() {
		return defaultCharacterWidth;
	}

	public static void setDefaultCharacterWidth(int defaultCharacterWidth) {
		if (defaultCharacterWidth > 0) GLOBALS.defaultCharacterWidth = defaultCharacterWidth;
	}

	public static int getDefaultCharacterHeight() {
		return defaultCharacterHeight;
	}

	public static void setDefaultCharacterHeight(int defaultCharacterHeight) {
		if (defaultCharacterHeight > 0) GLOBALS.defaultCharacterHeight = defaultCharacterHeight;
	}

	public static int getFramerate() {
		return framerate;
	}

	public static void setFramerate(int framerate) {
		if (framerate > 0) GLOBALS.framerate = framerate;
	}

	public static int getDefaultScreenHeight() {
		return defaultScreenHeight;
	}

	public static void setDefaultScreenHeight(int defaultScreenHeight) {
		if (defaultScreenHeight > 0) GLOBALS.defaultScreenHeight = defaultScreenHeight;
	}

	public static int getDefaultScreenWidth() {
		return defaultScreenWidth;
	}

	public static void setDefaultScreenWidth(int defaultScreenWidth) {
		if (defaultScreenWidth > 0) GLOBALS.defaultScreenWidth = defaultScreenWidth;
	}

	public static int getDefaultTileHeight() {
		return defaultTileHeight;
	}

	public static void setDefaultTileHeight(int defaultTileHeight) {
		if (defaultTileHeight > 0) GLOBALS.defaultTileHeight = defaultTileHeight;
	}

	public static int getDefaultTileWidth() {
		return defaultTileWidth;
	}

	public static void setDefaultTileWidth(int defaultTileWidth) {
		if (defaultTileWidth > 0) GLOBALS.defaultTileWidth = defaultTileWidth;
	}

	public static double getTransparencyForeground() {
		return transparencyForeground;
	}

	public static void setTransparencyForeground(double transparencyForeground) {
		if (0 <= transparencyForeground && transparencyForeground <= 1) GLOBALS.transparencyForeground = transparencyForeground;
	}

	public static Renderable getPlayerCharacter() {
		return playerCharacter;
	}

	public static void setPlayerCharacter(Renderable playerCharacter) {
		GLOBALS.playerCharacter = playerCharacter;
	}

	public static GameScene getCurrentMap() {
		return currentMap;
	}

	public static void setCurrentMap(GameScene currentMap) {
		GLOBALS.currentMap = currentMap;
	}
	
	public static double getWidthFactor() {
		return ((double) defaultCharacterWidth/(double) assumedCharWidth);
	}

	public static double getHeightFactor() {
		return ((double) defaultCharacterHeight/(double) assumedCharHeight);
	}

	
	//Control scheme stuff. If you try to set a non-existent key, the system falls back on the default.
	public static int getKeyLeft() {
		return keyLeft;
	}

	public static void setKeyLeft(int keyLeft) {
		if (Keyboard.getKeyName(keyLeft) != null) GLOBALS.keyLeft = keyLeft;
		else GLOBALS.keyLeft = GLOBALS.standard_key_left;
	}

	public static int getKeyRight() {
		return keyRight;
	}

	public static void setKeyRight(int keyRight) {
		if (Keyboard.getKeyName(keyRight) != null) GLOBALS.keyRight = keyRight;
		else GLOBALS.keyRight = GLOBALS.standard_key_right;
	}

	public static int getKeyUp() {
		return keyUp;
	}

	public static void setKeyUp(int keyUp) {
		if (Keyboard.getKeyName(keyUp) != null) GLOBALS.keyUp = keyUp;
		else GLOBALS.keyUp = GLOBALS.standard_key_up;
	}

	public static int getKeyDown() {
		return keyDown;
	}

	public static void setKeyDown(int keyDown) {
		if (Keyboard.getKeyName(keyDown) != null) GLOBALS.keyDown = keyDown;
		else GLOBALS.keyDown = GLOBALS.standard_key_down;
	}
	
	
}
