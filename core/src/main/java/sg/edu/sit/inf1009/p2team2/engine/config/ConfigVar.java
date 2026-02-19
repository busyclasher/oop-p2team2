package sg.edu.sit.inf1009.p2team2.engine.config;

import java.util.Objects;

/**
 * Typed configuration value holder.
 */
public class ConfigVar<T> {
    private T value;
    private T defaultValue;

    public ConfigVar() {
        this(null, null);
    }

    public ConfigVar(T value, T defaultValue) {
        this.value = value;
        this.defaultValue = defaultValue;
    }

    public void reset() {
        this.value = defaultValue;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public boolean hasSameValue(ConfigVar other) {
        return other != null && Objects.equals(this.value, other.value);
    }
}
