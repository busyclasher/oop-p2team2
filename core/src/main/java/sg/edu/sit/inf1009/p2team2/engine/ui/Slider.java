package sg.edu.sit.inf1009.p2team2.engine.ui;

import com.badlogic.gdx.math.Vector2;

import sg.edu.sit.inf1009.p2team2.engine.output.Renderer;

/**
 * Minimal slider model (e.g., volume).
 */
public class Slider {
    private float min;
    private float max;
    private float value;

    public Slider(float min, float max, float value) {
        this.min = min;
        this.max = max;
        this.value = value;
    }

    public Slider(Vector2 vector2) {
        //TODO Auto-generated constructor stub
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    public void render(Renderer renderer, boolean b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    }
}

