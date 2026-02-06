package sg.edu.sit.inf1009.p2team2.engine.collision;

import com.badlogic.gdx.math.Vector2;

/**
 * Circle collision shape.
 */
public class CircleShape implements Shape {
    private final Vector2 center = new Vector2();
    private float radius;

    public CircleShape() {
        this(0f, 0f, 0f);
    }

    public CircleShape(float x, float y, float radius) {
        this.center.set(x, y);
        this.radius = radius;
    }

    public Vector2 getCenter() {
        return center;
    }

    public void setCenter(float x, float y) {
        this.center.set(x, y);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
