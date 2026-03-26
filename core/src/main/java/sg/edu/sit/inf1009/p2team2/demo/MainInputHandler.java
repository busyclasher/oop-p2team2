package sg.edu.sit.inf1009.p2team2.demo;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.scene.InputHandler;


/**
 * MAININPUTHANDLER
 * Concrete input handler for the main simulation scene.
 */
public class MainInputHandler extends InputHandler {
    private final MainScene scene;

    public MainInputHandler(MainScene scene) {
        super(scene == null ? null : scene.getContext());
        this.scene = scene;
    }

    public MainInputHandler(EngineContext context) {
        super(context);
        this.scene = null;
    }

    @Override
    public void handleInput() {
        if (scene != null) {
            scene.processMainInput();
        }

        for (var entry : getKeyBindings().entrySet()) {
            if (isKeyPressed(entry.getKey())) {
                entry.getValue().run();
            }
        }
    }
}
