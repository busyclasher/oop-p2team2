package sg.edu.sit.inf1009.p2team2.engine.collision;

import com.badlogic.gdx.math.Rectangle;

/**
 * Axis-aligned rectangle collision shape.
 */
public class RectShape implements Shape {
    private final Rectangle bounds;

    public RectShape() {
        this.bounds = new Rectangle();
    }

    public RectShape(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public RectShape(Rectangle bounds) {
        this.bounds = bounds == null ? new Rectangle() : bounds;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setPosition(float x, float y) {
        bounds.setPosition(x, y);
    }

    public void setSize(float width, float height) {
        bounds.setSize(width, height);
    }

    public float getCenterX() {
        return bounds.x + bounds.width * 0.5f;
    }

    public float getCenterY() {
        return bounds.y + bounds.height * 0.5f;
    }
}
