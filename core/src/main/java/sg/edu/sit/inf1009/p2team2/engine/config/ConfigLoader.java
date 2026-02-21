package sg.edu.sit.inf1009.p2team2.engine.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * File loader/saver for configuration data.
 */
public class ConfigLoader implements IConfigLoader {
    private final List<IConfigFormat> formats;

    public ConfigLoader() {
        this(Arrays.asList(new JsonConfigFormat(), new PropertiesConfigFormat()));
    }

    public ConfigLoader(List<IConfigFormat> formats) {
        this.formats = formats == null ? new ArrayList<>() : new ArrayList<>(formats);
        if (this.formats.isEmpty()) {
            this.formats.add(new PropertiesConfigFormat());
        }
    }

    public Map<String, ConfigVar<?>> loadFromFile(String filePath) {
        Map<String, ConfigVar<?>> defaults = defaultSettings();
        if (filePath == null || filePath.isBlank()) {
            return defaults;
        }

        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return defaults;
        }

        IConfigFormat format = resolveFormat(path);
        try {
            Map<String, ConfigVar<?>> loaded = format.load(path);
            for (Map.Entry<String, ConfigVar<?>> entry : loaded.entrySet()) {
                String key = entry.getKey();
                ConfigVar<?> value = entry.getValue();
                if (key == null || value == null) {
                    continue;
                }
                ConfigVar<?> existing = defaults.get(key);
                if (existing != null) {
                    defaults.put(key, new ConfigVar<>(value.getValue(), existing.getDefaultValue()));
                } else {
                    defaults.put(key, new ConfigVar<>(value.getValue(), value.getValue()));
                }
            }
        } catch (IOException e) {
            System.err.println("[ConfigLoader] Failed to load '" + filePath + "': " + e.getMessage());
            return defaults;
        }
        return defaults;
    }

    public void saveToFile(String filePath, Map<String, ConfigVar<?>> settings) {
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

        IConfigFormat format = resolveFormat(path);
        try {
            format.save(path, settings);
        } catch (IOException e) {
            System.err.println("[ConfigLoader] Failed to save '" + filePath + "': " + e.getMessage());
        }
    }

    private Map<String, ConfigVar<?>> defaultSettings() {
        Map<String, ConfigVar<?>> defaults = new LinkedHashMap<>();
        defaults.put(ConfigKeys.DISPLAY_WIDTH.name(), ConfigKeys.DISPLAY_WIDTH.toVar(ConfigKeys.DISPLAY_WIDTH.defaultValue()));
        defaults.put(ConfigKeys.DISPLAY_HEIGHT.name(), ConfigKeys.DISPLAY_HEIGHT.toVar(ConfigKeys.DISPLAY_HEIGHT.defaultValue()));
        defaults.put(ConfigKeys.DISPLAY_FULLSCREEN.name(), ConfigKeys.DISPLAY_FULLSCREEN.toVar(ConfigKeys.DISPLAY_FULLSCREEN.defaultValue()));
        defaults.put(ConfigKeys.DISPLAY_TITLE.name(), ConfigKeys.DISPLAY_TITLE.toVar(ConfigKeys.DISPLAY_TITLE.defaultValue()));
        defaults.put(ConfigKeys.AUDIO_VOLUME.name(), ConfigKeys.AUDIO_VOLUME.toVar(ConfigKeys.AUDIO_VOLUME.defaultValue()));
        return defaults;
    }

    private IConfigFormat resolveFormat(Path path) {
        for (IConfigFormat format : formats) {
            if (format.supports(path)) {
                return format;
            }
        }
        return formats.get(formats.size() - 1);
    }
}
