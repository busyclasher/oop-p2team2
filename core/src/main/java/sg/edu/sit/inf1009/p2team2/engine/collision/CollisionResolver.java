package sg.edu.sit.inf1009.p2team2.engine.collision;

import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.VelocityComponent;

/**
 * Responsible for collision resolution (separation, impulses, velocity updates).
 */
public class CollisionResolver {
    private float epsilon = 0.0001f;
    private final Vector2 tmp = new Vector2();

    public CollisionResolver() {
    }

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
    }

    public float getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(float epsilon) {
        this.epsilon = epsilon;
    }
}
