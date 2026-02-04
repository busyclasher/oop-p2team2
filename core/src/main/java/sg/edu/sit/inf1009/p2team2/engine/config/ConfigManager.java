package sg.edu.sit.inf1009.p2team2.engine.config;

/**
 * Public API for configuration access in the engine.
 *
 * Keep the API small and stable; implementations can add type-safe wrappers on top.
 */
public interface ConfigManager {
    void loadConfig(String filePath);

    void saveConfig();

    void resetToDefaults();

    float getFloat(String key);

    int getInt(String key);

    boolean getBool(String key);

    String getString(String key);

    void setValue(String name, float value);

    void addObserver(ConfigListener listener);

    void reloadFromDisk();
}

