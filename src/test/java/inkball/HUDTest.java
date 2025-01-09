package inkball;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Test class for the HUD class
 */
public class HUDTest {

    private HUD hud;
    private PApplet appMock;
    private Level levelMock;

    @BeforeEach
    public void setUp() {
        // Mock the PApplet and Level objects
        appMock = mock(PApplet.class);
        levelMock = mock(Level.class);

        // Mock the getBalls() method of the level to return some sample balls
        when(levelMock.getBalls()).thenReturn(Arrays.asList("ball0", "ball1", "ball2"));

        // Mock the image loading in HUD constructor
        when(appMock.loadImage(anyString())).thenReturn(mock(PImage.class));

        // Initialize HUD object with mocked app and level
        hud = new HUD(appMock, levelMock);
    }

    @Test
    public void testConstructorInitializesCorrectly() {
        // Verify that getBalls was called in the HUD constructor
        verify(levelMock, times(1)).getBalls();
    }

    @Test
    public void testUpdateBallsToSpawn() {
        // Test updating the ball queue
        List<String> newBalls = Arrays.asList("ball3", "ball4");
        hud.updateBallsToSpawn(newBalls);

        // Verify that the balls to spawn were updated correctly
        assertEquals(newBalls, hud.getBallsToSpawn());
    }

    @Test
    public void testGetBallImageValidColour() {
        // Test retrieving the correct image for a valid ball colour
        PImage ballImage = hud.getBallImage("ball1");
        assertNotNull(ballImage);

                // Test all valid colours for balls
        assertNotNull(hud.getBallImage("ball0"));
        assertNotNull(hud.getBallImage("ball1"));
        assertNotNull(hud.getBallImage("ball2"));
        assertNotNull(hud.getBallImage("ball3"));
        assertNotNull(hud.getBallImage("ball4"));
    }
    
    @Test
    public void testGetBallImageInvalidColour() {
        // Test retrieving an image for an invalid ball colour (should return null)
        PImage ballImage = hud.getBallImage("invalidColour");
        assertNull(ballImage);
    }

    @Test
    public void testDrawTopBar() {
        // Test drawing the top bar, mock required values
        hud.drawTopBar(100, "1:00", 5.5f, GameStateManager.GameState.PLAYING);

        // Verify the PApplet's fill and rect methods were called to draw the background
        verify(appMock).fill(200);
        verify(appMock).rect(0, 0, appMock.width, App.TOPBAR);

        // Verify the text for score and time was drawn
        verify(appMock).text("Score: 100", 450, App.TOPBAR - 40);
        verify(appMock).text("Time:  1:00", 450, App.TOPBAR - 20);
        verify(appMock).text("5.5s", 185, App.TOPBAR - 29);
    }

    @Test
    public void testDrawTopBarWithTimeUpState() {
        // Test drawTopBar when the game state is TIMESUP
        hud.drawTopBar(100, "1:00", 5.5f, GameStateManager.GameState.TIMESUP);
        
        // Verify the time to spawn text is not drawn
        verify(appMock, never()).text(anyString(), eq(185), eq(App.TOPBAR - 29));
    }

    @Test
    public void testDrawTopBarWithZeroTimeToSpawn() {
        // Test drawTopBar with timeToSpawn as zero
        hud.drawTopBar(100, "1:00", 0f, GameStateManager.GameState.PLAYING);

        // Verify the PApplet's text methods were called to display "0.0s"
        verify(appMock).text("0.0s", 185, App.TOPBAR - 29);
    }

    @Test
    public void testDrawBallQueue() {
        // Call drawBallQueue and verify that it calls app.image with the correct parameters
        hud.drawTopBar(100, "1:00", 5.5f, GameStateManager.GameState.PLAYING);
        verify(appMock, times(3)).image(any(PImage.class), anyFloat(), anyFloat(), anyFloat(), anyFloat());
    }
}
