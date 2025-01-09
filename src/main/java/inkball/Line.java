package inkball;

import java.util.List;
import processing.core.PApplet;
import java.util.ArrayList;

/**
 * The Line class represents a user-drawn line in the game. A line is composed of multiple
 * line segments and is drawn on the screen using these segments. Lines can interact with
 * balls in the game, and can be removed upon collision with a ball.
 */
public class Line {
    private List<LineSegment> segments; // List to store all segments of the line

    // -------------------------- Constructor ----------------------------------
    /**
     * Constructor to create a new empty line. The line consists of multiple segments that
     * can be added using the addSegment method.
     */
    public Line() {
        segments = new ArrayList<>();
    }

    // ----------------------------- Methods -----------------------------------
    /**
     * Adds a new segment to the line. Segments represent individual parts of the line.
     * @param segment The line segment to be added to the line.
     */
    public void addSegment(LineSegment segment) {
        segments.add(segment);
    }

    /**
     * Retrieves the list of segments that make up the line.
     * @return The list of line segments.
     */
    public List<LineSegment> getSegments() {
        return segments;
    }

    /**
     * Draws the entire line on the screen, by iterating through all the segments and rendering
     * them on the game window.
     * @param app The PApplet object used to draw the line.
     */
    public void draw(PApplet app) {
        for (LineSegment segment : segments) {
            app.stroke(0); // Black colour for the line
            app.strokeWeight(10); // Set line thickness
            app.line(segment.getStartX(), segment.getStartY(), segment.getEndX(), segment.getEndY());
        }
    }

    /**
     * Check if the line is colliding with the ball.
     * @param ball  The ball to check for collision with the line.
     * @return      True if the line is colliding with the ball, false otherwise.
     */
    public boolean isCollidingWithBall(Ball ball) {
        for (LineSegment segment : segments) {
            if (ball.isCollidingWithLine(segment)) {
                return true; // If any segment is colliding, return true
            }
        }
        return false;
    }

    /**
     * Remove the line from the list of lines if it is colliding with the ball.
     * @param ball  The ball to check for collision with the line.
     * @param lines The list of lines from which the line should be removed.
     */
    public void removeLineIfCollidingWithBall(Ball ball, List<Line> lines) {
        if (isCollidingWithBall(ball)) {
            lines.remove(this); // Remove this line from the list of lines
        }
    }
}
