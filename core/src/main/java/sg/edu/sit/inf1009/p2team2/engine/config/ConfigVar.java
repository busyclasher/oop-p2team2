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

    public int asInt() {
        Object raw = resolvedValue();
        if (raw instanceof Number number) {
            return number.intValue();
        }
        if (raw instanceof Boolean bool) {
            return bool ? 1 : 0;
        }
        if (raw instanceof String text) {
            try {
                return Integer.parseInt(text.trim());
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }
        return 0;
    }

    public float asFloat() {
        Object raw = resolvedValue();
        if (raw instanceof Number number) {
            return number.floatValue();
        }
        if (raw instanceof Boolean bool) {
            return bool ? 1f : 0f;
        }
        if (raw instanceof String text) {
            try {
                return Float.parseFloat(text.trim());
            } catch (NumberFormatException ignored) {
                return 0f;
            }
        }
        return 0f;
    }

    public boolean asBool() {
        Object raw = resolvedValue();
        if (raw instanceof Boolean bool) {
            return bool;
        }
        if (raw instanceof Number number) {
            return number.floatValue() != 0f;
        }
        if (raw instanceof String text) {
            String normalized = text.trim();
            return "true".equalsIgnoreCase(normalized)
                || "1".equals(normalized)
                || "yes".equalsIgnoreCase(normalized)
                || "y".equalsIgnoreCase(normalized)
                || "on".equalsIgnoreCase(normalized);
        }
        return false;
    }

    public String asString() {
        Object raw = resolvedValue();
        return raw == null ? "" : String.valueOf(raw);
    }

    private Object resolvedValue() {
        return value != null ? value : defaultValue;
    }
}
