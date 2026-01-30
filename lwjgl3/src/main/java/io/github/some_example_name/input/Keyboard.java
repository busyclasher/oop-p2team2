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
        // Copy current to previous
        previousKeys.clear();
        previousKeys.putAll(currentKeys);
        
        // Poll current state for all keys we care about
        currentKeys.put(Keys.W, Gdx.input.isKeyPressed(Keys.W));
        currentKeys.put(Keys.A, Gdx.input.isKeyPressed(Keys.A));
        currentKeys.put(Keys.S, Gdx.input.isKeyPressed(Keys.S));
        currentKeys.put(Keys.D, Gdx.input.isKeyPressed(Keys.D));
        currentKeys.put(Keys.SPACE, Gdx.input.isKeyPressed(Keys.SPACE));
        currentKeys.put(Keys.ESCAPE, Gdx.input.isKeyPressed(Keys.ESCAPE));
        currentKeys.put(Keys.LEFT, Gdx.input.isKeyPressed(Keys.LEFT));
        currentKeys.put(Keys.RIGHT, Gdx.input.isKeyPressed(Keys.RIGHT));
        currentKeys.put(Keys.UP, Gdx.input.isKeyPressed(Keys.UP));
        currentKeys.put(Keys.DOWN, Gdx.input.isKeyPressed(Keys.DOWN));
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
    
    public boolean isAnyKeyPressed() {
        return Gdx.input.isKeyPressed(Keys.ANY_KEY);
    }
    
    public void reset() {
        currentKeys.clear();
        previousKeys.clear();
    }
}
