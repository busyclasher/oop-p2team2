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
            movementSystem.applyGravity(velocity, gravity, dt);
            movementSystem.applyFriction(velocity, friction, dt);
            movementSystem.integrate(transform, velocity, dt);
        }
    }

    public void setGravity(Vector2 gravity) {
        this.gravity = gravity == null ? new Vector2(0f, -9.8f) : gravity.cpy();
    }

    public Vector2 getGravity() {
        return gravity.cpy();
    }

    public void setFriction(float friction) {
        this.friction = Math.max(0f, friction);
    }

    public float getFriction() {
        return friction;
    }
}
