package util2d.core.helper;
import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;

import util2d.core.GLOBALS;

/**
 * An Animation consists of a set of Textures and a set of integers that describe for how many frames
 * each Texture will be displayed. 
 *
 */
public class Animation {
	//These two define the animation
	public Texture[] animation_frames= null;
	int[] animation_delay= null;

	//Current state of the animation
	public Texture current_frame = null;
	int current_delay= 0;
	int current_frame_number= 0;

	//Some stuff relating to animation
	int delay_counter=0;
	boolean loops=true;

	//Constructors	
	/**
	 * Create a new Animation. All images will be evenly spaced (each delay being one second)
	 * @param a_f Set of Textures forming the Animation
	 */
	public Animation(ArrayList<Texture> a_f) {
		this(a_f, GLOBALS.secondsToFrames(1.0));
	}
	
	/**
	 * Create a new Animation. All images will be evenly spaced (using the specified delay)
	 * @param a_f Set of Textures forming the Animation
	 * @param delay Number of seconds each image will be displayed before moving to the next
	 * @throws IllegalArgumentException Exception is thrown if specified delay is not greater than 0
	 */
	public Animation(ArrayList<Texture> a_f, double delaySeconds) throws IllegalArgumentException {
		this(a_f, GLOBALS.secondsToFrames(delaySeconds));
	}
	
	/**
	 * Create a new Animation. All images will be evenly spaced (using the specified delay)
	 * @param a_f Set of Textures forming the Animation
	 * @param delay Number of frames each image will be displayed before moving to the next
	 * @throws IllegalArgumentException Exception is thrown if specified delay is not greater than 0
	 */
	public Animation(ArrayList<Texture> a_f, int delay) throws IllegalArgumentException {
		if (delay <= 0) {
			throw new IllegalArgumentException("Delay value is <= 0: "+delay);
		}
		
		int[] delays = new int[a_f.size()];
		for (int i = 0; i < a_f.size(); i++) delays[i] = delay;
		
		Texture[] a = a_f.toArray(new Texture[0]);
		
		this.animation_frames = new Texture[a.length];
		this.animation_delay = new int[a.length];
		for (int i = 0; i < a.length; i++) {animation_delay[i]=delay;}
		
		System.arraycopy(a, 0, animation_frames, 0, a.length);
		
		this.delay_counter = 0;
		this.current_frame_number = 0;
		this.current_frame = this.animation_frames[0];
		this.current_delay = this.animation_delay[0];
	}
	
	/**
	 * @param a_f
	 * @param delays
	 * @throws IllegalArgumentException Exception is thrown if set of delays and set of frames are not the same size
	 */
	public Animation(ArrayList<Texture> a_f, double[] delaysDouble) throws IllegalArgumentException {
		int[] delays = new int[delaysDouble.length];
		for (int i = 0; i < delaysDouble.length; i++) delays[i]=GLOBALS.secondsToFrames(delaysDouble[i]);


		if (delays.length != a_f.size()) {
			throw new IllegalArgumentException("Array sizes do not match: "+a_f.size()+" vs. "+delays.length);
		}
		
		Texture[] a = a_f.toArray(new Texture[0]);
		
		this.animation_frames = new Texture[a.length];
		this.animation_delay = new int[a.length];
		for (int i = 0; i < a.length; i++) {animation_delay[i]=delays[i];}
		
		System.arraycopy(a, 0, animation_frames, 0, a.length);
		
		this.delay_counter = 0;
		this.current_frame_number = 0;
		this.current_frame = this.animation_frames[0];
		this.current_delay = this.animation_delay[0];
	}
	

	/**
	 * @param a_f
	 * @param delays
	 * @throws IllegalArgumentException Exception is thrown if set of delays and set of frames are not the same size
	 */
	public Animation(ArrayList<Texture> a_f, int[] delays) throws IllegalArgumentException {
		if (delays.length != a_f.size()) {
			throw new IllegalArgumentException("Array sizes do not match: "+a_f.size()+" vs. "+delays.length);
		}
		
		Texture[] a = a_f.toArray(new Texture[0]);
		
		this.animation_frames = new Texture[a.length];
		this.animation_delay = new int[a.length];
		for (int i = 0; i < a.length; i++) {animation_delay[i]=delays[i];}
		
		System.arraycopy(a, 0, animation_frames, 0, a.length);
		
		this.delay_counter = 0;
		this.current_frame_number = 0;
		this.current_frame = this.animation_frames[0];
		this.current_delay = this.animation_delay[0];
	}
	
	//The heart of the whole thing
	public boolean animate() {
		this.delay_counter++;
		boolean updated = false;
		
		if (this.delay_counter >= this.current_delay) { //Is the delay expired?
			if (this.current_frame_number < this.animation_frames.length-1) {//Still frames left?
				this.current_frame_number++; //If so, go to the next one
				updated = true;
			}
			
			else if (loops==true) {//No frames left, but it loops?
				this.current_frame_number=0; //Go back to the start
				updated = true;
			}
			
			if (updated) { //So do we have to update? If so...
				this.current_delay = animation_delay[current_frame_number]; //Load delay for next frame
				this.current_frame = animation_frames[current_frame_number]; //Load next frame
				this.delay_counter = 0; //Reset delay counter
			}
		}
		
		return updated;

	}
}
