package sg.edu.sit.inf1009.p2team2.engine.config;

import java.util.Objects;

/**
 * Typed configuration value holder.
 */
public class ConfigVar {
    private Object value;
    private Object defaultValue;

    public ConfigVar() {
        this(null, null);
    }

    public ConfigVar(Object value, Object defaultValue) {
        this.value = value;
        this.defaultValue = defaultValue;
    }

    public int asInt() {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String str) {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }
        return 0;
    }

    public float asFloat() {
        if (value instanceof Number number) {
            return number.floatValue();
        }
        if (value instanceof String str) {
            try {
                return Float.parseFloat(str);
            } catch (NumberFormatException ignored) {
                return 0f;
            }
        }
        return 0f;
    }

    public boolean asBool() {
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof String str) {
            return Boolean.parseBoolean(str);
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return false;
    }

    public String asString() {
        return value == null ? "" : String.valueOf(value);
    }

    public void reset() {
        this.value = defaultValue;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public boolean hasSameValue(ConfigVar other) {
        return other != null && Objects.equals(this.value, other.value);
    }
}
