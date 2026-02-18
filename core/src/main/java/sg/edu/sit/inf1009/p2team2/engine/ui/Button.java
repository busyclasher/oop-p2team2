package sg.edu.sit.inf1009.p2team2.engine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.output.Renderer;

/**
 * Minimal UI button model used by menu/settings scenes.
 *
 * Rendering and input hit-testing are handled elsewhere (scene + renderer/input).
 */
public class Button {
    private String label;
    private boolean enabled = true;
    private Vector2 position;
    private Runnable onClick;

    public Button(String label, Vector2 position) {       
        this.label = label;
        this.position = position;
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

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void click() {
        if (!enabled || onClick == null) {
            return;
        }
        onClick.run();
    }

    public void render(Renderer renderer, boolean isSelected) {
        if (renderer == null || position == null) {
            return;
        }
        Color color = isSelected ? Color.YELLOW : Color.WHITE;
        renderer.drawText(label, position, "default", color);
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }
}
