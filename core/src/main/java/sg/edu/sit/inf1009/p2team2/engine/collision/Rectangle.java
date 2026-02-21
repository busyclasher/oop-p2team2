package sg.edu.sit.inf1009.p2team2.engine.collision;

import com.badlogic.gdx.math.Vector2;

/**
 * Axis-aligned rectangle collision shape with position and dimensions.
 */
public class Rectangle extends Shape {
    private Vector2 position;
    private float width;
    private float height;

    public Rectangle(float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.width = width;
        this.height = height;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public ShapeType getType() {
        return ShapeType.RECTANGLE;
    }
}
