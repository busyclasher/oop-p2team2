package sg.edu.sit.inf1009.p2team2.engine.scene;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;

/**
 * MENUINPUTHANDLER
 * Concrete input handler for the menu scene.
 */
public class MenuInputHandler extends InputHandler {

    public MenuInputHandler(EngineContext context) {
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
