package inkball;

import processing.core.PApplet;
import processing.core.PVector;
import processing.core.PImage;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Board class represents the game board and contains the tiles, balls, 
 * spawners, holes, and the game state manager.
 */
public class Board {
    // Tile Variables
    private Tile[][] tiles;

    // Level Variables
    private Level level;

    // Ball variables
    private List<Ball> balls; 

    // Game State Manager
    private GameStateManager gameStateManager;

    // Spawner variables
    private List<Spawner> spawners;
    private Spawner activeSpawner;
    private int lastSpawnFrame = 0;
    private float timeToSpawn = 0;

    // Hole variables
    private List<Hole> holes;

    // Board Variables
    private int width;
    private int height;

    // Score Variables
    private int score = 0;

    // Yellow Tile Animation Variables
    private int yellowTile1X, yellowTile1Y;
    private int yellowTile2X, yellowTile2Y;
    private List<PVector> borderTilePositions = new ArrayList<>();
    private float yellowTileTimer = 0;
    private PImage yellowTileImage;



    // -------------------------- Constructor ----------------------------------
    /**
     * Constructor for the Board class.
     * @param app               The PApplet object
     * @param width             The width of the board
     * @param height            The height of the board
     * @param gameStateManager  The GameStateManager object
     */
    public Board(PApplet app, int width, int height, GameStateManager gameStateManager) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
        this.balls = new ArrayList<>(); 
        this.spawners = new ArrayList<>();
        this.holes = new ArrayList<>();
        
        this.gameStateManager = gameStateManager;

        try {
            yellowTileImage = app.loadImage(URLDecoder.decode(this.getClass().getResource("wall4.png").getPath(), StandardCharsets.UTF_8.name()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initialiseBorderTiles();
        initialiseBoard();

        yellowTile1X = 0;
        yellowTile1Y = 0;

        yellowTile2X = width - 1;
        yellowTile2Y = height - 1;
    }

    // --------------------- Getters and Setters -------------------------------
    /**
     * Get the width of the board.
     * @param x The width of the board
     * @param y The height of the board
     * @return  The width of the board
     */
    public Tile getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[x][y];
        }
        return null;
    }

    /**
     * Get list of balls on the board.
     * @return  List of ball objects
     */
    public List<Ball> getBalls() {
        return balls;
    }

    /**
     * Get the List of spawners on the board
     * @return  The List of Spawner objects
     */
    public List<Spawner> getSpawners() {
        return spawners;
    }

    /**
     * Get the List of holes on the board
     * @return  The List of Hole objects
     */
    public List<Hole> getHoles() {
        return holes;
    }

    /**
     * Set the level for the board to load
     * @param level The level object
     */
    public void setLevel(Level level) {
        this.level = level;
    }   

    /**
     * Get the score of the game
     * @return  The score of the game
     */
    public int getScore() {
        return score;
    }

    private Tile.TileType getWallTileType(char tileChar) {
        switch (tileChar) {
            case 'X': return Tile.TileType.WALL0;
            case '1': return Tile.TileType.WALL1;
            case '2': return Tile.TileType.WALL2;
            case '3': return Tile.TileType.WALL3;
            case '4': return Tile.TileType.WALL4;
            default: return Tile.TileType.EMPTY;
        }
    }

    private void setHoleTiles(int x, int y, int holeIndex, PApplet app) {
        // Mark the tiles for the 2x2 hole
        tiles[x][y] = new Tile(x, y, Tile.TileType.valueOf("HOLE" + holeIndex), this);
        tiles[x + 1][y] = new Tile(x + 1, y, Tile.TileType.valueOf("HOLE" + holeIndex), this);
        tiles[x][y + 1] = new Tile(x, y + 1, Tile.TileType.valueOf("HOLE" + holeIndex), this);
        tiles[x + 1][y + 1] = new Tile(x + 1, y + 1, Tile.TileType.valueOf("HOLE" + holeIndex), this);
    
        // Set the hole flag to true for these tiles
        tiles[x][y].setHole(true);
        tiles[x + 1][y].setHole(true);
        tiles[x][y + 1].setHole(true);
        tiles[x + 1][y + 1].setHole(true);
    
        // Set a debugging colour for the hole (red for example)
        tiles[x][y].setDebugColour(app.color(255, 0, 0));        // Top-left tile
        tiles[x + 1][y].setDebugColour(app.color(255, 0, 0));    // Top-right tile
        tiles[x][y + 1].setDebugColour(app.color(255, 0, 0));    // Bottom-left tile
        tiles[x + 1][y + 1].setDebugColour(app.color(255, 0, 0)); // Bottom-right tile
    }
    
    /**
     * Clear all the balls from the game
     */
    public void clearBalls() {
        balls.clear();
    }

    /**
     * Clear all the spawners from the game
     */
    public void clearSpawners() {
        spawners.clear();
    }

    /**
     * Get the level that is being played
     * @return  The level object
     */
    public Level getLevel() {
        return this.level;
    }

    /**
     * Get the spawn inverval for the balls
     * @return  The time to spawn a ball
     */
    public float getTimeToSpawn() {
        return timeToSpawn;
    }
    // ----------------------------- Methods -----------------------------------
    // --- Initialise the board ---
    private void initialiseBoard() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y] == null) {
                    tiles[x][y] = new Tile(x, y, Tile.TileType.EMPTY, this);
                }
            }
        }
    }

    // --- Initialise the border tiles ---
    /**
     * Initialise the border tiles of the board and place them into a list
     */
    public void initialiseBorderTiles() {
        borderTilePositions.clear(); // Clear any previously stored positions
    
        // Collect all the tile positions in the border (top row, bottom row, left and right borders)
        for (int x = 0; x < width; x++) {
            borderTilePositions.add(new PVector(x, 0));        // Top row
            borderTilePositions.add(new PVector(x, height - 1)); // Bottom row
        }
        for (int y = 1; y < height - 1; y++) {
            borderTilePositions.add(new PVector(0, y));        // Left column
            borderTilePositions.add(new PVector(width - 1, y)); // Right column
        }
    }

    // --- Load a level from JSON ---
    /**
     * Load a level from a JSON file
     * @param app   The PApplet object
     * @param level The level object
     */
    public void loadLevel(PApplet app, Level level) {
        // Layout FIle
        clearSpawners();
        String layoutFIle = level.getLayoutFile();

        // Spawner Metrics
        int spawnInterval = level.getSpawnInterval();
        List<String> ballsToSpawn = level.getBalls();

        String[] lines = app.loadStrings(layoutFIle);
        if (lines == null) {
            System.out.println("Error: Level file not found or empty.");
            return;
        }

        initialiseBorderTiles();

        for (int y = 0; y < lines.length && y < height; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length() && x < width; x++) {
                char tileChar = line.charAt(x);

                if (tiles[x][y].isHole()) {
                    x++;
                }

                switch (tileChar) {
                    // Wall tiles
                    case 'X': case '1': case '2': case '3': case '4':
                        if (x > 0 && (line.charAt(x - 1) == 'H' || line.charAt(x - 1) == 'B')) {
                            break;
                        }
                        tiles[x][y] = new Tile(x, y, getWallTileType(tileChar), this);
                        tiles[x][y].initialiseHitbox(); 
                        break;
                    
                    // Hole tiles
                    case 'H':
                        // Initialise a hole object at this location
                        if (x + 1 < line.length() && Character.isDigit(line.charAt(x + 1))) {
                            int holeIndex = Character.getNumericValue(line.charAt(x + 1));
                            String holeColour = "hole" + holeIndex;

                            Hole hole = new Hole(x, y, app, holeColour, this);
                            holes.add(hole);

                            setHoleTiles(x, y, holeIndex, app);
                            x++;
                        }
                        break;
                    
                    // Balls
                    case 'B':
                    // Create an empty tile at this location
                    tiles[x][y] = new Tile(x, y, Tile.TileType.EMPTY, this);
                    if (x + 1 < line.length() && Character.isDigit(line.charAt(x + 1))) {
                        // Initialise a ball object at this location
                        int ballIndex = Character.getNumericValue(line.charAt(x + 1));
                        String ballColour = "ball" + ballIndex; 
                        Ball ball = new Ball(x, y, app, ballColour);
                        balls.add(ball);
                        x++;  
                    } else {
                        System.err.println("Error: Invalid ball index.");
                    }
                    break;

                    case 'S':
                        // Initialise a spawner object at this location
                        Spawner spawner = new Spawner(x, y, app, spawnInterval, ballsToSpawn, this);
                        spawners.add(spawner);
                        break;

                    default:
                        // Create an empty tile at this location
                        tiles[x][y] = new Tile(x, y, Tile.TileType.EMPTY, this);
                }
            }
        }
    }

    // -- Check Line Collision with Ball ---
    /**
     * Check if the ball is colliding with any of the lines
     * @param ball  The ball object
     * @param lines The list of LineSegment objects
     */
    public void checkLineCollisions(Ball ball, List<LineSegment> lines) {
        for (int i = 0; i < lines.size(); i++) {
            LineSegment line = lines.get(i);
            if (ball.isCollidingWithLine(line)) {
                lines.remove(i);
                break;  
            }
        }
    }


    // --- Collision Handling ---
    /**
     * Check if the ball is colliding with any of the walls
     * @param ball  The ball object
     * @param app   The PApplet object
     */
    public void checkWallCollisions(Ball ball, PApplet app) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile = tiles[x][y];
                if (tile.isWall()) {
                    LineSegment[] hitbox = tile.getHitbox();
                    if (ball.checkCollisionWithWall(hitbox, app)) {
                        ball.changeColour(tile);  
                        return;
                    }
                }
            }
        }
    }
    
    // --- Handle Ball Capture ---
    /**
     * Handle the successful capture of a ball into a hole
     * @param ball  The ball object
     */
    public void handleSuccessfulCapture(Ball ball) {
        // ERROR CHECKS
        if (level == null) {
            System.err.println("Error: Level is null.");
            return;
        }

        if (ball == null) {
            System.err.println("Error: Ball is null.");
            return;
        }
        
        // Handle the capture
        String ballColour = ball.getColour();
        
        if (level.getScoreIncreaseFromHole() == null) {
            System.err.println("Error: ScoreIncreaseFromHole map is null.");
            return;
        }
        
        if (!level.getScoreIncreaseFromHole().containsKey(ballColour)) {
            System.err.println("Error: Ball colour " + ballColour + " is not in ScoreIncreaseFromHole.");
            return;
        }
        
        System.out.println("Ball Col: " + ballColour);
        // Increment the score
        float scoreIncrease = (level.getScoreIncreaseFromHole().get(ballColour) * level.getScoreIncreaseModifier());
        gameStateManager.updateScore(gameStateManager.getScore() + (int) scoreIncrease);

  
        if (balls != null && balls.contains(ball)) {
            balls.remove(ball);
        } else {
            System.err.println("Error: Ball list is null or ball not found in list.");
        }
        
    }

    /**
     * Handle the situation where a ball goes into the wrong coloured hole
     * @param ball
     */
    public void handleFailedCapture(Ball ball) {
        String ballColour = ball.getColour();
        float scorePenalty =  (level.getScoreDecreaseFromWrongHole().get(ballColour) * level.getScoreDecreaseModifier());
        gameStateManager.updateScore(gameStateManager.getScore() - (int) scorePenalty);

        if (!spawners.isEmpty() && spawners.get(0) != null) {
            spawners.get(0).addBallToSpawnQueue(ballColour);
        } else {
            System.err.println("Error: No spawners available to spawn ball.");
        }
        balls.remove(ball);
    }

    // --- Attract Balls to Holes ---
    private void attractBallsToHoles() {
        List<Ball> ballsCopy = new ArrayList<>(balls);  // Create a copy of the balls list
        List<Ball> ballsToRemove = new ArrayList<>();  // Collect balls to remove after iteration
    
        for (Ball ball : ballsCopy) {  // Iterate over the copy
            for (Hole hole : holes) {
                hole.attractBall(ball);
                
                // Check if the ball should be captured
                if (!ball.isActive()) {  
                    ballsToRemove.add(ball); 
                    break; 
                }
            }
        }
    
        // Now, safely remove the captured balls after iteration
        balls.removeAll(ballsToRemove);
    }
    
    /**
     * Check if the win condition has been met
     * @return  True if the win condition has been met, false otherwise
     */
    public boolean checkWinCondition() {
        return getBalls().isEmpty() && getSpawners().get(0).getBallsToSpawn().isEmpty();
    }
   
    // --- Move Yellow Tiles ---
    /**
     * Move the yellow tiles in a circular motion during level end
     * @param deltaTime
     */
    public void moveYellowTiles(float deltaTime) {
        yellowTileTimer += deltaTime;
    
        // Move tiles every 0.067 seconds
        if (yellowTileTimer >= 0.067f) {
            yellowTileTimer = 0;  // Reset the timer
    
            // Top tile: move clockwise
            if (yellowTile1Y == 0 && yellowTile1X < width - 1) {
                yellowTile1X++;  // Move right
            } else if (yellowTile1X == width - 1 && yellowTile1Y < height - 1) {
                yellowTile1Y++;  // Move down
            } else if (yellowTile1Y == height - 1 && yellowTile1X > 0) {
                yellowTile1X--;  // Move left
            } else if (yellowTile1X == 0 && yellowTile1Y > 0) {
                yellowTile1Y--;  // Move up
            }
    
            // Bottom tile: move counterclockwise
            if (yellowTile2Y == height - 1 && yellowTile2X > 0) {
                yellowTile2X--;  // Move left
            } else if (yellowTile2X == 0 && yellowTile2Y > 0) {
                yellowTile2Y--;  // Move up
            } else if (yellowTile2Y == 0 && yellowTile2X < width - 1) {
                yellowTile2X++;  // Move right
            } else if (yellowTile2X == width - 1 && yellowTile2Y < height - 1) {
                yellowTile2Y++;  // Move down
            }
        }
    }
    
    // ----------------------------- Drawing -----------------------------------
    // --- Draw Yellow Tiles ---
    /**
     * Draw the yellow tiles on the board
     * @param app The PApplet object
     */
    public void drawYellowTiles(PApplet app) {
        // Calculate screen positions for top and bottom yellow tiles
        int x1 = yellowTile1X * App.CELLSIZE;
        int y1 = yellowTile1Y * App.CELLSIZE + App.TOPBAR;  // Offset by the top bar

        int x2 = yellowTile2X * App.CELLSIZE;
        int y2 = yellowTile2Y * App.CELLSIZE + App.TOPBAR;  // Offset by the top bar

        // Draw the yellow tiles over the current board tiles
        if (yellowTileImage != null) {
            app.image(yellowTileImage, x1, y1, App.CELLSIZE, App.CELLSIZE);
            app.image(yellowTileImage, x2, y2, App.CELLSIZE, App.CELLSIZE);
        } else {
            System.err.println("Error: Yellow tile image is not loaded.");
        }
    }


    
    
    // --- Draw the balls ---
    private void drawBalls(PApplet app) {
        if (balls == null || balls.isEmpty()) {
            return;
        }

        for (Ball ball : balls) {
            if (ball == null) {
                System.out.println("Null ball detected. Skipping.");
                continue;
            }
            if (gameStateManager.getState() == GameStateManager.GameState.PAUSED
                    || gameStateManager.getState() == GameStateManager.GameState.GAMEOVER || gameStateManager.getState() == GameStateManager.GameState.TIMESUP) {
                        ball.draw(app);
                continue;
            }
            ball.draw(app);
            ball.move();
            checkWallCollisions(ball, app);
            
        }
    }

    // --- Draw the Spawners ---
    private void drawSpawners(PApplet app) {
        if (spawners == null && spawners.isEmpty()) {
            return;
        }

        for (Spawner spawner : spawners) {
            spawner.draw();
        }

        
        // Spawn a ball from a random spawner
        int spawnInterval = spawners.get(0).getSpawnInterval();

        if (spawners.get(0).getBallsToSpawn().isEmpty()) {
            timeToSpawn = 0;
        } else {
            timeToSpawn = (spawnInterval - (app.frameCount - lastSpawnFrame)) / (float) App.FPS;
        }

        if (gameStateManager.getState() == GameStateManager.GameState.PAUSED) {
            lastSpawnFrame = app.frameCount;
            return;
        }

        if (app.frameCount - lastSpawnFrame >= spawnInterval) { // You can adjust this interval
            int randInt = App.random.nextInt(spawners.size());
            activeSpawner = spawners.get(randInt);

            if (gameStateManager.getState() == GameStateManager.GameState.TIMESUP) {
                activeSpawner.setActive(false);
            } else {
                activeSpawner.setActive(true);  // Activate only this spawner
                activeSpawner.spawnBall(balls, app);
                activeSpawner.setActive(false);  // Deactivate after spawning
    
                lastSpawnFrame = app.frameCount;
                timeToSpawn = 0;
            }
            
        }
    }

    // --- Draw the holes ---
    private void drawHoles(PApplet app) {
        if (holes == null || holes.isEmpty()) {
            System.out.println("Holes list is null or empty. Skipping hole drawing.");
            return;
        }
    
        for (Hole hole : holes) {
            if (hole != null) {
                hole.draw();  // Ensure only non-null holes are drawn
            } else {
                System.out.println("Null hole detected. Skipping.");
            }
        }
    }
    

    // --- Draw the game board ---
    private void drawGameBoard(PApplet app) {
        app.pushMatrix();
        app.translate(0, App.TOPBAR);
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile = tiles[x][y];
                if (tile != null) {
                    tile.draw(app);
                } else {
                    System.err.println("Null tile detected at (" + x + ", " + y + "). Skipping.");
                }
            }
        }

        app.popMatrix();
    }

    // --- Draw the board and all balls ---
    /**
     * Draw the board and all its components
     * @param app The PApplet object
     */
    public void draw(PApplet app) {
        if (level == null) {
            System.err.println("Error: Level is not set.");
            return;  // Do not proceed if level is not set
        }
        // Draw the game board
        drawGameBoard(app);

        // Draw Spawners
        drawSpawners(app);

        // Draw the holes
        drawHoles(app);

        // Draw the balls
        drawBalls(app);

        // Ball and Hole Attraction
        attractBallsToHoles();

        if (checkWinCondition()) {
            gameStateManager.addRemainingTimeToScore(4.0f / App.FPS);
            moveYellowTiles(1f / App.FPS);
            drawYellowTiles(app);
        }
    }

}
