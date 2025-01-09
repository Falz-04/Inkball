package inkball;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;

/**
 * Test class for LineSegment
 */
public class LineSegmentTest {

    private LineSegment lineSegment;

    @BeforeEach
    public void setUp() {
        // Initialize a LineSegment object before each test
        lineSegment = new LineSegment(10, 20, 30, 40);
    }

    @Test
    public void testConstructorInitializesCorrectly() {
        // Test if the constructor initializes the line segment with correct values
        assertEquals(10, lineSegment.getStartX(), 0.01);
        assertEquals(20, lineSegment.getStartY(), 0.01);
        assertEquals(30, lineSegment.getEndX(), 0.01);
        assertEquals(40, lineSegment.getEndY(), 0.01);
    }

    @Test
    public void testGetStartX() {
        // Test if getStartX returns the correct starting x-coordinate
        assertEquals(10, lineSegment.getStartX(), 0.01);
    }

    @Test
    public void testGetStartY() {
        // Test if getStartY returns the correct starting y-coordinate
        assertEquals(20, lineSegment.getStartY(), 0.01);
    }

    @Test
    public void testGetEndX() {
        // Test if getEndX returns the correct ending x-coordinate
        assertEquals(30, lineSegment.getEndX(), 0.01);
    }

    @Test
    public void testGetEndY() {
        // Test if getEndY returns the correct ending y-coordinate
        assertEquals(40, lineSegment.getEndY(), 0.01);
    }

    @Test
    public void testDrawCallsPAppletMethods() {
        // Mock the PApplet object to simulate drawing
        PApplet appMock = mock(PApplet.class);

        // Call the draw method on the line segment
        lineSegment.draw(appMock);

        // Verify that the PApplet's stroke and strokeWeight methods were called with the correct parameters
        verify(appMock).stroke(0);  // Ensure stroke color is set to black (0)
        verify(appMock).strokeWeight(10);  // Ensure stroke weight is set to 10
        verify(appMock).line(10, 20, 30, 40);  // Ensure line is drawn from (10, 20) to (30, 40)
    }
}
