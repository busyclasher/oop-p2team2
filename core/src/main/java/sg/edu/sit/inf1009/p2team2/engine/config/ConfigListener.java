package sg.edu.sit.inf1009.p2team2.engine.config;

/**
 * Observer for configuration changes (e.g., volume, fullscreen).
 */
public interface ConfigListener {
    void onConfigChanged(String key, ConfigVar val);
}
