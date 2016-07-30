package util2d.core.helper;
import util2d.actor.Actor;
import util2d.core.*;
import util2d.core.helper.skeleton.Skeleton;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.newdawn.slick.opengl.Texture;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util2d.core.GLOBALS;

// Contains various means and ways to assemble characters from parts.


public class CharacterBuilder {
	//A skeleton is currently a list of AttachmentPoints (which in turn are named coordinates)
	private static HashMap<String, Skeleton> Skeletons = new HashMap<String, Skeleton>();
	
	
	private static HashMap<String, Renderable> Heads = new HashMap<String, Renderable>();
	private static Texture Blank = null;
	private static Skeleton defaultSkeleton = null;
	private static boolean isInitialized = false;
	
	//Set up the class, basically
	private static void initialize() throws IOException {
		//Load blank texture
		Blank = Renderable.load_res("blank.png", "res");
		
		//Generate default skeleton
		Skeleton defaultSkeleton = new Skeleton();
		//static final String[] NAMES = {"HEAD", "UPPER_TORSO", "LOWER_TORSO", "L_ARM", "R_ARM", "L_LEG", "R_LEG", "SHADOW"};

		//Head is just at (0, 0)
		defaultSkeleton.setPoint("HEAD", new Point2D.Double(0, 0));
		
		//Upper torso is normally at (13,67)
		defaultSkeleton.setPoint("UPPER_TORSO", new Point2D.Double( (13.0/58.0)*GLOBALS.default_character_width, (67.0/124.0) * GLOBALS.default_character_height));
		
		//Lower torso is normally at (13,91)
		defaultSkeleton.setPoint("LOWER_TORSO", new Point2D.Double( (13.0/58.0)*GLOBALS.default_character_width, (91.0/124.0) * GLOBALS.default_character_height));
		
		//Right hand is normally at (13, 73)
		defaultSkeleton.setPoint("R_ARM", new Point2D.Double( (13.0/58.0)*GLOBALS.default_character_width, (73.0/124.0) * GLOBALS.default_character_height));
		
		//Left hand is normally at (29, 73)
		defaultSkeleton.setPoint("L_ARM", new Point2D.Double( (29.0/58.0)*GLOBALS.default_character_width, (73.0/124.0) * GLOBALS.default_character_height));
		
		//Right leg is normally at (11, 102)
		defaultSkeleton.setPoint("R_LEG", new Point2D.Double( (11.0/58.0)*GLOBALS.default_character_width, (102.0/124.0) * GLOBALS.default_character_height));
		
		//Left leg is normally at (27, 102)
		defaultSkeleton.setPoint("L_LEG", new Point2D.Double( (27.0/58.0)*GLOBALS.default_character_width, (102.0/124.0) * GLOBALS.default_character_height));
		
		
		/* Figuring out shadows involves mathematics */
		/* ########### */
		//Shadows are used to hint at bounding rects
		Rectangle2D.Double calcRec = new Rectangle2D.Double(0 + 1.0/10.5*GLOBALS.default_character_width * GLOBALS.width_factor,
				0 + 6.0/7.0*GLOBALS.default_character_height * GLOBALS.height_factor,
				GLOBALS.default_character_width * GLOBALS.width_factor - 2.0/10.5*GLOBALS.default_character_width * GLOBALS.width_factor, 
				GLOBALS.default_character_height * GLOBALS.height_factor/7.0
				);
		//Shadow encompasses the bounding rect. "Results obtained through direct experimentation."
		double horiOffset = calcRec.getWidth()*0.2;    //Shadows are a bit larger than the actual collision rectangle,
		double vertiOffset = calcRec.getHeight()*0.45; //since "being hit when you clearly dodged" is frustrating
		//Dimensions and positioning
		double x_siz = (int) (Math.floor(calcRec.getWidth())+horiOffset);
		double y_siz = (int) (Math.floor(calcRec.getHeight())+vertiOffset);
		//Positioning
		double y_pos = y_siz-y_siz/2;
		/* ########### */
		defaultSkeleton.setPoint("SHADOW", new Point2D.Double(0,y_pos*GLOBALS.height_factor ) );
		
		//That's all the default points set
		CharacterBuilder.isInitialized = true;
	}
	
	//Assemble a custom actor
	public Actor build() throws IOException {
		//Have we set up a default yet? If no, do that now
		if (!CharacterBuilder.isInitialized) CharacterBuilder.initialize();
		
		throw new NotImplementedException();
		//return null;
	}
	
	//Assemble a default actor
	public static Actor createDefault(DefaultActors which) throws IOException {
		//Have we set up a default yet? If no, do that now
		if (!CharacterBuilder.isInitialized) CharacterBuilder.initialize();
		
		//Create a new animation consisting of one blank frame
		Animation[] n = new Animation[1];
		ArrayList<Texture> m = new ArrayList<Texture>();
		m.add(Blank);
		n[0] = new Animation(m);
		
		//Create a new actor using that blank frame		
		Actor product = new Actor(n);
		
		//What sort of actor are we making? Set default dimensions for that type
		switch (which) {
			case HUMANOID:
				//Set default dimensions
				product.setHeight(GLOBALS.default_character_height);
				product.setWidth(GLOBALS.default_character_width);
				
				break;
				
			case BLANK:
				break;
		}
		
		
		
		return null;
	}

}
