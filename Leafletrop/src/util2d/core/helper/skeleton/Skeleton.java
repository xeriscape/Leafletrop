package util2d.core.helper.skeleton;

import java.awt.geom.Point2D;
import java.util.HashMap;

public class Skeleton {
	//A skeleton is a list of named AttachmentPoints (i. e. named coordinates)
	//They are calculated from the top left corner
	private HashMap<String, AttachmentPoint> attachmentPoints = new HashMap<String, AttachmentPoint>();
	
	private int assumedWidth=1, assumedHeight=1;
	
	
	public Skeleton(String[] names, Point2D.Double[] values, int assumedWidth, int assumedHeight) {
		if (names.length != values.length) throw new java.lang.IndexOutOfBoundsException("Length of values does not match length of names");
		
		else {
			for (int i = 0; i < names.length; i++) {
				this.setPoint(names[i], values[i]);
				
			this.assumedWidth = assumedWidth;
			this.assumedHeight = assumedHeight;
			}
		}
	}
	
	public Skeleton() {
		// TODO Auto-generated constructor stub
	}

	public AttachmentPoint getPoint(String which) {
		if (this.attachmentPoints.containsKey(which)) 
			return this.attachmentPoints.get(which);
		
		else 
			return new AttachmentPoint(new Point2D.Double(0, 0));
	}
	
	public boolean setPoint(String which, Point2D.Double what) {
		//Currently does not check whether attachpoints are valid, but this might change in the future
		this.attachmentPoints.put(which, new AttachmentPoint(what));
		return true;
	}
}
