package sg.edu.sit.inf1009.p2team2.engine.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;

class MovementManagerConfigTest {

    @Test
    void gravityAndFrictionAccessorsWork() {
        MovementManager manager = new MovementManager(new EntityManager());

        manager.setGravity(new Vector2(0f, -15f));
        manager.setFriction(0.6f);

        assertEquals(-15f, manager.getGravity().y, 0.0001f);
        assertEquals(0.6f, manager.getFriction(), 0.0001f);

        manager.setFriction(-3f);
        assertEquals(0f, manager.getFriction(), 0.0001f);
    }
}
