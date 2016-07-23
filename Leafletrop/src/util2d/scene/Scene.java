package util2d.scene;

import util2d.core.Renderable;

abstract public class Scene {
	int screenHeight=0, screenWidth=0;
	protected Renderable background = null;
	
	public abstract void update(Renderable[] toRender) throws IllegalArgumentException, CloneNotSupportedException;
	public abstract void start(String title);
	public abstract void initGL();
}
