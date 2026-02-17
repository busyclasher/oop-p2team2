package sg.edu.sit.inf1009.p2team2.engine.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class ConfigFileTest {

    @Test
    void reloadPopulatesSettingsMap() throws Exception {
        Path tempFile = Files.createTempFile("p2-config-file", ".properties");
        Files.writeString(tempFile, "display.width=900\naudio.volume=0.5\n");

        ConfigFile configFile = new ConfigFile(tempFile.toString());
        configFile.reload();

        assertNotNull(configFile.getSettings().get("display.width"));
        assertNotNull(configFile.getSettings().get("audio.volume"));

        configFile.save();
    }
}
