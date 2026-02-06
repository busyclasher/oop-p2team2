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
        // Default implementation - can be overridden by child classes
        // Optional hook called by the SceneManager before this scene is paused or removed.
    }

    public void onExit() {
        // Optional hook.
        // Default implementation - can be overridden by child classes
    }

    public abstract void load(); 
        // TODO(Ivan): load assets/resources required by this scene.
        // Load assets/resources required by this scene.
        // Called once when the scene is first pushed to the stack.

    public abstract void unload(); 
        // TODO(Ivan): unload assets/resources required by this scene.
        // Unload assets/resources required by this scene to free memory.
        // Called when the scene is permanently removed from the stack.

    public abstract void update(); 
        // TODO(Ivan): update scene state.
        // Update the scene state (logic, animations, physics).
        // Typically called once per frame by the SceneManager.

    public abstract void render();
        // TODO(Ivan): render scene using OutputManager/Renderer.
        // Render the scene using the Renderer accessible via EngineContext.
        
    public void handleInput() {
        // TODO(Ivan): route user input to scene controls.
    }

    public EngineContext getContext() {
        return context;
    }
}

