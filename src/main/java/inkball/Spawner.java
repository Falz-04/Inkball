package inkball;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import java.io.UnsupportedEncodingException;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * The spawner class is a subclass of the GameObject class, it represents the
 * spawner object in the game, which spawns ball objects at regular intervals based on
 * the ballsToSpawn list
 * 
 */
public class Spawner extends GameObject {
    private PImage spawnerImage;

    private int spawnInterval;
    private int spawnFrameCounter;
    private boolean active;

    private List<String> ballsToSpawn;

    private PApplet app;

    // -------------------------- Constructor ----------------------------------
    /**
     * Constructor for the Spawner class, constructs a spawner object
     * @param x                 x-coordinate of the spawner 
     * @param y                 y-coordinate of the spawner
     * @param app               PApplet object
     * @param spawnInterval     interval at which the spawner spawns balls
     * @param ballsToSpawn      list of balls to spawn
     */
    public Spawner(int x, int y, PApplet app, int spawnInterval, List<String> ballsToSpawn, Board board) {
        super(x, y);

        try {
            this.spawnerImage = app.loadImage(URLDecoder.decode(this.getClass().getResource("entrypoint.png").getPath(), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        this.spawnInterval = spawnInterval * App.FPS;
        this.spawnFrameCounter = 0;
        this.active = false;
        this.ballsToSpawn = ballsToSpawn;
        this.app = app;
    }

    // --------------------- Getters and Setters -------------------------------
    /**
     * Method to check if the spawner is active
     * @return boolean value indicating if the spawner is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Method to set the spawner to active or inactive
     * @param active boolean value to set the spawner to active or inactive
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Method to get the spawn interval of the spawner
     * @return integer value of the spawn interval
     */
    public int getSpawnInterval() {
        return spawnInterval;
    }

    /**
     * Method to get the time to spawn the next ball
     * @return float value of the time to spawn the next ball
     */
    public float getTimeToSpawn() {
        float timeUntilNextSpawn = (spawnInterval - spawnFrameCounter) / (float) App.FPS;
        if (timeUntilNextSpawn < 0) {
            timeUntilNextSpawn = spawnInterval / (float) App.FPS;
        }
        return Math.max(0, timeUntilNextSpawn);
    }
    
    /**
     * Method to get the list of balls to spawn
     * @return list of strings of balls to spawn
     */
    public List<String> getBallsToSpawn() {
        return ballsToSpawn;
    }

    /**
     * Method to set frame counter of the spawner
     * @param spawnFrameCounter integer value of the frame counter
     */
    public void setSpawnFrameCounter(int spawnFrameCounter) {
        this.spawnFrameCounter = spawnFrameCounter;
    }
    // ----------------------------- Methods -----------------------------------
    /**
     * Method to check if it is time to spawn a ball
     * @return boolean value indicating if it is time to spawn a ball
     */
    public boolean isTimeToSpawn() {
        boolean isTimeToSpawnBool = (spawnFrameCounter) >= spawnInterval;
        if (isTimeToSpawnBool) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method to spawn a ball
     * @param balls list of balls
     * @param app PApplet object
     */
    public void spawnBall(List<Ball> balls, PApplet app) {
        // Check if ballsToSpawn list is empty
        if (ballsToSpawn.isEmpty()) {
            System.err.println("ballsToSpawn is null, Cannot spawn ball");
            spawnFrameCounter = 0;
            return;
        }
        
        // Check if the spawner is active and it is time to spawn a ball
        if (active && isTimeToSpawn()) {
            if (ballsToSpawn.isEmpty()) {
                this.active = false;
                return;
            }
            System.out.println("Spawning ball");
            String ballToSpawn = ballsToSpawn.remove(0);
            if (ballToSpawn.equals("grey")) {
                ballToSpawn = "ball0";
            } else if (ballToSpawn.equals("orange")) {
                ballToSpawn = "ball1";
            } else if (ballToSpawn.equals("blue")) {
                ballToSpawn = "ball2";
            } else if (ballToSpawn.equals("green")) {
                ballToSpawn = "ball3";
            } else if (ballToSpawn.equals("yellow")) {
                ballToSpawn = "ball4";
            }
            balls.add(new Ball(x, y, app, ballToSpawn)); 
            spawnFrameCounter = 0;
        }
    }

    /**
     * Method to add a ball to the spawn queue
     * @param ballColour string of the ball colour
     */
    public void addBallToSpawnQueue(String ballColour) {
        ballsToSpawn.add(ballColour);
    }

    // -------------------------- Draw Method ----------------------------------
    /**
     * Method to draw the spawner
     */
    public void draw() {
        app.image(spawnerImage, x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);

        spawnFrameCounter++;

        if (active) {
            spawnFrameCounter = 0;
        }
    }
}
