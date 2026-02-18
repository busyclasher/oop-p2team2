package sg.edu.sit.inf1009.p2team2.engine.ecs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic ECS entity.
 *
 * Stores components in a map keyed by the component's class type.
 */
public class Entity {
    private final int id;
    private final Map<Class<? extends ComponentAdapter>, ComponentAdapter> components = new HashMap<>();

    public Entity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void add(ComponentAdapter component) {
        if (component == null) {
            throw new IllegalArgumentException("component cannot be null");
        }
        components.put(component.getClass(), component);
    }

    public void remove(Class<? extends ComponentAdapter> componentType) {
        if (componentType == null) {
            return;
        }
        components.remove(componentType);
    }

    @SuppressWarnings("unchecked")
    public <T extends ComponentAdapter> T get(Class<T> componentType) {
        if (componentType == null) {
            return null;
        }
        return (T) components.get(componentType);
    }

    public boolean has(Class<? extends ComponentAdapter> componentType) {
        if (componentType == null) {
            return false;
        }
        return components.containsKey(componentType);
    }

    public Collection<ComponentAdapter> getAll() {
        return components.values();
    }

    public void clear() {
        components.clear();
    }

    // UML-friendly aliases kept with explicit names for team usage.

    public void addComponent(ComponentAdapter component) {
        add(component);
    }

    public void removeComponent(Class<? extends ComponentAdapter> componentType) {
        remove(componentType);
    }

    public <T extends ComponentAdapter> T getComponent(Class<T> componentType) {
        return get(componentType);
    }

    public boolean hasComponent(Class<? extends ComponentAdapter> componentType) {
        return has(componentType);
    }

    public Collection<ComponentAdapter> getAllComponents() {
        return getAll();
    }
}
