package sg.edu.sit.inf1009.p2team2.engine.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JSON-backed config serializer implementation.
 */
public class JsonConfigFormat implements IConfigFormat {
    private static final Pattern PAIR_PATTERN = Pattern.compile(
        "\"([^\"]+)\"\\s*:\\s*(\"(?:\\\\.|[^\"])*\"|true|false|-?\\d+(?:\\.\\d+)?)"
    );

    @Override
    public boolean supports(Path path) {
        if (path == null) {
            return false;
        }
        String name = path.getFileName().toString().toLowerCase();
        return name.endsWith(".json");
    }

    @Override
    public Map<String, ConfigVar<?>> load(Path path) throws IOException {
        String content = Files.readString(path, StandardCharsets.UTF_8);
        Map<String, ConfigVar<?>> map = new LinkedHashMap<>();
        Matcher matcher = PAIR_PATTERN.matcher(content);
        while (matcher.find()) {
            String key = unescape(matcher.group(1));
            Object value = parseToken(matcher.group(2));
            map.put(key, new ConfigVar<>(value, value));
        }
        return map;
    }

    @Override
    public void save(Path path, Map<String, ConfigVar<?>> settings) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        List<Map.Entry<String, ConfigVar<?>>> validEntries = new ArrayList<>();
        for (Map.Entry<String, ConfigVar<?>> entry : settings.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            validEntries.add(entry);
        }

        for (int i = 0; i < validEntries.size(); i++) {
            Map.Entry<String, ConfigVar<?>> entry = validEntries.get(i);
            sb.append("  \"").append(escape(entry.getKey())).append("\": ");
            Object value = entry.getValue().getValue();
            if (value instanceof Number || value instanceof Boolean) {
                sb.append(value);
            } else {
                sb.append("\"").append(escape(value == null ? "" : String.valueOf(value))).append("\"");
            }
            if (i + 1 < validEntries.size()) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("}\n");
        Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);
    }

    private Object parseToken(String token) {
        if (token == null) {
            return "";
        }
        String value = token.trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return unescape(value.substring(1, value.length() - 1));
        }
        return ConfigValueParser.parse(value);
    }

    private String escape(String input) {
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String unescape(String input) {
        return input.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
