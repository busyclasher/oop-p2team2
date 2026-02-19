package sg.edu.sit.inf1009.p2team2.engine.config;

import java.util.Objects;
import java.util.function.Function;

public final class ConfigKey<T> {
    private final String name;
    private final T defaultValue;
    private final Function<ConfigVar, T> reader;

    public ConfigKey(String name, T defaultValue, Function<ConfigVar, T> reader) {
        this.name = Objects.requireNonNull(name, "name");
        this.defaultValue = defaultValue;
        this.reader = Objects.requireNonNull(reader, "reader");
    }

    public String name() {
        return name;
    }

    public T defaultValue() {
        return defaultValue;
    }

    public T read(ConfigVar var) {
        return var == null ? defaultValue : reader.apply(var);
    }

    public ConfigVar toVar(T value) {
        T resolved = value == null ? defaultValue : value;
        return new ConfigVar(resolved, defaultValue);
    }
}
