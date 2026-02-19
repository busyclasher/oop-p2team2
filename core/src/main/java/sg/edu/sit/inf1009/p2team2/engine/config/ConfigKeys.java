package sg.edu.sit.inf1009.p2team2.engine.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public final class ConfigKeys {
    public static final ConfigKey<Integer> DISPLAY_WIDTH =
        intKey("display.width", 800, v -> v > 0, "Window width must be > 0");
    public static final ConfigKey<Integer> DISPLAY_HEIGHT =
        intKey("display.height", 600, v -> v > 0, "Window height must be > 0");
    public static final ConfigKey<Boolean> DISPLAY_FULLSCREEN =
        boolKey("display.fullscreen", false, v -> true, "Fullscreen flag");
    public static final ConfigKey<String> DISPLAY_TITLE =
        stringKey("display.title", "P2Team2AbstractEngine", v -> v != null && !v.isBlank(), "Window title");
    public static final ConfigKey<Float> AUDIO_VOLUME =
        floatKey("audio.volume", 0.7f, between01(), "Master volume [0, 1]");

    private static final Map<String, ConfigKey<?>> BY_NAME = buildIndex();

    private ConfigKeys() {
    }

    private static ConfigKey<Integer> intKey(String name, int defaultValue, Predicate<Integer> validator, String description) {
        return new ConfigKey<>(name, Integer.class, Integer.valueOf(defaultValue), validator, description);
    }

    private static ConfigKey<Float> floatKey(String name, float defaultValue, Predicate<Float> validator, String description) {
        return new ConfigKey<>(name, Float.class, Float.valueOf(defaultValue), validator, description);
    }

    private static ConfigKey<Boolean> boolKey(String name, boolean defaultValue, Predicate<Boolean> validator, String description) {
        return new ConfigKey<>(name, Boolean.class, Boolean.valueOf(defaultValue), validator, description);
    }

    private static ConfigKey<String> stringKey(String name, String defaultValue, Predicate<String> validator, String description) {
        return new ConfigKey<>(name, String.class, defaultValue, validator, description);
    }

    private static Predicate<Float> between01() {
        return v -> v >= 0f && v <= 1f;
    }

    private static Map<String, ConfigKey<?>> buildIndex() {
        Map<String, ConfigKey<?>> map = new LinkedHashMap<>();
        map.put(DISPLAY_WIDTH.name(), DISPLAY_WIDTH);
        map.put(DISPLAY_HEIGHT.name(), DISPLAY_HEIGHT);
        map.put(DISPLAY_FULLSCREEN.name(), DISPLAY_FULLSCREEN);
        map.put(DISPLAY_TITLE.name(), DISPLAY_TITLE);
        map.put(AUDIO_VOLUME.name(), AUDIO_VOLUME);
        return map;
    }

    public static ConfigKey<?> find(String keyName) {
        return BY_NAME.get(keyName);
    }

    public static boolean isValid(String keyName, ConfigVar<?> value) {
        ConfigKey<?> key = find(keyName);
        if (key == null) {
            return true;
        }
        return key.isValidVar(value);
    }
}
