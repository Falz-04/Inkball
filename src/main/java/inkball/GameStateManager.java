package inkball;

import processing.core.PApplet;
import processing.data.JSONObject;

/**
 * This class manages the game state, including the current state of the game,
 * the score, the timer, and the time to spawn new balls.
 */
public class GameStateManager {
    
    // Enum to represent the current state of the game
    public enum GameState {
        PLAYING,
        PAUSED,
        GAMEOVER,
        TIMESUP,
        ADDING_TIME_TO_SCORE
    }

    private GameState currentState;
    private int startFrame;
    private int score;
    private int preLevelScore; 
    private int pausedDuration;
    private int pauseFrame;
    private float timeToSpawn;
    private PApplet app;
    private double remainingTime;  
    private float accumulatedTime = 0;

    // -------------------------- Constructor ----------------------------------
    /**
     * Constructor for the GameStateManager class.
     * @param app   The PApplet object
     * @param level The current level
     */
    public GameStateManager(PApplet app, Level level) {
        this.app = app;
        currentState = GameState.PLAYING;
        startFrame = app.frameCount;
        this.preLevelScore = 0;
        this.score = preLevelScore;
        pausedDuration = 0;
        pauseFrame = 0;
        remainingTime = level.getTimeLimit(); 
    }

    // --------------------- Getters and Setters -------------------------------
    /**
     * Get the current state of the game.
     * @return  The current state of the game
     */
    public GameState getState() {
        return currentState;
    }

    /**
     * Set the current state of the game.
     * @param state The new state of the game
     */
    public void setState(GameState state) {
        this.currentState = state;
    }

    /**
     * Update the score.
     * @param score The new score
     */
    public void updateScore(int score) {
        this.score = score;
    }

    /**
     * Gets the current score
     * @return (int) score: The current score
     */
    public int getScore() {
        return score;
    }

    /**
     * Get the time to spawn new balls.
     * @return The time to spawn new balls
     */
    public float getTimeToSpawn() {
        return timeToSpawn;
    }

    /**
     * Get the remaining time.
     * @return (String) The time remaining
     */
    public String getRemainingTime() {
        return String.format("%.0f", Math.max(0, remainingTime));
    }

    // ----------------------------- Methods -----------------------------------
    /**
     * Update the timer.
     * @param deltaTime The time elapsed since the last frame
     */
    public void updateTimer(float deltaTime) {
        if (currentState == GameState.PLAYING && remainingTime > 0) {
            remainingTime -= deltaTime;
            if (remainingTime <= 0) {
                remainingTime = 0;
                setState(GameState.TIMESUP);
            }
        }
    }

    /**
     * Set the remaining time.
     * @param timeLimit The time limit
     * @return The remaining time
     */
    public String setRemainingTime(double timeLimit) {
        // If the game is paused, the timer should stop decreasing
        int framesElapsed;
        if (currentState == GameState.PAUSED) {
            framesElapsed = pauseFrame - startFrame - pausedDuration;
        } else {
            framesElapsed = app.frameCount - startFrame - pausedDuration;
        }
        float framesToSecond = framesElapsed / (float) App.FPS; // Convert frames to seconds
        remainingTime = Math.max(0, timeLimit - Math.floor(framesToSecond)); // Update remaining time
        return String.format("%.0f", remainingTime);
    }

    /**
     * Get the elapsed time.
     * @return The elapsed time
     */
    public float getElapsedTime() {
        int framesElapsed = app.frameCount - startFrame - pausedDuration; // Subtract the pause time
        float framesToSecond = framesElapsed / (float) App.FPS; // Convert frames to seconds
        return framesToSecond;
    }

    /**
     * Add the remaining time to the score.
     * @param deltaTime The time elapsed since the last frame
     */
    public void addRemainingTimeToScore(float deltaTime) {
        if (remainingTime > 0) {
            double timeToAdd = Math.min(deltaTime, remainingTime); // Ensure we don't overshoot
            accumulatedTime += timeToAdd; // Accumulate the time
            
            // Only increment the score when enough time has been accumulated (e.g., 0.067 seconds)
            while (accumulatedTime >= 0.067f) {
                remainingTime -= 1; // Decrease the remaining time
                updateScore(score + 1); // Increment score by 1
                accumulatedTime -= 0.067f; // Subtract the threshold
            }
        }
    }

    /**
     * Toggle the pause state of the game.
     */
    public void togglePause() {
        if (currentState == GameState.PLAYING) {
            currentState = GameState.PAUSED;
            pauseFrame = app.frameCount;
        } else if (currentState == GameState.PAUSED) {
            currentState = GameState.PLAYING;
            pausedDuration += app.frameCount - pauseFrame;
        }
    }

    /**
     * Restart the game.
     * @param app           The PApplet object
     * @param config        The configuration file
     * @param levelIndex    The index of the level
     */
    public void restart(App app, JSONObject config, int levelIndex) {
        currentState = GameState.PLAYING;
        savePreLevelScore();
        updateScore(levelIndex == 0 ? 0 : preLevelScore);
        resetTimer();
        remainingTime = config.getJSONArray("levels").getJSONObject(levelIndex).getFloat("time");  // initialise remaining time from config
        app.initialiseLevel(config, levelIndex);
    }

    /**
     * Resets the timer.
     */
    public void resetTimer() {
        this.startFrame = app.frameCount;
        this.pausedDuration = 0;
    }

    /**
     * Save the score from the previous level.
     */
    public void savePreLevelScore() {
        this.preLevelScore = this.score;
    }
}
