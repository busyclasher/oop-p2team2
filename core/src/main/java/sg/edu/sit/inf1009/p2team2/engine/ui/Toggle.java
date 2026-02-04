package sg.edu.sit.inf1009.p2team2.engine.ui;

/**
 * Minimal toggle model (e.g., fullscreen on/off).
 */
public class Toggle {
    private boolean on;

    public Toggle(boolean on) {
        this.on = on;
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
}

