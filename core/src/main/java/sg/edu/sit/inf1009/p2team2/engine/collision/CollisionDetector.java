package sg.edu.sit.inf1009.p2team2.engine.collision;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.ColliderComponent;

/**
 * Detects collisions for entity pairs.
 */
public class CollisionDetector {

    public CollisionDetector() {
    }

    public List<Collision> detectCollisions(List<Entity> entities) {
        List<Collision> collisions = new ArrayList<>();
        if (entities == null) {
            return collisions;
        }

        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                Collision collision = checkCollision(entities.get(i), entities.get(j));
                if (collision != null) {
                    collisions.add(collision);
                }
            }
        }
        return collisions;
    }

    public Collision checkCollision(Entity entityA, Entity entityB) {
        if (entityA == null || entityB == null) {
            return null;
        }

        ColliderComponent colliderA = entityA.get(ColliderComponent.class);
        ColliderComponent colliderB = entityB.get(ColliderComponent.class);
        if (colliderA == null || colliderB == null || colliderA.getBounds() == null || colliderB.getBounds() == null) {
            return null;
        }

        Rectangle rectA = toRectangle(colliderA);
        Rectangle rectB = toRectangle(colliderB);
        if (!checkAABB(rectA, rectB)) {
            return null;
        }

        Collision collision = new Collision(entityA, entityB);
        collision.setPenetrationDepth(calculatePenetration(rectA, rectB));
        collision.setContactNormal(calculateContactNormal(rectA, rectB));
        collision.setContactPoint(calculateContactPoint(rectA, rectB));
        return collision;
    }

    public boolean checkAABB(Rectangle rectA, Rectangle rectB) {
        float ax1 = rectA.getPosition().x;
        float ay1 = rectA.getPosition().y;
        float ax2 = ax1 + rectA.getWidth();
        float ay2 = ay1 + rectA.getHeight();

        float bx1 = rectB.getPosition().x;
        float by1 = rectB.getPosition().y;
        float bx2 = bx1 + rectB.getWidth();
        float by2 = by1 + rectB.getHeight();

        return ax1 < bx2 && ax2 > bx1 && ay1 < by2 && ay2 > by1;
    }

    public boolean checkCircle(Circle circleA, Circle circleB) {
        float distance = circleA.getCenter().dst(circleB.getCenter());
        return distance <= (circleA.getRadius() + circleB.getRadius());
    }

    public boolean checkCircleRect(Circle circle, Rectangle rect) {
        float closestX = clamp(circle.getCenter().x, rect.getPosition().x, rect.getPosition().x + rect.getWidth());
        float closestY = clamp(circle.getCenter().y, rect.getPosition().y, rect.getPosition().y + rect.getHeight());
        float dx = circle.getCenter().x - closestX;
        float dy = circle.getCenter().y - closestY;
        return (dx * dx + dy * dy) <= (circle.getRadius() * circle.getRadius());
    }

    public float calculatePenetration(Shape a, Shape b) {
        if (a instanceof Rectangle rectA && b instanceof Rectangle rectB) {
            float overlapX = Math.min(rectA.getPosition().x + rectA.getWidth(), rectB.getPosition().x + rectB.getWidth())
                - Math.max(rectA.getPosition().x, rectB.getPosition().x);
            float overlapY = Math.min(rectA.getPosition().y + rectA.getHeight(), rectB.getPosition().y + rectB.getHeight())
                - Math.max(rectA.getPosition().y, rectB.getPosition().y);
            return Math.max(0f, Math.min(overlapX, overlapY));
        }
        return 0f;
    }

    public Vector2 calculateContactNormal(Shape a, Shape b) {
        if (a instanceof Rectangle rectA && b instanceof Rectangle rectB) {
            float centerAX = rectA.getPosition().x + rectA.getWidth() * 0.5f;
            float centerAY = rectA.getPosition().y + rectA.getHeight() * 0.5f;
            float centerBX = rectB.getPosition().x + rectB.getWidth() * 0.5f;
            float centerBY = rectB.getPosition().y + rectB.getHeight() * 0.5f;
            Vector2 normal = new Vector2(centerBX - centerAX, centerBY - centerAY);
            if (normal.isZero()) {
                return new Vector2(1f, 0f);
            }
            return normal.nor();
        }
        return new Vector2();
    }

    private Vector2 calculateContactPoint(Rectangle rectA, Rectangle rectB) {
        float x = (Math.max(rectA.getPosition().x, rectB.getPosition().x)
            + Math.min(rectA.getPosition().x + rectA.getWidth(), rectB.getPosition().x + rectB.getWidth())) * 0.5f;
        float y = (Math.max(rectA.getPosition().y, rectB.getPosition().y)
            + Math.min(rectA.getPosition().y + rectA.getHeight(), rectB.getPosition().y + rectB.getHeight())) * 0.5f;
        return new Vector2(x, y);
    }

    private Rectangle toRectangle(ColliderComponent collider) {
        com.badlogic.gdx.math.Rectangle bounds = collider.getBounds();
        return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
<<<<<<< HEAD

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
=======
>>>>>>> 42d5b94b77bf64c118933a395c0fd6b4a32cce08
}
