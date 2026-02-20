package sg.edu.sit.inf1009.p2team2.engine.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ConfigLoaderTest {

    @Test
    void loadFromFileWithoutPathReturnsDefaults() {
        ConfigLoader loader = new ConfigLoader();

        Map<String, ConfigVar<?>> defaults = loader.loadFromFile(null);

        assertTrue(defaults.containsKey(ConfigKeys.DISPLAY_WIDTH.name()));
        assertTrue(defaults.containsKey(ConfigKeys.DISPLAY_HEIGHT.name()));
        assertTrue(defaults.containsKey(ConfigKeys.AUDIO_VOLUME.name()));
    }

    @Test
    void propertiesRoundTripPreservesValues() throws IOException {
        ConfigLoader loader = new ConfigLoader();
        Path file = Files.createTempFile("config", ".properties");

        Map<String, ConfigVar<?>> settings = new LinkedHashMap<>();
        settings.put("display.width", new ConfigVar<>(Integer.valueOf(1280), Integer.valueOf(800)));
        settings.put("audio.volume", new ConfigVar<>(Float.valueOf(0.35f), Float.valueOf(0.7f)));

        loader.saveToFile(file.toString(), settings);
        Map<String, ConfigVar<?>> loaded = loader.loadFromFile(file.toString());

        assertEquals(1280, loaded.get("display.width").asInt());
        assertEquals(0.35f, loaded.get("audio.volume").asFloat(), 0.0001f);
    }

    @Test
    void jsonRoundTripSkipsInvalidEntriesWithoutTrailingCommaIssues() throws IOException {
        ConfigLoader loader = new ConfigLoader();
        Path file = Files.createTempFile("config", ".json");

        Map<String, ConfigVar<?>> settings = new LinkedHashMap<>();
        settings.put("display.title", new ConfigVar<>("Engine", "Engine"));
        settings.put("audio.volume", new ConfigVar<>(Float.valueOf(0.5f), Float.valueOf(0.7f)));
        settings.put(null, new ConfigVar<>("x", "x"));
        settings.put("invalid", null);

        loader.saveToFile(file.toString(), settings);
        Map<String, ConfigVar<?>> loaded = loader.loadFromFile(file.toString());

        assertEquals("Engine", loaded.get("display.title").asString());
        assertEquals(0.5f, loaded.get("audio.volume").asFloat(), 0.0001f);
    }
}
