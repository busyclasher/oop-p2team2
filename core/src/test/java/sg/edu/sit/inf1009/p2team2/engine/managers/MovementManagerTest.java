package sg.edu.sit.inf1009.p2team2.engine.managers;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;

import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.VelocityComponent;

class MovementManagerTest {

    @Test
    void updateIntegratesEntityWithTransformAndVelocity() {
        EntityManager entityManager = new EntityManager();
        Entity entity = entityManager.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.setPosition(new Vector2(10f, 10f));

        VelocityComponent velocity = new VelocityComponent();
        velocity.setVelocity(new Vector2(3f, 4f));

        entity.add(transform);
        entity.add(velocity);

        MovementManager manager = new MovementManager(entityManager);
        manager.update(0.16f);

        assertNotEquals(10f, transform.getPosition().x, 0.0001f);
        assertNotEquals(10f, transform.getPosition().y, 0.0001f);
    }
}
