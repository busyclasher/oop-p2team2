package sg.edu.sit.inf1009.p2team2.engine.collision;

import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;

/**
 * Collision data container between two entities.
 */
public class Collision {
    private final Entity entityA;
    private final Entity entityB;
    private float penetrationDepth;
    private Vector2 contactNormal;
    private Vector2 contactPoint;
    private final long timestamp;

    public Collision(Entity entityA, Entity entityB) {
        this.entityA = entityA;
        this.entityB = entityB;
        this.penetrationDepth = 0f;
        this.contactNormal = new Vector2();
        this.contactPoint = new Vector2();
        this.timestamp = System.currentTimeMillis();
    }

    public Entity getEntityA() {
        return entityA;
    }

    public Entity getEntityB() {
        return entityB;
    }

    public float getPenetrationDepth() {
        return penetrationDepth;
    }

    public void setPenetrationDepth(float depth) {
        this.penetrationDepth = depth;
    }

    public Vector2 getContactNormal() {
        return contactNormal;
    }

    public void setContactNormal(Vector2 normal) {
        this.contactNormal = normal;
    }

    public Vector2 getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(Vector2 point) {
        this.contactPoint = point;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
