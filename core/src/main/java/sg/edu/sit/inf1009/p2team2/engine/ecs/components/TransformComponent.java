package sg.edu.sit.inf1009.p2team2.engine.ecs.components;

import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.ecs.ComponentAdapter;

public class TransformComponent implements ComponentAdapter {
    private Vector2 position;
    private float rotation;
    private Vector2 scale;

    public TransformComponent() {
        this.position = new Vector2();
        this.rotation = 0f;
        this.scale = new Vector2(1f, 1f);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public Vector2 getScale() {
        return scale;
    }

    public void setScale(Vector2 scale) {
        this.scale = scale;
    }
}
