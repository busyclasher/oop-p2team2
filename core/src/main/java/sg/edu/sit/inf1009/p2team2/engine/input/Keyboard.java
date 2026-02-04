package sg.edu.sit.inf1009.p2team2.engine.input;

import java.util.HashMap;
import java.util.Map;

public class Keyboard {
    private final Map<Integer, Boolean> currentKeys = new HashMap<>();
    private final Map<Integer, Boolean> previousKeys = new HashMap<>();

    public Keyboard() {
    }

    public void update() {
        // TODO(HongYih): sync currentKeys from the platform input (e.g., Gdx.input).
        previousKeys.clear();
        previousKeys.putAll(currentKeys);
    }

    public boolean isKeyHeld(int keyCode) {
        return currentKeys.getOrDefault(keyCode, false);
    }

    public boolean isKeyPressed(int keyCode) {
        return currentKeys.getOrDefault(keyCode, false) && !previousKeys.getOrDefault(keyCode, false);
    }

    public boolean isKeyReleased(int keyCode) {
        return !currentKeys.getOrDefault(keyCode, false) && previousKeys.getOrDefault(keyCode, false);
    }

    public boolean isAnyKeyPressed() {
        for (boolean pressed : currentKeys.values()) {
            if (pressed) return true;
        }
        return false;
    }

    public void reset() {
        currentKeys.clear();
        previousKeys.clear();
    }
}

