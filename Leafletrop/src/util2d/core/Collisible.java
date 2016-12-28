package util2d.core;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import util2d.core.helper.Animation;
import util2d.core.helper.Collision;
import util2d.map.Map;

public class Collisible extends Movable {
	
	private int ignore = 5;
	public boolean collisionEnabled = true;

	/**
	 * Create a new Collisible using the specified Animations
	 */
	public Collisible(Animation[] anims, Point2D.Double where) {
		super(anims, where);
	}
	
	/**
	 * Display the Collisible, checking collisions for Tiles and the whole set of Collisibles in the process.
	 * 
	 * @param collisionCandidates The set of Collisibles with which this Collisible could collide. Normally, this includes all Collisibles on the map.
	 * @param m The map on which the Collisible is being displayed.
	 */
	public void render(Renderable[] collisionCandidates, Map m) throws IllegalArgumentException {
		this.render(collisionCandidates, 0, collisionCandidates.length, m);
	}
	
	/**
	 * Display the Collisible, checking collisions for Tiles and a subset of Collisibles in the process, taking steps to avoid double-checks in the process.
	 * 
	 * @param collisionCandidates The set of Collisibles with which this Collisible could collide. Normally, this includes all Collisibles on the map.
	 * @param minpos The index at which Collision-checking should start.
	 * @param maxpos The index at which Collision-checking should cease.
	 * @param m The map on which the Collisible is being displayed.
	 */	
	public void render(Renderable[] collisionCandidates, int minpos, int maxpos, Map m) throws IllegalArgumentException {
		//TODO: Split out the collision detection logic in the loop
		//TODO: Allow for checks not at current position
		
		//Don't process collisions at all while we're in ignore-mode
		if (this.ignore > 0) {
			this.ignore--; 
			return;
		}
		
		//Otherwise: first, check if we will collide with non-passable tiles
		boolean[] tileStatus = Collision.checkTileCollision(this, m);
		
		//Collisions with non-passable tiles lead to skipped moves
		if (tileStatus[0]) this.skip_next_x = true; //The assignment is done like this to avoid setting skips 
		if (tileStatus[1]) this.skip_next_y = true; //back to false if they were already true
		
		//Then, if collisions with objects are currently enabled, process those 
		if (collisionEnabled) {
			for (int i = minpos; i<maxpos; i++) {
				//Don't double-process collisions, so don't being at start of array
				Renderable otherObject = collisionCandidates[i];
				
				if (!this.equals(otherObject)) {
					//Don't process self-collisions
					Rectangle2D.Double collisionResult = Collision.checkCollisionRectangle(this, otherObject, 0);
					
					if ((collisionResult != null)&&(collisionResult.getWidth()>0)&&(collisionResult.getHeight()>0)) {
						//Is there a collision? Then process it, but only the part that applies to *this* object
						//as the other Renderable will process its own movement
						
						//Can't move through objects! 
						//The assignment is done like this to avoid setting skips back to false if they were already true
						if (Collision.checkHorizontalCollision(this, otherObject, 0)) this.skip_next_x = true;
						if (Collision.checkVerticalCollision(this, otherObject, 0))   this.skip_next_y = true;
						
						//TODO: Then trigger Collisible's behavior
					}
				}
			}
		}
		super.render(); //Next layer up: Handle movement
	}
}
