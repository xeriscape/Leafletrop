package util2d.messaging;

import misc.Identity;
import util2d.core.Collisible;

public class CollisionMessage extends Identity implements Message {
	Collisible sender;
}
