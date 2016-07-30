package util2d.core.helper.skeleton;

import java.awt.geom.Point2D;

public class AttachmentPoint {
	static final String[] NAMES = {"HEAD", "UPPER_TORSO", "LOWER_TORSO", "L_ARM", "R_ARM", "L_LEG", "R_LEG", "SHADOW"};

	//Note that upon rendering, an AttachmentPoint is calculated from the top left corner
	//So the coordinates are RELATIVE TO that
	public Point2D.Double place = new Point2D.Double();
	
	
	public AttachmentPoint(Point2D.Double value) {
		this.place = value;
	}
}
