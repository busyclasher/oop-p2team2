package sg.edu.sit.inf1009.p2team2.engine.scene;

import java.util.Stack;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.scene.Scene;

/**
 * SCENE MANAGER - Abstract Engine (Control)
 * Manages scene stack and transitions
 * 
 * Following UML specification exactly:
 * - context: EngineContext
 * - scenes: Stack<Scene>
 * 
 * @author Ivan
 */
public class SceneManager {
    
    private final EngineContext context;
    private final Stack<Scene> scenes;
    private Scene currentScene;
    
    /**
     * Constructor
     * 
     * @param context Engine context
     */
    public SceneManager(EngineContext context) {
        this.context = context;
        this.scenes = new Stack<>();
        this.currentScene = null;
    }
    
    /**
     * Push a new scene onto the stack
     * The new scene becomes active
     * 
     * @param scene Scene to push
     */
    public void push(Scene scene) {
        if (scene == null) {
            return;
        }
        
        // Pause current scene
        if (!scenes.isEmpty()) {
            scenes.peek().onExit();
        }
        
        // Add new scene
        scenes.push(scene);
        
        // Activate new scene
        scene.load();
        scene.onEnter();
        currentScene = scene;
    }
    
    /**
     * Pop the current scene from the stack
     * The previous scene becomes active again
     */
    public void pop() {
        if (scenes.isEmpty()) {
            return;
        }
        
        // Remove and cleanup current scene
        Scene removed = scenes.pop();
        removed.onExit();
        removed.unload();
        
        // Resume previous scene
        if (!scenes.isEmpty()) {
            currentScene = scenes.peek();
            currentScene.onEnter();
        } else {
            currentScene = null;
        }
    }
    
    /**
     * Get the current active scene without removing it
     * 
     * @return Current scene, or null if stack is empty
     */
    public Scene peek() {
        return currentScene;
    }
    
    /**
     * Update the current scene
     * 
     * @param dt Delta time
     */
    public void update(float dt) {
        if (currentScene != null) {
            currentScene.handleInput();
            currentScene.update(dt);
        }
    }
    
    /**
     * Render the current scene
     */
    public void render() {
        if (currentScene != null) {
            currentScene.render();
        }
    }
    
    /**
     * Check if scene stack is empty
     * 
     * @return true if no scenes
     */
    public boolean isEmpty() {
        return scenes.isEmpty();
    }
    
    /**
     * Clear all scenes from the stack
     */
    public void clear() {
        while (!scenes.isEmpty()) {
            pop();
        }
    }
    
    /**
     * Clean up resources
     */
    public void dispose() {
        clear();
    }
}
