package sg.edu.sit.inf1009.p2team2.engine.ui;

/**
 * Minimal UI button model used by menu/settings scenes.
 *
 * Rendering and input hit-testing are handled elsewhere (scene + renderer/input).
 */
public class Button {
    private String label;
    private boolean enabled = true;

    public Button(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void click() {
        // TODO(Ivan): trigger button action callback.
    }
}

