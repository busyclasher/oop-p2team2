package sg.edu.sit.inf1009.p2team2.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import java.util.HashMap;
import java.util.Map;

/**
 * KEYBOARD - Abstract Engine
 * DEFINITIVE FIX for the alternating selection bug
 * 
 * The bug was: isKeyDown() was updating currentKeys DURING checks,
 * causing state to be corrupted when isKeyPressed() was called multiple times.
 * 
 * Solution: Update ALL key states ONCE in update(), never during checks.
 */
public class Keyboard {
    
    private Map<Integer, Boolean> currentKeys;
    private Map<Integer, Boolean> previousKeys;
    
    public Keyboard() {
        this.currentKeys = new HashMap<>();
        this.previousKeys = new HashMap<>();
        System.out.println("[Keyboard] Initialized");
    }
    
    /**
     * Update keyboard state - MUST be called once per frame BEFORE any checks
     */
    public void update() {
        // Save previous state
        previousKeys.clear();
        previousKeys.putAll(currentKeys);
        
        // CRITICAL: Clear current state before rebuilding
        currentKeys.clear();
        
        // Read ALL keys we care about from libGDX ONCE per frame
        // This prevents state corruption from multiple checks
        updateKey(Input.Keys.W);
        updateKey(Input.Keys.A);
        updateKey(Input.Keys.S);
        updateKey(Input.Keys.D);
        updateKey(Input.Keys.UP);
        updateKey(Input.Keys.DOWN);
        updateKey(Input.Keys.LEFT);
        updateKey(Input.Keys.RIGHT);
        updateKey(Input.Keys.SPACE);
        updateKey(Input.Keys.ENTER);
        updateKey(Input.Keys.ESCAPE);
        updateKey(Input.Keys.SHIFT_LEFT);
        updateKey(Input.Keys.SHIFT_RIGHT);
        updateKey(Input.Keys.CONTROL_LEFT);
        updateKey(Input.Keys.CONTROL_RIGHT);
        updateKey(Input.Keys.ALT_LEFT);
        updateKey(Input.Keys.ALT_RIGHT);
        updateKey(Input.Keys.TAB);
        updateKey(Input.Keys.BACKSPACE);
        updateKey(Input.Keys.F);
        updateKey(Input.Keys.M);
        updateKey(Input.Keys.PLUS);
        updateKey(Input.Keys.MINUS);
        updateKey(Input.Keys.EQUALS);
    }
    
    /**
     * Helper to update a single key's state from libGDX
     */
    private void updateKey(int keyCode) {
        currentKeys.put(keyCode, Gdx.input.isKeyPressed(keyCode));
    }
    
    /**
     * Check if a key is currently held down
     * 
     * IMPORTANT: This now ONLY reads from currentKeys, it does NOT update state!
     */
    public boolean isKeyDown(int keyCode) {
        // If key is tracked, return its state
        if (currentKeys.containsKey(keyCode)) {
            return currentKeys.get(keyCode);
        }
        
        // If key is NOT tracked, read directly from libGDX
        // (for keys we don't commonly use)
        boolean state = Gdx.input.isKeyPressed(keyCode);
        return state;
    }
    
    /**
     * Check if a key was just pressed this frame
     * 
     * Returns true only if:
     * - Key is DOWN this frame
     * - Key was NOT down last frame
     */
    public boolean isKeyPressed(int keyCode) {
        boolean currentlyDown = isKeyDown(keyCode);
        boolean wasDown = previousKeys.getOrDefault(keyCode, false);
        boolean result = currentlyDown && !wasDown;
        
        // Debug logging for arrow keys
        if (result && (keyCode == Input.Keys.UP || keyCode == Input.Keys.DOWN)) {
            System.out.println("[Keyboard] isKeyPressed(" + getKeyName(keyCode) + ") = true");
            System.out.println("  currentlyDown=" + currentlyDown + ", wasDown=" + wasDown);
        }
        
        return result;
    }
    
    /**
     * Check if a key was just released this frame
     */
    public boolean isKeyReleased(int keyCode) {
        boolean currentlyDown = isKeyDown(keyCode);
        boolean wasDown = previousKeys.getOrDefault(keyCode, false);
        return !currentlyDown && wasDown;
    }
    
    /**
     * Check if any key is pressed
     */
    public boolean isAnyKeyPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.ANY_KEY);
    }
    
    /**
     * Reset all keyboard state
     */
    public void reset() {
        currentKeys.clear();
        previousKeys.clear();
    }
    
    /**
     * Get human-readable key name for debugging
     */
    private String getKeyName(int keyCode) {
        if (keyCode == Input.Keys.UP) return "UP";
        if (keyCode == Input.Keys.DOWN) return "DOWN";
        if (keyCode == Input.Keys.LEFT) return "LEFT";
        if (keyCode == Input.Keys.RIGHT) return "RIGHT";
        if (keyCode == Input.Keys.W) return "W";
        if (keyCode == Input.Keys.A) return "A";
        if (keyCode == Input.Keys.S) return "S";
        if (keyCode == Input.Keys.D) return "D";
        if (keyCode == Input.Keys.SPACE) return "SPACE";
        if (keyCode == Input.Keys.ENTER) return "ENTER";
        return "KEY_" + keyCode;
    }
}