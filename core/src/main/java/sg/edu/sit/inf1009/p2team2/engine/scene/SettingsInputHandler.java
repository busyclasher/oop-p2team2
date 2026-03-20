package sg.edu.sit.inf1009.p2team2.engine.scene;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;

/**
 * SETTINGSINPUTHANDLER
 * Concrete input handler for the settings scene.
 */
public class SettingsInputHandler extends InputHandler {
    private final SettingsScene scene;

    public SettingsInputHandler(SettingsScene scene) {
        super(scene == null ? null : scene.getContext());
        this.scene = scene;
    }

    public SettingsInputHandler(EngineContext context) {
        super(context);
        this.scene = null;
    }

    @Override
    public void handleInput() {
        if (scene != null) {
            scene.processSettingsInput();
        }

        for (var entry : getKeyBindings().entrySet()) {
            if (isKeyPressed(entry.getKey())) {
                entry.getValue().run();
            }
        }
    }
}
