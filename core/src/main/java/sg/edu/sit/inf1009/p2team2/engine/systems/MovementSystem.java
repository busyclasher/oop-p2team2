package sg.edu.sit.inf1009.p2team2.engine.systems;

import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.VelocityComponent;

public class MovementSystem {
    private final Vector2 tmp = new Vector2();

    public MovementSystem() {
    }

    public void integrate(TransformComponent t, VelocityComponent v, float dt) {
        if (dt <= 0f || t == null || v == null) {
            return;
        }

        Vector2 position = t.getPosition();
        Vector2 velocity = v.getVelocity();
        Vector2 acceleration = v.getAcceleration();

        if (position == null || velocity == null || acceleration == null) {
            return;
        }

        // Simple Euler integration:
        // v = v + a * dt
        // p = p + v * dt
        tmp.set(acceleration).scl(dt);
        velocity.add(tmp);

        tmp.set(velocity).scl(dt);
        position.add(tmp);
    }
}
