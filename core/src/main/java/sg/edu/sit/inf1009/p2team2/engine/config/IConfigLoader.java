package sg.edu.sit.inf1009.p2team2.engine.config;

import java.util.Map;

/**
 * Loader contract for reading/writing config values from persistent storage.
 */
public interface IConfigLoader {
    Map<String, ConfigVar<?>> loadFromFile(String filePath);

    void saveToFile(String filePath, Map<String, ConfigVar<?>> settings);
}
