package sg.edu.sit.inf1009.p2team2.engine.ecs.components;

import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.ecs.ComponentAdapter;

public class VelocityComponent implements ComponentAdapter {
    private Vector2 velocity;
    private Vector2 acceleration;

    public VelocityComponent() {
        this.velocity = new Vector2();
        this.acceleration = new Vector2();
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public Vector2 getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector2 acceleration) {
        this.acceleration = acceleration;
    }
}
