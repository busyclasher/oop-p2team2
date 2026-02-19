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
        if (colliderA == null || colliderB == null) {
            return null;
        }

        Shape shapeA = colliderA.getShape();
        Shape shapeB = colliderB.getShape();
        if (shapeA == null || shapeB == null) {
            return null;
        }

        boolean intersects = intersects(shapeA, shapeB);
        if (!intersects) {
            return null;
        }

        Collision collision = new Collision(entityA, entityB);
        float depth = calculatePenetration(shapeA, shapeB);
        Vector2 normal = calculateContactNormal(shapeA, shapeB);
        collision.setPenetrationDepth(depth);
        collision.setContactNormal(normal);
        collision.setContactPoint(calculateContactPoint(shapeA, shapeB, normal));
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
        Vector2 closest = closestPointOnRectangle(circle.getCenter(), rect);
        float dx = circle.getCenter().x - closest.x;
        float dy = circle.getCenter().y - closest.y;
        return (dx * dx + dy * dy) <= (circle.getRadius() * circle.getRadius());
    }

    private boolean intersects(Shape a, Shape b) {
        if (a instanceof Rectangle rectA && b instanceof Rectangle rectB) {
            return checkAABB(rectA, rectB);
        }
        if (a instanceof Circle circleA && b instanceof Circle circleB) {
            return checkCircle(circleA, circleB);
        }
        if (a instanceof Circle circle && b instanceof Rectangle rect) {
            return checkCircleRect(circle, rect);
        }
        if (a instanceof Rectangle rect && b instanceof Circle circle) {
            return checkCircleRect(circle, rect);
        }
        return false;
    }

    private float calculatePenetration(Shape a, Shape b) {
        if (a instanceof Rectangle rectA && b instanceof Rectangle rectB) {
            float overlapX = Math.min(rectA.getPosition().x + rectA.getWidth(), rectB.getPosition().x + rectB.getWidth())
                - Math.max(rectA.getPosition().x, rectB.getPosition().x);
            float overlapY = Math.min(rectA.getPosition().y + rectA.getHeight(), rectB.getPosition().y + rectB.getHeight())
                - Math.max(rectA.getPosition().y, rectB.getPosition().y);
            return Math.max(0f, Math.min(overlapX, overlapY));
        }

        if (a instanceof Circle circleA && b instanceof Circle circleB) {
            float distance = circleA.getCenter().dst(circleB.getCenter());
            return Math.max(0f, (circleA.getRadius() + circleB.getRadius()) - distance);
        }

        if (a instanceof Circle circle && b instanceof Rectangle rect) {
            return calculateCircleRectPenetration(circle, rect);
        }

        if (a instanceof Rectangle rect && b instanceof Circle circle) {
            return calculateCircleRectPenetration(circle, rect);
        }

        return 0f;
    }

    private Vector2 calculateContactNormal(Shape a, Shape b) {
        if (a instanceof Rectangle rectA && b instanceof Rectangle rectB) {
            return normalRectRect(rectA, rectB);
        }

        if (a instanceof Circle circleA && b instanceof Circle circleB) {
            return normalCircleCircle(circleA, circleB);
        }

        if (a instanceof Circle circle && b instanceof Rectangle rect) {
            return normalCircleRect(circle, rect);
        }

        if (a instanceof Rectangle rect && b instanceof Circle circle) {
            return normalCircleRect(circle, rect).scl(-1f);
        }

        return new Vector2(1f, 0f);
    }

    private Vector2 calculateContactPoint(Shape a, Shape b, Vector2 normal) {
        if (a instanceof Rectangle rectA && b instanceof Rectangle rectB) {
            float x = (Math.max(rectA.getPosition().x, rectB.getPosition().x)
                + Math.min(rectA.getPosition().x + rectA.getWidth(), rectB.getPosition().x + rectB.getWidth())) * 0.5f;
            float y = (Math.max(rectA.getPosition().y, rectB.getPosition().y)
                + Math.min(rectA.getPosition().y + rectA.getHeight(), rectB.getPosition().y + rectB.getHeight())) * 0.5f;
            return new Vector2(x, y);
        }

        if (a instanceof Circle circleA && b instanceof Circle) {
            Vector2 n = normal == null || normal.isZero() ? new Vector2(1f, 0f) : new Vector2(normal).nor();
            return new Vector2(circleA.getCenter()).mulAdd(n, circleA.getRadius());
        }

        if (a instanceof Circle circle && b instanceof Rectangle rect) {
            return closestPointOnRectangle(circle.getCenter(), rect);
        }

        if (a instanceof Rectangle rect && b instanceof Circle circle) {
            return closestPointOnRectangle(circle.getCenter(), rect);
        }

        return new Vector2();
    }

    private float calculateCircleRectPenetration(Circle circle, Rectangle rect) {
        Vector2 closest = closestPointOnRectangle(circle.getCenter(), rect);
        float distance = circle.getCenter().dst(closest);
        if (distance == 0f && pointInsideRect(circle.getCenter(), rect)) {
            float left = circle.getCenter().x - rect.getPosition().x;
            float right = rect.getPosition().x + rect.getWidth() - circle.getCenter().x;
            float bottom = circle.getCenter().y - rect.getPosition().y;
            float top = rect.getPosition().y + rect.getHeight() - circle.getCenter().y;
            float nearest = Math.min(Math.min(left, right), Math.min(bottom, top));
            return circle.getRadius() + nearest;
        }
        return Math.max(0f, circle.getRadius() - distance);
    }

    private Vector2 normalRectRect(Rectangle rectA, Rectangle rectB) {
        float centerAX = rectA.getPosition().x + rectA.getWidth() * 0.5f;
        float centerAY = rectA.getPosition().y + rectA.getHeight() * 0.5f;
        float centerBX = rectB.getPosition().x + rectB.getWidth() * 0.5f;
        float centerBY = rectB.getPosition().y + rectB.getHeight() * 0.5f;

        float dx = centerBX - centerAX;
        float dy = centerBY - centerAY;
        float px = (rectA.getWidth() + rectB.getWidth()) * 0.5f - Math.abs(dx);
        float py = (rectA.getHeight() + rectB.getHeight()) * 0.5f - Math.abs(dy);

        if (px < py) {
            return new Vector2(Math.signum(dx) == 0f ? 1f : Math.signum(dx), 0f);
        }
        return new Vector2(0f, Math.signum(dy) == 0f ? 1f : Math.signum(dy));
    }

    private Vector2 normalCircleCircle(Circle circleA, Circle circleB) {
        Vector2 normal = new Vector2(circleB.getCenter()).sub(circleA.getCenter());
        if (normal.isZero()) {
            return new Vector2(1f, 0f);
        }
        return normal.nor();
    }

    private Vector2 normalCircleRect(Circle circle, Rectangle rect) {
        Vector2 closest = closestPointOnRectangle(circle.getCenter(), rect);
        Vector2 normal = new Vector2(closest).sub(circle.getCenter());
        if (!normal.isZero()) {
            return normal.nor();
        }

        float left = circle.getCenter().x - rect.getPosition().x;
        float right = rect.getPosition().x + rect.getWidth() - circle.getCenter().x;
        float bottom = circle.getCenter().y - rect.getPosition().y;
        float top = rect.getPosition().y + rect.getHeight() - circle.getCenter().y;
        float nearest = Math.min(Math.min(left, right), Math.min(bottom, top));

        if (nearest == left) {
            return new Vector2(-1f, 0f);
        }
        if (nearest == right) {
            return new Vector2(1f, 0f);
        }
        if (nearest == bottom) {
            return new Vector2(0f, -1f);
        }
        return new Vector2(0f, 1f);
    }

    private Vector2 closestPointOnRectangle(Vector2 point, Rectangle rect) {
        float x = clamp(point.x, rect.getPosition().x, rect.getPosition().x + rect.getWidth());
        float y = clamp(point.y, rect.getPosition().y, rect.getPosition().y + rect.getHeight());
        return new Vector2(x, y);
    }

    private boolean pointInsideRect(Vector2 point, Rectangle rect) {
        return point.x >= rect.getPosition().x
            && point.x <= rect.getPosition().x + rect.getWidth()
            && point.y >= rect.getPosition().y
            && point.y <= rect.getPosition().y + rect.getHeight();
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
