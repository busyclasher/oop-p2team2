package sg.edu.sit.inf1009.p2team2.engine.config;

import java.util.Map;

public interface IConfigLoader {
    Map<String, ConfigVar> loadFromFile(String filePath);

    void saveToFile(String filePath, Map<String, ConfigVar> settings);
}
