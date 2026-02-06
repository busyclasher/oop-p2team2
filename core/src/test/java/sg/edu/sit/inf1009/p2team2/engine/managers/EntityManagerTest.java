package sg.edu.sit.inf1009.p2team2.engine.managers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import sg.edu.sit.inf1009.p2team2.engine.ecs.Component;
import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.ColliderComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.VelocityComponent;

import java.util.List;


class EntityManagerTest {

    @Test
    void createAssignsIdsAndAddsEntities() {
        EntityManager manager = new EntityManager();

        Entity first = manager.create();
        Entity second = manager.create();

        assertEquals(1, first.getId());
        assertEquals(2, second.getId());
        assertEquals(2, manager.size());
        assertTrue(manager.getAll().contains(first));
        assertTrue(manager.getAll().contains(second));
    }

    @Test
    void addAndRemoveEntitiesById() {
        EntityManager manager = new EntityManager();
        Entity entity = new Entity(10);
        manager.add(entity);

        assertEquals(entity, manager.getById(10));

        manager.remove(10);

        assertNull(manager.getById(10));
        assertEquals(0, manager.size());
    }

    @Test
    void getAllReturnsUnmodifiableView() {
        EntityManager manager = new EntityManager();
        manager.add(new Entity(1));

        List<Entity> all = manager.getAll();
        assertThrows(UnsupportedOperationException.class, () -> all.add(new Entity(2)));
    }

    @Test
    void getWithFiltersEntitiesByComponents() {
        EntityManager manager = new EntityManager();

        Entity withTransform = new Entity(1);
        withTransform.add(new TransformComponent());

        Entity withAll = new Entity(2);
        withAll.add(new TransformComponent());
        withAll.add(new VelocityComponent());
        withAll.add(new ColliderComponent());

        Entity withVelocity = new Entity(3);
        withVelocity.add(new VelocityComponent());

        manager.add(withTransform);
        manager.add(withAll);
        manager.add(withVelocity);

        List<Entity> result = manager.getWith(TransformComponent.class, VelocityComponent.class);

        assertEquals(1, result.size());
        assertEquals(withAll, result.get(0));
    }

    @Test
    void getWithRejectsNullComponentArray() {
        EntityManager manager = new EntityManager();
        assertThrows(NullPointerException.class,
            () -> manager.getWith((Class<? extends Component>[]) null));

    }

    @Test
    void clearRemovesAllEntities() {
        EntityManager manager = new EntityManager();
        manager.add(new Entity(1));
        manager.add(new Entity(2));

        manager.clear();

        assertEquals(0, manager.size());
        assertTrue(manager.getAll().isEmpty());
    }
}
