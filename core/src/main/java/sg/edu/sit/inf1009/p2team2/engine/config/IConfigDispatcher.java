package sg.edu.sit.inf1009.p2team2.engine.config;

public interface IConfigDispatcher {
    void addObserver(ConfigListener listener);

    void removeObserver(ConfigListener listener);

    void notify(String key, ConfigVar<?> value);
}
