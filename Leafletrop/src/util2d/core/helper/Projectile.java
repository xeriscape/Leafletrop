/**
 * 
 */
package util2d.core.helper;

import java.awt.geom.Point2D;

/**
 * @author Kaye
 *
 */
public class Projectile extends util2d.core.Collisible {
	static final int ENEMY_PROJECTILE = 0, FRIENDLY_PROJECTILE=1, GENERAL_PROJECTILE=2;
	
	double xSpeed=0.0, ySpeed=0.0; //Current movement speed
	double xChange=0.0, yChange=0.0; //Changes in movement speed
	int bouncesRemaining=0; //Governs whether the projectile bounces off objects
	int timeToLive=0; //In milliseconds
	int type = Projectile.GENERAL_PROJECTILE; //Does it hit players only, enemies only, or either?
	
	//Full constructor
	public Projectile(double xSpeed, double ySpeed, double xChange, double yChange, int bouncesRemaining, int timeToLive, int type, Animation[] anims, Point2D.Double origin)
	{
		super(anims, origin);
		
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
		this.xChange = xChange;
		this.yChange = yChange;
		this.bouncesRemaining = bouncesRemaining;
		this.timeToLive = timeToLive;
		this.type = type;	
	}
	
	//Fire at a specific point
	/*public Projectile(Point2D.Double origin, Point2D.Double target, int bouncesRemaining, int timeToLive, int type) {
		double x_to_move = origin.getX() - target.getX();
		double y_to_move = origin.getY() - target.getY();
	}*/
}