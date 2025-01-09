package inkball;

import processing.core.PImage;
import processing.core.PVector;
import processing.core.PApplet;

/**
 * The Tile class represents each tile on the game board.
 * Tiles can be walls, holes, or empty spaces and contain properties such as
 * their type (wall, hole, or empty) and methods for interacting with other
 * game objects (e.g., drawing and collision detection).
 */


public class Tile extends GameObject {

    Board board;

    // Enum for tile types
    public enum TileType {
        EMPTY,
        WALL0,
        WALL1,
        WALL2,
        WALL3,
        WALL4,
        HOLE0,
        HOLE1,
        HOLE2,
        HOLE3,
        HOLE4
    }
    
    private TileType type;

    // For Wall Tiles
    LineSegment[] hitbox;
    private PImage[] walls;
    private boolean isWall;

    // For Empty Tiles
    private PImage backgroundTile;

    // For holes
    private boolean isHole;
    private int debugColour;

    // For ball tiles
    private String ballColour;  


    // -------------------------- Constructor ----------------------------------
    /**
     * Constructor for the Tile class
     * @param x         x coordinate of the tile 
     * @param y         y coordinate of the tile
     * @param type      type of the tile (WALL, HOLE or EMPTY)
     * @param board     Reference to game board in which the tile is a part of
    */
    public Tile(int x, int y, TileType type, Board board) {
        super(x, y);
        walls = new PImage[5];
        this.isWall = false;
        this.type = type;
        this.board = board;


        initialiseTileProperties(type);

        if (isWall) {
            initialiseHitbox();
        }
    
    }

    // --------------------- Getters and Setters -------------------------------
    /**
     * Gets the type of the tile
     * @return  The TileType of the tile
     */
    public TileType getType() {
        return this.type;
    }

    /**
     * Sets the image of the background tiles
     * 
     * @param backgroundTile  The image to be set as the background tile
     */
    public void setBackgroundTile(PImage backgroundTile) {
        this.backgroundTile = backgroundTile;
    }

    /**
     * Sets the Wall Image for the tile
     * @param index Index of the wall image to set (0-4)
     * @param wall  The image to be set as the wall
     */
    public void setWallImage(int index, PImage wall) {
        if (index >= 0 && index < walls.length) {
            walls[index] = wall;
        }
    }

    /**
     * Gets the color of the ball currently on this tile (if any).
     * @return The color of the ball on the tile.
     */

    public String getBallColour() {
        return this.ballColour;
    }

    /**
     * Sets a boolean to indicate if the tile is a hole tile
     * @param isHole    Boolean to indicate if the tile is a hole
     */
    public void setHole(boolean isHole) {
        this.isHole = isHole;
    }

    /**
     * Gets the boolean that indicates if the tile is a hole
     * @return  Boolean indicating if the tile is a hole
     */
    public boolean isHole() {
        return this.isHole;
    }

    /**
     * Sets a boolean to indicate if the tile is a wall
     * @return  Boolean indicating if the tile is a wall
     */
    public boolean isWall() {
        return this.isWall;
    }

    /**
     * Sets a boolean to indicate if the tile is a wall
     * @param isWall    Boolean indicating if the tile is a wall
     */
    public void setIsWall(boolean isWall) {
        this.isWall = isWall;
    }

    /**
     * Gets the hitbox for this tile (only applicable for wall tiles).
     * @return The hitbox of the tile (an array of line segments).
     */
    public LineSegment[] getHitbox() {
        return this.hitbox;
    }

    /**
     * Sets a colour to help debug hole tiles
     * @param colour    The colour to set for debugging
     */
    public void setDebugColour(int colour) {
        this.debugColour = colour;
    }

    /**
     * Sets the the type of the tile
     * @param type The TileType of the tile (WALL, HOLE or EMPTY)
     */
    public void setTileType(TileType type) {
        this.type = type;
    }
    // --------------------------- Methods -------------------------------------
    // --- Initialisation of tile properties ---   
    private void initialiseTileProperties(TileType type) {
        // Set flags based on tile type
        switch (type) {
            case WALL0:
            case WALL1:
            case WALL2:
            case WALL3:
            case WALL4:
                this.isWall = true;
                this.isHole = false;
                break;

            case HOLE0:
            case HOLE1:
            case HOLE2:
            case HOLE3:
            case HOLE4:
                this.isHole = true;
                this.isWall = false;
                break;

    
            case EMPTY:
            default:
                this.isWall = false;
                this.isHole = false;
                break;
        }
    }

    /**
     * Initializes the hitbox for wall tiles. The hitbox consists of the four edges
     * of the tile, represented as line segments.
     */

    public void initialiseHitbox() {
        PVector topLeft = new PVector(x * App.CELLSIZE - 2, y * App.CELLSIZE + App.TOPBAR - 2);
        PVector topRight = new PVector((x + 1) * App.CELLSIZE + 2, y * App.CELLSIZE + App.TOPBAR - 2);
        PVector bottomRight = new PVector((x + 1) * App.CELLSIZE + 2, (y + 1) * App.CELLSIZE + App.TOPBAR + 2);
        PVector bottomLeft = new PVector(x * App.CELLSIZE - 2, (y + 1) * App.CELLSIZE + App.TOPBAR + 2);

        hitbox = new LineSegment[4];
        hitbox[0] = new LineSegment(topLeft.x, topLeft.y, topRight.x, topRight.y);   // Top edge
        hitbox[1] = new LineSegment(bottomLeft.x, bottomLeft.y, bottomRight.x, bottomRight.y); // Bottom edge
        hitbox[2] = new LineSegment(topLeft.x, topLeft.y, bottomLeft.x, bottomLeft.y);   // Left edge
        hitbox[3] = new LineSegment(topRight.x, topRight.y, bottomRight.x, bottomRight.y); // Right edge
    }
    

    // ----------------------------- Drawing -----------------------------------

    /**
     * Draws the tile on the game board. The rendering depends on the type of tile
     * (e.g., wall, hole, or empty) and the associated images or colors.
     * @param app  The PApplet used to render the tile.
     */
    public void draw(PApplet app) {
        int drawX = x * App.CELLSIZE;
        int drawY = y * App.CELLSIZE;

        // Draw different tile types based on the type
        switch (type) {
            case EMPTY:
            if (backgroundTile != null && !isHole && !isWall) {
                app.image(backgroundTile, drawX, y * App.CELLSIZE);
            }
            break;
            
            case WALL0:
            if (walls[0] != null && isWall) {
                setIsWall(true);
                app.image(walls[0], drawX, drawY);
            }
            break;

            case WALL1:
            if (walls[1] != null && isWall) {
                app.image(walls[1], drawX, drawY);
            }
            break;
            
            case WALL2:
            if (walls[2] != null && isWall) {
                app.image(walls[2], drawX, drawY);
            }
            break;
            
            case WALL3:
            if (walls[3] != null && isWall) {
                app.image(walls[3], drawX, drawY);
            }
            break;
            
            case WALL4:
            if (walls[4] != null && isWall) {
                app.image(walls[4], drawX, drawY);
            }
            break;
            
            case HOLE0:
            case HOLE1:
            case HOLE2:
            case HOLE3:
            case HOLE4:
                if (debugColour != 0 && isHole) {
                    app.fill(debugColour);
                    app.rect(drawX, drawY, App.CELLSIZE, App.CELLSIZE);
                }
                break;
            
            default:
            if (backgroundTile != null) {
                app.image(backgroundTile, drawX, drawY);
            }
            break;
        }
    }
}
