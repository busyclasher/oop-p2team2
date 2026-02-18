package sg.edu.sit.inf1009.p2team2.engine.managers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.ColliderComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.VelocityComponent;

import java.util.List;


class EntityManagerTest {

    @Test
    void createAssignsIdsAndAddsEntities() {
        EntityManager manager = new EntityManager();

        Entity first = manager.createEntity();
        Entity second = manager.createEntity();

        assertEquals(1, first.getId());
        assertEquals(2, second.getId());
        assertEquals(2, manager.size());
        assertTrue(manager.getAllEntities().contains(first));
        assertTrue(manager.getAllEntities().contains(second));
    }

    @Test
    void addAndRemoveEntitiesById() {
        EntityManager manager = new EntityManager();
        Entity entity = new Entity(10);
        manager.addEntity(entity);

        assertEquals(entity, manager.getEntity(10));

        manager.removeEntity(10);

        assertNull(manager.getEntity(10));
        assertEquals(0, manager.size());
    }

    @Test
    void getAllReturnsUnmodifiableView() {
        EntityManager manager = new EntityManager();
        manager.addEntity(new Entity(1));

        List<Entity> all = manager.getAllEntities();
        assertThrows(UnsupportedOperationException.class, () -> all.add(new Entity(2)));
    }

    @Test
    void getEntitiesWithComponentFiltersBySimpleName() {
        EntityManager manager = new EntityManager();

        Entity withTransform = new Entity(1);
        withTransform.add(new TransformComponent());

        Entity withAll = new Entity(2);
        withAll.add(new TransformComponent());
        withAll.add(new VelocityComponent());
        withAll.add(new ColliderComponent());

        Entity withVelocity = new Entity(3);
        withVelocity.add(new VelocityComponent());

        manager.addEntity(withTransform);
        manager.addEntity(withAll);
        manager.addEntity(withVelocity);

        List<Entity> transformResult = manager.getEntitiesWithComponent("TransformComponent");
        List<Entity> colliderResult = manager.getEntitiesWithComponent("ColliderComponent");

        assertEquals(2, transformResult.size());
        assertTrue(transformResult.contains(withTransform));
        assertTrue(transformResult.contains(withAll));

        assertEquals(1, colliderResult.size());
        assertEquals(withAll, colliderResult.get(0));
    }

    @Test
    void getEntitiesWithComponentRejectsNullComponentName() {
        EntityManager manager = new EntityManager();
        assertThrows(NullPointerException.class, () -> manager.getEntitiesWithComponent(null));
    }

    @Test
    void clearRemovesAllEntities() {
        EntityManager manager = new EntityManager();
        manager.addEntity(new Entity(1));
        manager.addEntity(new Entity(2));

        manager.clear();

        assertEquals(0, manager.size());
        assertTrue(manager.getAllEntities().isEmpty());
    }
}
