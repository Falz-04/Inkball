package inkball;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;

class LevelTest {
    
    private PApplet appMock;
    private JSONObject configMock;

    @BeforeEach
  public void setUp() {
      // Mock the PApplet
      appMock = mock(PApplet.class);

      // Create a mock JSON configuration
      configMock = new JSONObject();

      // Create sample level data using JSONArray append method
      JSONArray levels = new JSONArray();
      JSONObject levelData = new JSONObject();
      levelData.put("layout", "level1.txt");
      levelData.put("time", 120);
      levelData.put("spawn_interval", 10);
      levelData.put("score_increase_from_hole_capture_modifier", 1.5);
      levelData.put("score_decrease_from_wrong_hole_modifier", -2.0);
      
      // Create balls array and append ball colors
      JSONArray ballsArray = new JSONArray();
      ballsArray.append("grey");
      ballsArray.append("orange");
      ballsArray.append("blue");
      
      levelData.put("balls", ballsArray);
      levels.append(levelData);
      configMock.put("levels", levels);

      // Add score values for increase and decrease
      JSONObject scoreIncrease = new JSONObject();
      scoreIncrease.put("grey", 10);
      scoreIncrease.put("orange", 20);
      configMock.put("score_increase_from_hole_capture", scoreIncrease);

      JSONObject scoreDecrease = new JSONObject();
      scoreDecrease.put("grey", -5);
      scoreDecrease.put("orange", -10);
      configMock.put("score_decrease_from_wrong_hole", scoreDecrease);
  }


    @Test
    public void testConstructorInitializesLevelCorrectly() {
        // Create a Level object using the mock config
        Level level = new Level(appMock, configMock, 0);

        assertEquals("level1.txt", level.getLayoutFile());
        assertEquals(120, level.getTimeLimit());
        assertEquals(10, level.getSpawnInterval());
        assertEquals(1.5, level.getScoreIncreaseModifier());
        assertEquals(-2.0, level.getScoreDecreaseModifier());
        
        List<String> balls = level.getBalls();
        assertEquals(3, balls.size());
        assertEquals("ball0", balls.get(0)); // grey -> ball0
        assertEquals("ball1", balls.get(1)); // orange -> ball1
        assertEquals("ball2", balls.get(2)); // blue -> ball2
    }

    @Test
    public void testConstructorHandlesInvalidBallColour() {
        // Modify config to include an invalid ball colour
        JSONObject levelData = configMock.getJSONArray("levels").getJSONObject(0);
        levelData.getJSONArray("balls").append("invalid");

        // Create the level object
        Level level = new Level(appMock, configMock, 0);

        // Ensure the invalid colour is not added to the balls list
        List<String> balls = level.getBalls();
        assertEquals(3, balls.size()); // Should not include "invalid"
        assertEquals("ball0", balls.get(0));
        assertEquals("ball1", balls.get(1));
        assertEquals("ball2", balls.get(2));
    }

    @Test
    public void testScoreIncreaseFromHole() {
        Level level = new Level(appMock, configMock, 0);
        
        Map<String, Integer> scoreIncrease = level.getScoreIncreaseFromHole();
        assertEquals(10, scoreIncrease.get("grey"));
        assertEquals(20, scoreIncrease.get("orange"));
    }

    @Test
    public void testScoreDecreaseFromWrongHole() {
        Level level = new Level(appMock, configMock, 0);

        Map<String, Integer> scoreDecrease = level.getScoreDecreaseFromWrongHole();
        assertEquals(-5, scoreDecrease.get("grey"));
        assertEquals(-10, scoreDecrease.get("orange"));
    }

    @Test
    public void testGetLayoutFile() {
        Level level = new Level(appMock, configMock, 0);
        assertEquals("level1.txt", level.getLayoutFile());
    }

    @Test
    public void testGetTimeLimit() {
        Level level = new Level(appMock, configMock, 0);
        assertEquals(120, level.getTimeLimit());
    }

    @Test
    public void testGetSpawnInterval() {
        Level level = new Level(appMock, configMock, 0);
        assertEquals(10, level.getSpawnInterval());
    }

    @Test
    public void testGetScoreIncreaseModifier() {
        Level level = new Level(appMock, configMock, 0);
        assertEquals(1.5, level.getScoreIncreaseModifier(), 0.01);
    }

    @Test
    public void testGetScoreDecreaseModifier() {
        Level level = new Level(appMock, configMock, 0);
        assertEquals(-2.0, level.getScoreDecreaseModifier(), 0.01);
    }

    @Test
    public void testInvalidLevelIndex() {
        // Test passing an invalid level index
        Level level = new Level(appMock, configMock, 10); // No level at index 10
        assertNull(level.getLayoutFile());  // Should return null or handle error gracefully
    }
}
