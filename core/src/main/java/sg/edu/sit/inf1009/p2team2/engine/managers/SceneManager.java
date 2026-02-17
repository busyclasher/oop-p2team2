package sg.edu.sit.inf1009.p2team2.engine.managers;

import java.util.Stack;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.scenes.Scene;

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
    
    /**
     * Constructor
     * 
     * @param context Engine context
     */
    public SceneManager(EngineContext context) {
        this.context = context;
        this.scenes = new Stack<>();
    }
    
    /**
     * Push a new scene onto the stack
     * The new scene becomes active
     * 
     * @param scene Scene to push
     */
    public void push(Scene scene) {
        if (scene == null) {
            System.err.println("[SceneManager] Cannot push null scene");
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
        
        System.out.println("[SceneManager] Pushed scene: " + scene.getClass().getSimpleName());
    }
    
    /**
     * Pop the current scene from the stack
     * The previous scene becomes active again
     */
    public void pop() {
        if (scenes.isEmpty()) {
            System.err.println("[SceneManager] Cannot pop - stack is empty");
            return;
        }
        
        // Remove and cleanup current scene
        Scene removed = scenes.pop();
        removed.onExit();
        removed.unload();
        
        System.out.println("[SceneManager] Popped scene: " + removed.getClass().getSimpleName());
        
        // Resume previous scene
        if (!scenes.isEmpty()) {
            scenes.peek().onEnter();
            System.out.println("[SceneManager] Resumed scene: " + scenes.peek().getClass().getSimpleName());
        }
    }
    
    /**
     * Get the current active scene without removing it
     * 
     * @return Current scene, or null if stack is empty
     */
    public Scene peek() {
        return scenes.isEmpty() ? null : scenes.peek();
    }
    
    /**
     * Update the current scene
     * 
     * @param dt Delta time
     */
    public void update(float dt) {
        if (!scenes.isEmpty()) {
            Scene current = scenes.peek();
            current.handleInput();
            current.update(dt);
        }
    }
    
    /**
     * Render the current scene
     */
    public void render() {
        if (!scenes.isEmpty()) {
            scenes.peek().render();
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
        System.out.println("[SceneManager] Cleared all scenes");
    }
    
    /**
     * Clean up resources
     */
    public void dispose() {
        clear();
        System.out.println("[SceneManager] Disposed");
    }
}