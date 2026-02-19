package sg.edu.sit.inf1009.p2team2.engine.collision;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;

import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.ColliderComponent;

class CollisionDetectorTest {

    @Test
    void checkCollisionReturnsCollisionWhenBoundsOverlap() {
        CollisionDetector detector = new CollisionDetector();

        Entity a = new Entity(1);
        Entity b = new Entity(2);

        ColliderComponent colliderA = new ColliderComponent();
        colliderA.setShape(new Rectangle(0f, 0f, 10f, 10f));
        a.add(colliderA);

        ColliderComponent colliderB = new ColliderComponent();
        colliderB.setShape(new Rectangle(5f, 5f, 10f, 10f));
        b.add(colliderB);

        Collision collision = detector.checkCollision(a, b);
        assertNotNull(collision);
    }

    @Test
    void checkCollisionReturnsNullWhenBoundsDoNotOverlap() {
        CollisionDetector detector = new CollisionDetector();

        Entity a = new Entity(1);
        Entity b = new Entity(2);

        ColliderComponent colliderA = new ColliderComponent();
        colliderA.setShape(new Rectangle(0f, 0f, 10f, 10f));
        a.add(colliderA);

        ColliderComponent colliderB = new ColliderComponent();
        colliderB.setShape(new Rectangle(40f, 40f, 10f, 10f));
        b.add(colliderB);

        Collision collision = detector.checkCollision(a, b);
        assertNull(collision);
    }

    @Test
    void checkCollisionReturnsCollisionForCircleCircleOverlap() {
        CollisionDetector detector = new CollisionDetector();

        Entity a = new Entity(1);
        Entity b = new Entity(2);

        ColliderComponent colliderA = new ColliderComponent();
        colliderA.setShape(new Circle(new Vector2(10f, 10f), 5f));
        a.add(colliderA);

        ColliderComponent colliderB = new ColliderComponent();
        colliderB.setShape(new Circle(new Vector2(16f, 10f), 5f));
        b.add(colliderB);

        Collision collision = detector.checkCollision(a, b);
        assertNotNull(collision);
    }

    @Test
    void checkCollisionReturnsCollisionForCircleRectOverlap() {
        CollisionDetector detector = new CollisionDetector();

        Entity a = new Entity(1);
        Entity b = new Entity(2);

        ColliderComponent colliderA = new ColliderComponent();
        colliderA.setShape(new Circle(new Vector2(15f, 15f), 6f));
        a.add(colliderA);

        ColliderComponent colliderB = new ColliderComponent();
        colliderB.setShape(new Rectangle(20f, 10f, 10f, 10f));
        b.add(colliderB);

        Collision collision = detector.checkCollision(a, b);
        assertNotNull(collision);
    }
}
