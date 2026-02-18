package sg.edu.sit.inf1009.p2team2.engine.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import sg.edu.sit.inf1009.p2team2.engine.ecs.ComponentAdapter;
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

    public Entity createEntity() {
        Entity entity = new Entity(nextId++);
        addEntity(entity);
        return entity;
    }

    public void addEntity(Entity entity) {
        entities.add(Objects.requireNonNull(entity, "entity cannot be null"));
    }

    public void removeEntity(int id) {
        entities.removeIf(e -> e.getId() == id);
    }

    public Entity getEntity(int id) {
        for (Entity entity : entities) {
            if (entity.getId() == id) {
                return entity;
            }
        }
        return null;
    }

    public List<Entity> getAllEntities() {
        return Collections.unmodifiableList(entities);
    }

    public List<Entity> getEntitiesWithComponent(String componentType) {
        Objects.requireNonNull(componentType, "componentType cannot be null");
        List<Entity> matches = new ArrayList<>();
        for (Entity entity : entities) {
            for (ComponentAdapter component : entity.getAll()) {
                if (component != null
                    && component.getClass().getSimpleName().equals(componentType)) {
                    matches.add(entity);
                    break;
                }
            }
        }
        return matches;
    }

    @SafeVarargs
    public final List<Entity> getWith(Class<? extends ComponentAdapter>... componentTypes) {
        Objects.requireNonNull(componentTypes, "componentTypes cannot be null");
        List<Entity> matches = new ArrayList<>();
        for (Entity entity : entities) {
            if (hasAllComponents(entity, componentTypes)) {
                matches.add(entity);
            }
        }
        return matches;
    }

    private boolean hasAllComponents(Entity entity, Class<? extends ComponentAdapter>[] componentTypes) {
        for (Class<? extends ComponentAdapter> componentType : componentTypes) {
            if (componentType == null || !entity.has(componentType)) {
                return false;
            }
        }
        return true;
    }

    public void update(float dt) {
        // Intentionally lightweight for skeleton phase.
    }

    public void clear() {
        entities.clear();
    }

    public int size() {
        return entities.size();
    }

    // Compatibility wrappers for older callsites.

    public Entity create() {
        return createEntity();
    }

    public void add(Entity entity) {
        addEntity(entity);
    }

    public void remove(int id) {
        removeEntity(id);
    }

    public Entity getById(int id) {
        return getEntity(id);
    }

    public List<Entity> getAll() {
        return getAllEntities();
    }
}
