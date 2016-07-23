package test;
import java.io.File;
import java.io.FileInputStream;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import static org.lwjgl.opengl.GL11.*;

public class DisplayTest {
    public static void main(String[] args) throws Throwable {
        /*try {
            Display.setDisplayMode(new DisplayMode(640, 480));
            Display.setTitle("Episode 1 – Display Test");
            Display.create();
        } catch (LWJGLException e) {
            System.err.println("Display wasn't initialized correctly.");
            System.exit(1);
        }*/
    	
    	/*try {
    		Display.setDisplayMode(new DisplayMode(850, 480));
    		Display.setTitle("Episode 2 - A fresh display!");
    		Display.create();
    	} catch (LWJGLException e) {
    		e.printStackTrace();
    		Display.destroy();
    		System.exit(1);
    	}*/
    	
    	Random rand = new Random();
    	
    	Texture texture = null;
    	
    	try {
    		
    		//Set up display
    		Display.setDisplayMode(new DisplayMode(800,600));
    		Display.setTitle("Episode 3 - Flashing figures");
    		Display.create();
    		GL11.glEnable(GL11.GL_TEXTURE_2D);
    		
    		//Set up projection matrix at 0,0
    		glMatrixMode(GL_PROJECTION);
    		glLoadIdentity(); // Resets any previous projection matrices
    		glOrtho(0, 800, 600, 0, 1, -1);
    		glMatrixMode(GL_MODELVIEW);
    		
       		//Load texture
    		texture = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/image.png")));
			System.out.println("Texture loaded: "+texture);
			System.out.println(">> Image width: "+texture.getImageWidth());
			System.out.println(">> Image height: "+texture.getImageHeight());
			System.out.println(">> Texture width: "+texture.getTextureWidth());
			System.out.println(">> Texture height: "+texture.getTextureHeight());
			System.out.println(">> Texture ID: "+texture.getTextureID());

    		
     	}
    	
    	catch (LWJGLException e) {
    		e.printStackTrace();
    		Display.destroy();
    		System.exit(1);    		
    	}
 
        while (!Display.isCloseRequested()) {  
        	//Clear canvas
    		glClear(GL_COLOR_BUFFER_BIT);
    		
    		//Draw		
    		
    		texture.bind(); // or GL11.glBind(texture.getTextureID());
    		
    		GL11.glBegin(GL11.GL_QUADS);
    			GL11.glTexCoord2f(0,0);
    			GL11.glVertex2f(100+rand.nextInt(50)-25,100+rand.nextInt(50)-25);
    			GL11.glTexCoord2f(1,0);
    			GL11.glVertex2f(100+texture.getTextureWidth()+rand.nextInt(50)-25,100);
    			GL11.glTexCoord2f(1,1);
    			GL11.glVertex2f(100+texture.getTextureWidth()+rand.nextInt(50)-25,100+texture.getTextureHeight());
    			GL11.glTexCoord2f(0,1);
    			GL11.glVertex2f(100+rand.nextInt(50)-25,100+texture.getTextureHeight());
    		GL11.glEnd();
    		
    		
        	Display.update();
            Display.sync(60);
        }
        
        glBindTexture(GL_TEXTURE_2D, 0);
        texture.release();
        Display.destroy();
        System.exit(0);
    }
}