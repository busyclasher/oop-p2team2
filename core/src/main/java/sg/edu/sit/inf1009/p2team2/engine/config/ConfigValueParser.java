package sg.edu.sit.inf1009.p2team2.engine.config;

/**
 * Utility parser for converting raw string values into primitive config types.
 */
public final class ConfigValueParser {
    private ConfigValueParser() {
    }

    public static Object parse(String rawValue) {
        if (rawValue == null) {
            return "";
        }
        String value = rawValue.trim();
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return Boolean.parseBoolean(value);
        }
        try {
            if (value.contains(".")) {
                return Float.parseFloat(value);
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return value;
        }
    }
}
