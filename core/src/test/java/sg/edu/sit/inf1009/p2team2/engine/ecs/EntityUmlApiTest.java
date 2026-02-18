package sg.edu.sit.inf1009.p2team2.engine.ecs;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import sg.edu.sit.inf1009.p2team2.engine.ecs.components.TransformComponent;

class EntityUmlApiTest {

    @Test
    void umlAliasMethodsWork() {
        Entity entity = new Entity(42);
        TransformComponent transform = new TransformComponent();

        entity.addComponent(transform);
        assertTrue(entity.hasComponent(TransformComponent.class));
        assertNotNull(entity.getComponent(TransformComponent.class));
        assertFalse(entity.getAllComponents().isEmpty());

        entity.removeComponent(TransformComponent.class);
        assertFalse(entity.hasComponent(TransformComponent.class));
    }
}
