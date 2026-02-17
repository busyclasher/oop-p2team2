package sg.edu.sit.inf1009.p2team2.engine.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ConfigLoaderTest {

    @Test
    void saveAndLoadRoundTripWorks() throws Exception {
        ConfigLoader loader = new ConfigLoader();
        Map<String, ConfigVar> settings = new LinkedHashMap<>();
        settings.put("display.width", new ConfigVar(1024, 800));
        settings.put("display.fullscreen", new ConfigVar(true, false));
        settings.put("audio.volume", new ConfigVar(0.4f, 0.7f));

        Path tempFile = Files.createTempFile("p2-config", ".properties");
        loader.saveToFile(tempFile.toString(), settings);

        Map<String, ConfigVar> loaded = loader.loadFromFile(tempFile.toString());

        assertNotNull(loaded.get("display.width"));
        assertEquals(1024, loaded.get("display.width").asInt());
        assertEquals(true, loaded.get("display.fullscreen").asBool());
        assertEquals(0.4f, loaded.get("audio.volume").asFloat(), 0.0001f);
    }
}
