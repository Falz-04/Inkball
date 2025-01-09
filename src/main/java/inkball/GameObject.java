/* GAMEOBJECT.JAVA
 * 
 * This file contains the GameObject class, which is the superclass for all 
 * objects in the game.
 * 
 * 
 */



package inkball;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * Abstract class for all objects that are spawned on the board such as the ball,
 * tiles (walls), holes and spawners.
 */
public abstract class GameObject {
    protected int x, y;
    protected PImage sprite;

    /**
     * Constructor for GameObject
     * @param x x-coordinate of the object
     * @param y y-coordinate of the object
     */
    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    
    // --------------------- Getters and Setters -------------------------------
    // --- Position ---
    /**
     * Get the x-coordinate of the object
     * @return x-coordinate of the object
     */
    public int getX() {
        return x;
    } 
    
    /**
     * Get the y-coordinate of the object
     * @return y-coordinate of the object
     */
    public int getY() {
        return y;
    }
    
    /**
     * Set the x-coordinate of the object
     * @param x x-coordinate of the object
     */
    public void setX(int x) {
        this.x = x;
    }
    
    /**
     * Set the y-coordinate of the object
     * @param y
     */
    public void setY(int y) {
        this.y = y;
    }
    
    // --- Sprite ---
    /**
     * Set the sprite of the object
     * @param sprite object representing the sprite of the object
     */
    public void setSprite(PImage sprite) {
        this.sprite = sprite;
    }
    
    // ----------------------------- Drawing -----------------------------------
    /**
     * Draw the object on the screen
     * @param app
     */
    public void draw(PApplet app) {
        app.image(sprite, x, y);
    }
}
