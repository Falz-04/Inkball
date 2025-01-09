package inkball;

import processing.core.PApplet;
import processing.core.PImage;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * The HUD class is responsible for drawing the Heads-Up Display (HUD) of the game.
 * This includes the top bar displaying the score and elapsed time, as well as the ball queue.
 */
public class HUD {
    private PApplet app;

    private int ballQueueWidth = App.CELLSIZE * 5;
    private int ballQueueHeight = App.CELLSIZE;

    Level level;

    private List<String> ballsToSpawn;
    private PImage greyBallImage, orangeBallImage, blueBallImage, greenBallImage, yellowBallImage;

    // -------------------------- Constructor ----------------------------------
    /**
     * Constructor for the HUD class.
     * @param app   The PApplet object
     * @param level The current level
     */
    public HUD(PApplet app, Level level) {
        this.app = app;
        this.level = level; 
        this.ballsToSpawn = level.getBalls();

        try {
            this.greyBallImage = app.loadImage(URLDecoder.decode(this.getClass().getResource("ball0.png").getPath(), StandardCharsets.UTF_8.name()));
            this.orangeBallImage = app.loadImage(URLDecoder.decode(this.getClass().getResource("ball1.png").getPath(), StandardCharsets.UTF_8.name()));
            this.blueBallImage = app.loadImage(URLDecoder.decode(this.getClass().getResource("ball2.png").getPath(), StandardCharsets.UTF_8.name()));
            this.greenBallImage = app.loadImage(URLDecoder.decode(this.getClass().getResource("ball3.png").getPath(), StandardCharsets.UTF_8.name()));
            this.yellowBallImage = app.loadImage(URLDecoder.decode(this.getClass().getResource("ball4.png").getPath(), StandardCharsets.UTF_8.name()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --------------------- Getters and Setters -------------------------------
    // --- Ball Image ---
    /**
     * Get the image of the ball with the specified colour.
     * @param colour    The colour of the ball
     * @return          The image of the ball
     */
     public PImage getBallImage(String colour) {
        switch (colour) {
            case "ball0":
            case "grey":
                return greyBallImage;
            case "ball1":
            case "orange":
                return orangeBallImage;
            case "ball2":
            case "blue":
                return blueBallImage;
            case "ball3":
            case "green":
                return greenBallImage;
            case "ball4":
            case "yellow":
                return yellowBallImage;
            default:
                System.err.println("Error: Invalid ball colour: " + colour);
                return null;
        }
    }
    

    // Add this to the HUD class
    public List<String> getBallsToSpawn() {
        return ballsToSpawn;
    }

    // --------------------------- Methods ------------------------------------
    /**
     * Update the list of balls to spawn.
     * @param ballsToSpawn The list strings of ball colours to spawn
     */
    public void updateBallsToSpawn(List<String> ballsToSpawn) {
        this.ballsToSpawn = ballsToSpawn;
    }
    
    
    // ----------------------------- Drawing -----------------------------------
    /**
     * Draw the top bar of the game, which displays the score, elapsed time, time to spawn, and ball queue.
     * @param score         The current score
     * @param elapsedTime   The elapsed time
     * @param timeToSpawn   The time to spawn the next ball
     * @param currentState  The current game state
     */
    public void drawTopBar(int score, String elapsedTime, float timeToSpawn, GameStateManager.GameState currentState) {

        app.fill(200);
        app.strokeWeight(0);
        app.rect(0, 0, app.width, App.TOPBAR);

        app.fill(0);
        app.textSize(20);
        app.textFont(App.FONT);
        app.text("Score: " + score, 450, App.TOPBAR - 40);
        app.text("Time:  " + elapsedTime, 450, App.TOPBAR - 20);

        if (currentState == GameStateManager.GameState.TIMESUP) {
            app.text("", 20, App.TOPBAR - 40);
        } else {
            app.text(String.format("%.1f", timeToSpawn) + "s", 185, App.TOPBAR - 29);
        }

        drawBallQueue();
    }

    // -- Ball Queue --
    private void drawBallQueue() {
        app.fill(0);
        app.rect(20, App.TOPBAR / 2 - 20, ballQueueWidth, ballQueueHeight);
        
        // Starting x position inside the rectangle to draw balls
        int xPos = 25;
        int yPos = App.TOPBAR / 2 - 16; 
    
        // Loop through each ball colour string and display the corresponding ball image
        for (int i = 0; i < Math.min(ballsToSpawn.size(), 5); i++) {  
            String ballColour = ballsToSpawn.get(i);
            PImage ballImage = getBallImage(ballColour);
    
            if (ballImage != null) {
                app.image(ballImage, xPos, yPos, App.CELLSIZE - 10, App.CELLSIZE - 10); 
            }
            
            // Move the x position to the right for the next ball
            xPos += App.CELLSIZE + 1; 
        }
    }
    
   
}


