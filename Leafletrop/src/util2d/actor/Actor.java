package util2d.actor;

import java.awt.geom.Point2D;

import util2d.ai.Behavior;
import util2d.core.Collisible;
import util2d.core.helper.Animation;

public class Actor extends Collisible {
	boolean isPlayer = false;
	private Behavior aiMode  = null;
	
	public Actor(Animation[] anims, Point2D.Double where) {
		super(anims, where);
		this.respectsForegroundTransparency = false; //Actors do not go semi-transparent
	}
	
	public Actor(Animation[] anims) {
		this(anims, new Point2D.Double());
	}

	public Behavior getAiMode() {
		return aiMode;
	}

	public void setAiMode(Behavior aiMode) {
		this.aiMode = aiMode;
	}
	
	//Rendering void goes here
}
