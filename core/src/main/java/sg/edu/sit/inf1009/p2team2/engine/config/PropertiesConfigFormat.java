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
 * Java properties-backed config serializer implementation.
 */
public class PropertiesConfigFormat implements IConfigFormat {

    @Override
    public boolean supports(Path path) {
        if (path == null) {
            return true;
        }
        String name = path.getFileName().toString().toLowerCase();
        return name.endsWith(".properties") || !name.contains(".");
    }

    @Override
    public Map<String, ConfigVar<?>> load(Path path) throws IOException {
        Map<String, ConfigVar<?>> map = new LinkedHashMap<>();
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(path)) {
            properties.load(in);
        }

        for (String key : properties.stringPropertyNames()) {
            Object parsed = ConfigValueParser.parse(properties.getProperty(key));
            map.put(key, new ConfigVar<>(parsed, parsed));
        }
        return map;
    }

    @Override
    public void save(Path path, Map<String, ConfigVar<?>> settings) throws IOException {
        Properties properties = new Properties();
        for (Map.Entry<String, ConfigVar<?>> entry : settings.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            Object raw = entry.getValue().getValue();
            properties.setProperty(entry.getKey(), raw == null ? "" : String.valueOf(raw));
        }

        try (OutputStream out = Files.newOutputStream(path)) {
            properties.store(out, "P2Team2 engine config");
        }
    }
}
