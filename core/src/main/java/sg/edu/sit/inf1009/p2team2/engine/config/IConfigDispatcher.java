package sg.edu.sit.inf1009.p2team2.engine.config;

/**
 * Dispatch contract for notifying listeners about config changes.
 */
public interface IConfigDispatcher {
    void addObserver(ConfigListener listener);

    void removeObserver(ConfigListener listener);

    void notify(String key, ConfigVar<?> value);
}
