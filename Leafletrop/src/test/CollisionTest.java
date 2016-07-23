package test;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.*;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.opengl.Texture;
import util2d.actor.Actor;
import util2d.actor.Addon;
import util2d.core.Animatable;
import util2d.core.Collisible;
import util2d.core.GLOBALS;
import util2d.core.Movable;
import util2d.core.Renderable;
import util2d.core.helper.Animation;
import util2d.core.helper.Shadow;
import util2d.map.Map;
import util2d.scene.GameScene;

import java.util.*;
import java.lang.*;

public class CollisionTest extends GameScene {

	String[] philes = {"animate/1.png", "animate/2.png", "animate/3.png", "animate/3.png"};
	String[] pPhiles = {"playuh/1.png", "playuh/2.png", "playuh/3.png", "playuh/4.png"};
	String[] oPhiles = {"obstacles/1.png", "obstacles/2.png", "obstacles/3.png", "obstacles/4.png", "obstacles/5.png", "obstacles/6.png"};
	int tH=100, tW=100;

	/**
	 * Start the example
	 * @throws Exception 
	 * @throws InstantiationException 
	 */
	public void start() throws Exception {
		//Initialise display
		this.screenHeight = 700;// GLOBALS.default_screen_height ;
		this.screenWidth = GLOBALS.default_screen_width;
		this.initGL("Leafletrop - Collision and Map Test");

		//Initialise map
		int[] passabilityStatus = new int[(screenWidth/tW) * (screenWidth/tH)];

		int numLines = screenHeight/tH;
		int numColumns = screenWidth/tW;

		for (int line = 0; line < numLines; line++) {
			for (int column = 0; column < numColumns; column++) {
				int cellNumber = (line * numColumns) + column;	//The number of a cell is equal to (#row * columns_in_a_row) + (#column)
				passabilityStatus[cellNumber] = 0;
				if (column == 0 || column == numColumns-1) passabilityStatus[cellNumber] = 1;
				if (line == 0 || line == numLines-1) passabilityStatus[cellNumber] = 1;
				if (column==4 && line ==4)  passabilityStatus[cellNumber] = 1;
				if (( (column==0)||(column==numColumns-1)) &&( (line==0)||(line==numLines-1))) passabilityStatus[cellNumber] = 0;
			}
		}

		try {
			this.sceneMap = new Map(screenWidth, screenHeight, tW, tH, passabilityStatus);
			this.sceneMap.tileAtCoordinates(new Point2D.Double(1, 1)).doRender = false;
			this.sceneMap.tileAtCoordinates(new Point2D.Double(0, screenHeight-1)).doRender = false;
			this.sceneMap.tileAtCoordinates(new Point2D.Double(screenWidth-1, 1)).doRender = false;
			this.sceneMap.tileAtCoordinates(new Point2D.Double(screenWidth-1, screenHeight-1)).doRender = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}

		//Load sprites
		File f = new File("animate/");
		File pl = new File("playuh/");
		File rr = new File("obstacles/");


		ArrayList<String> names = new ArrayList<String>();
		for (String s:f.list()) {
			names.add(s);
		}

		ArrayList<String> pnames = new ArrayList<String>();
		for (String s:pl.list()) {
			pnames.add(s);
		}
		ArrayList<String> onames = new ArrayList<String>();
		for (String s:rr.list()) {
			onames.add(s);
		}

		philes = names.toArray(new String[0]);
		pPhiles = pnames.toArray(new String[0]);
		oPhiles = onames.toArray(new String[0]);

		int num_objs = 10;

		Texture[] resources = null, presources = null, oresources = null;
		try {
			resources = Renderable.load_res(philes, "animate");
			presources = Renderable.load_res(pPhiles, "playuh"); 
			oresources = Renderable.load_res(oPhiles, "obstacles");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(0);
		} 

		//Load background
		//public Renderable(Texture tx, Point2D.Double p, int width, int height)
		Renderable bck = new Renderable(Renderable.load_res("background.png", "background"), new Point2D.Double(0, 0), screenWidth, screenHeight);
		this.background = bck;


		Animation[] objs      = new Animation[num_objs];
		Collisible[] sprt = new Collisible[num_objs/2];
		Animatable[] back = new Animatable[num_objs/2];

		ArrayList<Texture> play = new ArrayList<Texture>();
		ArrayList<Texture> al = new ArrayList<Texture>();
		ArrayList<Texture> obst = new ArrayList<Texture>();

		for (Texture txt:resources) al.add(txt);
		for (Texture txt:presources) play.add(txt);
		for (Texture txt:oresources) obst.add(txt);


		for (int i = 0; i < num_objs; i++) {
			int[] lens;

			if (i <= num_objs/2 -1) lens = new int[resources.length];
			else lens = new int[oresources.length];

			for (int j = 0; j < resources.length; j++)
				lens[j] = new java.util.Random().nextInt(9)+1;

			if (i == 0)  objs[i] = new Animation(play, lens);
			else if (i <= num_objs/2 -1) objs[i] = new Animation(al, lens);
			else objs[i] = new Animation(obst, lens);


			Animation[] argl = {objs[i]};

			if (i <= num_objs/2 -1) {
				sprt[i] = new Collisible(argl, new Point2D.Double(i*150+15, i*150+9)); 
				if (i==0) {sprt[i] = new Actor(argl); sprt[i].setCurrentPosition(new Point2D.Double(900,200)); sprt[i].setWidth(68); sprt[i].setHeight(124);}
				else {
					sprt[i].setWidth( (int) Math.floor(sprt[i].getWidth()*(1+(0.2* new Random().nextInt(i*2))))); 
					sprt[i].setHeight( (int) Math.floor(sprt[i].getHeight()*(2-(0.2* new Random().nextInt(i*2))))); 
					}
				sprt[i].animateOnlyWhenMoving = (i%2==0);
				if (i != 0) sprt[i].goal_mode = Movable.MOVE_WITH_VELOCITY;
				//sprt[i].addAcceleration(new Acceleration(new Random().nextInt(2), new Random().nextInt(2), 0 ));
			}

			else {
				int xxx = (new Random().nextInt( (screenWidth/tW)-2 )+1)*tW;
				int yyy = (new Random().nextInt( (screenHeight/tH)-2 )+1)*tH;
				
				back[i-(num_objs/2)] = new Animatable(argl, new Point2D.Double(xxx, yyy)); 
			}
			System.out.print("CREATED: ");
			System.out.print(objs[i].toString());
		}

		//Create list of objects to render
		Renderable[] isToBeRendered = new Renderable[num_objs];
		for (int i = 0; i < num_objs; i++) {
			if (i <= num_objs/2 -1) isToBeRendered[i] = sprt[i];
			else isToBeRendered[i]=back[i-(num_objs/2)];

			isToBeRendered[i].friendlyName = " "+i+" ";
		}

		this.playerCharacter = isToBeRendered[0];
		isToBeRendered[0].setWidth(68);
		isToBeRendered[0].setHeight(124);
		System.err.print("\n\n"+this.playerCharacter.getWidth());
		((Movable) isToBeRendered[0]).setDistancePerSecond(300.0);
		((Actor) isToBeRendered[0]).setAiMode(new util2d.ai.ActOnControls());

		//Transparency info
		/*System.out.print("\n");
		byte[] texData = isToBeRendered[0].t.getTextureData();
		for (int px=0; px<isToBeRendered[0].t.getTextureWidth(); px++) {
			for (int py=0; py<isToBeRendered[0].t.getTextureHeight(); py++) {
				byte alpha = texData[4 * (px * isToBeRendered[0].t.getTextureWidth() + py) + 3];
				if (alpha == 0) System.out.print("0");
				else System.out.print("1");
			}
			System.out.print("\n");
		}
		System.out.print("\n");*/


		//OK, shadow time
		for (Renderable r:isToBeRendered) {
			//Setup addons
			
			//Shadows are used to hint at bounding rects
			Rectangle2D.Double calcRec = r.boundingRectangle();

			//Movables need some extra logic because their shadows end up in weird places otherwise
			if (r instanceof Movable) {
				//For Movables, we need to display the shadow where the Movable was last
				//This means we have to check if it moved
				calcRec.x = ((Movable) r).isSkip_last_x() ? ((Movable) r).boundingRectangle(0).getX() : ((Movable) r).boundingRectangle(-1).getX();
				calcRec.y = ((Movable) r).isSkip_last_y() ? ((Movable) r).boundingRectangle(0).getY() : ((Movable) r).boundingRectangle(-1).getY();
			}

			//Shadow encompasses the bounding rect. "Results obtained through direct experimentation."
			double horiOffset = calcRec.getWidth()*0.2;    //Shadows are a bit larger than the actual collision rectangle,
			double vertiOffset = calcRec.getHeight()*0.45; //since "being hit when you clearly dodged" is frustrating

			Addon adn = new Addon(Renderable.getShadowTexture(), true);

			//Dimensions and positioning
			adn.setWidth((int) (Math.floor(calcRec.getWidth())+horiOffset));
			adn.setHeight((int) (Math.floor(calcRec.getHeight())+vertiOffset));
			
			//Positioning
			/*			r.addons[0].attachX = calcRec.x-horiOffset/2;
			r.addons[0].attachY = calcRec.y+calcRec.getHeight()/2-vertiOffset/2;*/
			adn.attachY = r.getHeight()-adn.getHeight()/2;
			
			r.addAddon(adn);
		}
		
		//And also equipment
		try {
			Texture shirt = Renderable.load_res("shirt.png","equipment");
			Texture pants = Renderable.load_res("pwnts.png","equipment");
			
			Addon shrt = new Addon(shirt, false, 0, 0);
			shrt.setWidth(this.playerCharacter.getWidth());
			shrt.setHeight(this.playerCharacter.getHeight());
			this.playerCharacter.addAddon(shrt);
			
			Addon pnts = new Addon(pants, false, 0, 0);
			pnts.setWidth(this.playerCharacter.getWidth());
			pnts.setHeight(this.playerCharacter.getHeight());
			this.playerCharacter.addAddon(pnts);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		


		//Main loop
		while (true) {
			try {
				this.update(isToBeRendered);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (Display.isCloseRequested()) {
				Display.destroy();
				System.exit(0);
			}
		}
	}

	/**
	 * Main class, responsible for display logic (and other things)
	 * @throws CloneNotSupportedException 
	 * @throws IOException 
	 */
	public void update(Renderable[] toRender) throws IllegalArgumentException, CloneNotSupportedException {	
		//Player controls
		Movable n = (Movable) toRender[0];
		if (!n.isMoving()) n.moveTo(n.getCurrentPosition(), Movable.MODE_ACCELERATE);

		//Display
		super.update(toRender);
	}

	/**
	 * Just start the test
	 * 
	 * @param argv Does nothing
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws CloneNotSupportedException
	 */
	public static void main(String[] argv) throws Exception {
		CollisionTest nt = new CollisionTest();
		nt.start();
	}
}
