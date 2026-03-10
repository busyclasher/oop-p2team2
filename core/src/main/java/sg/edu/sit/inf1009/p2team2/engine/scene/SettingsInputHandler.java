package sg.edu.sit.inf1009.p2team2.engine.scene;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;

/**
 * SETTINGSINPUTHANDLER
 * Concrete input handler for the settings scene.
 */
public class SettingsInputHandler extends InputHandler {

    public SettingsInputHandler(EngineContext context) {
        super(context);
    }

    @Override
    public void handleInput() {
        for (var entry : keyBindings.entrySet()) {
            if (isKeyPressed(entry.getKey())) {
                entry.getValue().run();
            }
        }
    }
}
