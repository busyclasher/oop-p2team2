package sg.edu.sit.inf1009.p2team2.engine.config;

import java.util.Map;

/**
 * Storage contract for in-memory config value lookup and updates.
 */
public interface IConfigStore {
    ConfigVar<?> find(String key);

    boolean update(String key, ConfigVar<?> var);

    void clear();

    Map<String, ConfigVar<?>> getAll();
}
