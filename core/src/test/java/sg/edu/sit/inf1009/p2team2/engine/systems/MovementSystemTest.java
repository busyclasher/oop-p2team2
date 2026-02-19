package sg.edu.sit.inf1009.p2team2.engine.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;

import sg.edu.sit.inf1009.p2team2.engine.ecs.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.VelocityComponent;

class MovementSystemTest {

    @Test
    void integrateUpdatesVelocityAndPosition() {
        MovementSystem system = new MovementSystem();

        TransformComponent transform = new TransformComponent();
        transform.setPosition(new Vector2(0f, 0f));

        VelocityComponent velocity = new VelocityComponent();
        velocity.setVelocity(new Vector2(10f, 0f));
        velocity.setAcceleration(new Vector2(2f, 0f));

        system.integrate(transform, velocity, 0.5f);

        assertEquals(11f, velocity.getVelocity().x, 0.0001f);
        assertEquals(5.5f, transform.getPosition().x, 0.0001f);
    }

    @Test
    void applyGravityAdjustsVelocity() {
        MovementSystem system = new MovementSystem();

        VelocityComponent velocity = new VelocityComponent();
        velocity.setVelocity(new Vector2(0f, 0f));

        system.applyGravity(velocity, new Vector2(0f, -9.8f), 0.5f);

        assertEquals(-4.9f, velocity.getVelocity().y, 0.0001f);
    }

    @Test
    void applyFrictionDampensVelocity() {
        MovementSystem system = new MovementSystem();

        VelocityComponent velocity = new VelocityComponent();
        velocity.setVelocity(new Vector2(10f, 0f));

        system.applyFriction(velocity, 0.2f, 0.5f);

        assertEquals(9f, velocity.getVelocity().x, 0.0001f);
    }
}
