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

    public void load() {
        // TODO(Ivan): load assets/resources required by this scene.
    }

    public void unload() {
        // TODO(Ivan): unload assets/resources required by this scene.
    }

    public void update() {
        // TODO(Ivan): update scene state.
    }

    public void render() {
        // TODO(Ivan): render scene using OutputManager/Renderer.
    }

    public void handleInput() {
        // TODO(Ivan): route user input to scene controls.
    }

    public EngineContext getContext() {
        return context;
    }
}

