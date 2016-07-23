package util2d.core.helper.skeleton;

import java.awt.geom.Point2D;

import util2d.core.Renderable;

public class Attachment {
	//What is the thing that is actually being rendered?
	Renderable worldObject = null;
	
	//Where is it going to be attached? This is calculated by overlaying the attachWhere 
	//and the attachmentPoint of the skeleton
	AttachmentPoint attachWhere = new AttachmentPoint(new Point2D.Double(0,0));
	
	//Display order? <50 means in back, >50 means in front
	int displayLayer = 50;
}
