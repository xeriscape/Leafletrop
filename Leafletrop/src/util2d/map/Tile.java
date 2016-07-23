package util2d.map;

import java.awt.geom.Point2D;

import util2d.core.*;

public class Tile {
	public boolean isPassable = false;
	public Renderable tileGraphic = null;
	int height=0, width=0;
	public boolean doRender=true;
	
	public Tile(Renderable image, Boolean passable, int width, int height) {
		this.isPassable = passable;
		this.tileGraphic = image;
		this.tileGraphic.setWidth(width);
		this.tileGraphic.setHeight(height);
		//this.tileGraphic.hasShadow = false;
	}
	
	public Tile(Renderable image, Boolean passable) {
		this(image, passable, 0, 0);
	}
	
	public void render(Point2D.Double where) {
		if (this.doRender) this.tileGraphic.render(where);
	}
}
