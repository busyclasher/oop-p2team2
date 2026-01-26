package io.github.some_example_name.input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import java.util.HashMap;
import java.util.Map;

public class Keyboard {
    private Map<Integer, Boolean> currentKeys;
    private Map<Integer, Boolean> previousKeys;
    
    public Keyboard() {
        currentKeys = new HashMap<>();
        previousKeys = new HashMap<>();
    }
    
    public void update() {
        // Copy current to previous before updating
        previousKeys.clear();
        previousKeys.putAll(currentKeys);
        
        // Update current state (you'd poll all keys you care about)
        // For efficiency, only check keys you use in your game
        currentKeys.put(Keys.W, Gdx.input.isKeyPressed(Keys.W));
        currentKeys.put(Keys.A, Gdx.input.isKeyPressed(Keys.A));
        currentKeys.put(Keys.S, Gdx.input.isKeyPressed(Keys.S));
        currentKeys.put(Keys.D, Gdx.input.isKeyPressed(Keys.D));
        currentKeys.put(Keys.SPACE, Gdx.input.isKeyPressed(Keys.SPACE));
        currentKeys.put(Keys.ESCAPE, Gdx.input.isKeyPressed(Keys.ESCAPE));
        // Add more keys as needed
    }
    
    public boolean isKeyHeld(int keyCode) {
        return currentKeys.getOrDefault(keyCode, false);
    }
    
    public boolean isKeyPressed(int keyCode) {
        // Just pressed = true now, false before
        return currentKeys.getOrDefault(keyCode, false) && 
               !previousKeys.getOrDefault(keyCode, false);
    }
    
    public boolean isKeyReleased(int keyCode) {
        // Just released = false now, true before
        return !currentKeys.getOrDefault(keyCode, false) && 
               previousKeys.getOrDefault(keyCode, false);
    }
}
