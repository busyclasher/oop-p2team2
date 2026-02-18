package sg.edu.sit.inf1009.p2team2.engine.ecs;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import sg.edu.sit.inf1009.p2team2.engine.ecs.components.TransformComponent;

class EntityUmlApiTest {

    @Test
    void umlMethodsWork() {
        Entity entity = new Entity(42);
        TransformComponent transform = new TransformComponent();

        entity.add(transform);
        assertTrue(entity.has(TransformComponent.class));
        assertNotNull(entity.get(TransformComponent.class));
        assertFalse(entity.getAll().isEmpty());

        entity.remove(TransformComponent.class);
        assertFalse(entity.has(TransformComponent.class));
    }
}
