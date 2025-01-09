package inkball;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * The ball class inherits from the abstract GameObject class. 
 * Ball class represents the ball object in the game. The ball object interacts
 * with the wall and the hole objects in the game, as well as spawned from the 
 * spawner object. The ball object is responsible for moving, checking for 
 * collisions, reflecting off walls, and changing colour when colliding with
 * coloured walls.
 */
public class Ball extends GameObject {
    private PImage ballImage;
    private boolean active;
    private String ballColour;
    private float scale;
    private PVector position;
    private PVector velocity;

    private PApplet app;

    private int lastCollisionFrame;  // Store the frame number of the last collision
    private static final int COLLISION_COOLDOWN = 5; // Cooldown period in frames



    // -------------------------- Constructor ----------------------------------
    /**
     * Constructor for the Ball object. The ball object is created with a given
     * position, colour, and board object. The ball object is initialised with a
     * random velocity and is set to be active.
     * @param x             The x-coordinate of the ball
     * @param y            The y-coordinate of the ball
     * @param app           The PApplet object
     * @param ballColour    The colour of the ball
     */
    public Ball(int x, int y, PApplet app, String ballColour) {
        super(x, y);
    
        if (ballColour == null) {
            throw new IllegalArgumentException("Error: ballColour is null!");
        }
    
        this.ballColour = ballColour;
    
        if (app != null) {
            try {
                // Check if the resource exists for the ball colour
                if (this.getClass().getResource(ballColour + ".png") == null) {
                    throw new IllegalArgumentException("Error: Ball image not found for colour " + ballColour);
                }
                this.ballImage = app.loadImage(URLDecoder.decode(this.getClass().getResource(ballColour + ".png").getPath(), StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    
        int randvx = (Math.random() < 0.5) ? -1 : 1;
        int randvy = (Math.random() < 0.5) ? -1 : 1;
        this.position = new PVector(x * App.CELLSIZE + (App.CELLSIZE / 2), y * App.CELLSIZE + App.TOPBAR + (App.CELLSIZE / 2));
        this.velocity = new PVector(randvx, randvy);
        this.active = true;
        this.scale = 1.0f;
    
        this.lastCollisionFrame = -1;
        this.app = app;
    }
    

    // --------------------- Getters and Setters -------------------------------

    // --- Position ---
    /**
     * Get the x-coordinate of the ball
     * @return The x-coordinate of the ball
     */
    public float getPosX() {
        return position.x;
    }

    /**
     * Get the y-coordinate of the ball
     * @return The y-coordinate of the ball
     */
    public float getPosY() {
        return position.y;
    }

    /**
     * Gets the future position of the ball
     * @return futurePosition: The future position of the ball
     */
    public PVector getFuturePosition() {
        PVector futurePosition = PVector.add(position, velocity);
        return futurePosition;
    }
    
    

    // --- Velocity ---
    /**
     * Get the x-component of the velocity of the ball
     * @return The x-component of the velocity of the ball
     */
    public float getVelocityX() {
        return velocity.x;
    }

    /**
     * Get the y-component of the velocity of the ball
     * @return The y-component of the velocity of the ball
     */
    public float getVelocityY() {
        return velocity.y;
    }

    /**
     * Set the velocity of the ball
     * @param vx The x-component of the velocity
     * @param vy The y-component of the velocity
     */
    public void setVelocity(float vx, float vy) {
        this.velocity.x = vx;
        this.velocity.y = vy;
    }

    // --- Active ---
    /**
     * Check if the ball is active
     * @return True if the ball is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set the ball to be active or inactive
     * @param active True to set the ball as active, false to set it as inactive
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    // --- Radius ---
    /**
     * Get the radius of the ball
     * @return The radius of the ball
     */
    public float getRadius() {
        return App.CELLSIZE / 2;
    }

    // --- Colour ---
    /**
     * Get the colour of the ball
     * @return The colour of the ball
     */
    public String getColour() {
        if (ballColour.equals("ball0")) {
            return "grey";
        } else if (ballColour.equals("ball1")) {
            return "orange";
        } else if (ballColour.equals("ball2")) {
            return "blue";
        } else if (ballColour.equals("ball3")) {
            return "green";
        } else if (ballColour.equals("ball4")) {
            return "yellow";
        } else {
            return "Ball Colour not found";
        }
    }

    // --- Scale ---
    /**
     * Set the scale of the ball
     */
    public void setScale(float scale) {
        this.scale = scale;  
    }

    /**
     * Get the scale of the ball
     * @return The scale of the ball
     */
    public float getScale() {
        return scale;
    }

    // --- Apply Force ---
    /**
     * Apply a force to the ball
     * @param force The force to be applied to the ball
     */
    public void applyForce(PVector force) {
        velocity.add(force);  
    }

    // -------------------------- Methods --------------------------------------

    // --- Move ---
    /**
     * Move the ball by updating its position based on its velocity. The method
     * also checks for border collisions and reflects the ball if it collides with
     * the wall.
     */
    public void move() {
        position.add(velocity);
        checkBorderCollision();
    }

    // --- Check Border Collision ---
    /**
     * Check if the ball collides with the borders of the game window. If the ball
     * collides with the left or right border, the x-component of the velocity is
     * reversed. If the ball collides with the top or bottom border, the y-component
     * of the velocity is reversed.
     */
    public void checkBorderCollision() {
        if (position.x < 0 || position.x > App.WIDTH - (App.CELLSIZE)) {
            velocity.x *= -1;
        }
        if (position.y < App.TOPBAR || position.y > App.HEIGHT - App.CELLSIZE) {
            velocity.y *= -1;
        }
    }

    // --- Check Collision with Wall ---
    /**
     * Check if the ball collides with the wall hitbox. The method checks each line
     * segment of the wall hitbox and reflects the ball upon collision.
     * @param hitbox The wall hitbox
     * @param app    The PApplet object
     * @return True if the ball collides with the wall, false otherwise
     */
    public boolean checkCollisionWithWall(LineSegment[] hitbox, PApplet app) {
        // Frame buffer to prevent multiple collisions in the same frame
        if (lastCollisionFrame != -1 && (app.frameCount - lastCollisionFrame) < COLLISION_COOLDOWN) {
            return false;
        }

        // Get the future position and radius of the ball
        PVector futurePosition = getFuturePosition();
        float radius = getRadius();

        // Check each line segment of the wall hitbox
        for (LineSegment edge : hitbox) {
            if (isCollidingWithLine(edge, futurePosition, radius)) {
                updateTrajectory(edge);  // Reflect ball upon collision
                lastCollisionFrame = app.frameCount;
                return true;
            }
        }
        return false;
    }
    
    // --- Collision with Line ---
    /**
     * Check if the ball is colliding with a line segment. The method calculates the
     * distance between the ball's predicted position in the next frame and the line
     * segment. If the sum of the distances from the ball to the line segment's end
     * points is less than the line segment's length plus the ball's radius, the ball
     * is colliding with the line segment.
     * @param line The line segment to check for collision
     * @return     True if the ball is colliding with the line segment, false otherwise
     */
    public boolean isCollidingWithLine(LineSegment line) {
        // Calculate the future position of the ball
        PVector ballPosNext = PVector.add(position, velocity); 

        // Get the start and end points of the line segment
        PVector P1 = new PVector(line.startX, line.startY);
        PVector P2 = new PVector(line.endX, line.endY);
        
        // Calculate the distances from the ball to the line segment's end points
        float distanceP1ToBall = PVector.dist(ballPosNext, P1);
        float distanceP2ToBall = PVector.dist(ballPosNext, P2);
        float lineSegmentLength = PVector.dist(P1, P2);
    
        // Check if the ball is colliding with the line segment
        if (distanceP1ToBall + distanceP2ToBall < lineSegmentLength + this.getRadius()) {
            return true;
        }
        return false;
    }

    // --- Reflect Ball From Line Segment---
    /**
     * Reflect the ball from the line segment. The method calculates the normal vector
     * of the line segment and reflects the ball's velocity based on the normal vector.
     * @param hitbox The line segment to reflect the ball from
     */
    public void reflectBallLine(PVector[] hitbox) {
        // Extract the corners of the hitbox
        PVector topLeft = hitbox[0];
        PVector bottomRight = hitbox[2];
    
        // Reflect on the x-axis if the ball hits a vertical wall
        if (position.x <= topLeft.x || position.x >= bottomRight.x) {
            velocity.x *= -1;
        }
    
        // Reflect on the y-axis if the ball hits a horizontal wall
        if (position.y <= topLeft.y || position.y >= bottomRight.y) {
            velocity.y *= -1;
        }
    }

    // --- Reflect Ball From Wall ---
    /**
     * Reflect the ball from the wall. The method calculates the normal vector of the
     * wall edge and reflects the ball's velocity based on the normal vector.
     * @param hitbox The wall hitbox
     */
    public void reflectBallWall(PVector[] hitbox) {
        // Extract the corners of the hitbox
        PVector topLeft = hitbox[0];
        PVector topRight = hitbox[1];
        PVector bottomRight = hitbox[2];
        PVector bottomLeft = hitbox[3];
    
        // Create line segments for each wall edge
        LineSegment topEdge = new LineSegment(topLeft.x, topLeft.y, topRight.x, topRight.y);
        LineSegment bottomEdge = new LineSegment(bottomLeft.x, bottomLeft.y, bottomRight.x, bottomRight.y);
        LineSegment leftEdge = new LineSegment(topLeft.x, topLeft.y, bottomLeft.x, bottomLeft.y);
        LineSegment rightEdge = new LineSegment(topRight.x, topRight.y, bottomRight.x, bottomRight.y);
    
        // Check which edge the ball is colliding with and update the trajectory
        if (isCollidingWithLine(topEdge)) {
            updateTrajectory(topEdge);  
        } else if (isCollidingWithLine(bottomEdge)) {
            updateTrajectory(bottomEdge);  
        } else if (isCollidingWithLine(leftEdge)) {
            updateTrajectory(leftEdge); 
        } else if (isCollidingWithLine(rightEdge)) {
            updateTrajectory(rightEdge);  
        }
    }

    
    
    // --- Change Ball Colour ---
    /**
     * Change the colour of the ball based on the wall tile it collides with. The
     * method changes the ball's colour and image based on the wall tile type.
     * @param wallTile  The wall tile the ball collides with
     */
    public void changeColour(Tile wallTile) {
        // Check the wall tile type and match the ball colour
        switch (wallTile.getType()) {
            case WALL1: this.ballColour = "ball1"; break;
            case WALL2: this.ballColour = "ball2"; break;
            case WALL3: this.ballColour = "ball3"; break;
            case WALL4: this.ballColour = "ball4"; break;
            default: break;
        }

        // Load the new ball image based on the colour
        try {
            this.ballImage = app.loadImage(URLDecoder.decode(this.getClass().getResource(ballColour + ".png").getPath(), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    // --- Check Collision with Line ---
    /**
     * Check if the ball is colliding with a line segment. The method calculates the
     * @param line              The line segment to check for collision
     * @param futurePosition    The future position of the ball
     * @param radius            The radius of the ball
     * @return                  True if the ball is colliding with the line segment, false otherwise
     */
    public boolean isCollidingWithLine(LineSegment line, PVector futurePosition, float radius) {
        // Get the start and end points of the line segment
        PVector P1 = new PVector(line.startX, line.startY);
        PVector P2 = new PVector(line.endX, line.endY);
    
        // Calculate the distances from the ball to the line segment's end points
        float distanceP1ToBall = PVector.dist(futurePosition, P1);
        float distanceP2ToBall = PVector.dist(futurePosition, P2);
        float lineSegmentLength = PVector.dist(P1, P2);
    
        // Check if the future position of the ball is close enough to the line segment
        return distanceP1ToBall + distanceP2ToBall <= lineSegmentLength + this.getRadius();
    }
    

    // --- Calculate Normal Vector ---
    /**
     * Calculate the normal vector of the line segment. The method calculates two
     * normal vectors and chooses the normal vector that is closer to the ball.
     * @param line  The line segment to calculate the normal vector
     * @return      The normal vector of the line segment
     */
    public PVector calculateNormalVector(LineSegment line) {
        PVector P1 = new PVector(line.startX, line.startY);
        PVector P2 = new PVector(line.endX, line.endY);
    
        float dx = P2.x - P1.x;
        float dy = P2.y - P1.y;
    
        // Calculate two normals
        PVector N1 = new PVector(-dy, dx);
        PVector N2 = new PVector(dy, -dx);
    
        // Normalize the vectors (so their magnitude is 1)
        N1.normalize();
        N2.normalize();
    
        // Check which normal is closer to the ball
        PVector midPoint = PVector.lerp(P1, P2, 0.5f); // Midpoint of the line segment
        PVector ballPos = new PVector(this.getPosX(), this.getPosY());
    
        // Choose the normal that is closer to the ball
        if (PVector.dist(PVector.add(midPoint, N1), ballPos) < PVector.dist(PVector.add(midPoint, N2), ballPos)) {
            return N1;
        } else {
            return N2;
        }
    }

    // --- Update Trajectory ---
    /**
     * Update the ball's trajectory upon collision with the line segment. The method
     * @param line  The line segment to reflect the ball from
     */
    public void updateTrajectory(LineSegment line) {
        PVector normal = calculateNormalVector(line); // Get the closest normal vector
    
        // Calculate the reflection vector
        PVector reflectedVelocity = PVector.sub(velocity, PVector.mult(normal, 2 * PVector.dot(velocity, normal)));
    
        // Update the ball's velocity
        this.setVelocity(reflectedVelocity.x, reflectedVelocity.y);
    }
    
        
    // -------------------------- Drawing --------------------------------------
    /**
     * Draw the ball on the game window. The method renders the ball image at the
     */
    public void draw(PApplet app) {
        if (active) {
            // Adjust the ball's rendering by shifting the Y position by half a cell size upwards
            float adjustedY = position.y - (App.CELLSIZE / 2);
            float adjustedX = position.x - (App.CELLSIZE / 2);
            
            app.image(ballImage, adjustedX, adjustedY, App.CELLSIZE * scale, App.CELLSIZE * scale);
        }
    }
    
}
