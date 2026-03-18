package sg.edu.sit.inf1009.p2team2.engine.scene;

import java.util.HashMap;
import java.util.Map;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Keyboard;

/**
 * INPUTHANDLER - Abstract
 * Handles input for a specific scene. Each concrete scene gets its own
 * input handler with its own key bindings.
 *
 * # context: EngineContext
 * # keyBindings: Map<Key, Action>
 */
public abstract class InputHandler {

    private final EngineContext context;
    private final Map<Integer, Runnable> keyBindings;

    public InputHandler(EngineContext context) {
        this.context = context;
        this.keyBindings = new HashMap<>();
    }

    /** Process all input for the current frame. */
    public abstract void handleInput();

    /** Check whether the given key is currently pressed. */
    public boolean isKeyPressed(int key) {
        if (getContext() == null || getContext().getInputManager() == null) {
            return false;
        }

        Keyboard keyboard = getContext().getInputManager().getKeyboard();
        return keyboard != null && keyboard.isKeyPressed(key);
    }

    protected final EngineContext getContext() {
        return context;
    }

    protected final Map<Integer, Runnable> getKeyBindings() {
        return keyBindings;
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
