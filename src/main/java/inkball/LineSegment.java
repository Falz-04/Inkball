
package inkball;

import processing.core.PApplet;

/**
 * This class represents a line segment in the game. Line segments are used to
 * draw player-drawn lines, create hitboxes for wall tiles, and check for
 * collisions with the ball. Each segment has a starting and ending point 
 * defined by its coordinates.
 */
public class LineSegment {
    float startX, startY, endX, endY;

    // -------------------------- Constructor ----------------------------------
    /**
     * Constructor for the LineSegment class, constructs a LineSegment object
     * @param startX    Starting x-coordinate of the line segment
     * @param startY    Starting y-coordinate of the line segment
     * @param endX      Ending x-coordinate of the line segment
     * @param endY      Ending y-coordinate of the line segment
     */
    public LineSegment(float startX, float startY, float endX, float endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    // --------------------- Getters and Setters -------------------------------
    /**
     * Get the starting x-coordinate of the line segment
     * @return The starting x-coordinate of the line segment
     */
    public float getStartX() {
        return startX;
    }

    /**
     * Get the starting y-coordinate of the line segment
     * @return The starting y-coordinate of the line segment
     */
    public float getStartY() {
        return startY;
    }

    /**
     * Get the ending x-coordinate of the line segment
     * @return  The ending x-coordinate of the line segment
     */
    public float getEndX() {
        return endX;
    }

    /**
     * Get the ending y-coordinate of the line segment
     * @return The ending y-coordinate of the line segment
     */
    public float getEndY() {
        return endY;
    }
    
    // ----------------------------- Drawing -----------------------------------
    /**
     * Draw the line segment on the game window. The line is drawn with a stroke
     * color (black) and a stroke weight (10 pixels).
     * @param app  The PApplet object used to draw the line segment.
     */

    public void draw(PApplet app) {
        app.stroke(0); // Set line colour (white in this case)
        app.strokeWeight(10); // Set line thickness
        app.line(startX, startY, endX, endY);
    }
}


