package sg.edu.sit.inf1009.p2team2.engine.core;

import sg.edu.sit.inf1009.p2team2.engine.config.ConfigManager;
import sg.edu.sit.inf1009.p2team2.engine.config.ConfigurationManager;
import sg.edu.sit.inf1009.p2team2.engine.managers.EntityManager;
import sg.edu.sit.inf1009.p2team2.engine.managers.InputManager;
import sg.edu.sit.inf1009.p2team2.engine.managers.OutputManager;

/**
 * Central dependency container for engine managers.
 *
 * Keep this focused: it wires core services together so other parts of the engine
 * can depend on the context instead of constructing managers directly.
 */
public class EngineContext {
    private final InputManager inputManager;
    private final OutputManager outputManager;
    private final ConfigManager configManager;

    public EngineContext() {
        // TODO(Team): decide where default settings (resolution, title, etc.) come from.
        EntityManager entityManager = new EntityManager();
        this.configManager = ConfigurationManager.getInstance();
        this.inputManager = new InputManager(entityManager);
        this.outputManager = new OutputManager(1280, 720);
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public OutputManager getOutputManager() {
        return outputManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}

