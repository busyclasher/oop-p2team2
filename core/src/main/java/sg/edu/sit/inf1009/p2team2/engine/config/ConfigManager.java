package sg.edu.sit.inf1009.p2team2.engine.config;

import java.util.Map;
import java.util.Objects;

/**
 * UML-aligned configuration manager.
 */
public final class ConfigManager {
    private static volatile ConfigManager instance;
    private final IConfigStore configStore;
    private final IConfigLoader configLoader;
    private final IConfigDispatcher configDispatcher;
    private String lastLoadedPath;

    public ConfigManager(IConfigStore configStore, IConfigLoader configLoader, IConfigDispatcher configDispatcher) {
        this(configStore, configLoader, configDispatcher, "");
    }

    public ConfigManager(IConfigStore configStore, IConfigLoader configLoader, IConfigDispatcher configDispatcher, String lastLoadedPath) {
        this.configStore = Objects.requireNonNull(configStore, "configStore");
        this.configLoader = Objects.requireNonNull(configLoader, "configLoader");
        this.configDispatcher = Objects.requireNonNull(configDispatcher, "configDispatcher");
        this.lastLoadedPath = lastLoadedPath == null ? "" : lastLoadedPath;
        if (instance == null) {
            instance = this;
        }
    }

    public static ConfigManager getInstance() {
        ConfigManager local = instance;
        if (local != null) {
            return local;
        }
        synchronized (ConfigManager.class) {
            if (instance == null) {
                instance = new ConfigManager(new ConfigRegistry(), new ConfigLoader(), new ConfigDispatcher());
            }
            return instance;
        }
    }

    public void load(String filePath) {
        Map<String, ConfigVar<?>> loaded = configLoader.loadFromFile(filePath);
        for (Map.Entry<String, ConfigVar<?>> entry : loaded.entrySet()) {
            applyLoadedValue(entry.getKey(), entry.getValue());
        }
        if (filePath != null && !filePath.isBlank()) {
            lastLoadedPath = filePath;
        }
    }

    public void save(String filePath) {
        String targetPath = (filePath == null || filePath.isBlank()) ? lastLoadedPath : filePath;
        configLoader.saveToFile(targetPath, configStore.getAll());
    }

    public <T> T get(ConfigKey<T> key) {
        if (key == null) {
            throw new IllegalArgumentException("Config key cannot be null");
        }
        ConfigVar<?> value = configStore.find(key.name());
        if (value == null) {
            return key.defaultValue();
        }
        return key.read(value);
    }

    public <T> void set(ConfigKey<T> key, T value) {
        if (key == null) {
            throw new IllegalArgumentException("Config key cannot be null");
        }
        if (!key.isValid(value)) {
            throw new IllegalArgumentException("Invalid config value for key '" + key.name() + "'. " + key.description());
        }
        ConfigVar<?> oldValue = configStore.find(key.name());
        ConfigVar<T> newValue = key.toVar(value);
        boolean updated = configStore.update(key.name(), newValue);
        if (updated && hasValueChanged(oldValue, newValue)) {
            notifyObservers(key.name(), oldValue, newValue);
        }
    }

    public ConfigVar<?> get(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        return configStore.find(key);
    }

    public void set(String key, ConfigVar<?> value) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Config key cannot be null or blank");
        }
        if (value == null) {
            throw new IllegalArgumentException("Config value cannot be null");
        }
        if (!ConfigKeys.isValid(key, value)) {
            throw new IllegalArgumentException("Invalid config value for key '" + key + "'");
        }

        ConfigVar<?> oldValue = configStore.find(key);
        boolean updated = configStore.update(key, value);
        if (updated && hasValueChanged(oldValue, value)) {
            notifyObservers(key, oldValue, value);
        }
    }

    public int getInt(String key, int fallback) {
        ConfigVar<?> value = get(key);
        return value == null ? fallback : value.asInt();
    }

    public float getFloat(String key, float fallback) {
        ConfigVar<?> value = get(key);
        return value == null ? fallback : value.asFloat();
    }

    public boolean getBool(String key, boolean fallback) {
        ConfigVar<?> value = get(key);
        return value == null ? fallback : value.asBool();
    }

    public String getString(String key, String fallback) {
        ConfigVar<?> value = get(key);
        return value == null ? fallback : value.asString();
    }

    public boolean has(ConfigKey<?> key) {
        if (key == null) {
            return false;
        }
        return configStore.find(key.name()) != null;
    }

    public void addObserver(ConfigListener observer) {
        configDispatcher.addObserver(observer);
    }

    public void removeObserver(ConfigListener observer) {
        configDispatcher.removeObserver(observer);
    }

    private void notifyObservers(String key, ConfigVar<?> oldValue, ConfigVar<?> newValue) {
        configDispatcher.notify(key, newValue);
    }

    private boolean hasValueChanged(ConfigVar<?> oldValue, ConfigVar<?> newValue) {
        if (oldValue == newValue) {
            return false;
        }
        return !Objects.equals(oldValue == null ? null : oldValue.getValue(), newValue == null ? null : newValue.getValue());
    }

    private void applyLoadedValue(String key, ConfigVar<?> value) {
        if (key == null || value == null) {
            return;
        }
        ConfigKey<?> coreKey = ConfigKeys.find(key);
        if (coreKey != null && !coreKey.isValidVar(value)) {
            return;
        }

        ConfigVar<?> oldValue = configStore.find(key);
        boolean updated = configStore.update(key, value);
        if (updated && hasValueChanged(oldValue, value)) {
            notifyObservers(key, oldValue, value);
        }
    }
}
