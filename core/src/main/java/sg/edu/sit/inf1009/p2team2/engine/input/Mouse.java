package sg.edu.sit.inf1009.p2team2.engine.input;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;

public class Mouse {
    private final Vector2 position = new Vector2();
    private final Map<Integer, Boolean> buttonStates = new HashMap<>();
    private final Map<Integer, Boolean> previousButtonStates = new HashMap<>();
    private float scrollDelta;

    public Mouse() {
    }

    public void update() {
        // TODO(HongYih): sync position/buttonStates/scrollDelta from the platform input (e.g., Gdx.input).
        previousButtonStates.clear();
        previousButtonStates.putAll(buttonStates);
        scrollDelta = 0f;
    }

    public Vector2 getPosition() {
        return position;
    }

    public boolean isButtonHeld(int button) {
        return buttonStates.getOrDefault(button, false);
    }

    public boolean isButtonClicked(int button) {
        return buttonStates.getOrDefault(button, false) && !previousButtonStates.getOrDefault(button, false);
    }

    public boolean isButtonReleased(int button) {
        return !buttonStates.getOrDefault(button, false) && previousButtonStates.getOrDefault(button, false);
    }

    public float getScrollDelta() {
        return scrollDelta;
    }

    public void setScrollDelta(float delta) {
        this.scrollDelta = delta;
    }
}

