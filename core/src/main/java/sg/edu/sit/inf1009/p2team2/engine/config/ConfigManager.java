package sg.edu.sit.inf1009.p2team2.engine.config;

import java.util.Map;
import java.util.Objects;

/**
 * UML-aligned configuration manager singleton.
 */
public final class ConfigManager {
    private static ConfigManager instance;

    private final IConfigStore configStore;
    private final IConfigLoader configLoader;
    private final IConfigDispatcher configDispatcher;
    private String lastLoadedPath;

    private ConfigManager() {
        this(new ConfigRegistry(), new ConfigLoader(), new ConfigDispatcher(), "");
    }

    public ConfigManager(IConfigStore configStore, IConfigLoader configLoader, IConfigDispatcher configDispatcher) {
        this(configStore, configLoader, configDispatcher, "");
    }

    public ConfigManager(IConfigStore configStore, IConfigLoader configLoader, IConfigDispatcher configDispatcher, String lastLoadedPath) {
        this.configStore = Objects.requireNonNull(configStore, "configStore");
        this.configLoader = Objects.requireNonNull(configLoader, "configLoader");
        this.configDispatcher = Objects.requireNonNull(configDispatcher, "configDispatcher");
        this.lastLoadedPath = lastLoadedPath == null ? "" : lastLoadedPath;
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public void load(String filePath) {
        Map<String, ConfigVar> loaded = configLoader.loadFromFile(filePath);
        for (Map.Entry<String, ConfigVar> entry : loaded.entrySet()) {
            ConfigVar oldValue = configStore.find(entry.getKey());
            boolean updated = configStore.update(entry.getKey(), entry.getValue());
            if (updated && hasValueChanged(oldValue, entry.getValue())) {
                notifyObservers(entry.getKey(), oldValue, entry.getValue());
            }
        }
        if (filePath != null && !filePath.isBlank()) {
            lastLoadedPath = filePath;
        }
    }

    public void save(String filePath) {
        String targetPath = (filePath == null || filePath.isBlank()) ? lastLoadedPath : filePath;
        configLoader.saveToFile(targetPath, configStore.getAll());
    }

    public ConfigVar get(String key) {
        return configStore.find(key);
    }

    public <T> T get(ConfigKey<T> key) {
        if (key == null) {
            return null;
        }
        return key.read(get(key.name()));
    }

    public void set(String key, ConfigVar value) {
        ConfigVar oldValue = configStore.find(key);
        boolean updated = configStore.update(key, value);
        if (!updated) {
            return;
        }
        if (hasValueChanged(oldValue, value)) {
            notifyObservers(key, oldValue, value);
        }
    }

    public <T> void set(ConfigKey<T> key, T value) {
        if (key == null) {
            return;
        }
        set(key.name(), key.toVar(value));
    }

    public int getInt(String key, int fallback) {
        ConfigVar var = get(key);
        return var == null ? fallback : var.asInt();
    }

    public float getFloat(String key, float fallback) {
        ConfigVar var = get(key);
        return var == null ? fallback : var.asFloat();
    }

    public boolean getBool(String key, boolean fallback) {
        ConfigVar var = get(key);
        return var == null ? fallback : var.asBool();
    }

    public String getString(String key, String fallback) {
        ConfigVar var = get(key);
        return var == null ? fallback : var.asString();
    }

    public void addObserver(ConfigListener observer) {
        configDispatcher.addObserver(observer);
    }

    public void removeObserver(ConfigListener observer) {
        configDispatcher.removeObserver(observer);
    }

    public void notifyObservers(String key, ConfigVar oldValue, ConfigVar newValue) {
        configDispatcher.notify(key, newValue);
    }

    private boolean hasValueChanged(ConfigVar oldValue, ConfigVar newValue) {
        if (oldValue == newValue) {
            return false;
        }
        return !Objects.equals(oldValue == null ? null : oldValue.getValue(), newValue == null ? null : newValue.getValue());
    }
}
