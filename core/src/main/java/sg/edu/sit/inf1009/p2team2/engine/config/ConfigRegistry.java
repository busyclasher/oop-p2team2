package sg.edu.sit.inf1009.p2team2.engine.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stores configuration variables keyed by name.
 */
public class ConfigRegistry implements IConfigStore {
    private final Map<String, ConfigVar<?>> settings = new LinkedHashMap<>();

    public ConfigRegistry() {
    }

    public ConfigVar<?> find(String key) {
        return settings.get(key);
    }

    public boolean update(String key, ConfigVar<?> var) {
        if (key == null || key.isBlank() || var == null) {
            return false;
        }
        settings.put(key, var);
        return true;
    }

    public void clear() {
        settings.clear();
    }

    public Map<String, ConfigVar<?>> getAll() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(settings));
    }
}
