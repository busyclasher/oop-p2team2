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
public class ConfigLoader {

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
        } catch (IOException ignored) {
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
        } catch (IOException ignored) {
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
        } catch (IOException ignored) {
            // Intentionally no-op for skeleton robustness.
        }
    }

    private Map<String, ConfigVar> defaultSettings() {
        Map<String, ConfigVar> defaults = new LinkedHashMap<>();
        defaults.put("display.width", new ConfigVar(800, 800));
        defaults.put("display.height", new ConfigVar(600, 600));
        defaults.put("display.fullscreen", new ConfigVar(true, false));
        defaults.put("display.title", new ConfigVar("P2Team2AbstractEngine", "P2Team2AbstractEngine"));
        defaults.put("audio.volume", new ConfigVar(0.7f, 0.7f));
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
