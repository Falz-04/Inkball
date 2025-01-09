package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.util.*;

public class App extends PApplet {

    // WINDOW VARIABLES
    public static final int CELLSIZE = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 576;
    public static int HEIGHT = 640;
    public static final int BOARD_WIDTH = WIDTH / CELLSIZE;
    public static final int BOARD_HEIGHT = 18;
    public static PFont FONT;

    // GAME VARIABLES
    public static final int FPS = 60;
    public static Random random = new Random();

    // Level Variables
    private Level level;
    private int levelIndex;

    // BOARD VARIABLES
    private Board board;
    private HUD hud;
    private GameStateManager gameStateManager;

    private PImage wall0, wall1, wall2, wall3, wall4;
    private PImage backgroundTile;

    // Lines
    private List<Line> lines;
    private Line currentLine;
    private float lastX, lastY;
    private boolean isDrawing;

    public int frameCount = 0;

    public App() {
        this.lines = new ArrayList<>();
        this.isDrawing = false;
    }


    /**
     * Initialise the setting of the window size.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
    @Override
    public void setup() {
        frameRate(FPS);
        FONT = createFont("Arial-Bold", 16, true);
        textFont(FONT);
        JSONObject config = loadJSONObject("config.json");
        if (config == null) {
            println("Error: config.json file not found.");
            exit();
        }

        // Load Board Images
        try {
            wall0 = loadImage(URLDecoder.decode(this.getClass().getResource("wall0.png").getPath(), StandardCharsets.UTF_8.name()));
            wall1 = loadImage(URLDecoder.decode(this.getClass().getResource("wall1.png").getPath(), StandardCharsets.UTF_8.name()));
            wall2 = loadImage(URLDecoder.decode(this.getClass().getResource("wall2.png").getPath(), StandardCharsets.UTF_8.name()));
            wall3 = loadImage(URLDecoder.decode(this.getClass().getResource("wall3.png").getPath(), StandardCharsets.UTF_8.name()));
            wall4 = loadImage(URLDecoder.decode(this.getClass().getResource("wall4.png").getPath(), StandardCharsets.UTF_8.name()));

            // spawner = loadImage(URLDecoder.decode(this.getClass().getResource("entrypoint.png").getPath(), StandardCharsets.UTF_8.name()));
            backgroundTile = loadImage(URLDecoder.decode(this.getClass().getResource("tile.png").getPath(), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        if (wall0 == null || wall1 == null || wall2 == null || wall3 == null || wall4 == null || backgroundTile == null) {
            println("Error: One or more images failed to load.");
            exit();
        }

        try {
            // Initialise Game Elements
            this.levelIndex = 0;
            level = new Level(this, config, levelIndex);
            gameStateManager = new GameStateManager(this, level); 
            hud = new HUD(this, this.level); 
            board = new Board(this, BOARD_WIDTH, BOARD_HEIGHT, gameStateManager); 
            board.setLevel(this.level);
            board.loadLevel(this, level); // Load the level layout
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                for (int y = 0; y < BOARD_HEIGHT; y++) {
                    Tile tile = board.getTile(x, y);
                    tile.setWallImage(0, wall0);
                    tile.setWallImage(1, wall1);
                    tile.setWallImage(2, wall2);
                    tile.setWallImage(3, wall3);
                    tile.setWallImage(4, wall4);

                    tile.setBackgroundTile(backgroundTile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // board.printTileProperties();
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKey() == 'r' || event.getKey() == 'R') {
            // Restart the game and reset the level
            gameStateManager.restart(this, loadJSONObject("config.json"), this.levelIndex); 
        }
    
        if (event.getKey() == ' ') {
            // Pause/unpause the game
            gameStateManager.togglePause();
        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
    @Override
    public void keyReleased() {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (mouseButton == LEFT) {
            lastX = e.getX();
            lastY = e.getY();
            currentLine = new Line();
            isDrawing = true;
        }

        if (mouseButton == RIGHT && lines.size() > 0) {
            lines.remove(lines.size() - 1);
        }

        if (mouseButton == LEFT && (e.isControlDown())) {
            lines.remove(lines.size() - 1);
        }
       
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        float currentX = e.getX();
        float currentY = e.getY();

        if (currentY > TOPBAR) {
            if (isDrawing) {
                // Create a new line segment from the last position to the current mouse position
                LineSegment newSegment = new LineSegment(lastX, lastY, currentX, currentY);

                // Add the new segment to the currently drawn line
                if (currentLine != null) {
                    currentLine.addSegment(newSegment);

                    // Check for collision with any ball during the drawing process
                    for (Ball ball : board.getBalls()) {
                        if (currentLine.isCollidingWithBall(ball)) {
                            // If collision occurs, update trajectory and remove the entire line
                            ball.updateTrajectory(currentLine.getSegments().get(0));
                            lines.remove(currentLine);  // Remove the line
                            currentLine = null;  // Clear the current line
                            isDrawing = false;  // Stop drawing
                            return;  // Exit immediately after collision
                        }
                    }
                }

                // Update the last known mouse position for the next segment
                lastX = currentX;
                lastY = currentY;
            }
        }
    }




    @Override
    public void mouseReleased(MouseEvent e) {
        isDrawing = false;
        if (currentLine != null) {
            lines.add(currentLine);  // Only add the line if it wasn't removed due to collision
            currentLine = null;
        }
    }
    

    /**
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {
        // Clear the background
        // background(0);

        float timeToSpawn = board.getTimeToSpawn();
        hud.drawTopBar(gameStateManager.getScore(), gameStateManager.getRemainingTime(), timeToSpawn, gameStateManager.getState());

        if (gameStateManager.getState() == GameStateManager.GameState.GAMEOVER) {
            // Display the GAME OVER message in the middle of the top bar
            if (levelIndex < 2) {
                levelIndex++;
                level = new Level(this, loadJSONObject("config.json"), levelIndex);
                gameStateManager.restart(this, loadJSONObject("config.json"), levelIndex);
            } else {
                fill(0); // White colour for text
                textSize(15);
                text("== GAME ENDED==", width / 2 - 50, TOPBAR / 2 + 10);
            }
            
        }
        // Game is paused: still render everything, but don't update ball movement or time
        else if (gameStateManager.getState() == GameStateManager.GameState.PAUSED) {
            // Render the game as usual (board, balls, lines, etc.)
            board.draw(this);

            // Render the lines
            for (Line line : lines) {
                line.draw(this);
            }

            // Render the current line being drawn (if any)
            if (isDrawing && currentLine != null) {
                currentLine.draw(this);
            }

            // Display the PAUSED message in the middle of the top bar
            fill(0); // White colour for text
            textSize(30);
            text("** PAUSED **", width / 2 - 100, TOPBAR / 2 + 10);

        } else if (gameStateManager.getState() == GameStateManager.GameState.TIMESUP){

            // Render the game as usual (board, balls, lines, etc.)
            board.draw(this);

            // Render the lines
            for (Line line : lines) {
                line.draw(this);
            }

            // Render the current line being drawn (if any)
            if (isDrawing && currentLine != null) {
                currentLine.draw(this);
            }


            // Display the PAUSED message in the middle of the top bar
            fill(0); // White colour for text
            textSize(30);
            text("==TIMES UP==", width / 2 - 100, TOPBAR / 2 + 10);

         }
    
        else if (gameStateManager.getState() == GameStateManager.GameState.PLAYING) {
            // Game state logic (only update if the game is playing)
            gameStateManager.updateTimer(1f / App.FPS);
            int gameScore = gameStateManager.getScore();
            gameStateManager.updateScore(gameScore);

            // Draw the board and other game elements
            board.draw(this);

            if (board.checkWinCondition()) {
                gameStateManager.addRemainingTimeToScore(1.0f / App.FPS);
                if (gameStateManager.getRemainingTime().equals("0") && board.getBalls().isEmpty() && board.getSpawners().get(0).getBallsToSpawn().isEmpty()) {
                    gameStateManager.setState(GameStateManager.GameState.GAMEOVER);
                }

            }
            

            // Check for ball collisions with all completed lines
            for (Iterator<Line> iterator = lines.iterator(); iterator.hasNext();) {
                Line line = iterator.next();

                // Check collision for each ball with the current line
                for (Ball ball : board.getBalls()) {
                    if (line.isCollidingWithBall(ball)) {
                        ball.updateTrajectory(line.getSegments().get(0)); // Update ball's trajectory based on the line's first segment
                        iterator.remove(); // Remove the entire line
                        break; // Stop checking once the line is removed
                    }
                }

                // Draw the remaining lines
                line.draw(this);
            }

            
            if (gameScore < 0) {
                levelIndex = 0;
                gameStateManager.restart(this, loadJSONObject("config.json"), levelIndex); 
            }

            // Draw the current line being drawn (if the user is still drawing)
            if (isDrawing && currentLine != null) {
                currentLine.draw(this);
            }

            if (gameStateManager.getRemainingTime().equals("0") && (!board.getBalls().isEmpty() || !board.getSpawners().get(0).getBallsToSpawn().isEmpty())) {
                gameStateManager.setState(GameStateManager.GameState.TIMESUP);
            
            }

            
        }
    }

    



    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }


    public void clearLines() {
        lines.clear();
    }

    public void initialiseLevel(JSONObject config, int levelIndex) {
        // Load the level again
        level = new Level(this, config, levelIndex);
        board = new Board(this, BOARD_WIDTH, BOARD_HEIGHT, gameStateManager); 
        board.setLevel(level);
        board.loadLevel(this, level); // Reload the level layout
        
        // Reset all tiles to their initial state
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                Tile tile = board.getTile(x, y);
                tile.setWallImage(0, wall0);
                tile.setWallImage(1, wall1);
                tile.setWallImage(2, wall2);
                tile.setWallImage(3, wall3);
                tile.setWallImage(4, wall4);
                tile.setBackgroundTile(backgroundTile);
            }
        }
    
        // Clear any drawn lines
        lines.clear();
    
        // Update HUD to reflect the new balls to spawn for the new level
        hud.updateBallsToSpawn(level.getBalls());
    }
    
}
