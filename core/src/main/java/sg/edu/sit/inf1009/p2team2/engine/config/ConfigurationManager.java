package sg.edu.sit.inf1009.p2team2.engine.config;

/**
 * Backward-compatible wrapper for earlier naming.
 */
public final class ConfigurationManager {
    private ConfigurationManager() {
    }

    public static ConfigManager getInstance() {
        return ConfigManager.getInstance();
    }
}
