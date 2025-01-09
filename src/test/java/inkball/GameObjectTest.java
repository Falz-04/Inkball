package inkball;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;

class GameObjectTest {

    private GameObject obj;  // An instance of a subclass of GameObject

    @BeforeEach
    public void setUp() {
        // Since GameObject is abstract, we create an anonymous subclass for testing.
        obj = new GameObject(10, 20) {};
    }

    @Test
    public void testGetX() {
        // Test that getX() returns the correct x-coordinate.
        assertEquals(10, obj.getX(), "The x-coordinate should be 10.");
    }

    @Test
    public void testGetY() {
        // Test that getY() returns the correct y-coordinate.
        assertEquals(20, obj.getY(), "The y-coordinate should be 20.");
    }

    @Test
    public void testSetX() {
        // Test that setX() updates the x-coordinate.
        obj.setX(30);
        assertEquals(30, obj.getX(), "The x-coordinate should be updated to 30.");
    }

    @Test
    public void testSetY() {
        // Test that setY() updates the y-coordinate.
        obj.setY(40);
        assertEquals(40, obj.getY(), "The y-coordinate should be updated to 40.");
    }

    @Test
    public void testSetSprite() {
        // Test that setSprite() correctly sets the sprite.
        PImage spriteMock = mock(PImage.class);  // Mock a PImage object
        obj.setSprite(spriteMock);
        assertEquals(spriteMock, obj.sprite, "The sprite should be set correctly.");
    }

    @Test
    public void testDraw() {
        // Test that the draw method calls the app's image method with correct parameters.
        PApplet appMock = mock(PApplet.class);
        PImage spriteMock = mock(PImage.class);  // Mock a PImage object

        // Set the sprite
        obj.setSprite(spriteMock);

        // Call the draw method
        obj.draw(appMock);

        // Verify that app.image was called with the correct parameters
        // We use Mockito's verify method to check if the image method was called with spriteMock, 10, and 20.
        org.mockito.Mockito.verify(appMock).image(spriteMock, 10, 20);
    }
}
