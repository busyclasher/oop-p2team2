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

    float calculatePenetration(Shape a, Shape b) {
        if (a instanceof Rectangle rectA && b instanceof Rectangle rectB) {
            float overlapX = Math.min(rectA.getPosition().x + rectA.getWidth(), rectB.getPosition().x + rectB.getWidth())
                - Math.max(rectA.getPosition().x, rectB.getPosition().x);
            float overlapY = Math.min(rectA.getPosition().y + rectA.getHeight(), rectB.getPosition().y + rectB.getHeight())
                - Math.max(rectA.getPosition().y, rectB.getPosition().y);
            return Math.max(0f, Math.min(overlapX, overlapY));
        }
        return 0f;
    }

    Vector2 calculateContactNormal(Shape a, Shape b) {
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
}
