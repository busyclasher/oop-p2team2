package sg.edu.sit.inf1009.p2team2.engine.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ConfigManagerTest {

    @Test
    void getInstanceReturnsSingleton() {
        ConfigManager first = ConfigManager.getInstance();
        ConfigManager second = ConfigManager.getInstance();

        assertSame(first, second);
    }

    @Test
    void loadSeedsDefaultsAndGetReturnsExpectedValues() {
        ConfigManager manager = ConfigManager.getInstance();
        manager.load("test-config.json");

        assertEquals(800, manager.get("display.width").asInt());
        assertEquals(600, manager.get("display.height").asInt());
        assertTrue(manager.get("display.title").asString().contains("P2Team2"));
        assertFalse(manager.get("display.fullscreen").asBool());
    }

    @Test
    void setAndObserverNotificationWorks() {
        ConfigManager manager = ConfigManager.getInstance();
        manager.load("test-config.json");

        final boolean[] called = {false};
        manager.addObserver((key, val) -> {
            if ("audio.volume".equals(key) && val != null) {
                called[0] = true;
            }
        });

        ConfigVar oldValue = manager.get("audio.volume");
        ConfigVar newValue = new ConfigVar(0.25f, oldValue == null ? 0.7f : oldValue.getDefaultValue());
        manager.set("audio.volume", newValue);
        manager.notifyObservers("audio.volume", oldValue, newValue);

        assertTrue(called[0]);
        ConfigVar stored = manager.get("audio.volume");
        assertNotNull(stored);
        assertEquals(0.25f, stored.asFloat(), 0.0001f);
    }
}
