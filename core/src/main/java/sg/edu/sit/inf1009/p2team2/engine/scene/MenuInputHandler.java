package sg.edu.sit.inf1009.p2team2.engine.scene;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;

/**
 * MENUINPUTHANDLER
 * Concrete input handler for the menu scene.
 */
public class MenuInputHandler extends InputHandler {
    private final MenuScene scene;

    public MenuInputHandler(MenuScene scene) {
        super(scene == null ? null : scene.getContext());
        this.scene = scene;
    }

    public MenuInputHandler(EngineContext context) {
        super(context);
        this.scene = null;
    }

    @Override
    public void handleInput() {
        if (scene != null) {
            scene.processMenuInput();
        }

        for (var entry : getKeyBindings().entrySet()) {
            if (isKeyPressed(entry.getKey())) {
                entry.getValue().run();
            }
        }
    }
}
