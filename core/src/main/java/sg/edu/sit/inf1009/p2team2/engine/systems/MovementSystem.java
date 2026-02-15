package sg.edu.sit.inf1009.p2team2.engine.systems;

import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.VelocityComponent;
import sg.edu.sit.inf1009.p2team2.engine.managers.EntityManager;

public class MovementSystem {
    private final Vector2 tmp = new Vector2();
    private final EntityManager entityManager;

    public MovementSystem() {
        this(null);
    }

    public MovementSystem(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void update(float dt) {
        if (dt <= 0f || entityManager == null) {
            return;
        }

        for (Entity entity : entityManager.getAll()) {
            TransformComponent t = entity.get(TransformComponent.class);
            VelocityComponent v = entity.get(VelocityComponent.class);
            if (t == null || v == null) {
                continue;
            }
            integrate(t, v, dt);
        }
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
