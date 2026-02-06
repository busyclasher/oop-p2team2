package sg.edu.sit.inf1009.p2team2.engine.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton implementation of {@link ConfigManager}.
 *
 * This is part of the abstract engine (non-contextual). Keep configuration keys and
 * semantics out of this class; those belong to the simulation/game layer.
 */
public final class ConfigurationManager implements ConfigManager {
    private static ConfigManager instance;

    private final Map<String, ConfigVar> settings = new HashMap<>();
    private final List<ConfigListener> observers = new ArrayList<>();
    private final List<ConfigFile> configLayers = new ArrayList<>();

    private ConfigurationManager() {
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    @Override
    public void loadConfig(String filePath) {
        // TODO(Alvin): load config from filePath into configLayers/settings and notify observers.
    }

    @Override
    public void saveConfig() {
        // TODO(Alvin): save current settings back to disk.
    }

    @Override
    public void resetToDefaults() {
        // TODO(Alvin): reset settings map back to ConfigVar defaults.
    }

    @Override
    public float getFloat(String key) {
        // TODO(Alvin): read typed value from settings/config layers.
        return 0f;
    }

    @Override
    public int getInt(String key) {
        // TODO(Alvin): read typed value from settings/config layers.
        return 0;
    }

    @Override
    public boolean getBool(String key) {
        // TODO(Alvin): read typed value from settings/config layers.
        return false;
    }

    @Override
    public String getString(String key) {
        // TODO(Alvin): read typed value from settings/config layers.
        return "";
    }

    @Override
    public void setValue(String name, float value) {
        // TODO(Alvin): update setting and notify observers.
    }

    @Override
    public void addObserver(ConfigListener listener) {
        // TODO(Alvin): manage observer list.
    }

    @Override
    public void reloadFromDisk() {
        // TODO(Alvin): reload all config layers and re-apply overrides.
    }

    // Package-private getters can help with testing/inspection without exposing internals publicly.
    Map<String, ConfigVar> getSettings() {
        return settings;
    }

    List<ConfigListener> getObservers() {
        return observers;
    }

    List<ConfigFile> getConfigLayers() {
        return configLayers;
    }

    @Override
    public void setValue(String name, Object value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setValue'");
    }
}

