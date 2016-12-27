package util2d.scene;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import util2d.actor.Actor;
import util2d.core.Collisible;
import util2d.core.GLOBALS;
import util2d.core.Renderable;
import util2d.map.Map;

public class GameScene extends Scene {

	protected int screenHeight=0, screenWidth=0;
	protected Map sceneMap = null;
	
	protected Renderable playerCharacter;

	Object[] renderOrder = null;
	int maxFrameCount = 5;
	int frameCount = maxFrameCount;
	public boolean orderInvalid = true;

	/**
	 * Retrieve the set of Renderables at a given Point2D.Double. USE SPARINGLY!
	 * 
	 * @param coordinates The coordinates to check
	 * @returns An array of all Renderables which touch or overlap the given point.
	 */
	public boolean isOtherRenderableAt(Point2D.Double coordinates, Renderable[] toRender, Renderable[] toExclude) {
		//Helper function, use sparingly
		boolean result = false;
		
		HashMap<UUID, Renderable> checkAgainst = new HashMap<UUID, Renderable>();
		for (Renderable r:toRender)  checkAgainst.put(r.getID(), r);
		for (Renderable r:toExclude) checkAgainst.remove(r.getID());

		for (Renderable r:checkAgainst.values() )
			if (r.boundingRectangle().contains(coordinates))
			{
				result = true; 
				break;
			}

		return result;
	}

	/**
	 * Retrieve the set of Renderables at a given Point2D.Double. USE SPARINGLY!
	 * 
	 * @param coordinates The coordinates to check
	 * @returns An array of all Renderables which touch or overlap the given point.
	 */
	public static Renderable[] getRenderableAt(Point2D.Double coordinates, Renderable[] toRender) {
		//Helper function, use sparingly
		ArrayList<Renderable> results = new ArrayList<Renderable>();

		for (Renderable r:toRender) {
			if (r.boundingRectangle().contains(coordinates)) results.add(r);
		}

		if (results.isEmpty()) return null;
		else				   return (Renderable[]) results.toArray();
	}

	/**
	 * Figure out the order in which Renderables are to be displayed. Objects with low y-values are rendered first, then objects with higher
		y-values, to ensure that objects are displayed "in front of" another properly. This results in a 2 1/2-D
		perspective, which is both visually pleasing and easy to work with.
	 * 
	 * @param toRender The Renderables to consider.
	 * @returns Renderables ordered by ascending getCenterY() values.
	 */
	@SuppressWarnings("unchecked")
	private Object[] getRenderingOrder(Renderable[] toRender) {
		HashMap<Integer, Double> hm = new HashMap<Integer, Double>();
		for (int i = 0; i < toRender.length; i++) hm.put(i, toRender[i].fullBoundingRectangle().getMaxY() );
		ValueComparator bvc = new ValueComparator(hm);
		TreeMap<Integer, Double> sorted_map = new TreeMap<Integer, Double>(bvc);
		sorted_map.putAll(hm);

		return sorted_map.keySet().toArray();
	}

	/**
	 * The GameScene's main method, rendering all objects belonging to the scene in the appropriate order (background, tiles, Renderables)
	 * 
	 * @param toRender The Renderables to display.
	 */
	@Override
	public void update(Renderable[] toRender) throws IllegalArgumentException {
		//1.) Clear screen
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		//2.) Render background
		if (this.background != null) this.background.render();

		//3.) Render map
		if (this.sceneMap != null) this.sceneMap.render();

		//4.) Render objects
		//   a) Re-establish rendering order
		frameCount++;
		if (frameCount >= maxFrameCount || orderInvalid || toRender.length != renderOrder.length ) {
			//Ordering the set of renderables by y-values is sort of expensive, which is why it's not done every frame.
			//For now, it happens every 5 frames. This means that incorrect positions won't appear for more than 1/12th
			//of a second, which should be acceptable (it's visible but only if you're paying really close attention.)
			//Re-ordering is also forced if something changes externally (and orderInvalid is set to true) or if the length
			//of the array changes (e. g. because a projectile is added or an enemy dies).
			this.renderOrder = getRenderingOrder(toRender);
			frameCount = 0; orderInvalid = false;
		}

		//   b) Process behavior of actors
		for (Renderable r:toRender) {
			if (r instanceof util2d.actor.Actor) {
				if (((util2d.actor.Actor) r).getAiMode() != null) {
					((Actor) r).getAiMode().process(this.sceneMap, ((Actor) r), ((Actor) playerCharacter));
				}
			}
		}	

		//   c) Render objects in the previously established order
		for (int i = 0; i < renderOrder.length; i++) {
			int j = (Integer) renderOrder[i];

			//Am I an object that's blocking view of the player character? If so, render semi-transparently
			toRender[j].transparency = 0.0;
			//Try to do the less expensive checks first
			if (toRender[j].respectsForegroundTransparency && GLOBALS.getTransparencyForeground() > 0) {
				if (!(toRender[j].equals(this.playerCharacter)))  {
					if ((toRender[j].reducedBoundingRectangle(10, 10).intersects(this.playerCharacter.smallBoundingRectangle()))) {
						if (toRender[j].fullBoundingRectangle().getMaxY() > playerCharacter.fullBoundingRectangle().getMaxY()) {
							toRender[j].transparency = GLOBALS.getTransparencyForeground();
						}
					}
				}
			}


			//Collisibles require special logic to avoid duplicate collision checks
			if (toRender[j] instanceof Collisible) ((Collisible) toRender[j]).render(toRender, j+1, toRender.length, this.sceneMap);
			else toRender[j].render(toRender[j].getCurrentPosition());
		}		

		//5.) Done, display the results
		Display.update();
		Display.sync(GLOBALS.getFramerate());
	}

	/**
	 * Start the scene with no title.
	 * @throws Exception 
	 */
	public void start() throws Exception {
		this.start("New Scene");
	}

	/**
	 * Start the scene. This method needs to be overwritten.
	 * 
	 * @param title The title to display in the window.
	 */
	@Override
	public void start(String title) {
		this.initGL(title);
	}

	/**
	 * Initialise the display, GL parameters etc.
	 * 
	 * @param title The title to display in the window.
	 */
	public void initGL(String title) {
		try {
			Display.setTitle(title);
			Display.setDisplayMode(new DisplayMode(screenWidth,screenHeight));
			Display.create();
			Display.setVSyncEnabled(true);
		} catch (LWJGLException e) {
			//TODO: Exception logging
			e.printStackTrace();
			System.exit(0);
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);               

		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);          

		// enable alpha blending
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glViewport(0,0,screenWidth,screenHeight);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, screenWidth, screenHeight, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	/**
	 * Initialise the GL display, using screenWidth and screenHeight
	 */
	@Override
	public void initGL() {
		this.initGL("New Scene");
	}
}


/**
 * Helper class purely for sorting Renderables by X-value
 * 
 */
@SuppressWarnings("rawtypes")
class ValueComparator implements Comparator {
	HashMap<Integer, Double> base;

	public ValueComparator(HashMap<Integer, Double> hm) {
		this.base = hm;
	}

	// Note: this comparator imposes orderings that are inconsistent with equals.
	public int compare(Object a, Object b) {
		if (((java.util.Map<Integer, Double>) base).get(a) >= ((java.util.Map<Integer, Double>) base).get(b)) {
			return 1;
		} else {
			return -1;
		} // returning 0 would merge keys
	}
}