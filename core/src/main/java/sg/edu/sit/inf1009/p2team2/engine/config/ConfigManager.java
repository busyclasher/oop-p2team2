package sg.edu.sit.inf1009.p2team2.engine.config;

import java.util.Map;

/**
 * UML-aligned configuration manager singleton.
 */
public final class ConfigManager {
    private static ConfigManager instance;

    private final ConfigRegistry configStore;
    private final ConfigLoader configLoader;
    private final ConfigDispatcher configDispatcher;
    private String lastLoadedPath;

    private ConfigManager() {
        this.configStore = new ConfigRegistry();
        this.configLoader = new ConfigLoader();
        this.configDispatcher = new ConfigDispatcher();
        this.lastLoadedPath = "";
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
            configStore.update(entry.getKey(), entry.getValue());
            notifyObservers(entry.getKey(), oldValue, entry.getValue());
        }
        if (filePath != null) {
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

    public void set(String key, ConfigVar value) {
        ConfigVar oldValue = configStore.find(key);
        configStore.update(key, value);
        notifyObservers(key, oldValue, value);
    }

    public void addObserver(ConfigListener observer) {
        configDispatcher.addObserver(observer);
    }

    public void removeObserver(ConfigListener observer) {
        configDispatcher.removeObserver(observer);
    }

    private void notifyObservers(String key, ConfigVar oldValue, ConfigVar newValue) {
        configDispatcher.notify(key, newValue);
    }

    // Compatibility wrappers for existing scene/context call sites.

    public void loadConfig(String filePath) {
        load(filePath);
    }

    public void saveConfig() {
        save(lastLoadedPath);
    }

    public void resetToDefaults() {
        for (ConfigVar value : configStore.getAll().values()) {
            if (value != null) {
                value.reset();
            }
        }
    }

    public float getFloat(String key) {
        ConfigVar value = get(key);
        return value == null ? 0f : value.asFloat();
    }

    public int getInt(String key) {
        ConfigVar value = get(key);
        return value == null ? 0 : value.asInt();
    }

    public boolean getBool(String key) {
        ConfigVar value = get(key);
        return value != null && value.asBool();
    }

    public String getString(String key) {
        ConfigVar value = get(key);
        return value == null ? "" : value.asString();
    }

    public void setValue(String name, Object value) {
        ConfigVar current = get(name);
        ConfigVar target = current == null ? new ConfigVar(value, value) : current;
        if (current == null) {
            set(name, target);
        } else {
            ConfigVar oldValue = new ConfigVar(current.getValue(), current.getDefaultValue());
            current.setValue(value);
            notifyObservers(name, oldValue, current);
        }
    }

    public void setValue(String name, float value) {
        setValue(name, Float.valueOf(value));
    }

    public void reloadFromDisk() {
        if (lastLoadedPath != null && !lastLoadedPath.isBlank()) {
            load(lastLoadedPath);
        }
    }

    ConfigRegistry getConfigStore() {
        return configStore;
    }

    ConfigLoader getConfigLoader() {
        return configLoader;
    }

    ConfigDispatcher getConfigDispatcher() {
        return configDispatcher;
    }
}
