package sg.edu.sit.inf1009.p2team2.engine.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * File loader/saver for configuration data.
 *
 * Skeleton behavior: return defaults and keep save as no-op for now.
 */
public class ConfigLoader implements IConfigLoader {

    public ConfigLoader() {
    }

    public Map<String, ConfigVar> loadFromFile(String filePath) {
        Map<String, ConfigVar> defaults = defaultSettings();
        if (filePath == null || filePath.isBlank()) {
            return defaults;
        }

        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return defaults;
        }

        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(path)) {
            properties.load(in);
        } catch (IOException e) {
            System.err.println("[ConfigLoader] Failed to load '" + filePath + "': " + e.getMessage());
            return defaults;
        }

        for (String key : properties.stringPropertyNames()) {
            String rawValue = properties.getProperty(key);
            Object parsed = parseValue(rawValue);
            ConfigVar existing = defaults.get(key);
            if (existing != null) {
                defaults.put(key, new ConfigVar(parsed, existing.getDefaultValue()));
            } else {
                defaults.put(key, new ConfigVar(parsed, parsed));
            }
        }
        return defaults;
    }

    public void saveToFile(String filePath, Map<String, ConfigVar> settings) {
        if (filePath == null || filePath.isBlank() || settings == null) {
            return;
        }

        Path path = Path.of(filePath);
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
        } catch (IOException e) {
            System.err.println("[ConfigLoader] Failed to create config directory for '" + filePath + "': " + e.getMessage());
            return;
        }

        Properties properties = new Properties();
        for (Map.Entry<String, ConfigVar> entry : settings.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            properties.setProperty(entry.getKey(), entry.getValue().asString());
        }

        try (OutputStream out = Files.newOutputStream(path)) {
            properties.store(out, "P2Team2 engine config");
        } catch (IOException e) {
            System.err.println("[ConfigLoader] Failed to save '" + filePath + "': " + e.getMessage());
        }
    }

    private Map<String, ConfigVar> defaultSettings() {
        Map<String, ConfigVar> defaults = new LinkedHashMap<>();
        defaults.put(ConfigKeys.DISPLAY_WIDTH.name(), ConfigKeys.DISPLAY_WIDTH.toVar(ConfigKeys.DISPLAY_WIDTH.defaultValue()));
        defaults.put(ConfigKeys.DISPLAY_HEIGHT.name(), ConfigKeys.DISPLAY_HEIGHT.toVar(ConfigKeys.DISPLAY_HEIGHT.defaultValue()));
        defaults.put(ConfigKeys.DISPLAY_FULLSCREEN.name(), ConfigKeys.DISPLAY_FULLSCREEN.toVar(ConfigKeys.DISPLAY_FULLSCREEN.defaultValue()));
        defaults.put(ConfigKeys.DISPLAY_TITLE.name(), ConfigKeys.DISPLAY_TITLE.toVar(ConfigKeys.DISPLAY_TITLE.defaultValue()));
        defaults.put(ConfigKeys.AUDIO_VOLUME.name(), ConfigKeys.AUDIO_VOLUME.toVar(ConfigKeys.AUDIO_VOLUME.defaultValue()));
        return defaults;
    }

    private Object parseValue(String rawValue) {
        if (rawValue == null) {
            return "";
        }
        String value = rawValue.trim();
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return Boolean.parseBoolean(value);
        }
        try {
            if (value.contains(".")) {
                return Float.parseFloat(value);
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return value;
        }
    }
}
