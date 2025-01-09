package inkball;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * Hole class represents the holes on the board where the balls are captured.
 * The hole can capture a ball if the ball is within the hole's radius.
 */
public class Hole extends GameObject {
    private PImage holeImage; 
    private PVector center;
    private String holeColour;

    private Board board;
    private PApplet app;

    private static final float ATTRACTION_FORCE = 0.005f;

    // -------------------------- Constructor ----------------------------------
    /**
     * Constructor for the Hole class
     * @param x             x-coordinate of the hole
     * @param y             y-coordinate of the hole 
     * @param app           PApplet object
     * @param holeColour    Colour of the hole
     * @param board         Board object
     */
    public Hole(int x, int y, PApplet app, String holeColour, Board board) {
        super(x, y);
        this.app = app;
        this.board = board;
        this.holeColour = holeColour;

        // Calculate the center of the hole
        this.center = new PVector((x + 1) * App.CELLSIZE, (y + 1) * App.CELLSIZE + App.TOPBAR);

        try {
            this.holeImage = app.loadImage(URLDecoder.decode(this.getClass().getResource(holeColour + ".png").getPath(), StandardCharsets.UTF_8.name()));
            if (holeImage == null) {
                System.out.println("Hole Image not found: " + holeColour);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --------------------- Getters and Setters -------------------------------
    // --- Center ---
    /**
     * Get the center of the hole
     * @return PVector object representing the center of the hole
     */
    public PVector getCenter() {
        return center;
    }

    // --- Colour ---
    /**
     * Get the colour of the hole
     * @return String representing the colour of the hole
     */
    public String getColour() {
        if (holeColour.equals("hole0")) {
            return "grey";
        } else if (holeColour.equals("hole1")) {
            return "orange";
        } else if (holeColour.equals("hole2")) {
            return "blue";
        } else if (holeColour.equals("hole3")) {
            return "green";
        } else if (holeColour.equals("hole4")) {
            return "yellow";
        } else {
            return "Ball Colour Not Found";
        }
    }

    // ----------------------------- Methods -----------------------------------
   
    /**
     * Apply the attraction the ball towards the hole
     * @param ball Ball object to be attracted
     */
    public void attractBall(Ball ball) {
        PVector ballPosition = new PVector(ball.getPosX(), ball.getPosY());
        PVector forceDirection = PVector.sub(center, ballPosition);
        float distanceToCenter = forceDirection.mag();
    
        if (distanceToCenter <= 32) {
            forceDirection.normalize(); 
            forceDirection.mult(ATTRACTION_FORCE * distanceToCenter);
            ball.applyForce(forceDirection);
    
            float newScale = PApplet.map(distanceToCenter, 0, App.CELLSIZE, 0, 1.0f);
            ball.setScale(PApplet.constrain(newScale, 0.0f, 1.0f));
    
            if (distanceToCenter <= ball.getRadius()) {
                captureBall(ball);
            }
        }
    }
    

    // --- Ball Capture ---
    /**
     * Capture the ball if it is within the hole's radius
     * @param ball Ball object to be captured
     */
    public void captureBall(Ball ball) {
        ball.setActive(false);

        if (isMatchingColour(ball)) {
            board.handleSuccessfulCapture(ball);
        } else {
            board.handleFailedCapture(ball);
        }
    }

    // --- Matching Colour ---
    /**
     * Check if the ball's colour matches the hole's colour
     * @param ball Ball object to be checked
     * @return boolean representing if the ball's colour matches the hole's colour
     */
    public boolean isMatchingColour(Ball ball) {
      String ballColour = ball.getColour();
      String holeColour = this.getColour();

      if (ballColour.equals("grey") || holeColour.equals("grey")) {
          return true;
      }
      return ballColour.equals(holeColour);
    }


    // ----------------------------- Drawing -----------------------------------
    /**
     * Draw the hole on the board
     */
    public void draw() {
      if (holeImage != null) {
          // Draw the hole image over 4 tiles (2x2 area)
          int imgW = holeImage.width / 2; 
          int imgH = holeImage.height / 2; 

          // Top-left
          app.image(holeImage, x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR, App.CELLSIZE, App.CELLSIZE, 0, 0, imgW, imgH);
          // Top-right
          app.image(holeImage, (x + 1) * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR, App.CELLSIZE, App.CELLSIZE, imgW, 0, imgW * 2, imgH);
          // Bottom-left
          app.image(holeImage, x * App.CELLSIZE, (y + 1) * App.CELLSIZE + App.TOPBAR, App.CELLSIZE, App.CELLSIZE, 0, imgH, imgW, imgH * 2);
          // Bottom-right
          app.image(holeImage, (x + 1) * App.CELLSIZE, (y + 1) * App.CELLSIZE + App.TOPBAR, App.CELLSIZE, App.CELLSIZE, imgW, imgH, imgW * 2, imgH * 2);
      } else {
          System.out.println("Error: holeImage is null.");
      }
  }
}
