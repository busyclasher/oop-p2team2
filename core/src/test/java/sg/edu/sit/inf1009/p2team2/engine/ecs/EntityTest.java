package sg.edu.sit.inf1009.p2team2.engine.ecs;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.VelocityComponent;


class EntityTest {

    @Test
    void addGetRemoveAndHasComponents() {
        Entity entity = new Entity(1);
        TransformComponent transform = new TransformComponent();
        VelocityComponent velocity = new VelocityComponent();

        entity.add(transform);
        entity.add(velocity);

        assertTrue(entity.has(TransformComponent.class));
        assertTrue(entity.has(VelocityComponent.class));
        assertSame(transform, entity.get(TransformComponent.class));
        assertSame(velocity, entity.get(VelocityComponent.class));

        entity.remove(TransformComponent.class);
        assertFalse(entity.has(TransformComponent.class));
        assertNull(entity.get(TransformComponent.class));
        assertTrue(entity.has(VelocityComponent.class));
    }

    @Test
    void handlesNullComponentTypesSafely() {
        Entity entity = new Entity(2);
        assertNull(entity.get(null));
        assertFalse(entity.has(null));
        entity.remove(null);
        assertTrue(entity.getAll().isEmpty());
    }

    @Test
    void clearRemovesAllComponents() {
        Entity entity = new Entity(3);
        entity.add(new TransformComponent());
        entity.add(new VelocityComponent());

        assertEquals(2, entity.getAll().size());

        entity.clear();

        assertTrue(entity.getAll().isEmpty());
    }

    @Test
    void addRejectsNullComponent() {
        Entity entity = new Entity(4);
        assertThrows(IllegalArgumentException.class, () -> entity.add(null));
    }
}
