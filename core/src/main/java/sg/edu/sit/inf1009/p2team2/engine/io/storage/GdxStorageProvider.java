package sg.edu.sit.inf1009.p2team2.engine.io.storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * libGDX-backed storage provider implementation.
 */
public final class GdxStorageProvider implements StorageProvider {
    private final Preferences preferences;

    public GdxStorageProvider(String preferencesName) {
        this.preferences = Gdx.app.getPreferences(preferencesName);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    @Override
    public int getInteger(String key, int defaultValue) {
        return preferences.getInteger(key, defaultValue);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return preferences.getFloat(key, defaultValue);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        preferences.putBoolean(key, value);
    }

    @Override
    public void putInteger(String key, int value) {
        preferences.putInteger(key, value);
    }

    @Override
    public void putFloat(String key, float value) {
        preferences.putFloat(key, value);
    }

    @Override
    public void putString(String key, String value) {
        preferences.putString(key, value);
    }

    @Override
    public void clear() {
        preferences.clear();
    }

    @Override
    public void flush() {
        preferences.flush();
    }
}
