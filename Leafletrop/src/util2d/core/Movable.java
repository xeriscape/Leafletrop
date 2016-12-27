package util2d.core;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import util2d.core.helper.Acceleration;
import util2d.core.helper.Animation;

public class Movable extends Animatable implements Cloneable {

	//TODO: Everything regarding different movement modes is a complete and utter mess.

	public static final int MODE_LINEAR=0, MODE_ACCELERATE=1, MODE_OFF=-1;
	final int STATE_STOPPED=0, STATE_PAUSED=1, STATE_MOVING=2; //TODO: Deceleration

	public static final int MOVE_TO_GOAL = 0;
	public static final int MOVE_WITH_VELOCITY = 1;
	public int goal_mode = MOVE_TO_GOAL;

	public double acc_x = 0.0, acc_y = 0.0;

	ArrayList<Point2D.Double> waypoints;
	double distancePerSecond; //Pixels travelled in 60 frames
	public double getDistancePerSecond() {
		return distancePerSecond;
	}

	public void setDistancePerSecond(double distancePerSecond) {
		this.distancePerSecond = distancePerSecond;
	}

	public double getDistancePerFrame() {
		return this.distancePerSecond/GLOBALS.secondsToFrames(1);
	}
	public void setdistancePerFrame(double distancePerFrame) {
		this.distancePerSecond = GLOBALS.framesToSeconds(1)*distancePerFrame;
	}

	double accelerationPercentage;
	double currentSpeed;
	int currentMode=MODE_LINEAR, currentState=STATE_STOPPED;
	public Point2D.Double currentGoal, currentOverrideGoal = null;
	double velocityAccelerationRate = 1.0;

	//How does movement affect animations?
	public boolean animateOnlyWhenMoving = false;
	public boolean returnToDefaultWhenNotMoving = false;

	//Skipping movements
	protected boolean skip_next_x = false, skip_next_y = false;
	private boolean skip_last_x = false;
	private boolean skip_last_y = false;

	ArrayList<Acceleration> accelerations = new ArrayList<Acceleration>();

	/**
	 * Check if the Movable is currently moving
	 * @return true if (this.currentState == STATE_MOVING), false otherwise
	 */
	public boolean isMoving() {
		return (this.currentState == STATE_MOVING);
	}

	/**
	 * Create a Movable with the specified set of animations.
	 * @param anims Set of animations
	 */
	public Movable(Animation[] anims) {
		this(anims, new Point2D.Double());
	}

	/**
	 * Create a Movable with the specified set of animations, position
	 * and active animation index.
	 * @param anims Set of animations
	 * @param p Initial position
	 * @param activeIndex Initially active animation
	 */
	public Movable(Animation[] anims, Double p, int activeIndex) {
		super(anims, p, activeIndex);
		this.distancePerSecond = 60;
		this.accelerationPercentage = 10;
		this.currentSpeed = 0.0;
		this.currentMode=MODE_LINEAR;
		this.currentState=STATE_STOPPED;
		this.waypoints = null;
		//this.nextPosition = this.currentPosition;
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create a Movable with the specified set of animations and position
	 * @param anims Set of animations
	 * @param p Initial position
	 */
	public Movable(Animation[] anims, Double p) {
		super(anims, p);
		this.distancePerSecond = 60;
		this.accelerationPercentage = 10;
		this.currentSpeed = 0.0;
		this.currentMode=MODE_LINEAR;
		this.currentState=STATE_STOPPED;
		this.waypoints = null;
	}

	/**
	 * Instruct the Movable to move to the specified point, using its standard mode.
	 * 
	 * @param goal The point the movable should move to.
	 * @return true if the movable will start moving, false if the instruction fails.
	 */
	public boolean moveTo(Point2D.Double goal) {
		return this.moveTo(goal, this.currentMode);
	}

	/**
	 * Instruct the Movable to move to the specified point, using the specified mode.
	 * @param goal The point the movable should move to.
	 * @param mode MODE_LINEAR or MODE_ACCELERATE
	 * @return true if the movable will start moving, false if the instruction fails.
	 */
	public boolean moveTo(Point2D.Double goal, int mode) {
		//Don't move if we're already moving
		if (this.isMoving()) return false;

		this.currentState = STATE_MOVING;
		this.currentGoal = goal;

		return true;
	}

	/**
	 * Add an acceleration to this Movable.
	 * 
	 * @param arg0 The Acceleration object to be added.
	 */
	public void addAcceleration(Acceleration arg0) {
		this.accelerations.add(arg0);
	}


	/**
	 * Immediately abort all movement, set state to STATE_STOPPED and do NOT preserve goal/waypoints
	 */
	public void stop() {
		this.waypoints = null;
		this.currentGoal = null;
		this.currentOverrideGoal = null;
		this.currentState = STATE_STOPPED;
		this.currentSpeed = 0.0;
		this.acc_x = this.acc_y = 0.0;
		for (int i = 0; i < this.accelerations.size(); i++ ) { this.accelerations.remove(i); }
	}

	/**
	 * Immediately abort all horizontal movement, but do not set state to STATE_STOPPED.
	 */
	public void stopX() {
		if (this.currentState != STATE_STOPPED) {
			this.waypoints = null;
			this.currentGoal = new Point2D.Double(this.currentPosition.x, this.currentGoal.y);

			if (this.currentGoal == this.currentPosition) this.stop();

			this.acc_x = 0.0;
		}
	}

	/**
	 * Immediately abort all vertical movement, but do not set state to STATE_STOPPED.
	 */
	public void stopY() {
		if (this.currentState != STATE_STOPPED) {
			this.waypoints = null;
			this.currentGoal = new Point2D.Double(this.currentGoal.x, this.currentPosition.y);

			if (this.currentGoal == this.currentPosition) this.stop();

			this.acc_y = 0.0;
		}
	}

	/**
	 * Immediately pause all movement, set state to STATE_PAUSED, but DO preserve goal/waypoints
	 */
	public void pause() {
		this.currentState = STATE_PAUSED;
		this.currentSpeed = 0.0;
	}

	/**
	 * Display this Movable, processing all accelerations along the way, starting at its current position.
	 */
	@Override
	public void render() {
		this.render(this.currentPosition);
	}

	/**
	 * Display this Movable, processing all accelerations along the way, starting at the specified position.
	 * 
	 * @param d Starting point for the Movable
	 */
	@Override
	public void render(Point2D.Double d) {
		if (this.currentState==STATE_MOVING) {
			this.executeMove();
		}

		if (animateOnlyWhenMoving && !isMoving()) {
			/*if (returnToDefaultWhenNotMoving)*/ super.resetAnimation();
			super.render(d, false);
		}
		else super.render(d); //Next layer up: Handle animations
	}

	/**
	 * Figure out the total X and Y movement this Movable will execute in one frame. Note that a Movable is not currently allowed
	 * to move more than its width horizontally or its height vertically in one frame, to avoid glitching through objects.
	 * 
	 * @return [xmov, ymov]
	 */
	public double[] calculateVelocities() {
		//Are we moving in goal mode?
		if (this.goal_mode == MOVE_TO_GOAL) {		
			double[] d = {0.0, 0.0};

			if (!this.isMoving()) return d;

			//Are we just always moving at top speed? If so, calculate that
			if (currentMode == MODE_LINEAR) this.currentSpeed = this.distancePerSecond/60; //TODO: Framerate shouldn't be hardcoded

			//Otherwise, accelerate towards top speed
			if (currentMode == MODE_ACCELERATE) {
				if (this.currentSpeed < this.distancePerSecond) this.currentSpeed += this.distancePerSecond/60 * this.accelerationPercentage/60;
				this.currentSpeed = Math.min(this.currentSpeed, this.distancePerSecond/60); //TODO: Framerate shouldn't be hardcoded
			}

			//Determine in which direction(s) we need to move to move closer to goal
			double xmov=0.0, ymov=0.0;

			if (this.currentPosition.x < this.currentGoal.x) xmov=0+Math.min(this.currentGoal.x-this.currentPosition.x, (this.currentSpeed));
			if (this.currentPosition.x > this.currentGoal.x) xmov=0-Math.min(this.currentPosition.x-this.currentGoal.x, (this.currentSpeed));
			if (this.currentPosition.y < this.currentGoal.y) ymov=0+Math.min(this.currentGoal.y-this.currentPosition.y, (this.currentSpeed));
			if (this.currentPosition.y > this.currentGoal.y) ymov=0-Math.min(this.currentPosition.y-this.currentGoal.y, (this.currentSpeed));

			d[0] = Math.min(xmov, this.getWidth()); d[1] = Math.min(ymov, this.getHeight());

			return d;
		}

		//Velocity mode?
		double additional_x = 0.0, additional_y = 0.0;
		//Process accelerations. Note: Do not make changes permanent, as this function may be called several times per frame.

		//Process all accelerations one after another
		for (int i = 0; i < this.accelerations.size(); i++) {
			//Accelerations may be divided over several frames
			if (this.accelerations.get(i).frames_expired < this.accelerations.get(i).total_frames) {
				additional_x += (this.accelerations.get(i).x_change / this.accelerations.get(i).total_frames);
				additional_y += (this.accelerations.get(i).y_change / this.accelerations.get(i).total_frames);
			}
			//Alternatively, some accelerations (gravity?) never expire
			else if ( this.accelerations.get(i).total_frames <= 0 ) { 
				additional_x += this.accelerations.get(i).x_change;
				additional_y += this.accelerations.get(i).y_change;
			}
			//Expired accelerations can be removed
			else { this.accelerations.remove(i); }
		}

		//If we're moving in velocity mode, just return what we have after accelerations have been processed
		if (this.goal_mode == MOVE_WITH_VELOCITY ) {
			double[] res = {this.acc_x+additional_x, this.acc_y+additional_y };
			return res;
		}

		else return null;
	}

	/**
	 * Figure out where the object will be one frame in advance.
	 * 
	 * @return This object's next position
	 */
	public Point2D.Double calculateNextPosition() {
		return this.calculateNextPosition(1);
	}

	/**
	 * Figure out where the object will be in n frames.
	 * 
	 * @param n Number of moves to look into the future
	 * @return This object's next position
	 */
	public Point2D.Double calculateNextPosition(double n) {
		double[] d = this.calculateVelocities();

		if (this.skip_next_x) d[0] = 0.0;
		if (this.skip_next_y) d[1] = 0.0;

		return new Point2D.Double(this.currentPosition.x+(d[0]*n), this.currentPosition.y+(d[1]*n));
	}

	/**
	 * Moves the object according to its current mode, goal, accelerations, etc.
	 * 
	 */	
	private void executeMove() {		
		//First figure out if we're moving
		Point2D.Double storeGoal = this.currentGoal;
		if (this.currentOverrideGoal != null) this.currentGoal = this.currentOverrideGoal;

		//Execute the movement
		this.currentPosition = this.calculateNextPosition();
		this.setSkip_last_x(this.skip_next_x); this.setSkip_last_y(this.skip_next_y); //Remember whether we skipped the movement
		this.skip_next_x = this.skip_next_y = false; //Never skip more than one movement

		//Make the effect of the accelerations permanent
		for (int i = 0; i < this.accelerations.size(); i++) {
			//Accelerations may be divided over several frames
			if (this.accelerations.get(i).frames_expired < this.accelerations.get(i).total_frames) {
				this.accelerations.get(i).frames_expired += 1;
				this.acc_x += (this.accelerations.get(i).x_change / this.accelerations.get(i).total_frames);
				this.acc_y += (this.accelerations.get(i).y_change / this.accelerations.get(i).total_frames);
			}
			//Alternatively, some accelerations (gravity?) never expire
			else if ( this.accelerations.get(i).total_frames <= 0 ) { 
				this.acc_x += this.accelerations.get(i).x_change;
				this.acc_y += this.accelerations.get(i).y_change;
			}
			//Expired accelerations need to be removed
			else { this.accelerations.remove(i); }
		}

		//Close enough to goal? If so, stop
		if ((Math.abs(this.currentPosition.x - this.currentGoal.x) < 1.5) && ((Math.abs(this.currentPosition.y - this.currentGoal.y) < 1.5))) {
			this.currentGoal = storeGoal;
			this.currentOverrideGoal = null;
			this.stop();
		}

		//Otherwise, are we in waypoint mode?
		else {
			if (this.waypoints != null) { //Are we in waypoint mode?
				if (this.waypoints.size() > 0) { //... and have waypoints left?
					//... and are sufficiently close to our goal? (It will never be == due to rounding errors etc.)
					if ((Math.abs(this.currentPosition.x - this.currentGoal.x) < 2.0) && ((Math.abs(this.currentPosition.y - this.currentGoal.y) < 2.0))) {
						this.currentGoal = this.waypoints.get(0); 
						this.waypoints.remove(0);
						if (this.waypoints.size() == 0) this.waypoints = null;
						//If so, move to the next waypoint...and remove it from the list...and kill the reference if needed
					}
				}
			}
			this.currentGoal = storeGoal; //Restore goal
		}
	}

	/**
	 * Default bounding rectangle of the object at its current position.
	 * 
	 */	
	@Override
	public Rectangle2D.Double boundingRectangle() {
		return super.boundingRectangle();
	}

	/**
	 * Default bounding rectangle of the object several steps in the future. Note that d can be
	 * negative, but that the result may not correctly represent the object's previous position
	 * (it's just the current movement inverted).
	 * 
	 * @param d Number of frames to look ahead
	 * 
	 */	
	public Rectangle2D.Double boundingRectangle(double d) {
		return super.boundingRectangle(this.calculateNextPosition(d));
	}

	/**
	 * Small bounding rectangle of the object at its current position.
	 * 
	 */	
	@Override
	public Rectangle2D.Double smallBoundingRectangle() {
		return super.smallBoundingRectangle();
	}

	/**
	 * Small bounding rectangle of the object several steps in the future.
	 * 
	 * @param steps Number of frames to look ahead
	 * 
	 */	
	public Rectangle2D.Double smallBoundingRectangle(int steps) {
		return super.smallBoundingRectangle(this.calculateNextPosition(steps));
	}

	/**
	 * Large bounding rectangle of the object at its current position.
	 * 
	 */	
	@Override
	public Rectangle2D.Double fullBoundingRectangle() {
		return super.fullBoundingRectangle();
	}

	/**
	 * Large bounding rectangle of the object several steps in the future.
	 * 
	 * @param steps Number of frames to look ahead
	 * 
	 */	
	public Rectangle2D.Double fullBoundingRectangle(int steps) {
		return super.fullBoundingRectangle(this.calculateNextPosition(steps));
	}

	@Override
	public Object clone() throws CloneNotSupportedException {return super.clone();}

	public boolean isSkip_last_y() {
		return skip_last_y;
	}

	public void setSkip_last_y(boolean skip_last_y) {
		this.skip_last_y = skip_last_y;
	}

	public boolean isSkip_last_x() {
		return skip_last_x;
	}

	public void setSkip_last_x(boolean skip_last_x) {
		this.skip_last_x = skip_last_x;
	}

	public java.awt.Polygon movementShape(int steps) {
		Rectangle2D.Double origin = this.fullBoundingRectangle(0);
		Rectangle2D.Double destination = this.fullBoundingRectangle(steps);
		Rectangle2D.Double temp = null;
		
		java.awt.Polygon result = new java.awt.Polygon();
		
		if (origin.getMinX() > destination.getMinX()) {temp = origin; origin = destination; destination = temp;}
		
		if (origin.getMinX() <= destination.getMinX()) {
			if (origin.getMinY() <= destination.getMinY()) {	
				result.addPoint((int) origin.getMinX(), (int) origin.getMinY()); //Top left, top left
				result.addPoint((int) origin.getMaxX(), (int) origin.getMinY()); //Top left, top right
				result.addPoint((int) origin.getMinX(), (int) origin.getMaxY()); //Top left, bottom left
				result.addPoint((int) destination.getMinX(), (int) destination.getMaxY()); //Bottom right, bottom left
				result.addPoint((int) destination.getMaxX(), (int) destination.getMaxY()); //Bottom right, bottom right
				result.addPoint((int) destination.getMaxX(), (int) destination.getMinY()); //Bottom right, top right
			}
			else {
				result.addPoint((int) origin.getMinX(), (int) origin.getMinY()); //Bottom left, top left
				result.addPoint((int) origin.getMinX(), (int) origin.getMaxY()); //Bottom left, bottom left
				result.addPoint((int) origin.getMaxX(), (int) origin.getMaxY()); //Bottom left, bottom right
				result.addPoint((int) destination.getMinX(), (int) destination.getMinY()); //Top right, top right
				result.addPoint((int) destination.getMaxX(), (int) destination.getMaxY()); //Top right, bottom right
				result.addPoint((int) destination.getMaxX(), (int) destination.getMinY()); //Top right, top right
			}
		}
		
		return result;
	}
}
