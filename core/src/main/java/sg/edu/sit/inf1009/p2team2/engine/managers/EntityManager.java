package sg.edu.sit.inf1009.p2team2.engine.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;

/**
 * Manages creation and storage of entities in the engine.
 *
 * Note: This is intentionally lightweight; teams can extend it with indexing,
 * queries, pooling, etc.
 */
public class EntityManager {
    private final List<Entity> entities = new ArrayList<>();
    private int nextId = 1;

    public EntityManager() {
    }

    public Entity create() {
        Entity entity = new Entity(nextId++);
        add(entity);
        return entity;
    }

    public void add(Entity entity) {
        entities.add(Objects.requireNonNull(entity, "entity cannot be null"));
    }

    public void remove(int id) {
        entities.removeIf(e -> e.getId() == id);
    }

    public Entity getById(int id) {
        for (Entity entity : entities) {
            if (entity.getId() == id) {
                return entity;
            }
        }
        return null;
    }

    public List<Entity> getAll() {
        return Collections.unmodifiableList(entities);
    }

    @SafeVarargs
    public final List<Entity> getWith(Class<? extends sg.edu.sit.inf1009.p2team2.engine.ecs.Component>... componentTypes) {
        Objects.requireNonNull(componentTypes, "componentTypes cannot be null");
        List<Entity> matches = new ArrayList<>();
        for (Entity entity : entities) {
            if (hasAllComponents(entity, componentTypes)) {
                matches.add(entity);
            }
        }
        return matches;
    }

    private boolean hasAllComponents(Entity entity,
                                     Class<? extends sg.edu.sit.inf1009.p2team2.engine.ecs.Component>[] componentTypes) {
        for (Class<? extends sg.edu.sit.inf1009.p2team2.engine.ecs.Component> componentType : componentTypes) {
            if (componentType == null || !entity.has(componentType)) {
                return false;
            }
        }
        return true;
    }

    public void clear() {
        entities.clear();
    }

    public int size() {
        return entities.size();
    }
}

