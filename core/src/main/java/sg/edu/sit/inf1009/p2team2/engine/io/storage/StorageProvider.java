package sg.edu.sit.inf1009.p2team2.engine.io.storage;

/**
 * Minimal key-value persistence abstraction used by higher-level systems
 * that should not depend directly on framework storage APIs.
 */
public interface StorageProvider {
    boolean getBoolean(String key, boolean defaultValue);

    int getInteger(String key, int defaultValue);

    float getFloat(String key, float defaultValue);

    String getString(String key, String defaultValue);

    void putBoolean(String key, boolean value);

    void putInteger(String key, int value);

    void putFloat(String key, float value);

    void putString(String key, String value);

    void clear();

    void flush();
}
