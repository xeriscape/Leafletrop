package util2d.ai;

import java.awt.geom.Point2D;

import org.lwjgl.input.Keyboard;

import util2d.actor.Actor;
import util2d.core.GLOBALS;
import util2d.core.Movable;
import util2d.map.Map;

public class ActOnControls extends Behavior {

	@Override
	public void process(Map m, Actor self) {
		// TODO Auto-generated method stub
		this.process(m, self, null, 0);

	}

	@Override
	public void process(Map m, Actor self, Actor player) {
		// TODO Auto-generated method stub
			this.process(m, self, player, 0);
	}

	@Override
	public void process(Map m, Actor self, Actor player, int keyboard) {
		// TODO Auto-generated method stub
		Map sceneMap = m;
		
		//This is needed to allow movement processing
		if (!self.isMoving()) self.moveTo(self.getCurrentPosition(), Movable.MODE_ACCELERATE);
		
		//If the player is pressing arrow keys, that means movement
				if (Keyboard.isKeyDown(GLOBALS.getKeyLeft()))
				{ //If player is not aiming left yet, aim player left
					if (! (((sceneMap.xyTile(self.currentGoal))[0]) < (sceneMap.xyTile(self.getCurrentPosition())[0] )))
						self.currentGoal = (new Point2D.Double( sceneMap.tileCenter(sceneMap.xyTile( self.currentGoal )[0]-1 , sceneMap.xyTile( self.currentGoal )[1]).getX()  , self.currentGoal.y));
				}

				else if (Keyboard.isKeyDown(GLOBALS.getKeyRight()))
				{ //If player is not aiming right yet, aim player right
					if (! (((sceneMap.xyTile(self.currentGoal))[0]) > (sceneMap.xyTile(self.getCurrentPosition())[0] )))
						self.currentGoal = (new Point2D.Double( sceneMap.tileCenter(sceneMap.xyTile( self.currentGoal )[0]+1 , sceneMap.xyTile( self.currentGoal )[1]).getX()  , self.currentGoal.y));
				}

				if (Keyboard.isKeyDown(GLOBALS.getKeyDown()))
				{ //If player is not aiming down yet, aim player down
					if (! (((sceneMap.xyTile(self.currentGoal))[1]) > (sceneMap.xyTile(self.getCurrentPosition())[1] )))
						self.currentGoal = (new Point2D.Double(self.currentGoal.x ,  sceneMap.tileCenter(sceneMap.xyTile( self.currentGoal )[0] , sceneMap.xyTile( self.currentGoal )[1]+1).getY()));
				}


				else if (Keyboard.isKeyDown(GLOBALS.getKeyUp()))
				{ //If player is not aiming up yet, aim player up
					if (! (((sceneMap.xyTile(self.currentGoal))[1]) < (sceneMap.xyTile(self.getCurrentPosition())[1] )))
						self.currentGoal = (new Point2D.Double(self.currentGoal.x ,  sceneMap.tileCenter(sceneMap.xyTile( self.currentGoal )[0] , sceneMap.xyTile( self.currentGoal )[1]-1).getY()));
				}

				//If player lets go of a key, stop 'em
				if (Keyboard.next()) {
					if (Keyboard.getEventKeyState() == false) {
						int eventKey = Keyboard.getEventKey();
						
						if ((eventKey == GLOBALS.getKeyLeft())||(eventKey == GLOBALS.getKeyRight()))
							self.currentGoal = (new Point2D.Double(self.getCurrentPosition().x + (self.calculateVelocities()[0]*0.1), self.currentGoal.y));

						if ((eventKey == GLOBALS.getKeyUp())||(eventKey == GLOBALS.getKeyDown()))
							self.currentGoal =(new Point2D.Double(self.currentGoal.x, self.getCurrentPosition().y + (self.calculateVelocities()[1]*0.1)));
						}
					}
				}

	}
