package sg.edu.sit.inf1009.p2team2.engine.scenes;

import java.util.function.Predicate;
import sg.edu.sit.inf1009.p2team2.engine.config.ConfigKey;

public final class SimulationConfigKeys {
    public static final ConfigKey<Float> SIMULATION_FRICTION =
        floatKey("simulation.friction", 0.10f, between01(), "Simulation friction [0, 1]");
    public static final ConfigKey<Float> SIMULATION_GRAVITY_Y =
        floatKey("simulation.gravityY", 0f, v -> v >= -1000f && v <= 1000f, "Simulation gravity Y in reasonable range");
    public static final ConfigKey<Float> SIMULATION_PLAYER_SPEED =
        floatKey("simulation.playerSpeed", 240f, v -> v >= 0f, "Player speed must be >= 0");
    public static final ConfigKey<Boolean> SIMULATION_COLLISIONS_ENABLED =
        boolKey("simulation.collisionsEnabled", true, v -> true, "Simulation collision toggle");
    public static final ConfigKey<Integer> SIMULATION_PRESET_INDEX =
        intKey("simulation.presetIndex", 0, v -> v >= 0, "Simulation preset index must be >= 0");
    public static final ConfigKey<Boolean> SIMULATION_MUSIC_ENABLED =
        boolKey("simulation.musicEnabled", false, v -> true, "Simulation music toggle");

    private SimulationConfigKeys() {
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

    private static Predicate<Float> between01() {
        return v -> v >= 0f && v <= 1f;
    }
}
