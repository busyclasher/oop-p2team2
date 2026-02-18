package sg.edu.sit.inf1009.p2team2.engine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.output.Renderer;

/**
 * Minimal slider model (e.g., volume).
 */
public class Slider {
    private float min;
    private float max;
    private float value;
    private final Vector2 position;
    private float width;

    public Slider(float min, float max, float value) {
        this.min = min;
        this.max = Math.max(min, max);
        this.position = new Vector2();
        this.width = 160f;
        setValue(value);
    }

    public Slider(Vector2 position) {
        this(0f, 100f, 50f);
        if (position != null) {
            this.position.set(position);
        }
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
        if (max < min) {
            max = min;
        }
        setValue(value);
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = Math.max(max, min);
        setValue(value);
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = Math.max(min, Math.min(max, value));
    }

    public void update() {
        // Slider is data-only in abstract engine skeleton.
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void render(Renderer renderer, boolean selected) {
        if (renderer == null) {
            return;
        }

        Color trackColor = selected ? Color.YELLOW : Color.LIGHT_GRAY;
        Color fillColor = selected ? Color.ORANGE : Color.WHITE;
        float normalized = max <= min ? 0f : (value - min) / (max - min);
        float knobX = position.x + (normalized * width);

        renderer.drawLine(new Vector2(position.x, position.y), new Vector2(position.x + width, position.y), trackColor, 2f);
        renderer.drawCircle(new Vector2(knobX, position.y), 6f, fillColor, true);
    }
}
