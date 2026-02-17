package sg.edu.sit.inf1009.p2team2.engine.managers;

import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.VelocityComponent;
import sg.edu.sit.inf1009.p2team2.engine.systems.MovementSystem;

/**
 * Coordinates movement updates for entities that have movement-related components.
 */
public class MovementManager {
    private final EntityManager entityManager;
    private final MovementSystem movementSystem;
    private Vector2 gravity;
    private float friction;

    public MovementManager() {
        this(new EntityManager());
    }

    public MovementManager(EntityManager entityManager) {
        this.entityManager = entityManager == null ? new EntityManager() : entityManager;
        this.movementSystem = new MovementSystem();
        this.gravity = new Vector2(0f, -9.8f);
        this.friction = 0.0f;
    }

    public void update(float dt) {
        if (dt <= 0f) {
            return;
        }

        for (Entity entity : entityManager.getAllEntities()) {
            TransformComponent transform = entity.get(TransformComponent.class);
            VelocityComponent velocity = entity.get(VelocityComponent.class);
            if (transform == null || velocity == null) {
                continue;
            }
            applyGravity(velocity, dt);
            applyFriction(velocity, dt);
            movementSystem.integrate(transform, velocity, dt);
        }
    }

    private void applyGravity(VelocityComponent velocity, float dt) {
        if (gravity == null || velocity.getVelocity() == null) {
            return;
        }
        velocity.getVelocity().x += gravity.x * dt;
        velocity.getVelocity().y += gravity.y * dt;
    }

    private void applyFriction(VelocityComponent velocity, float dt) {
        if (friction <= 0f || velocity.getVelocity() == null) {
            return;
        }
        float damping = Math.max(0f, 1f - friction * dt);
        velocity.getVelocity().scl(damping);
    }
}
