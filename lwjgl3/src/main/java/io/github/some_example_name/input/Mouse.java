package io.github.some_example_name.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import java.util.HashMap;
import java.util.Map;

public class Mouse {
    private Vector2 position;
    private Map<Integer, Boolean> buttonStates;
    private Map<Integer, Boolean> previousButtonStates;
    private float scrollDelta;
    
    public Mouse() {
        position = new Vector2();
        buttonStates = new HashMap<>();
        previousButtonStates = new HashMap<>();
        scrollDelta = 0;
    }
    
    public void update() {
        // Update position
        position.set(Gdx.input.getX(), Gdx.input.getY());
        
        // Copy button states
        previousButtonStates.clear();
        previousButtonStates.putAll(buttonStates);
        
        // Update button states (0=left, 1=right, 2=middle)
        buttonStates.put(0, Gdx.input.isButtonPressed(0));
        buttonStates.put(1, Gdx.input.isButtonPressed(1));
        buttonStates.put(2, Gdx.input.isButtonPressed(2));
        
        // Reset scroll (will be set by InputProcessor if needed)
        scrollDelta = 0;
    }
    
    public Vector2 getPosition() {
        return position.cpy();
    }
    
    public boolean isButtonHeld(int button) {
        return buttonStates.getOrDefault(button, false);
    }
    
    public boolean isButtonClicked(int button) {
        return buttonStates.getOrDefault(button, false) && 
               !previousButtonStates.getOrDefault(button, false);
    }
    
    public boolean isButtonReleased(int button) {
        return !buttonStates.getOrDefault(button, false) && 
               previousButtonStates.getOrDefault(button, false);
    }
    
    public float getScrollDelta() {
        return scrollDelta;
    }
    
    public void setScrollDelta(float delta) {
        this.scrollDelta = delta;
    }
}
