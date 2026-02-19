package sg.edu.sit.inf1009.p2team2.engine.config;

import java.util.Objects;
import java.util.function.Predicate;

public final class ConfigKey<T> {
    private final String name;
    private final Class<T> type;
    private final T defaultValue;
    private final Predicate<T> validator;
    private final String description;

    public ConfigKey(
        String name,
        Class<T> type,
        T defaultValue,
        Predicate<T> validator,
        String description
    ) {
        this.name = Objects.requireNonNull(name, "name");
        this.type = Objects.requireNonNull(type, "type");
        this.defaultValue = defaultValue;
        this.validator = Objects.requireNonNull(validator, "validator");
        this.description = description == null ? "" : description;
    }

    public String name() {
        return name;
    }

    public T defaultValue() {
        return defaultValue;
    }

    public Class<T> type() {
        return type;
    }

    public T read(ConfigVar<?> var) {
        if (var == null) {
            return defaultValue;
        }
        Object raw = var.getValue();
        if (raw == null) {
            return defaultValue;
        }
        if (!type.isInstance(raw)) {
            return defaultValue;
        }
        return type.cast(raw);
    }

    public ConfigVar toVar(T value) {
        T resolved = value == null ? defaultValue : value;
        return new ConfigVar<>(resolved, defaultValue);
    }

    public boolean isValid(T value) {
        T resolved = value == null ? defaultValue : value;
        return validator.test(resolved);
    }

    public boolean isValidVar(ConfigVar<?> value) {
        if (value == null || value.getValue() == null) {
            return isValid(defaultValue);
        }
        if (!type.isInstance(value.getValue())) {
            return false;
        }
        return isValid(type.cast(value.getValue()));
    }

    public String description() {
        return description;
    }
}
