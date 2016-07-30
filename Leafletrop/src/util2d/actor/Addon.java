package util2d.actor;

import org.newdawn.slick.opengl.Texture;

//This'll follow another Renderable around
//Can be a shadow, a sword, a halo, whatevs
public class Addon extends util2d.core.Renderable {	
	//Distance from superior Renderable's centre
	public double attachX = 0.0, attachY = 0.0;

	//Render behind or in front of parent Renderable?
	public boolean renderBehind = true;
	
	//Names
	public String identifier="";
	
	//Constructors
	public Addon(Texture tx) {
		super(tx);
		this.identifier = tx.toString();
	}
	
	public Addon(Texture tx, boolean isBehind) {
		this(tx);
		this.renderBehind = isBehind;
		
	}
	
	public Addon(Texture tx, boolean isBehind, double xOffset, double yOffset) {
		this(tx, isBehind);
		this.attachX = xOffset;
		this.attachY = yOffset;
	}
}
