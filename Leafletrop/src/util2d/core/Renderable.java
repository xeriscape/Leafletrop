package util2d.core;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import misc.Identity;
import util2d.actor.Addon;

/**
 * Renderables provide basic functionality for image display - very basic. You specify 
 * a Texture (which is the image that will be shown) and a Point2D.Double, and
 * calling render will display the texture at the specified position.
 * 
 * This is to allow "object-based" rendering in the "main loop".
 *
 */
public class Renderable extends Identity implements Cloneable {
	//Texture
	protected Texture t;

	//Dimensions
	protected Point2D.Double currentPosition;
	public Point2D.Double getCurrentPosition() {
		return this.currentPosition;
	}
	public void setCurrentPosition(double x, double y) {
		this.setCurrentPosition(new Point2D.Double(x,  y));
	}
	public void setCurrentPosition(Point2D.Double where) {
		this.currentPosition = where;
	}

	protected int[] dimensions = new int[2];
	public void setWidth(int w) {
		if (w>0) this.dimensions[0]=w;
	}
	public void setHeight(int h) {
		if (h>0) this.dimensions[1]=h;
	}

	protected boolean usesFullBoundingRectangle = false;

	//TODO: Remove remaining "special logic" for Shadows
	static boolean shadowsEnabled = false; //TODO: Move to settings class
	private static Texture shadowTexture = null;
	
	protected ArrayList<Addon> addons = null;
	
	//Stuff
	public double transparency=0.0;
	public boolean respectsForegroundTransparency = true;


	//--------------------------------------------------------------------------------------------------------

	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		else if (o instanceof Renderable) return this.getID() == ((Renderable) o).getID();
		else return ((Object) this).equals(o);
		}

	/**
	 * Creates a Renderable displaying the texture tx at point p, with dimensions width and height.
	 * 
	 * @param tx Texture the Renderable is supposed to display (dimensions must be of the form 2^n)
	 * @param p Point2D.Double where the Renderable is supposed to be located
	 * @param width Horizontal size of the texture (leave blank to use texture width)
	 * @param height Vertical size of the texture (leave blank to use texture height)
	 */
	public Renderable(Texture tx, Point2D.Double p, int width, int height) {
		super();
		
		this.t = tx;
		this.currentPosition = p;
		this.dimensions[0] = width;
		this.dimensions[1] = height;
		this.addons = new ArrayList<Addon>();
	}


	/**
	 * Creates a Renderable displaying the texture tx at point p with default dimensions.
	 * 
	 * @param tx Texture the Renderable is supposed to display (dimensions must be of the form 2^n)
	 * @param p Point2D.Double where the Renderable is supposed to be located
	 */
	public Renderable(Texture tx, Point2D.Double p) {
		this(tx, p, 0, 0);
	}

	/**
	 * Creates a new Renderable displaying the texture tx at point (0, 0) with default dimensions.
	 * 
	 * @param tx Texture the Renderable is supposed to display (dimensions must be of the form 2^n)
	 */
	public Renderable (Texture tx) {
		this(tx, new Point2D.Double(0,0));
	}

	/**
	 * Creates a new Renderable with no texture (null, not blank) at (0, 0) with default dimensions.
	 */
	public Renderable() {
		this (null, new Point2D.Double(0,0));
	}

	/**
	 * Width of the Renderable.
	 * @return Width if specified, texture width otherwise.
	 */
	public int getWidth() {
		if (this.dimensions[0] <= 0) return this.t.getTextureWidth(); //Use texture width as a fallback
		else return this.dimensions[0];
	}

	/**
	 * Height of the Renderable.
	 * @return Height if specified, texture height otherwise.
	 */
	public int getHeight() {
		if (this.dimensions[1] <= 0) return this.t.getTextureHeight(); //Use texture height as a fallback
		else return this.dimensions[1];
	}

	/**
	 * Middle of the Renderable.
	 * @return A Point2D.Double representing the approximate centre of the Renderable.
	 */
	public Point2D.Double getCenter() {
		return new Point2D.Double(
				this.currentPosition.getX() + this.getWidth()/2, 
				this.currentPosition.getY() + this.getHeight()/2);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = "";

		result+=(String.format("[ Position: {0}, {1}", currentPosition.getX(), currentPosition.getY()));

		result+=("[ Position: "+currentPosition.getX()+", "+currentPosition.getY());

		result+=(" - Texture loaded: "+t.toString());
		result+=(" - >> Image width: "+t.getImageWidth());
		result+=(" - >> Image height: "+t.getImageHeight());
		result+=(" - >> Texture width: "+t.getTextureWidth());
		result+=(" - >> Texture height: "+t.getTextureHeight());
		result+=(" - >> Texture ID: "+t.getTextureID());
		result+=(" ]");

		return result.toString();
	}
	
	/**
	 * Addon handling.
	 */
	
	/**
	 * Append a new Addon to the list of Addons. But only if no Addon with the
	 * same name exists.
	 * 
	 * @param a The Addon to append.
	 * @return Whether Addon was appended
	 */
	public boolean addAddon(Addon a) {
		for (Addon b: this.addons) {
			if (b.identifier == a.identifier)
				return false;
		}
		this.addons.add(a);
		return true;
	}
	
	public void removeAddon(String identifier) {
		for (Addon a: this.addons) {
			if (a.identifier == identifier) this.addons.remove(a);
		}
	}

	/**
	 * Displays the Renderable at its current position (this.currentPosition)
	 * @see LWJGL.util2D.Renderable.render(Point2D.Double p)
	 */
	public void render() {
		this.render(this.currentPosition, this.transparency);
	}
	
	public void render(Point2D.Double p) {
		this.render(p, this.transparency);
	}

	/**
	 * This method displays the Renderable via GL_QUADS (and textures).
	 * @param p The point where it will be rendered (leave blank to use its current position)
	 */
	public void render(Point2D.Double p, Double transparency ) {
		//Addons that go BEHIND the renderable are rendered FIRST
		this.renderAddons(true);
		
		// --- Core rendering logic starts here ---

		//Make sure we don't render at weird fractional coordinates (these cause trouble!)
		p.x = Math.round(p.x); //TODO: Consider offset for maps that are larger than a screen
		p.y = Math.round(p.y);

		// store the current model matrix
		GL11.glPushMatrix();

		//Figure out what to draw and how large to draw it
		Texture texture = this.t;
		int drawWidth = this.getWidth(), drawHeight = this.getHeight();

		// bind to the appropriate texture for this sprite
		texture.bind();

		// translate to the right location and prepare to draw
		GL11.glTranslatef((int) p.getX(), (int) p.getY(), 0);		
		//GL11.glColor3f(1,1,1);
		GL11.glColor4d(1, 1, 1, 1-transparency);

		// draw a quad textured to match the sprite
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(0, 0);
			GL11.glTexCoord2f(0, texture.getHeight());
			GL11.glVertex2f(0, drawHeight);
			GL11.glTexCoord2f(texture.getWidth(), texture.getHeight());
			GL11.glVertex2f(drawWidth,drawHeight);
			GL11.glTexCoord2f(texture.getWidth(), 0);
			GL11.glVertex2f(drawWidth,0);
		}
		GL11.glEnd();

		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
		
		// --- Core rendering logic ends here ---

		//Addons that go DO NOT GO BEHIND the Renderable are rendered LAST
		this.renderAddons(false);
	}
	
	/**
	 * Display the Addons associated with this Renderable, if any.
	 * @param behindStatus Only Addons with this status will be rendered. This is used by the main rendering method. TRUE means the Addon goes behind the parent. FALSE means it goes in front of the parent.
	 */
	public void renderAddons(boolean behindStatus) {
		if (this.addons != null) {
			for (Addon a : this.addons) { 
				if (a.renderBehind == behindStatus) {
					//Establish the main Renderable's current position, including special logic for Movables
					Rectangle2D.Double calcRec = this.fullBoundingRectangle();

					//Movables need some extra logic because their shadows end up in weird places otherwise
					if (this instanceof Movable) {
						//For Movables, we need to display the shadow where the Movable was last.
						//This means we have to check if it moved.
						calcRec.x = ((Movable) this).isSkip_last_x() ? ((Movable) this).fullBoundingRectangle(0).getX() : ((Movable) this).fullBoundingRectangle(-1).getX();
						calcRec.y = ((Movable) this).isSkip_last_y() ? ((Movable) this).fullBoundingRectangle(0).getY() : ((Movable) this).fullBoundingRectangle(-1).getY();
					}

					//Update position of the addon
					a.currentPosition = new Point2D.Double(calcRec.x + a.attachX, calcRec.y + a.attachY);

					//Display the addon
					a.render();
				}
			}
		}
	}


	@Override
	public Object clone() throws CloneNotSupportedException {return super.clone();}

	/**
	 * Static utility function to load a set of Textures from given files / directory.
	 * @param fileName The set of files to be loaded as textures.
	 * @param directoryName The directory from which files are to be loaded.
	 */
	public static Texture[] load_res(String[] fileName, String directoryName) throws IOException {
		Texture[] sprites = new Texture[fileName.length];

		for (int i=0; i<fileName.length; i++) {
			sprites[i] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(directoryName+"/"+fileName[i]));
		}

		return sprites;
	}

	/**
	 * Static utility function to load a single Texture.
	 * @param fileName The name of the file to be loaded.
	 * @param directoryName Path to the file.
	 */
	public static Texture load_res(String fileName, String directoryName) throws IOException {
		String[] holder = {fileName};
		return load_res(holder, directoryName)[0];
	}

	//Not specified whether bounding rectangle is full or small? Use default
	/**
	 * Get the bounding rectangle at the current position, using default settings for size
	 * @return Bounding rectangle
	 */
	public Rectangle2D.Double boundingRectangle() {
		if (this.usesFullBoundingRectangle) return this.fullBoundingRectangle();
		else return smallBoundingRectangle();
	}

	/**
	 * Get the bounding rectangle at the specified position, using default settings for size
	 * @param where Point assumed to be the top-left point of the Renderable
	 * @return Bounding rectangle
	 */
	public Rectangle2D.Double boundingRectangle(Point2D.Double where) {
		if (this.usesFullBoundingRectangle) return this.fullBoundingRectangle(where);
		else return smallBoundingRectangle(where);
	}

	//No point specified? Just use current position
	/**
	 * Get the small bounding rectangle at the current position
	 * @return Bounding rectangle
	 */
	public Rectangle2D.Double smallBoundingRectangle() {
		return this.smallBoundingRectangle(this.currentPosition);
	}

	/**
	 * Get the full bounding rectangle at the current position
	 * @return Bounding rectangle
	 */
	public Rectangle2D.Double fullBoundingRectangle() {
		return new Rectangle2D.Double(this.currentPosition.x, this.currentPosition.y, this.getWidth(), this.getHeight());
	}
	
	/**
	 * Get the full bounding rectangle at the current position
	 * with the specified inset
	 */
	public Rectangle2D.Double reducedBoundingRectangle(double shrinkX, double shrinkY) {
		return new Rectangle2D.Double(this.currentPosition.x+shrinkX/2.0, 
				this.currentPosition.y+shrinkY/2.0, 
				this.getWidth()-shrinkX, 
				this.getHeight()-shrinkX);
	}

	//Finally, these'll get you the actual rectangles
	/**
	 * Get the small bounding rectangle at the specified position
	 * @param where Top left corner of the position
	 * @return Bounding rectangle
	 */
	public Rectangle2D.Double smallBoundingRectangle(Point2D.Double where) {
		//"Results obtained through direct experimentation"
		return new Rectangle2D.Double(
				where.x + 1.0/10.5*this.getWidth(),
				where.y + 6.0/7.0*this.getHeight(),
				this.getWidth() - 2.0/10.5*this.getWidth(), 
				this.getHeight()/7.0
				);
	}

	/**
	 * Get the full bounding rectangle at the specified position
	 * @param where Top left corner of the position
	 * @return Bounding rectangle
	 */
	public Rectangle2D.Double fullBoundingRectangle(Point2D.Double where) {
		return new Rectangle2D.Double(where.x, where.y, this.getWidth(), this.getHeight());
	}
	public static Texture getShadowTexture() {
		if (Renderable.shadowTexture == null)
			try {
				Renderable.setShadowTexture(Renderable.load_res("shadow.png", "shadow"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} //First initialisation?
		
		
		return shadowTexture;
	}
	public static void setShadowTexture(Texture shadowTexture) {
		Renderable.shadowTexture = shadowTexture;
	}



}
