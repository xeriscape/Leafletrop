package util2d.core;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import util2d.core.helper.Animation;
import util2d.core.helper.Collision;
import util2d.map.Map;

public class Collisible extends Movable implements Cloneable {
	
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
	public void render(Renderable[] collisionCandidates, Map m) throws IllegalArgumentException, CloneNotSupportedException {
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
	public void render(Renderable[] collisionCandidates, int minpos, int maxpos, Map m) throws IllegalArgumentException, CloneNotSupportedException {
		//TODO: Split out the collision detection logic in the loop
		//TODO: Allow for checks not at current position
		
		//Don't process collisions at all while we're in ignore-mode
		if (this.ignore > 0) {
			this.ignore--; 
			return;
		}
		
		//Otherwise: first, check if we will collide with non-passable tiles
		boolean[] tileStatus = Collision.checkTileCollision(this, m);
		if (tileStatus[0]) this.skip_next_x = true;
		if (tileStatus[1]) this.skip_next_y = true;
		
		//Then, if collisions with objects are currently enabled, process those 
		if (collisionEnabled) {
			for (int i = minpos; i<maxpos; i++) { //Don't double-process collisions
				Renderable r = collisionCandidates[i];
				
				if (!this.equals(r)) {//Don't process self-collisions
					//((Movable) this).movementShape(0)
					Rectangle2D.Double collisionResult = Collision.checkCollisionRectangle(this, r, 0);
					
					if ((collisionResult != null)&&(collisionResult.getWidth()>0)&&(collisionResult.getHeight()>0)) {
						//Is there a collision? Then process it						
						//TODO: Then trigger Collisible's behavior
						
						//Can't move through objects! Avoid setting skips to false if they were already true
						if (Collision.checkHorizontalCollision(this, r, 0)) this.skip_next_x = true;
						if (Collision.checkVerticalCollision(this, r, 0)) this.skip_next_y = true;
						
						/*if (r instanceof Collisible) {
							//Establish some facts about the tiles in which collisions and collided objects are located
							int[] collisionPosition = m.xyTile((int) Math.floor(collisionResult.getCenterX()), (int) Math.floor(collisionResult.getCenterY()));
							int[] collidedPosition = m.xyTile((int) Math.floor(r.boundingRectangle().getCenterX()), (int) Math.floor(r.boundingRectangle().getCenterY()));
							
							//Figure out where bumps need to go
							
							//TODO: Fix bumping, somehow - rn it glitches thru Collisibles & blocked Tiles
							double horiMov = 0.0, vertiMov = 0.0;
							double xDelta = 0.0, yDelta = 0.0;
							
							if (collisionPosition[0] > collidedPosition[0] && xCollision) horiMov = +1.0;
							else if (collisionPosition[0] < collidedPosition[0] && xCollision) horiMov = -1.0;

							if (collisionPosition[1] > collidedPosition[1] && yCollision) vertiMov = +1.0;
							else if (collisionPosition[1] < collidedPosition[1] && yCollision) vertiMov = -1.0;
							
							double fasterSpeed = Math.max(this.getDistancePerFrame(), ((Collisible) r).getDistancePerFrame());
							double moveDelta = Math.max(fasterSpeed, 1.01);
							//System.err.println(moveDelta);
							//System.err.println(this.getDistancePerFrame());
							
							Renderable[] toExclude = {this, r};
							
							//Bump left/right
							if (collisionResult.getCenterX() < r.boundingRectangle().getCenterX() && xCollision) {
								//r.currentPosition = new Point2D.Double(r.currentPosition.x + 3.0, r.currentPosition.y);
								boolean[] doMove = ((Collisible) r).isMoveOkay(((Renderable[])collisionCandidates), minpos, maxpos, m, 1.0*moveDelta, 0.0, toExclude);
								if (doMove[0]) ((Collisible) r).setCurrentPosition(r.getCurrentPosition().x+moveDelta, r.getCurrentPosition().getY());
								}
							if (collisionResult.getCenterX() > r.boundingRectangle().getCenterX() && xCollision) {
								//r.currentPosition = new Point2D.Double(r.currentPosition.x - 3.0, r.currentPosition.y);
								boolean[] doMove = ((Collisible) r).isMoveOkay(((Renderable[])collisionCandidates), minpos, maxpos, m, -1.0*moveDelta, 0.0, toExclude);
								if (doMove[0]) ((Collisible) r).setCurrentPosition(r.getCurrentPosition().x-moveDelta, r.getCurrentPosition().getY());
								}
							
							//Bump up/down
							if (collisionResult.getCenterY() < r.boundingRectangle().getCenterY() && yCollision){ 
								// r.currentPosition = new Point2D.Double(r.currentPosition.x, r.currentPosition.y +3.0);
								boolean[] doMove = ((Collisible) r).isMoveOkay(((Renderable[])collisionCandidates), minpos, maxpos, m, 0.0, 1.0*moveDelta, toExclude);
								if (doMove[1]) r.setCurrentPosition(r.getCurrentPosition().x, r.getCurrentPosition().getY()+moveDelta);
								}
							if (collisionResult.getCenterY() > r.boundingRectangle().getCenterY() && yCollision) {/*yDelta = +3.0;
								//r.currentPosition = new Point2D.Double(r.currentPosition.x, r.currentPosition.y -3.0);
								boolean[] doMove = ((Collisible) r).isMoveOkay(((Renderable[])collisionCandidates), minpos, maxpos, m, 0.0, -1.0*moveDelta, toExclude);
								if (doMove[1]) r.setCurrentPosition(r.getCurrentPosition().x, r.getCurrentPosition().getY()-moveDelta);
							}
							
							//Do bumps
							if (Math.abs(vertiMov) > 0 ) {
								if (((Collisible) r).goal_mode == Movable.MOVE_WITH_VELOCITY ) { ((Collisible) r).acc_y = -vertiMov * (Math.abs(this.calculateVelocities()[1]));}
								((Collisible) r).skip_next_x = true;
								this.skip_next_y = true; this.stopY();
								
							}
							
							if (Math.abs(horiMov) > 0 ) {
								if (((Collisible) r).goal_mode == Movable.MOVE_WITH_VELOCITY ) { ((Collisible) r).acc_x = -horiMov * (Math.abs(this.calculateVelocities()[0]));}
								((Collisible) r).skip_next_y = true;
								this.skip_next_x = true; this.stopX();
							}
							
							//Bumps use move override
							//if (((Collisible) r).isMoving() || ((Collisible) r).currentMode == Movable.MODE_OFF) ((Collisible) r).currentOverrideGoal = new Point2D.Double(r.currentPosition.x+xDelta, r.currentPosition.y+yDelta);
							//else ((Collisible) r).moveTo(new Point2D.Double(r.currentPosition.x+xDelta, r.currentPosition.y+yDelta), MODE_LINEAR);
							
							
							
						}*/						
					}
				}
			}
		}
		super.render(); //Next layer up: Handle movement
	}
	

	public Object clone() throws CloneNotSupportedException {return super.clone();}
}
