package sg.edu.sit.inf1009.p2team2.engine.collision;

import com.badlogic.gdx.math.Vector2;

public class Circle extends Shape {
    private Vector2 center;
    private float radius;

    public Circle(Vector2 center, float radius) {
        this.center = center == null ? new Vector2() : center;
        this.radius = radius;
    }

    public Vector2 getCenter() {
        return center;
    }

    public void setCenter(Vector2 center) {
        this.center = center;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public ShapeType getType() {
        return ShapeType.CIRCLE;
    }
}
