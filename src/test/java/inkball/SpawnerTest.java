package inkball;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import processing.core.PApplet;
import processing.core.PImage;

public class SpawnerTest {

    private Spawner spawner;
    private PApplet appMock;
    private List<String> ballsToSpawn;
    private Board boardMock;

    @BeforeEach
    public void setUp() {
        // Mock the PApplet and Board objects
        appMock = mock(PApplet.class);
        boardMock = mock(Board.class);

        // Mock the PImage for the spawner image
        when(appMock.loadImage(anyString())).thenReturn(mock(PImage.class));

        // Initialize the ballsToSpawn list and add some sample balls
        ballsToSpawn = new ArrayList<>();
        ballsToSpawn.add("ball1");
        ballsToSpawn.add("ball2");

        // Initialize the Spawner object
        spawner = new Spawner(5, 5, appMock, 5, ballsToSpawn, boardMock);
    }

    @Test
    public void testConstructorInitializesCorrectly() {
        // Verify that the spawner was initialized with the correct spawn interval
        assertEquals(5 * App.FPS, spawner.getSpawnInterval());

        // Verify that the spawner starts as inactive
        assertFalse(spawner.isActive());
    }

    @Test
    public void testGetTimeToSpawn() {
        // Set the spawn frame counter to 0
        spawner.setSpawnFrameCounter(0);

        // Check that the time to spawn is correct
        assertEquals(5, spawner.getTimeToSpawn(), 0.01);

        // Simulate some frames passing and check the new time to spawn
        spawner.setSpawnFrameCounter(2);
        assertEquals(4.96, spawner.getTimeToSpawn(), 0.01);
    }

    @Test
    public void testIsTimeToSpawn() {
        // Set the spawn frame counter to a value lower than the interval
        spawner.setSpawnFrameCounter(3);
        assertFalse(spawner.isTimeToSpawn());

        // Set the spawn frame counter equal to the interval
        spawner.setSpawnFrameCounter(5 * App.FPS);
        assertTrue(spawner.isTimeToSpawn());
    }

    @Test
    public void testSpawnBall() {
        // Create a list to hold the balls
        List<Ball> balls = new ArrayList<>();

        // Make the spawner active and simulate a spawn
        spawner.setActive(true);
        spawner.setSpawnFrameCounter(5 * App.FPS);  // Set frame counter to indicate it's time to spawn

        spawner.spawnBall(balls, appMock);

        // Verify that a ball was added to the list
        assertEquals(1, balls.size());
        assertEquals("ball1", balls.get(0).getColour());

        // Verify that the spawn frame counter was reset
        assertEquals(0, spawner.getTimeToSpawn(), 0.01);

        // Verify that the next ball is different
        spawner.spawnBall(balls, appMock);
        assertEquals(2, balls.size());
        assertEquals("ball2", balls.get(1).getColour());
    }

    @Test
    public void testDraw() {
        // Set the spawner to active and simulate drawing
        spawner.setActive(true);
        spawner.draw();

        // Verify that the spawner image was drawn
        verify(appMock).image(any(PImage.class), eq(5 * App.CELLSIZE), eq(5 * App.CELLSIZE + App.TOPBAR));
    }

    @Test
    public void testAddBallToSpawnQueue() {
        // Add a ball to the spawn queue
        spawner.addBallToSpawnQueue("ball3");

        // Verify that the ball was added to the queue
        assertTrue(spawner.getBallsToSpawn().contains("ball3"));
    }
}
