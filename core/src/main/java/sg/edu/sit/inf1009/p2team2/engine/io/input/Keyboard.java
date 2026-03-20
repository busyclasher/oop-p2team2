package sg.edu.sit.inf1009.p2team2.engine.io.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Keyboard state tracker used by scenes and managers.
 */
public class Keyboard {
    private static final int[] TRACKED_KEYS = buildTrackedKeys();

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

    private static int[] buildTrackedKeys() {
        List<Integer> keys = new ArrayList<>();

        // Navigation and common game controls.
        keys.add(Input.Keys.W);
        keys.add(Input.Keys.A);
        keys.add(Input.Keys.S);
        keys.add(Input.Keys.D);
        keys.add(Input.Keys.E);
        keys.add(Input.Keys.R);
        keys.add(Input.Keys.UP);
        keys.add(Input.Keys.DOWN);
        keys.add(Input.Keys.LEFT);
        keys.add(Input.Keys.RIGHT);
        keys.add(Input.Keys.SPACE);
        keys.add(Input.Keys.ENTER);
        keys.add(Input.Keys.ESCAPE);
        keys.add(Input.Keys.SHIFT_LEFT);
        keys.add(Input.Keys.SHIFT_RIGHT);
        keys.add(Input.Keys.CONTROL_LEFT);
        keys.add(Input.Keys.CONTROL_RIGHT);
        keys.add(Input.Keys.ALT_LEFT);
        keys.add(Input.Keys.ALT_RIGHT);
        keys.add(Input.Keys.TAB);
        keys.add(Input.Keys.BACKSPACE);
        keys.add(Input.Keys.F);
        keys.add(Input.Keys.M);
        keys.add(Input.Keys.C);
        keys.add(Input.Keys.P);
        keys.add(Input.Keys.PLUS);
        keys.add(Input.Keys.MINUS);
        keys.add(Input.Keys.EQUALS);
        keys.add(Input.Keys.LEFT_BRACKET);
        keys.add(Input.Keys.RIGHT_BRACKET);

        // Number row.
        for (int keyCode = Input.Keys.NUM_0; keyCode <= Input.Keys.NUM_9; keyCode++) {
            keys.add(keyCode);
        }

        // Letter keys for text-style input scenes.
        for (int keyCode = Input.Keys.A; keyCode <= Input.Keys.Z; keyCode++) {
            keys.add(keyCode);
        }

        // Numpad digits.
        for (int keyCode = Input.Keys.NUMPAD_0; keyCode <= Input.Keys.NUMPAD_9; keyCode++) {
            keys.add(keyCode);
        }

        int[] result = new int[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            result[i] = keys.get(i);
        }
        return result;
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
