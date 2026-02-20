package sg.edu.sit.inf1009.p2team2.engine.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ConfigManagerTest {

    @Test
    void singletonGetInstanceReturnsStableReference() {
        ConfigManager first = ConfigManager.getInstance();
        ConfigManager second = ConfigManager.getInstance();

        assertNotNull(first);
        assertSame(first, second);
    }

    @Test
    void typedKeyApisReadAndWriteValues() {
        ConfigManager manager = createManager();

        manager.set(ConfigKeys.DISPLAY_WIDTH, Integer.valueOf(1024));

        assertEquals(1024, manager.get(ConfigKeys.DISPLAY_WIDTH));
    }

    @Test
    void typedKeyValidationRejectsInvalidValues() {
        ConfigManager manager = createManager();

        assertThrows(IllegalArgumentException.class, () -> manager.set(ConfigKeys.DISPLAY_WIDTH, Integer.valueOf(0)));
    }

    @Test
    void stringApisSupportFallbackTypeReads() {
        ConfigManager manager = createManager();

        manager.set("test.int", new ConfigVar<>(Integer.valueOf(7), Integer.valueOf(0)));
        manager.set("test.float", new ConfigVar<>(Float.valueOf(2.5f), Float.valueOf(0f)));
        manager.set("test.bool", new ConfigVar<>(Boolean.TRUE, Boolean.FALSE));
        manager.set("test.string", new ConfigVar<>("hello", ""));

        assertEquals(7, manager.getInt("test.int", 1));
        assertEquals(2.5f, manager.getFloat("test.float", 1f), 0.0001f);
        assertEquals(true, manager.getBool("test.bool", false));
        assertEquals("hello", manager.getString("test.string", "fallback"));
        assertEquals(99, manager.getInt("missing", 99));
    }

    @Test
    void observerNotifiedOnlyWhenValueChanges() {
        ConfigManager manager = createManager();
        TestListener listener = new TestListener();
        manager.addObserver(listener);

        manager.set("custom.key", new ConfigVar<>("a", "a"));
        manager.set("custom.key", new ConfigVar<>("a", "a"));
        manager.set("custom.key", new ConfigVar<>("b", "a"));

        assertEquals(2, listener.notifications);
    }

    private ConfigManager createManager() {
        InMemoryLoader loader = new InMemoryLoader();
        return new ConfigManager(new ConfigRegistry(), loader, new ConfigDispatcher());
    }

    private static final class InMemoryLoader implements IConfigLoader {
        private final Map<String, ConfigVar<?>> values = new LinkedHashMap<>();

        @Override
        public Map<String, ConfigVar<?>> loadFromFile(String filePath) {
            return new LinkedHashMap<>(values);
        }

        @Override
        public void saveToFile(String filePath, Map<String, ConfigVar<?>> settings) {
            values.clear();
            values.putAll(settings);
        }
    }

    private static final class TestListener implements ConfigListener {
        int notifications;

        @Override
        public void onConfigChanged(String key, ConfigVar<?> val) {
            notifications++;
        }
    }
}
