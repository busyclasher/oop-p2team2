package sg.edu.sit.inf1009.p2team2.engine.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a configuration layer loaded from disk.
 *
 * Format/parsing is intentionally left to the implementation team.
 */
public class ConfigFile {
    private final String filePath;
    private final ConfigLoader loader;
    private final Map<String, ConfigVar> settings;

    public ConfigFile(String filePath) {
        this.filePath = filePath;
        this.loader = new ConfigLoader();
        this.settings = new LinkedHashMap<>();
    }

    public String getFilePath() {
        return filePath;
    }

    public void reload() {
        settings.clear();
        settings.putAll(loader.loadFromFile(filePath));
    }

    public void save() {
        loader.saveToFile(filePath, settings);
    }

    public Map<String, ConfigVar> getSettings() {
        return settings;
    }
}
