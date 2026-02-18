package sg.edu.sit.inf1009.p2team2.engine.collision;

import com.badlogic.gdx.math.Vector2;
import java.util.List;
import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.VelocityComponent;

/**
 * Resolves collision response for detected contacts.
 */
public class CollisionResolver {
    private float restitution;

    public CollisionResolver() {
        this.restitution = 0f;
    }

    public void resolveCollisions(List<Collision> collisions) {
        if (collisions == null) {
            return;
        }
        for (Collision collision : collisions) {
            resolve(collision);
        }
    }

    public void resolve(Collision collision) {
        if (collision == null) {
            return;
        }
        separateEntities(collision);
        applyImpulse(collision);
    }

    public void setRestitution(float value) {
        this.restitution = Math.max(0f, value);
    }

    void separateEntities(Collision collision) {
        Entity entityA = collision.getEntityA();
        Entity entityB = collision.getEntityB();
        TransformComponent transformA = entityA == null ? null : entityA.get(TransformComponent.class);
        TransformComponent transformB = entityB == null ? null : entityB.get(TransformComponent.class);
        if (transformA == null || transformB == null || transformA.getPosition() == null || transformB.getPosition() == null) {
            return;
        }

        Vector2 normal = collision.getContactNormal();
        if (normal == null || normal.isZero()) {
            return;
        }

        float separation = collision.getPenetrationDepth() * 0.5f;
        transformA.getPosition().mulAdd(normal, -separation);
        transformB.getPosition().mulAdd(normal, separation);
    }

    void applyImpulse(Collision collision) {
        Entity entityA = collision.getEntityA();
        Entity entityB = collision.getEntityB();
        VelocityComponent velocityA = entityA == null ? null : entityA.get(VelocityComponent.class);
        VelocityComponent velocityB = entityB == null ? null : entityB.get(VelocityComponent.class);
        if (velocityA == null || velocityB == null || velocityA.getVelocity() == null || velocityB.getVelocity() == null) {
            return;
        }

        Vector2 normal = collision.getContactNormal();
        if (normal == null || normal.isZero()) {
            return;
        }

        float relativeSpeed = velocityB.getVelocity().dot(normal) - velocityA.getVelocity().dot(normal);
        float impulse = -(1f + restitution) * relativeSpeed * 0.5f;
        velocityA.getVelocity().mulAdd(normal, -impulse);
        velocityB.getVelocity().mulAdd(normal, impulse);

        stopVelocityAlongNormal(velocityA, normal);
        stopVelocityAlongNormal(velocityB, new Vector2(normal).scl(-1f));
    }

    void stopVelocityAlongNormal(VelocityComponent velocity, Vector2 normal) {
        if (velocity == null || velocity.getVelocity() == null || normal == null || normal.isZero()) {
            return;
        }

        float alongNormal = velocity.getVelocity().dot(normal);
        if (alongNormal > 0f) {
            velocity.getVelocity().mulAdd(normal, -alongNormal);
        }
    }
}
