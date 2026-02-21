package sg.edu.sit.inf1009.p2team2.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import java.util.HashMap;
import java.util.Map;

/**
 * Keyboard state tracker used by scenes and managers.
 */
public class Keyboard {
    private static final int[] TRACKED_KEYS = {
        Input.Keys.W, Input.Keys.A, Input.Keys.S, Input.Keys.D,
        Input.Keys.E, Input.Keys.R,
        Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT,
        Input.Keys.SPACE, Input.Keys.ENTER, Input.Keys.ESCAPE,
        Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT,
        Input.Keys.CONTROL_LEFT, Input.Keys.CONTROL_RIGHT,
        Input.Keys.ALT_LEFT, Input.Keys.ALT_RIGHT,
        Input.Keys.TAB, Input.Keys.BACKSPACE,
        Input.Keys.F, Input.Keys.M, Input.Keys.C, Input.Keys.P,
        Input.Keys.PLUS, Input.Keys.MINUS, Input.Keys.EQUALS,
        Input.Keys.LEFT_BRACKET, Input.Keys.RIGHT_BRACKET,
        Input.Keys.NUM_1, Input.Keys.NUM_2, Input.Keys.NUM_3, Input.Keys.NUM_4, Input.Keys.NUM_5
    };

    private final Map<Integer, Boolean> currentKeys;
    private final Map<Integer, Boolean> previousKeys;

    public Keyboard() {
        this.currentKeys = new HashMap<>();
        this.previousKeys = new HashMap<>();
    }

    /**
     * Updates keyboard states once per frame.
     */
    public void update() {
        previousKeys.clear();
        previousKeys.putAll(currentKeys);

        currentKeys.clear();

        for (int keyCode : TRACKED_KEYS) {
            updateKey(keyCode);
        }
    }

    private void updateKey(int keyCode) {
        currentKeys.put(keyCode, Gdx.input != null && Gdx.input.isKeyPressed(keyCode));
    }

    public boolean isKeyDown(int keyCode) {
        if (currentKeys.containsKey(keyCode)) {
            return currentKeys.get(keyCode);
        }

        boolean state = Gdx.input != null && Gdx.input.isKeyPressed(keyCode);
        currentKeys.put(keyCode, state);
        return state;
    }

    public boolean isKeyHeld(int keyCode) {
        return isKeyDown(keyCode);
    }
    
    public boolean isKeyPressed(int keyCode) {
        boolean currentlyDown = isKeyDown(keyCode);
        boolean wasDown = previousKeys.getOrDefault(keyCode, false);
        return currentlyDown && !wasDown;
    }

    public boolean isKeyReleased(int keyCode) {
        boolean currentlyDown = isKeyDown(keyCode);
        boolean wasDown = previousKeys.getOrDefault(keyCode, false);
        return !currentlyDown && wasDown;
    }

    public boolean isAnyKeyPressed() {
        return Gdx.input != null && Gdx.input.isKeyPressed(Input.Keys.ANY_KEY);
    }

    public void reset() {
        currentKeys.clear();
        previousKeys.clear();
    }
}
