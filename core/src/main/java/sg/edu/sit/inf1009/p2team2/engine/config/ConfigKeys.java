package sg.edu.sit.inf1009.p2team2.engine.config;

public final class ConfigKeys {
    public static final ConfigKey<Integer> DISPLAY_WIDTH =
        intKey("display.width", 800);
    public static final ConfigKey<Integer> DISPLAY_HEIGHT =
        intKey("display.height", 600);
    public static final ConfigKey<Boolean> DISPLAY_FULLSCREEN =
        boolKey("display.fullscreen", false);
    public static final ConfigKey<String> DISPLAY_TITLE =
        stringKey("display.title", "P2Team2AbstractEngine");
    public static final ConfigKey<Float> AUDIO_VOLUME =
        floatKey("audio.volume", 0.7f);

    public static final ConfigKey<Float> SIMULATION_FRICTION =
        floatKey("simulation.friction", 0.10f);
    public static final ConfigKey<Float> SIMULATION_GRAVITY_Y =
        floatKey("simulation.gravityY", 0f);
    public static final ConfigKey<Float> SIMULATION_PLAYER_SPEED =
        floatKey("simulation.playerSpeed", 240f);
    public static final ConfigKey<Boolean> SIMULATION_COLLISIONS_ENABLED =
        boolKey("simulation.collisionsEnabled", true);
    public static final ConfigKey<Integer> SIMULATION_PRESET_INDEX =
        intKey("simulation.presetIndex", 0);
    public static final ConfigKey<Boolean> SIMULATION_MUSIC_ENABLED =
        boolKey("simulation.musicEnabled", false);

    private ConfigKeys() {
    }

    private static ConfigKey<Integer> intKey(String name, int defaultValue) {
        return new ConfigKey<>(name, Integer.valueOf(defaultValue), ConfigVar::asInt);
    }

    private static ConfigKey<Float> floatKey(String name, float defaultValue) {
        return new ConfigKey<>(name, Float.valueOf(defaultValue), ConfigVar::asFloat);
    }

    private static ConfigKey<Boolean> boolKey(String name, boolean defaultValue) {
        return new ConfigKey<>(name, Boolean.valueOf(defaultValue), ConfigVar::asBool);
    }

    private static ConfigKey<String> stringKey(String name, String defaultValue) {
        return new ConfigKey<>(name, defaultValue, ConfigVar::asString);
    }
}
