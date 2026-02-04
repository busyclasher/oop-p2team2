package sg.edu.sit.inf1009.p2team2.engine.config;

/**
 * Represents a configuration layer loaded from disk.
 *
 * Format/parsing is intentionally left to the implementation team.
 */
public class ConfigFile {
    private final String filePath;

    public ConfigFile(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void reload() {
        // TODO(Alvin): load/parse config file content into ConfigVar entries.
    }

    public void save() {
        // TODO(Alvin): persist current config values to disk.
    }
}

