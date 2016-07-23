package util2d.core;

import java.awt.geom.Point2D;
import util2d.core.helper.Animation;


/**
 * Animatables extend Renderables by animation functionality. You specify a set of Animations,
 * and select the Animatable's active Animation from this.
 *
 */
public class Animatable extends Renderable implements Cloneable  {
	Animation[] animations;
	Animation current_animation;

	/**
	 * Jump back to the first frame
	 */
	public void resetAnimation() {
		this.t = this.current_animation.animation_frames[0];
		this.current_animation.current_frame = this.current_animation.animation_frames[0];
	}
	
	/**
	 * Calling render() on an Animatable causes it to call animate() on its currently active
	 * animation, then to call render() on its parent. In effect, this extends Renderable by
	 * animation functionality.
	 * 
	 * @see util2d.core.helper.Animation#animate()
	 * @see util2d.core.Renderable#render(java.awt.geom.Point2D.Double)
	 */
	public void render() {
		this.render(this.currentPosition);
	}

	
	/**
	 * Calling render() on an Animatable causes it to call animate() on its currently active
	 * animation, then to call render() on its parent. In effect, this extends Renderable by
	 * animation functionality.
	 * 
	 * @param p Specific position at which to render
	 * @see util2d.core.helper.Animation#animate()
	 * @see util2d.core.Renderable#render(java.awt.geom.Point2D.Double)
	 */
	public void render(Point2D.Double p) {
		if (this.current_animation.animate()) { //Animate...
			this.t = this.current_animation.current_frame;
			//...but update the current frame only if the animation actually advanced
		}
		super.render(p); //Next layer up: Handle actual display
	}
	
	public void render(Point2D.Double p, boolean doNotAnimate) {
		//skippit
		super.render(p); //Next layer up: Handle actual display
	}
	
	/* (non-Javadoc)
	 * @see util2d.Renderable#toString()
	 */
	public String toString() {
		String result = "";
		
		result += super.toString();
		result += ("Animations: "+this.animations.length );
		for (Animation a:animations) result += (">> "+a.toString());
		
		return result.toString();
	}
	
	/**
	 * Create a new Animatable with a specific Animation set. The Animatable will be placed
	 * at (0,0) and the first Animation in the set will be set active.
	 * @param anims Animation set
	 */
	public Animatable(Animation[] anims) {
		this(anims, new Point2D.Double(0,0), 0);
	}
	
	/**
	 * Create a new Animatable with a specific Animation set and position. The Animatable
	 * will be placed at the specific Point and the first Animation in the set will be set active.
	 * @param anims Animation set
	 * @param p Initial position
	 */
	public Animatable(Animation[] anims, Point2D.Double p) {
		this(anims, p, 0);
	}
	
	/**
	 * Create a new Animatable with a specific Animation set, position and active frame.
	 * 
	 * @param anims Animation set
	 * @param p Initial position
	 * @param activeIndex Active frame index
	 */
	public Animatable(Animation[] anims, Point2D.Double p, int activeIndex) {
		this.currentPosition = p;
		
		this.animations = new Animation[anims.length];
		System.arraycopy(anims, 0, this.animations, 0, anims.length);
		
		this.current_animation = this.animations[activeIndex];
		this.t = this.current_animation.current_frame;
	}
	
	public Object clone() throws CloneNotSupportedException {return super.clone();}
}
