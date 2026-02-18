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

    public void notifyObservers(String key, ConfigVar oldValue, ConfigVar newValue) {
        configDispatcher.notify(key, newValue);
    }
}
