package sg.edu.sit.inf1009.p2team2.engine.scene;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;

/**
 * MAININPUTHANDLER
 * Concrete input handler for the main simulation scene.
 */
public class MainInputHandler extends InputHandler {

    public MainInputHandler(EngineContext context) {
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
