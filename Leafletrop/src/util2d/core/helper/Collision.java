/**
 * 
 */
package util2d.core.helper;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import util2d.core.Movable;
import util2d.core.Renderable;
import util2d.map.Map;
import java.awt.geom.Area; 



/**
 * 
 * Helper functions useful in collision processing - mostly figuring out whether a Renderable collides with a Tile,
 * or whether a Renderable collides with another Renderable, or whether that is going to happen next move.
 *
 */
public class Collision {
	
	/**
	 * Helper function that figures out the corner points of a rectangle.
	 * 
	 * @param currRec The Rectangle2D.Double to be examined
	 * @return [topLeft, topRight, bottomLeft, bottomRight]
	 */
	public static Point2D.Double[] rectangleFacts(Rectangle2D.Double currRec) {
		double w = currRec.getMaxX() - currRec.getMinX();
		double h = currRec.getMaxY() - currRec.getMinY();
		double x = currRec.x;
		double y = currRec.y;
		
		//Check the four corners of the moved position for impassable tiles
		Point2D.Double topLeft = new Point2D.Double(x, y) ;
		Point2D.Double topRight = new Point2D.Double(x+w, y) ;
		Point2D.Double bottomLeft = new Point2D.Double(x, y+h) ;
		Point2D.Double bottomRight = new Point2D.Double(x+w, y+h) ;
		
		Point2D.Double[] result = {topLeft, topRight, bottomLeft, bottomRight};
		return result;
	}
		
	/**
	 * Figure out if an object will collide with a non-passable tile next move.
	 * 
	 * @param mov The Movable to be examined
	 * @param m The Map it is located on
	 * @return [topLeft, topRight, bottomLeft, bottomRight]
	 */
	public static boolean[] checkTileCollision(Movable mov, Map m) {		
		//Only horizontal movement
		Rectangle2D.Double recHorizontal = mov.boundingRectangle(1);
		recHorizontal.y = mov.boundingRectangle(0).y;
		
		Point2D.Double[] horiPoints = rectangleFacts(recHorizontal);
		
		boolean horizontalCollision = 
				  (m.tileAtCoordinates(horiPoints[0]).isPassable == false
				|| m.tileAtCoordinates(horiPoints[1]).isPassable == false
				|| m.tileAtCoordinates(horiPoints[2]).isPassable == false
				|| m.tileAtCoordinates(horiPoints[3]).isPassable == false);
		
		
		//Only vertical movement
		Rectangle2D.Double recVertical = mov.boundingRectangle(1);
		recVertical.x = mov.boundingRectangle(0).x;
		Point2D.Double[] vertiPoints = rectangleFacts(recVertical);
		
		boolean verticalCollision = 
				  (m.tileAtCoordinates(vertiPoints[0]).isPassable == false
				|| m.tileAtCoordinates(vertiPoints[1]).isPassable == false
				|| m.tileAtCoordinates(vertiPoints[2]).isPassable == false
				|| m.tileAtCoordinates(vertiPoints[3]).isPassable == false);
		
		
		//Combine for results
		boolean[] results = {horizontalCollision, verticalCollision};
		return results;
		
		
		
	}
	
	public static Rectangle2D.Double checkCollisionRectangle(Area area, Renderable b, int minPixels) {		
		Rectangle2D.Double checkB=new Rectangle2D.Double();
		
		
		if (b instanceof Movable) checkB = ((Movable) b).boundingRectangle(1); //.setRect(((Movable) b).calculateNextPosition(1).getX(), ((Movable) b).calculateNextPosition(1).getY(), b.getWidth(), b.getHeight());
		else 					  checkB= b.boundingRectangle();//.setRect(b.currentPosition.x, b.currentPosition.y, b.getWidth(), b.getHeight());
		
		Area d = new Area(area);
		Area c = new Area(checkB.getBounds2D());
		
		d.intersect(c); 
		
		Rectangle2D e = d.getBounds();
		
		Rectangle2D.Double collisionResult = new Rectangle2D.Double(e.getMinX(), e.getMinY(), Math.max(e.getWidth(), 1), Math.max(e.getHeight(), 1)); 
		
		return collisionResult;
		}
	
	/**
	 * Figure out if and how two Renderables collide.
	 * For Movables, collisions are checked one move in the future.
	 * 
	 * @param a The first Renderable to be examined
	 * @param b The second Renderable to be examined
	 * @param minPixels Not yet implemented
	 * @return Intersection of the (small) boundingRectangles of the two Renderables, if any
	 */
	public static Rectangle2D.Double checkCollisionRectangle(Renderable a, Renderable b, int minPixels) {		
		Rectangle2D.Double checkA=new Rectangle2D.Double(), checkB=new Rectangle2D.Double();
		if (a instanceof Movable) checkA = ((Movable) a).boundingRectangle(1);  //checkA.setRect(((Movable) a).calculateNextPosition(1).getX(), ((Movable) a).calculateNextPosition(1).getY(), a.getWidth(), a.getHeight());
		else checkA = a.smallBoundingRectangle(); //.setRect(a.currentPosition.x, a.currentPosition.y, a.getWidth(), a.getHeight() );
		
		if (b instanceof Movable) checkB = ((Movable) b).boundingRectangle(1); //.setRect(((Movable) b).calculateNextPosition(1).getX(), ((Movable) b).calculateNextPosition(1).getY(), b.getWidth(), b.getHeight());
		else checkB= b.smallBoundingRectangle();//.setRect(b.currentPosition.x, b.currentPosition.y, b.getWidth(), b.getHeight());
		
		return intersectionRectangle(checkA, checkB);
		}
	
	/**
	 * Figure out if two Renderables collide on the x-Axis, or, in case of Movables, will collide on the x-Axis the next time
	 * a move is executed.
	 *  
	 * @param a The first Renderable to be examined
	 * @param b The second Renderable to be examined
	 * @param minPixels Not yet implemented
	 * @return Do they collide if only X movement is considered?
	 */
	public static boolean checkHorizontalCollision(Renderable a, Renderable b, int minPixels) {		
		Rectangle2D.Double checkA=new Rectangle2D.Double(), checkB=new Rectangle2D.Double();
		if (a instanceof Movable) checkA = ((Movable) a).boundingRectangle(1);
		else checkA = a.smallBoundingRectangle();
		
		if (b instanceof Movable) checkB = ((Movable) b).boundingRectangle(1);
		else checkB= b.smallBoundingRectangle();
		
		checkA.y = a.smallBoundingRectangle().y;
		checkB.y = b.smallBoundingRectangle().y;
		
		return (intersectionRectangle(checkA, checkB) != null);
		}
	
	/**
	 * Figure out if two Renderables collide on the y-Axis, or, in case of Movables, will collide on the x-Axis the next time
	 * a move is executed.
	 *  
	 * @param a The first Renderable to be examined
	 * @param b The second Renderable to be examined
	 * @param minPixels Not yet implemented
	 * @return Do they collide if only Y movement is considered?
	 */
	public static boolean checkVerticalCollision(Renderable a, Renderable b, int minPixels) {		
		Rectangle2D.Double checkA=new Rectangle2D.Double(), checkB=new Rectangle2D.Double();
		if (a instanceof Movable) checkA = ((Movable) a).boundingRectangle(1);
		else checkA = a.smallBoundingRectangle();
		
		if (b instanceof Movable) checkB = ((Movable) b).boundingRectangle(1);
		else checkB= b.smallBoundingRectangle();
		
		checkA.x = a.smallBoundingRectangle().x;
		checkB.x = b.smallBoundingRectangle().x;
		
		return (intersectionRectangle(checkA, checkB) != null);
		}

	/**
	 * Figure out if and how the small bounding rectangles of two Renderables overlap.
	 * 
	 * @param a The first Renderable to be examined
	 * @param b The second Renderable to be examined
	 * @return Rectangle2D.Double defining the intersection or null 
	 */
	public static Rectangle2D.Double intersectionRectangle(Renderable a, Renderable b) {	
		return Collision.intersectionRectangle(a.smallBoundingRectangle() , b.smallBoundingRectangle());
	}
	
	/**
	 * Figure out if and how two Rectangles intersect.
	 * 
	 * @param a The first Rectangle2D to be examined
	 * @param b The second Rectangle2D to be examined
	 * @return Rectangle2D.Double defining the intersection or null 
	 */
	public static Rectangle2D.Double intersectionRectangle(Rectangle2D a, Rectangle2D b) {
		Rectangle2D.Double res = new Rectangle2D.Double();
		
		//Return null if they don't intersect
		if (a.intersects(b)) {			
			Rectangle2D.intersect(a, b, res);
			return res;
		}
		
		else return null;
	}
}
