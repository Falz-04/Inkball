package inkball;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import processing.core.PApplet;
import java.util.ArrayList;
import java.util.List;

class LineTest {

    private Line line;
    private Ball ballMock;
    private PApplet appMock;

    @BeforeEach
    public void setUp() {
        // Set up the Line and the Ball mock objects
        line = new Line();
        ballMock = mock(Ball.class);
        appMock = mock(PApplet.class);
    }

    @Test
    public void testAddSegment() {
        LineSegment segment = new LineSegment(0, 0, 100, 100);
        line.addSegment(segment);

        // Verify that the segment was added
        List<LineSegment> segments = line.getSegments();
        assertEquals(1, segments.size(), "There should be one segment in the line.");
        assertEquals(segment, segments.get(0), "The added segment should match the one retrieved.");
    }

    @Test
    public void testGetSegments() {
        // Add two segments to the line
        LineSegment segment1 = new LineSegment(0, 0, 100, 100);
        LineSegment segment2 = new LineSegment(100, 100, 200, 200);
        line.addSegment(segment1);
        line.addSegment(segment2);

        // Retrieve the segments and check the size and content
        List<LineSegment> segments = line.getSegments();
        assertEquals(2, segments.size(), "There should be two segments in the line.");
        assertEquals(segment1, segments.get(0), "The first segment should match the one added.");
        assertEquals(segment2, segments.get(1), "The second segment should match the one added.");
    }

    @Test
    public void testDraw() {
        // Add a segment to the line
        LineSegment segment = new LineSegment(0, 0, 100, 100);
        line.addSegment(segment);

        // Call the draw method
        line.draw(appMock);

        // Verify that the PApplet stroke and strokeWeight methods were called with the correct parameters
        Mockito.verify(appMock).stroke(0);  // Verify stroke color set to black
        Mockito.verify(appMock).strokeWeight(10);  // Verify stroke weight set to 10

        // Verify that the PApplet line method was called to draw the segment
        Mockito.verify(appMock).line(segment.getStartX(), segment.getStartY(), segment.getEndX(), segment.getEndY());
    }


    @Test
    public void testIsCollidingWithBall_True() {
        // Set up a scenario where the ball is colliding with the line
        LineSegment segment = new LineSegment(0, 0, 100, 100);
        line.addSegment(segment);

        // Mock the ball's collision method to return true
        when(ballMock.isCollidingWithLine(segment)).thenReturn(true);

        assertTrue(line.isCollidingWithBall(ballMock), "The ball should be colliding with the line.");
    }

    @Test
    public void testIsCollidingWithBall_False() {
        // Set up a scenario where the ball is not colliding with the line
        LineSegment segment = new LineSegment(0, 0, 100, 100);
        line.addSegment(segment);

        // Mock the ball's collision method to return false
        when(ballMock.isCollidingWithLine(segment)).thenReturn(false);

        assertFalse(line.isCollidingWithBall(ballMock), "The ball should not be colliding with the line.");
    }

    @Test
    public void testRemoveLineIfCollidingWithBall_RemovesLine() {
        // Set up a scenario where the ball is colliding with the line
        LineSegment segment = new LineSegment(0, 0, 100, 100);
        line.addSegment(segment);

        // Mock the ball's collision method to return true
        when(ballMock.isCollidingWithLine(segment)).thenReturn(true);

        // Create a list of lines and add the current line
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        // Call the removeLineIfCollidingWithBall method
        line.removeLineIfCollidingWithBall(ballMock, lines);

        // Verify that the line was removed from the list
        assertEquals(0, lines.size(), "The line should be removed from the list after collision.");
    }

    @Test
    public void testRemoveLineIfCollidingWithBall_DoesNotRemoveLine() {
        // Set up a scenario where the ball is not colliding with the line
        LineSegment segment = new LineSegment(0, 0, 100, 100);
        line.addSegment(segment);

        // Mock the ball's collision method to return false
        when(ballMock.isCollidingWithLine(segment)).thenReturn(false);

        // Create a list of lines and add the current line
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        // Call the removeLineIfCollidingWithBall method
        line.removeLineIfCollidingWithBall(ballMock, lines);

        // Verify that the line was not removed from the list
        assertEquals(1, lines.size(), "The line should not be removed if there is no collision.");
    }
}
