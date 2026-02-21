package sg.edu.sit.inf1009.p2team2.engine.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Pluggable serializer contract for config file formats.
 */
public interface IConfigFormat {
    boolean supports(Path path);

    Map<String, ConfigVar<?>> load(Path path) throws IOException;

    void save(Path path, Map<String, ConfigVar<?>> settings) throws IOException;
}
