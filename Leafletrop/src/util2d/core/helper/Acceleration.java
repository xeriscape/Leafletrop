package util2d.core.helper;

public class Acceleration {
	public double x_change=0.0, y_change=0.0;
	public int total_frames = 0, frames_expired = 0;
	
	public Acceleration (double xc, double yc, int tf) {
		this.x_change = xc;
		this.y_change = yc;
		this.total_frames = tf; //<= 0 means it never expires 
	}
}
