package sg.edu.sit.inf1009.p2team2.engine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.output.Renderer;

/**
 * Minimal toggle model (e.g., fullscreen on/off).
 */
public class Toggle {
    private boolean on;
    private final Vector2 position;

    public Toggle(boolean on) {
        this.on = on;
        this.position = new Vector2();
    }

    public Toggle(int x, int y) {
        this(false);
        this.position.set(x, y);
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public void toggle() {
        this.on = !this.on;
    }

    public void setValue(boolean value) {
        setOn(value);
    }

    public void update() {
        // Toggle is data-only in abstract engine skeleton.
    }

    public void render(Renderer renderer, boolean selected) {
        if (renderer == null) {
            return;
        }

        String state = on ? "ON" : "OFF";
        Color color = on ? Color.GREEN : Color.RED;
        if (selected) {
            color = on ? Color.LIME : Color.SCARLET;
        }

        renderer.drawText("[" + state + "]", position, "default", color);
    }

    public float getValue() {
        return on ? 1f : 0f;
    }

    public boolean isEnabled() {
        return on;
    }
}
