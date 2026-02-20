package sg.edu.sit.inf1009.p2team2.engine.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Broadcasts configuration changes to registered listeners.
 */
public class ConfigDispatcher implements IConfigDispatcher {
    private final List<ConfigListener> observers = new ArrayList<>();

    public ConfigDispatcher() {
    }

    public void addObserver(ConfigListener listener) {
        if (listener == null || observers.contains(listener)) {
            return;
        }
        observers.add(listener);
    }

    public void removeObserver(ConfigListener listener) {
        observers.remove(listener);
    }

    public void notify(String key, ConfigVar<?> value) {
        List<ConfigListener> snapshot = new ArrayList<>(observers);
        for (ConfigListener listener : snapshot) {
            listener.onConfigChanged(key, value);
        }
    }
}
