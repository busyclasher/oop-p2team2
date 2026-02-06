package sg.edu.sit.inf1009.p2team2.engine.ui;

import com.badlogic.gdx.math.Vector2;

import sg.edu.sit.inf1009.p2team2.engine.output.Renderer;

/**
 * Minimal toggle model (e.g., fullscreen on/off).
 */
public class Toggle {
    private boolean on;

    public Toggle(boolean on) {
        this.on = on;
    }

    public Toggle(int i, int j) {
        //TODO Auto-generated constructor stub
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

    public void setValue(boolean isFullscreen) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setValue'");
    }

    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    public void render(Renderer renderer, boolean b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    }

    public float getValue() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getValue'");
    }

    public Object isEnabled() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isEnabled'");
    }
}

