package inkball;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * Test class for the Hole class
 */
public class HoleTest {

    private Hole hole;
    private PApplet appMock;
    private Board boardMock;
    private Ball ballMock;

    @BeforeEach
    public void setUp() {
        // Mock PApplet and Board
        appMock = mock(PApplet.class);
        boardMock = mock(Board.class);

        // Create the hole object (ignore draw functionality for now)
        hole = new Hole(10, 10, appMock, "hole1", boardMock);

        // Mock a ball object for testing
        ballMock = mock(Ball.class);
    }

    @Test
    public void testConstructorInitializesCorrectly() {
        // Verify that the hole is initialized with correct center
        PVector center = hole.getCenter();
        assertEquals((10 + 1) * App.CELLSIZE, center.x, 0.01);
        assertEquals((10 + 1) * App.CELLSIZE + App.TOPBAR, center.y, 0.01);
    }

    @Test
    public void testGetColour() {
        // Test valid color mapping
        assertEquals("orange", hole.getColour());

        // Create another hole with different color
        Hole hole2 = new Hole(10, 10, appMock, "hole0", boardMock);
        assertEquals("grey", hole2.getColour());

        // Test invalid color (since it will default to the else branch)
        Hole holeInvalid = new Hole(10, 10, appMock, "invalidColor", boardMock);
        assertEquals("Ball Colour Not Found", holeInvalid.getColour());
    }

    @Test
    public void testGetColourWithInvalidColour() {
        Hole invalidColourHole = new Hole(10, 10, appMock, "invalidHoleColour", boardMock);
        assertEquals("Ball Colour Not Found", invalidColourHole.getColour());
    }

    @Test
    public void testIsMatchingColour() {
        // Test ball matching with hole
        when(ballMock.getColour()).thenReturn("orange");  // Ball color matches hole
        assertTrue(hole.isMatchingColour(ballMock));

        when(ballMock.getColour()).thenReturn("grey");  // Grey ball should match any hole
        assertTrue(hole.isMatchingColour(ballMock));

        when(ballMock.getColour()).thenReturn("blue");  // Mismatch color
        assertFalse(hole.isMatchingColour(ballMock));
    }

    @Test
    public void testAttractBallWithinRange() {
        // Set up the ball to be close enough to be attracted to the hole
        when(ballMock.getPosX()).thenReturn(10f * App.CELLSIZE);
        when(ballMock.getPosY()).thenReturn(10f * App.CELLSIZE + App.TOPBAR);
        when(ballMock.getRadius()).thenReturn(5f);

        // Simulate attraction
        hole.attractBall(ballMock);

        // Verify that the ball is being applied a force
        verify(ballMock).applyForce(any(PVector.class));

        // Verify that the ball's scale changes
        verify(ballMock).setScale(anyFloat());
    }

    @Test
    public void testAttractBallOutsideRange() {
        // Set the ball to be far from the hole
        when(ballMock.getPosX()).thenReturn(200f);  // Far away from the hole
        when(ballMock.getPosY()).thenReturn(200f);

        // Call attractBall (no force should be applied as it's outside range)
        hole.attractBall(ballMock);

        // Verify that applyForce was never called
        verify(ballMock, never()).applyForce(any(PVector.class));
    }

    @Test
    public void testCaptureBallSuccessful() {
        // Simulate successful capture with matching color
        when(ballMock.getColour()).thenReturn("orange");
        hole.captureBall(ballMock);

        // Verify the ball is deactivated and a successful capture is handled
        verify(ballMock).setActive(false);
        verify(boardMock).handleSuccessfulCapture(ballMock);
    }

    @Test
    public void testCaptureBallFailed() {
        // Simulate failed capture with mismatching color
        when(ballMock.getColour()).thenReturn("blue");
        hole.captureBall(ballMock);

        // Verify the ball is deactivated and a failed capture is handled
        verify(ballMock).setActive(false);
        verify(boardMock).handleFailedCapture(ballMock);
    }

    @Test
    public void testDraw() {
        // Mock the hole image loading to simulate a non-null image
        PImage mockImage = mock(PImage.class);
        when(appMock.loadImage(anyString())).thenReturn(mockImage);
        hole = new Hole(10, 10, appMock, "hole1", boardMock);  // Reinitialize to load image

        // Call draw to verify it works
        hole.draw();

        // Verify that the image is drawn four times (since the hole occupies 2x2 space)
        verify(appMock, times(4)).image(any(PImage.class), anyFloat(), anyFloat(), anyFloat(), anyFloat(), anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    public void testDrawWithNullImage() {
        // Ensure the hole image is null
        when(appMock.loadImage(anyString())).thenReturn(null);
        hole = new Hole(10, 10, appMock, "hole1", boardMock);  // Reinitialize with null image

        // Capture console output if needed, or just verify that it doesn't throw exceptions
        assertDoesNotThrow(() -> hole.draw());
    }

    @Test
    public void testAttractBallAtBoundary() {
        // Set the ball to be exactly 32 pixels away from the hole center
        PVector holeCenter = hole.getCenter();
        when(ballMock.getPosX()).thenReturn(holeCenter.x - 32);
        when(ballMock.getPosY()).thenReturn(holeCenter.y);
        when(ballMock.getRadius()).thenReturn(16f);  // Arbitrary radius

        // Call attractBall and verify that attraction is applied
        hole.attractBall(ballMock);
        verify(ballMock).applyForce(any(PVector.class));

        // Verify that scale is adjusted, but the ball isn't captured yet (as it's still at the edge)
        verify(ballMock).setScale(anyFloat());
        verify(boardMock, never()).handleSuccessfulCapture(ballMock);
        verify(boardMock, never()).handleFailedCapture(ballMock);
    }

    @Test
    public void testAttractBallCapturesBall() {
        // Set the ball to be very close to the hole (within the radius) to simulate capture
        when(ballMock.getPosX()).thenReturn(hole.getCenter().x);
        when(ballMock.getPosY()).thenReturn(hole.getCenter().y);
        when(ballMock.getRadius()).thenReturn(64f);  // Simulate a radius that would overlap

        // Call attractBall and ensure capture happens
        hole.attractBall(ballMock);
        verify(boardMock).handleSuccessfulCapture(ballMock);  // Should be captured
    }

}
