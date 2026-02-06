package sg.edu.sit.inf1009.p2team2.engine.collision;

import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;

/**
 * Responsible for collision detection between colliders.
 *
 * Implementation can use broad-phase + narrow-phase depending on needs.
 */
public class CollisionDetector {
    private final Entity owner;
    private final Shape shape;
    private final boolean trigger;
    private final Vector2 offset = new Vector2();

    public CollisionDetector(Entity owner, Shape shape, boolean trigger) {
        this.owner = owner;
        this.shape = shape;
        this.trigger = trigger;
    }

    public Entity getOwner() {
        return owner;
    }

    public Shape getWorldShape() {
        return shape;
    }

    public boolean isTrigger() {
        return trigger;
    }

    public Vector2 getOffset() {
        return offset;
    }

    public Contact detect(Collider a, Collider b) {
        if (a == null || b == null) {
            return null;
        }
        Shape shapeA = a.getWorldShape();
        Shape shapeB = b.getWorldShape();
        if (shapeA == null || shapeB == null) {
            return null;
        }

        if (shapeA instanceof RectShape && shapeB instanceof RectShape) {
            return rectRect((RectShape) shapeA, (RectShape) shapeB);
        }
        if (shapeA instanceof CircleShape && shapeB instanceof CircleShape) {
            return circleCircle((CircleShape) shapeA, (CircleShape) shapeB);
        }
        if (shapeA instanceof RectShape && shapeB instanceof CircleShape) {
            return rectCircle((RectShape) shapeA, (CircleShape) shapeB);
        }
        if (shapeA instanceof CircleShape && shapeB instanceof RectShape) {
            Contact contact = rectCircle((RectShape) shapeB, (CircleShape) shapeA);
            if (contact != null) {
                contact.getNormal().scl(-1f);
            }
            return contact;
        }

        return null;
    }

    private Contact rectRect(RectShape a, RectShape b) {
        if (a == null || b == null) {
            return null;
        }

        float aMinX = a.getBounds().x;
        float aMaxX = a.getBounds().x + a.getBounds().width;
        float aMinY = a.getBounds().y;
        float aMaxY = a.getBounds().y + a.getBounds().height;

        float bMinX = b.getBounds().x;
        float bMaxX = b.getBounds().x + b.getBounds().width;
        float bMinY = b.getBounds().y;
        float bMaxY = b.getBounds().y + b.getBounds().height;

        float overlapX = Math.min(aMaxX, bMaxX) - Math.max(aMinX, bMinX);
        float overlapY = Math.min(aMaxY, bMaxY) - Math.max(aMinY, bMinY);

        if (overlapX <= 0f || overlapY <= 0f) {
            return null;
        }

        float centerAx = a.getCenterX();
        float centerAy = a.getCenterY();
        float centerBx = b.getCenterX();
        float centerBy = b.getCenterY();

        Contact contact = new Contact();
        if (overlapX < overlapY) {
            contact.getNormal().set(centerBx >= centerAx ? 1f : -1f, 0f);
            contact.setPenetration(overlapX);
        } else {
            contact.getNormal().set(0f, centerBy >= centerAy ? 1f : -1f);
            contact.setPenetration(overlapY);
        }
        return contact;
    }

    private Contact circleCircle(CircleShape a, CircleShape b) {
        if (a == null || b == null) {
            return null;
        }

        float dx = b.getCenter().x - a.getCenter().x;
        float dy = b.getCenter().y - a.getCenter().y;
        float r = a.getRadius() + b.getRadius();
        float dist2 = dx * dx + dy * dy;
        if (dist2 >= r * r) {
            return null;
        }

        Contact contact = new Contact();
        if (dist2 > 0f) {
            float dist = (float) Math.sqrt(dist2);
            contact.getNormal().set(dx / dist, dy / dist);
            contact.setPenetration(r - dist);
        } else {
            contact.getNormal().set(1f, 0f);
            contact.setPenetration(r);
        }
        return contact;
    }

    private Contact rectCircle(RectShape rect, CircleShape circle) {
        if (rect == null || circle == null) {
            return null;
        }

        float cx = circle.getCenter().x;
        float cy = circle.getCenter().y;

        float rx = rect.getBounds().x;
        float ry = rect.getBounds().y;
        float rw = rect.getBounds().width;
        float rh = rect.getBounds().height;

        float closestX = clamp(cx, rx, rx + rw);
        float closestY = clamp(cy, ry, ry + rh);

        float dx = cx - closestX;
        float dy = cy - closestY;
        float dist2 = dx * dx + dy * dy;
        float radius = circle.getRadius();

        if (dist2 > radius * radius) {
            return null;
        }

        Contact contact = new Contact();
        if (dist2 > 0f) {
            float dist = (float) Math.sqrt(dist2);
            contact.getNormal().set(dx / dist, dy / dist);
            contact.setPenetration(radius - dist);
            return contact;
        }

        float left = cx - rx;
        float right = (rx + rw) - cx;
        float bottom = cy - ry;
        float top = (ry + rh) - cy;

        float min = left;
        contact.getNormal().set(-1f, 0f);
        if (right < min) {
            min = right;
            contact.getNormal().set(1f, 0f);
        }
        if (bottom < min) {
            min = bottom;
            contact.getNormal().set(0f, -1f);
        }
        if (top < min) {
            min = top;
            contact.getNormal().set(0f, 1f);
        }

        contact.setPenetration(radius + min);
        return contact;
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
