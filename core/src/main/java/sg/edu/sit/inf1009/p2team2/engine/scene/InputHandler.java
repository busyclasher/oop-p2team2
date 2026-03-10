package sg.edu.sit.inf1009.p2team2.engine.scene;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import java.util.HashMap;
import java.util.Map;

/**
 * INPUTHANDLER - Abstract
 * Handles input for a specific scene. Each concrete scene gets its own
 * input handler with its own key bindings.
 *
 * # context: EngineContext
 * # keyBindings: Map<Key, Action>
 */
public abstract class InputHandler {

    protected final EngineContext context;
    protected final Map<Integer, Runnable> keyBindings;

    public InputHandler(EngineContext context) {
        this.context = context;
        this.keyBindings = new HashMap<>();
    }

    /** Process all input for the current frame. */
    public abstract void handleInput();

    /** Check whether the given key is currently pressed. */
    public boolean isKeyPressed(int key) {
        return com.badlogic.gdx.Gdx.input.isKeyPressed(key);
    }

    /** Bind an action to a key. */
    public void bindKey(int key, Runnable action) {
        keyBindings.put(key, action);
    }

    /** Remove the binding for a key. */
    public void unbindKey(int key) {
        keyBindings.remove(key);
    }
}
