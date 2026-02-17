package sg.edu.sit.inf1009.p2team2.engine.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.TransformComponent;

class EntityManagerUmlApiTest {

    @Test
    void umlManagerMethodsWork() {
        EntityManager manager = new EntityManager();
        Entity entity = manager.createEntity();
        entity.add(new TransformComponent());

        assertEquals(entity, manager.getEntity(entity.getId()));
        assertEquals(1, manager.getAllEntities().size());
        assertEquals(1, manager.getEntitiesWithComponent("TransformComponent").size());

        manager.removeEntity(entity.getId());
        assertEquals(0, manager.size());
    }
}
