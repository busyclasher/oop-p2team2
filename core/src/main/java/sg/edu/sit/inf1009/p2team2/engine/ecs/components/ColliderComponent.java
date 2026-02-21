package sg.edu.sit.inf1009.p2team2.engine.ecs.components;

import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.collision.Circle;
import sg.edu.sit.inf1009.p2team2.engine.collision.Rectangle;
import sg.edu.sit.inf1009.p2team2.engine.collision.Shape;
import sg.edu.sit.inf1009.p2team2.engine.ecs.ComponentAdapter;

/**
 * Collider ECS component storing shape and collision filtering data.
 */
public class ColliderComponent implements ComponentAdapter {
    private Shape shape;
    private boolean isTrigger;
    private int layer;
    private int mask;

    public ColliderComponent() {
        this.shape = new Rectangle(0f, 0f, 0f, 0f);
        this.isTrigger = false;
        this.layer = 0;
        this.mask = 0;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public boolean isTrigger() {
        return isTrigger;
    }

    public void setTrigger(boolean trigger) {
        isTrigger = trigger;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }

    /**
     * Backward-compatibility accessor for existing runtime rendering code.
     * This mirrors the current shape as an AABB rectangle.
     */
    public com.badlogic.gdx.math.Rectangle getBounds() {
        if (shape instanceof Rectangle rect) {
            Vector2 position = rect.getPosition();
            return new com.badlogic.gdx.math.Rectangle(
                position.x,
                position.y,
                rect.getWidth(),
                rect.getHeight()
            );
        }
        if (shape instanceof Circle circle) {
            Vector2 center = circle.getCenter();
            float radius = circle.getRadius();
            return new com.badlogic.gdx.math.Rectangle(
                center.x - radius,
                center.y - radius,
                radius * 2f,
                radius * 2f
            );
        }
        return null;
    }

    /**
     * Backward-compatibility mutator for existing tests/scene code.
     */
    public void setBounds(com.badlogic.gdx.math.Rectangle bounds) {
        if (bounds == null) {
            this.shape = null;
            return;
        }
        this.shape = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void updatePosition(float x, float y) {
        if (shape instanceof Rectangle rect) {
            rect.setPosition(new Vector2(x, y));
            return;
        }
        if (shape instanceof Circle circle) {
            float radius = circle.getRadius();
            circle.setCenter(new Vector2(x + radius, y + radius));
            return;
        }
        shape = new Rectangle(x, y, 0f, 0f);
    }
}
