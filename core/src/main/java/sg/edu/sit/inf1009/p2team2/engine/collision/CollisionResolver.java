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
<<<<<<< HEAD
    private float epsilon = 0.0001f;
    private final Vector2 tmp = new Vector2();
=======
    private float restitution;
>>>>>>> 42d5b94b77bf64c118933a395c0fd6b4a32cce08

    public CollisionResolver() {
        this.restitution = 0f;
    }

<<<<<<< HEAD
    public void resolve(Collision c) {
        if (c == null) {
            return;
        }
        Collider a = c.getA();
        Collider b = c.getB();
        Contact contact = c.getContact();
        if (a == null || b == null || contact == null) {
            return;
        }

        if (contact.getPenetration() <= epsilon) {
            return;
        }

        if (a.isTrigger() || b.isTrigger()) {
            return;
        }

        TransformComponent ta = a.getOwner() == null ? null : a.getOwner().get(TransformComponent.class);
        TransformComponent tb = b.getOwner() == null ? null : b.getOwner().get(TransformComponent.class);

        separatePositions(ta, tb, contact);
        applyImpulse(a, b, contact.getNormal());
    }

    private void applyImpulse(Collider a, Collider b, Vector2 contactNormal) {
        if (a == null || b == null || contactNormal == null) {
            return;
        }

        VelocityComponent va = a.getOwner() == null ? null : a.getOwner().get(VelocityComponent.class);
        VelocityComponent vb = b.getOwner() == null ? null : b.getOwner().get(VelocityComponent.class);

        if (va != null) {
            stopVelocity(va, contactNormal);
        }
        if (vb != null) {
            stopVelocity(vb, tmp.set(contactNormal).scl(-1f));
        }
    }

    private void separatePositions(TransformComponent a, TransformComponent b, Contact contact) {
        if (contact == null) {
            return;
        }
        Vector2 normal = contact.getNormal();
        float penetration = contact.getPenetration();
        if (normal == null || penetration <= epsilon) {
            return;
        }

        Vector2 posA = a == null ? null : a.getPosition();
        Vector2 posB = b == null ? null : b.getPosition();

        if (posA != null && posB != null) {
            float half = penetration * 0.5f;
            posA.sub(tmp.set(normal).scl(half));
            posB.add(tmp.set(normal).scl(half));
            return;
        }

        if (posA != null) {
            posA.sub(tmp.set(normal).scl(penetration));
            return;
        }

        if (posB != null) {
            posB.add(tmp.set(normal).scl(penetration));
        }
    }

    private void stopVelocity(VelocityComponent v, Vector2 normal) {
        if (v == null || normal == null) {
            return;
        }
        Vector2 velocity = v.getVelocity();
        if (velocity == null) {
            return;
        }

        float along = velocity.dot(normal);
        if (along <= 0f) {
            return;
        }

        velocity.sub(tmp.set(normal).scl(along));
=======
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

    public void separateEntities(Collision collision) {
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
>>>>>>> 42d5b94b77bf64c118933a395c0fd6b4a32cce08
    }

    public void applyImpulse(Collision collision) {
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

    public void stopVelocityAlongNormal(VelocityComponent velocity, Vector2 normal) {
        if (velocity == null || velocity.getVelocity() == null || normal == null || normal.isZero()) {
            return;
        }

        float alongNormal = velocity.getVelocity().dot(normal);
        if (alongNormal > 0f) {
            velocity.getVelocity().mulAdd(normal, -alongNormal);
        }
    }
}
