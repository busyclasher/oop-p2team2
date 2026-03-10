package sg.edu.sit.inf1009.p2team2.engine.scene;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;

/**
 * SCENE - Abstract
 * Thin coordinator that delegates input, rendering, and resource management
 * to its three component handlers (InputHandler, SceneRenderer, ResourceLoader).
 *
 * # context: EngineContext
 */
public abstract class Scene {
    protected final EngineContext context;

    protected InputHandler inputHandler;
    protected SceneRenderer sceneRenderer;
    protected ResourceLoader resourceLoader;

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
        if (resourceLoader != null) {
            resourceLoader.load();
        }
    }

    public void unload() {
        if (resourceLoader != null) {
            resourceLoader.unload();
        }
    }

    public abstract void update(float dt);

    public void render() {
        if (sceneRenderer != null) {
            sceneRenderer.render();
        }
    }

    public void handleInput() {
        if (inputHandler != null) {
            inputHandler.handleInput();
        }
    }

    public EngineContext getContext() {
        return context;
    }
}
