package inkball;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.data.JSONObject;

/**
 * Test class for GameStateManager
 */
public class GameStateManagerTest {

    private GameStateManager gameStateManager;
    private PApplet appMock;
    private Level levelMock;
    private JSONObject configMock;

    @BeforeEach
    public void setUp() {
        // Mock the PApplet, Level, and JSONObject
        appMock = mock(PApplet.class);
        levelMock = mock(Level.class);
        configMock = mock(JSONObject.class);

        // Mocking the getTimeLimit() method to return an integer value instead of a float
        when(levelMock.getTimeLimit()).thenReturn(100); // Changed to return Integer instead of float

        // Create an instance of GameStateManager with the mocked PApplet and Level
        gameStateManager = new GameStateManager(appMock, levelMock);
    }

    @Test
    public void testGetRemainingTime() {
        // Set the remaining time and assert the expected output
        String remainingTime = gameStateManager.setRemainingTime(120);
        assertEquals("120", remainingTime);
    }

    @Test
    public void testAddRemainingTimeToScore() {
        gameStateManager.addRemainingTimeToScore(0.067f);
        assertEquals(0, gameStateManager.getScore());

        // Simulate time passage and check if the score is updated
        gameStateManager.addRemainingTimeToScore(1.0f);
        assertTrue(gameStateManager.getScore() > 0);
    }

    @Test
    public void testTogglePause() {
        gameStateManager.togglePause();
        assertEquals(GameStateManager.GameState.PAUSED, gameStateManager.getState());

        gameStateManager.togglePause();
        assertEquals(GameStateManager.GameState.PLAYING, gameStateManager.getState());
    }

    @Test
    public void testRestart() {
        // Use a mock for App instead of PApplet
        App appInstance = mock(App.class);  // Changed to App instead of PApplet

        // Mock the config to return a level with time set to 100
        when(configMock.getJSONArray("levels").getJSONObject(0).getFloat("time")).thenReturn(100f);

        // Call restart and check the state
        gameStateManager.restart(appInstance, configMock, 0);
        assertEquals(GameStateManager.GameState.PLAYING, gameStateManager.getState());
    }

    @Test
    public void testUpdateTimer() {
        try {
            gameStateManager.updateTimer(10f);
            System.out.println("REMAINING TIME: " + gameStateManager.getRemainingTime());
            
            // Assertion here
            assertTrue(Float.parseFloat(gameStateManager.getRemainingTime()) < 100);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

}
