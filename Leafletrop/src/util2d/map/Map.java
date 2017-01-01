package util2d.map;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.lang.IllegalArgumentException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.newdawn.slick.opengl.Texture;

import util2d.core.Renderable;

public class Map {
	public int mapHeight=0, mapWidth=0;
	public int tileHeight=0, tileWidth=0;
	public Tile[] mapTiles;
	
	//TODO: Implement tile set specification
	static String defaultDir = "ground";
	static String defaultImpassable = "impassable.png";
	static String defaultPassable = "passable.png";
	
	/**
	 * Creates a new Map object with the specified dimensions for itself and its tiles.
	 * 
	 * @param screenWidth Horizontal size of the map in pixels
	 * @param screenHeight Vertical size of the map in pixels
	 * @param tileWidth Horizontal size of each tile in pixels
	 * @param tileHeight Horizontal size of each tile in pixels
	 * @param isPassable 0 means passable, 1 means impassable
	 * @throws IOException 
	 * 
	 */
	public Map (int screenWidth, int screenHeight, int tileWidth, int tileHeight, int[] isPassable) throws IOException {
		Logger logger = LogManager.getRootLogger();
		
		//Some very basic sanity checking
		if (screenWidth <= 0 || screenHeight <= 0 || tileWidth <= 0 || tileHeight <= 0) throw new IllegalArgumentException("All parameters must be greater than zero");
		if (screenWidth % tileWidth != 0 ) {logger.error("Screen width "+screenWidth+" not divisible by tile width "+tileWidth);}
		if (screenHeight % tileHeight != 0 ) {logger.error("Screen height "+screenHeight+" not divisible by tile width "+tileHeight);}
		
		this.mapHeight = screenHeight;
		this.mapWidth = screenWidth;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		
		int numLines = mapHeight/tileHeight;
		int numColumns = mapWidth/tileWidth;
		
		this.mapTiles = new Tile[numLines * numColumns];
		
		//Create the Tile objects that make up the map
		Texture passableTexture = Renderable.load_res(defaultPassable, defaultDir);
		Texture impassableTexture = Renderable.load_res(defaultImpassable, defaultDir);
		
		Texture[] textures = { passableTexture, impassableTexture };
		
		for (int line = 0; line < numLines; line++) {
			for (int column = 0; column < numColumns; column++) {
				int cellNumber = (line * numColumns) + column;	//The number of a cell is equal to (#row * columns_in_a_row) + (#column)
				int passabilityStatus;
				if (isPassable == null) { passabilityStatus=0; }
				else passabilityStatus = isPassable[cellNumber]; //Passability status determines which tile graphic is used
				
				Point2D.Double tilePosition = tileCenter(column, line); //This returns the centre, so make adjustments 
				tilePosition.x = tilePosition.x - tileWidth/2;
				tilePosition.y = tilePosition.y - tileHeight/2;
				
				//Create & place Tile objects				
				this.mapTiles[cellNumber] = new Tile(new Renderable( textures[passabilityStatus], tilePosition ), (passabilityStatus == 0), tileWidth, tileHeight );
				//this.mapTiles[cellNumber].tileGraphic.currentPosition = tilePosition;
			}
		}
	}
	
	/**
	 * Creates a new Map object with the specified dimensions for itself and its tiles. All tiles can be passed.
	 * 
	 * @param screenWidth Horizontal size of the map in pixels
	 * @param screenHeight Vertical size of the map in pixels
	 * @param tileWidth Horizontal size of each tile in pixels
	 * @param tileHeight Horizontal size of each tile in pixels
	 * @throws IOException 
	 * 
	 */
	public Map (int screenWidth, int screenHeight, int tileWidth, int tileHeight) throws IOException {
		this(screenWidth, screenHeight, tileWidth, tileHeight, null);
	}
	
	/**
	 * Calculate the X and Y number of the Tile containing the Point.
	 * 
	 * @param coordinates Coordinates of the point being examined
	 * @return [column-number, row-number]
	 */
	public int[] xyTile(Point2D.Double coordinates) {
		return xyTile((int) Math.floor(coordinates.getX()), (int) Math.floor(coordinates.getY())) ;
	}
	
	/**
	 * Calculate the X and Y number of the Tile containing the coordinates.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @return [column-number, row-number]
	 */
	public int[] xyTile(int x, int y) {
		int x_t = 0, y_t = 0;
		
		if ( x > mapWidth ) x_t = mapWidth/tileWidth;
		else x_t = Math.max(x, 0) / tileWidth;
		
		if ( y > mapHeight ) y_t = mapHeight/tileHeight;
		else y_t = Math.max(y, 0) / tileHeight;
		
		int result[] = {x_t, y_t};
		return result;		
	}
	
	public int[] xyTile(double x, double y) {
		return xyTile((int) Math.floor(x), (int) Math.floor(y) );
	}
	
	/**
	 * Calculate the central point of the Tile that is the Xth horizontal, Yth vertical Tile.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @return Central point
	 */
	public Point2D.Double tileCenter(int x, int y) {
		x = Math.min(x,  mapWidth);
		y = Math.min(y, mapHeight);
		
		Point2D.Double result = new Point2D.Double( (x * tileWidth) + tileWidth/2 , (y * tileHeight) + tileHeight/2 );
		return result;
	}
	
	private int coordinatesToIndex(int x, int y) {
		int columns_per_line = mapWidth/tileWidth;
		
		int cellNumber = (y * columns_per_line) + x;	//The number of a cell is equal to (#row * columns_in_a_row) + (#column)
		
		
		return cellNumber;	//The number of a cell is equal to (#row * columns_in_a_row) + (#column)
	}
	
	public Tile tileAtCoordinates(int x, int y) {
		x = Math.min(x,  mapWidth-1);
		y = Math.min(y, tileWidth-1);
		int i = Math.min(coordinatesToIndex(x, y), (this.mapTiles.length-1)); //TODO: Fix off-screen movement
		
		return this.mapTiles[i];
	}
	
	public Tile tileAtCoordinates(Point2D.Double coords) {
		int[] holder = xyTile(coords);
		return tileAtCoordinates(holder[0], holder[1]);
	}
	
	/**
	 * Render the map (and each of its tiles) at the appropriate position.
	 */
	public void render() {
		int numLines = mapHeight/tileHeight;
		int numColumns = mapWidth/tileWidth;
		
		for (int line = 0; line < numLines; line++) {
			for (int column = 0; column < numColumns; column++) {
				//The number of a cell is equal to (#row * columns_in_a_row) + (#column)
				this.mapTiles[(line * numColumns) + column].render(new Point2D.Double(column * tileWidth, line * tileHeight));
			}
		}
	}
	
	public int getRows() {
		return (mapHeight/tileHeight);
	}
	
	public int getColumns() {
		return (mapWidth/tileWidth);
	}
}
