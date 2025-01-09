package inkball;

import processing.core.PApplet;
import processing.data.JSONObject;
import processing.data.JSONArray;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * The Level class represents a game level. It loads the configuration for each level
 * from a JSON file, including layout, time limit, spawn intervals, ball colours, 
 * and score modifiers.
 */
public class Level {
  private String layoutFile;
  private int timeLimit;
  private int spawnInterval;
  private float scoreIncreaseModifier;
  private float scoreDecreaseModifier;
  private List<String> balls;
  private Map<String, Integer> scoreIncreaseFromHole;
  private Map<String, Integer> scoreDecreaseFromWrongHole;

  // Map ball colour to resource filename
  private static final Map<String, String> colourToResourceMap = new HashMap<>();
  static {
    colourToResourceMap.put("grey", "ball0");
    colourToResourceMap.put("orange", "ball1");
    colourToResourceMap.put("blue", "ball2");
    colourToResourceMap.put("green", "ball3");
    colourToResourceMap.put("yellow", "ball4");
}


  // -------------------------- Constructor ----------------------------------
  /**
   * Constructor for the Level class. It reads the level configuration from a JSON file.
   * @param app         The PApplet object
   * @param config      The .json file containing the configuration
   * @param levelIndex  The index of the level to load
   */
  public Level(PApplet app, JSONObject config, int levelIndex) {
    // Load the level configuration
    JSONArray levels = config.getJSONArray("levels");

    if (levelIndex >= levels.size()) {
      System.out.println("Error: Level index out of bounds.");
      return;
    }

    // Load Level and score Data
    JSONObject levelData = levels.getJSONObject(levelIndex);
    this.layoutFile = levelData.getString("layout");
    this.timeLimit = levelData.getInt("time");
    this.spawnInterval = levelData.getInt("spawn_interval");
    this.scoreIncreaseModifier = levelData.getFloat("score_increase_from_hole_capture_modifier");
    this.scoreDecreaseModifier = levelData.getFloat("score_decrease_from_wrong_hole_modifier");

    // Load Ball Colours
    JSONArray ballColoursArray = levelData.getJSONArray("balls");
    this.balls = new ArrayList<>();
    for (int i = 0; i < ballColoursArray.size(); i++) {
        String colour = ballColoursArray.getString(i);
        String ballResource = colourToResourceMap.get(colour);  // Convert colour to resource (e.g., "ball0")
        if (ballResource != null) {
            this.balls.add(ballResource);
        } else {
            System.err.println("Error: Invalid ball colour: " + colour);
        }
    }

    // Read Score Values
    this.scoreIncreaseFromHole = new HashMap<>();
    this.scoreDecreaseFromWrongHole = new HashMap<>();

    JSONObject scoreIncreaseJSON = config.getJSONObject("score_increase_from_hole_capture");
    JSONObject scoreDecreaseJSON = config.getJSONObject("score_decrease_from_wrong_hole");

    for (Object key : scoreIncreaseJSON.keys()) {
      String colour = (String) key;
      int score = scoreIncreaseJSON.getInt(colour);
      this.scoreIncreaseFromHole.put(colour, score);
    }

    for (Object key : scoreDecreaseJSON.keys()) {
      String colour = (String) key;
      int score = scoreDecreaseJSON.getInt(colour);
      this.scoreDecreaseFromWrongHole.put(colour, score);
    }
  }

  // --------------------- Getters and Setters -------------------------------
  /**
   * Get the layout file for the level.
   * @return  The layout file for the level
   */
  public String getLayoutFile() {
    return layoutFile;
  }

  /**
   * Get the time limit for the level.
   * @return  The time limit for the level
   */
  public int getTimeLimit() {
      return timeLimit;
  }

  /**
   * Get the spawn interval for the level.
   * @return  The spawn interval for the level
   */
  public int getSpawnInterval() {
      return spawnInterval;
  }

  /**
   * Get the score increase modifier for the level.
   * @return  The score increase modifier for the level
   */
  public float getScoreIncreaseModifier() {
      return scoreIncreaseModifier;
  }

  /**
   * Get the score decrease modifier for the level.
   * @return  The score decrease modifier for the level
   */
  public float getScoreDecreaseModifier() {
      return scoreDecreaseModifier;
  }

  /**
   * Get the list of balls for the level.
   * @return  The list strings of balls for the level
   */
  public List<String> getBalls() {
      return balls;
  }

  /**
   * Get the score increase from hole for the level.
   * @return  The score increase from hole for the level
   */
  public Map<String, Integer> getScoreIncreaseFromHole() {
      return scoreIncreaseFromHole;
  }

  /**
   * Get the score decrease from wrong hole for the level.
   * @return  The score decrease from wrong hole for the level
   */
  public Map<String, Integer> getScoreDecreaseFromWrongHole() {
      return scoreDecreaseFromWrongHole;
  }

}
