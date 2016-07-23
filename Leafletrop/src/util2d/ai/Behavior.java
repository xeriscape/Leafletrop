package util2d.ai;

import util2d.actor.Actor;
import util2d.map.Map;

public abstract class Behavior {
	Actor self = null;
	
	public abstract void process(Map m, Actor self);
	public abstract void process(Map m, Actor self, Actor player);
	public abstract void process(Map m, Actor self, Actor player, int keyboard);
}
