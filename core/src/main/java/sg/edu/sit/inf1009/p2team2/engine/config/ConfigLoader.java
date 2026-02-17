package sg.edu.sit.inf1009.p2team2.engine.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * File loader/saver for configuration data.
 *
 * Skeleton behavior: return defaults and keep save as no-op for now.
 */
public class ConfigLoader {

    public ConfigLoader() {
    }

    public Map<String, ConfigVar> loadFromFile(String filePath) {
        Map<String, ConfigVar> defaults = new LinkedHashMap<>();
        defaults.put("display.width", new ConfigVar(800, 800));
        defaults.put("display.height", new ConfigVar(600, 600));
        defaults.put("display.fullscreen", new ConfigVar(false, false));
        defaults.put("display.title", new ConfigVar("P2Team2AbstractEngine", "P2Team2AbstractEngine"));
        defaults.put("audio.volume", new ConfigVar(0.7f, 0.7f));
        return defaults;
    }

    public void saveToFile(String filePath, Map<String, ConfigVar> settings) {
        // TODO: Persist settings to disk format chosen by config owner.
    }
}
