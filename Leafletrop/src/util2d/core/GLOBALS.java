package util2d.core;

import util2d.actor.Actor;

public final class GLOBALS {
	//AKA globals
	public static final int assumed_char_width = 58, assumed_char_height = 124;
	public static final int default_character_width = 58, default_character_height = 124;
	public static int default_screen_height = 900, 	default_screen_width = 1200;
	
	public static final double width_factor = ((double) default_character_width/(double) assumed_char_width);
	public static final double height_factor = ((double) default_character_height/(double) assumed_char_height);

	public static final double transparency_foreground = 0.3;
}
