package sg.edu.sit.inf1009.p2team2.engine.scenes;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;

/**
 * Base class for all scenes (screens/states) in the engine.
 */
public abstract class Scene {
    protected final EngineContext context;

    protected Scene(EngineContext context) {
        this.context = context;
    }

    public void onEnter() {
        // Optional hook.
    }

    public void onExit() {
        // Optional hook.
    }

    public abstract void load();

    public abstract void unload();

    public abstract void update(float dt);

    public abstract void render();

    public void handleInput() {
        // Optional hook.
    }

    public EngineContext getContext() {
        return context;
    }
}
