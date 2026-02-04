package sg.edu.sit.inf1009.p2team2.engine.config;

/**
 * A single configuration variable (value + metadata).
 *
 * Keep this class generic so the engine stays non-contextual; higher-level code can
 * decide what keys exist and how values are interpreted.
 */
public class ConfigVar {
    private Object value;
    private Object defaultValue;
    private int flags;

    public ConfigVar() {
        this(null, 0);
    }

    public ConfigVar(Object defaultValue, int flags) {
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.flags = flags;
    }

    public Object get() {
        return value;
    }

    public void set(Object val) {
        this.value = val;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public int getFlags() {
        return flags;
    }
}

