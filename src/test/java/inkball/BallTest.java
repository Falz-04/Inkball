package inkball;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import org.mockito.Mockito;

class BallTest {

    private Ball ball;
    private PApplet appMock;  // We still need to mock this for non-drawing functions

    @BeforeEach
    public void setUp() {
        // Mock the PApplet
        appMock = Mockito.mock(PApplet.class);

        // Mock the loadImage method to return a non-null PImage
        Mockito.when(appMock.loadImage(Mockito.anyString())).thenReturn(Mockito.mock(PImage.class));
        ball = new Ball(10, 10, appMock, "ball1");
    }

    @Test
    // Test that the constructor throws illegal argument exception for null colour
    public void testConstructorThrowsExceptionForNullColour() {
        assertThrows(IllegalArgumentException.class, () -> new Ball(10, 10, appMock, null));
    }

    @Test
    // Test that the constructor throws illegal argument exception for null app
    public void testConstructorInitializesCorrectlyWithNullApp() {
        Ball ball = new Ball(10, 10, null, "ball1");
        assertEquals(10 * App.CELLSIZE + (App.CELLSIZE / 2), ball.getPosX(), 0.01);
        assertEquals(10 * App.CELLSIZE + App.TOPBAR + (App.CELLSIZE / 2), ball.getPosY(), 0.01);
        assertTrue(ball.isActive());
        assertEquals("orange", ball.getColour());
    }

    @Test
    // Test that the constructor throws illegal argument exception for invalid colour
    public void testConstructorThrowsExceptionForInvalidColour() {
        assertThrows(IllegalArgumentException.class, () -> new Ball(10, 10, appMock, "invalidColour"));
    }

    @Test
    // Test that the constructor randomly initialises the velocity components as 1 or -1
    public void testRandomVelocityInitialization() {
        Ball ball = new Ball(10, 10, appMock, "ball1");
        assertTrue(ball.getVelocityX() == 1 || ball.getVelocityX() == -1);
        assertTrue(ball.getVelocityY() == 1 || ball.getVelocityY() == -1);
    }

    @Test
    // Test that the constructor throws illegal argument exception when ball image is not found
    public void testConstructorThrowsExceptionWhenBallImageNotFound() {
        // Mocking appMock to return null when loadImage is called
        when(appMock.loadImage(anyString())).thenReturn(null);
        
        assertThrows(IllegalArgumentException.class, () -> new Ball(10, 10, appMock, "ball"));
    }

    @Test
    // Test that the ball skips the image loading when app is null
    public void testConstructorWithNullAppSkipsImageLoading() {
        Ball ball = new Ball(10, 10, null, "ball1");
        assertNotNull(ball); // Ensure the ball is initialized correctly
    }

    @Test
    // Test that the constructor initializes the ball with the correct image scale
    public void testConstructorInitializesScaleCorrectly() {
        Ball ball = new Ball(10, 10, appMock, "ball1");
        assertEquals(1.0f, ball.getScale(), 0.01);
    }



    @Test
    // Test that the constructor initializes correctly
    public void testConstructorInitializesCorrectly() {
        Ball ball = new Ball(10, 10, appMock, "ball1");
        assertEquals(10 * App.CELLSIZE + (App.CELLSIZE / 2), ball.getPosX(), 0.01);
        assertEquals(10 * App.CELLSIZE + App.TOPBAR + (App.CELLSIZE / 2), ball.getPosY(), 0.01);
        assertTrue(ball.isActive());
        assertEquals("orange", ball.getColour());
    }

    @Test
    // Test the move function, which updates the ball's position
    public void testMove() {
        PVector initialPosition = new PVector(ball.getPosX(), ball.getPosY());
        
        // Call move to update the ball's position
        ball.move();
        
        PVector newPosition = new PVector(ball.getPosX(), ball.getPosY());

        // Assert the ball has moved
        assertNotEquals(initialPosition, newPosition, "Ball should move after calling move.");
    }

    @Test
    // Test the collision of the balls with the wall
    public void testCollisionWithWall() {
        LineSegment[] wallHitbox = new LineSegment[] {
            new LineSegment(0, 0, 10, 0),  // Example wall hitbox
            new LineSegment(0, 10, 10, 10),
            new LineSegment(0, 0, 0, 10),
            new LineSegment(10, 0, 10, 10)
        };
        Ball ball = new Ball(5, 5, appMock, "ball1");
        boolean collided = ball.checkCollisionWithWall(wallHitbox, appMock);
        assertTrue(collided);  // Assuming the ball collides with the wall
    }


    @Test
    // Test the is the ball detects a collision with the wall
    public void testCheckCollisionWithWall() {
        LineSegment[] wallHitbox = new LineSegment[] {
            new LineSegment(0, 0, 100, 0),  // Horizontal wall
            new LineSegment(0, 0, 0, 100),  // Vertical wall
        };

        ball.setVelocity(1, 1);  // Set the ball moving towards the wall
        
        boolean collision = ball.checkCollisionWithWall(wallHitbox, appMock);
        
        // Assuming the ball is close enough to collide
        assertTrue(collision, "Ball should detect collision with wall");
    }

    @Test
    // Test the ball does not detect a collision with a far-away wall
    public void testNoCollisionWithWall() {
        LineSegment[] wallHitbox = new LineSegment[] {
            new LineSegment(200, 200, 300, 200),  // Wall far from the ball
        };

        ball.setVelocity(1, 1);
        boolean collision = ball.checkCollisionWithWall(wallHitbox, appMock);
        
        // Ball should not detect collision with a far-away wall
        assertFalse(collision, "Ball should not detect collision with far-away wall");
    }


    @Test
    // Test the ball detects a collision with the border
    public void testBorderCollision() {
        ball.setVelocity(10, 10); // Set velocity towards the border

        // Move the ball multiple times to simulate it reaching the border
        for (int i = 0; i < 200; i++) {
            ball.move();
        }

        // Check if the ball's velocity reversed after hitting the border
        assertTrue(ball.getVelocityX() < 0 || ball.getVelocityY() < 0, "Ball should reverse direction after hitting border.");
    }

    @Test
    // Test that the ball can experience the force from the hole 
    public void testApplyForce() {
        PVector initialVelocity = new PVector(ball.getVelocityX(), ball.getVelocityY());
        
        // Apply some force to the ball
        ball.applyForce(new PVector(2, 3));
        
        // Check if the velocity has changed
        PVector newVelocity = new PVector(ball.getVelocityX(), ball.getVelocityY());
        assertNotEquals(initialVelocity, newVelocity, "Ball velocity should change after applying force.");
    }

    @Test
    // Test that the ball can change its colour once it hits a wall tile
    public void testChangeColour() {
        Ball ball = new Ball(10, 10, appMock, "ball0");
        Tile mockTile = mock(Tile.class);

        when(mockTile.getType()).thenReturn(Tile.TileType.WALL1); 
        ball.changeColour(mockTile);
        assertEquals("orange", ball.getColour());

        when(mockTile.getType()).thenReturn(Tile.TileType.WALL2); 
        ball.changeColour(mockTile);
        assertEquals("blue", ball.getColour());

        when(mockTile.getType()).thenReturn(Tile.TileType.WALL3); 
        ball.changeColour(mockTile);
        assertEquals("green", ball.getColour());

        when(mockTile.getType()).thenReturn(Tile.TileType.WALL4); 
        ball.changeColour(mockTile);
        assertEquals("yellow", ball.getColour());

        when(mockTile.getType()).thenReturn(Tile.TileType.WALL0);
        ball.changeColour(mockTile);
        assertEquals("yellow", ball.getColour());

    }


    @Test
    // Test that the getColour function returns the actual colour of the ball
    public void testGetColour() {
        Ball ball0 = new Ball(10, 10, appMock, "ball0");
        Ball ball1 = new Ball(10, 10, appMock, "ball1");
        Ball ball2 = new Ball(10, 10, appMock, "ball2");
        Ball ball3 = new Ball(10, 10, appMock, "ball3");
        Ball ball4 = new Ball(10, 10, appMock, "ball4");

        // Valid ball colors
        assertEquals("grey", ball0.getColour());
        assertEquals("orange", ball1.getColour());
        assertEquals("blue", ball2.getColour());
        assertEquals("green", ball3.getColour());
        assertEquals("yellow", ball4.getColour());

        // Test for invalid ball color and expect exception
        assertThrows(IllegalArgumentException.class, () -> new Ball(10, 10, appMock, "ball5"));
    }


    @Test
    // Test that the ball can be set to active or inactive
    public void testSetActive() {
        ball.setActive(false);
        assertFalse(ball.isActive());
        ball.setActive(true);
        assertTrue(ball.isActive());
    }

    @Test
    // Test that the balls scale can be set
    public void testSetScale() {
        ball.setScale(2);
        assertEquals(2, ball.getScale());
    }

    @Test
    // Test that we can get the balls radius
    public void testGetRadius() {
        assertEquals(16, ball.getRadius());
    }

    @Test
    // Test that the ball will change its trajectory when it hits a wall
    public void testReflectBallWall() {
        Ball ball = new Ball(10, 10, appMock, "ball1");
    
        // Mock hitbox for a wall tile
        PVector[] hitbox = new PVector[4];
        hitbox[0] = new PVector(0, 0);
        hitbox[1] = new PVector(10, 0);
        hitbox[2] = new PVector(10, 10);
        hitbox[3] = new PVector(0, 10);
    
        ball.setVelocity(1, 1);  // Moving towards the wall
        ball.reflectBallWall(hitbox);
        
        // After reflection, velocity should have changed direction
        assertEquals(-1, ball.getVelocityX(), 0.01);
        assertEquals(-1, ball.getVelocityY(), 0.01);
    }

    @Test
    // Test that the ball can calculate the normal vector of a line
    public void testCalculateNormalVector() {
        Ball ball = new Ball(10, 10, appMock, "ball1");
        LineSegment lineSegment = new LineSegment(0, 0, 10, 0);

        PVector normal = ball.calculateNormalVector(lineSegment);
        assertEquals(0, normal.x, 0.01);  // Normal should be along the Y-axis
        assertEquals(1, normal.y, 0.01);
    }

    @Test
    // Test that the ball can reflect off a line
    public void testReflectBallLine() {
        Ball ball = new Ball(10, 10, appMock, "ball1");

        PVector[] hitbox = new PVector[4];
        hitbox[0] = new PVector(0, 0);
        hitbox[2] = new PVector(10, 10);  // Mocking a diagonal line

        ball.setVelocity(1, 1);  // Moving towards the line
        ball.reflectBallLine(hitbox);

        // After reflection, velocity should have changed direction
        assertEquals(-1, ball.getVelocityX(), 0.01);
        assertEquals(-1, ball.getVelocityY(), 0.01);
    }

    @Test
    // Test that the ball can detect a collision with a line
    public void testIsCollidingWithLine() {
        Ball ball = new Ball(10, 10, appMock, "ball1");
        LineSegment line = new LineSegment(0, 0, 10, 0);  // A horizontal line

        // Test a collision case
        ball.setVelocity(1, 0);  // Moving towards the line
        assertTrue(ball.isCollidingWithLine(line));

        // Test a no collision case
        ball.setVelocity(0, 1);  // Moving parallel to the line
        assertFalse(ball.isCollidingWithLine(line));
    }


    @Test
    // Test that the ball can detect a collision with a line
    public void testNotCollidingWithLine() {
        LineSegment line = new LineSegment(0, 0, 10, 0);  // Horizontal line
        PVector futurePosition = new PVector(100, 100);  // Far from the line
        float radius = ball.getRadius();

        boolean collision = ball.isCollidingWithLine(line, futurePosition, radius);

        assertFalse(collision, "Ball should not detect collision when far from the line");
    }

    @Test
    // Test that the ball can be drawn on the board objcet
    public void testDraw() {
        // Assuming ball is active
        ball.setActive(true);
        
        // Simulate drawing
        ball.draw(appMock);

        // Verify that the PApplet `image` method was called with the correct parameters
        Mockito.verify(appMock).image(Mockito.any(PImage.class), Mockito.anyFloat(), Mockito.anyFloat(), Mockito.anyFloat(), Mockito.anyFloat());
    }


    @Test
    // Test that the balls tradectoryt can be updated
    public void testUpdateTrajectory() {
        LineSegment line = new LineSegment(0, 0, 10, 0);  // Horizontal line
        ball.setVelocity(1, -1);  // Ball moving towards the line

        ball.updateTrajectory(line);

        // Assert that the velocity has been updated (reflected)
        assertEquals(1, ball.getVelocityX(), 0.01);
        assertEquals(1, ball.getVelocityY(), 0.01);  // Velocity should have flipped
    }


    @Test
    // Test that the ball can detect a collision with a line
    public void testGetFuturePosition() {
        ball.setVelocity(5, 7);
        PVector futurePosition = ball.getFuturePosition();
    
        // Calculate expected future position
        float expectedX = ball.getPosX() + 5;
        float expectedY = ball.getPosY() + 7;
    
        assertEquals(expectedX, futurePosition.x, 0.01);
        assertEquals(expectedY, futurePosition.y, 0.01);
    }
}
