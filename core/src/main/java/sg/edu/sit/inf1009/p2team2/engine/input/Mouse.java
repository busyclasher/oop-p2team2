package sg.edu.sit.inf1009.p2team2.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import java.util.HashMap;
import java.util.Map;

/**
 * MOUSE - Abstract Engine
 * Tracks mouse state across frames.
 * 
 * Tracks:
 * - Position
 * - Button states
 * - Scroll delta
 */
public class Mouse {
    @FunctionalInterface
    public interface CoordinateTransformer {
        Vector2 toWorld(float screenX, float screenY);
    }
    
    // Mouse position
    private Vector2 position;
    
    // Current frame button states
    private Map<Integer, Boolean> buttonStates;
    
    // Previous frame button states
    private Map<Integer, Boolean> previousButtonStates;
    
    // Scroll wheel delta
    private float scrollDelta;
    private CoordinateTransformer coordinateTransformer;
    
    /**
     * Constructor
     */
    public Mouse() {
        this.position = new Vector2();
        this.buttonStates = new HashMap<>();
        this.previousButtonStates = new HashMap<>();
        this.scrollDelta = 0;
        this.coordinateTransformer = null;
    }
    
    /**
     * Update mouse state - call once per frame
     */
    public void update() {
        // Copy current to previous
        previousButtonStates.clear();
        previousButtonStates.putAll(buttonStates);
        
        float screenX = Gdx.input.getX();
        float screenY = Gdx.input.getY();
        if (coordinateTransformer != null) {
            position.set(coordinateTransformer.toWorld(screenX, screenY));
        } else {
            float screenHeight = Gdx.graphics.getHeight();
            position.set(screenX, screenHeight - screenY);
        }
        
        // Scroll delta is handled by libGDX input processor
        // (will be set via setScrollDelta if using InputProcessor)
    }
    
    /**
     * Get current mouse position
     * 
     * @return Vector2 with x, y coordinates
     */
    public Vector2 getPosition() {
        return position.cpy(); // Return copy to prevent external modification
    }
    
    /**
     * Check if a mouse button is currently held down
     * 
     * @param button Button code (0 = left, 1 = right, 2 = middle)
     * @return true if button is currently down
     */
    public boolean isButtonDown(int button) {
        return Gdx.input.isButtonPressed(button);
    }
    
    /**
     * Check if a mouse button was just clicked THIS frame
     * 
     * @param button Button code
     * @return true if button was just pressed
     */
    public boolean isButtonPressed(int button) {
        boolean currentlyDown = Gdx.input.isButtonPressed(button);
        boolean wasDown = previousButtonStates.getOrDefault(button, false);
        
        // Update current state
        buttonStates.put(button, currentlyDown);
        
        // Just pressed = down now, but not before
        return currentlyDown && !wasDown;
    }
    
    /**
     * Check if a mouse button was just released THIS frame
     * 
     * @param button Button code
     * @return true if button was just released
     */
    public boolean isButtonReleased(int button) {
        boolean currentlyDown = Gdx.input.isButtonPressed(button);
        boolean wasDown = previousButtonStates.getOrDefault(button, false);
        
        // Update current state
        buttonStates.put(button, currentlyDown);
        
        // Just released = up now, but was down before
        return !currentlyDown && wasDown;
    }
    
    /**
     * Get scroll wheel delta
     * 
     * @return Scroll amount (positive = up, negative = down)
     */
    public float getScrollDelta() {
        return scrollDelta;
    }
    
    /**
     * Set scroll delta (called by input processor)
     * 
     * @param delta Scroll amount
     */
    public void setScrollDelta(float delta) {
        this.scrollDelta = delta;
    }

    public void setCoordinateTransformer(CoordinateTransformer coordinateTransformer) {
        this.coordinateTransformer = coordinateTransformer;
    }
    
    /**
     * Reset mouse state
     */
    public void reset() {
        position.set(0, 0);
        buttonStates.clear();
        previousButtonStates.clear();
        scrollDelta = 0;
    }
}
