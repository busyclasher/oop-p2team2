package sg.edu.sit.inf1009.p2team2.engine.managers;

import java.util.Stack;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.scenes.LeadershipBoardScene;
import sg.edu.sit.inf1009.p2team2.engine.scenes.MenuScene;
import sg.edu.sit.inf1009.p2team2.engine.scenes.Scene;
import sg.edu.sit.inf1009.p2team2.engine.scenes.SettingsScene;

/**
 * Manages scene transitions and a stack of scenes.
 */
public class SceneManager {
    private Scene currentScene;
    private final Stack<Scene> scenes = new Stack<>();
    private final EngineContext context;

    public SceneManager(EntityManager entityManager, EngineContext context) {
        this.context = context;
    }

    public void push(Scene scene) {
        // TODO(Ivan): call onExit/onEnter correctly when stacking scenes.
        if (scene == null) return;
        

        // Pause the current scene before moving to the next
        if (currentScene != null) {
            currentScene.onExit();
        }

        scenes.push(scene);
        currentScene = scene;

        // Load resources and trigger enter logic for the new scene
        currentScene.load();
        currentScene.onEnter();
        
        System.out.println("[SceneManager] Pushed scene: " + getActiveSceneName());
    }

    public void pop() {
        // TODO(Ivan): pop current scene and restore previous.
        if (scenes.isEmpty()) {
            currentScene = null;
            return;
        }

        // Cleanup the current scene before removing it
        Scene removedScene = scenes.pop();
        removedScene.onExit();
        removedScene.unload();

        // Restore the previous scene if one exists
        if (!scenes.isEmpty()) {
            currentScene = scenes.peek();
            currentScene.onEnter();
            System.out.println("[SceneManager] Restored scene: " + getActiveSceneName());
        } else {
            currentScene = null;
        }
    }

    public void set(Scene scene) {
        // TODO(Ivan): replace stack with a single active scene.
        // Unload everything currently in the stack
        while (!scenes.isEmpty()) {
            Scene s = scenes.pop();
            s.onExit();
            s.unload();
        }
        push(scene);
    }

    public void changeScene(String name) {
        // TODO(Ivan): map scene name -> concrete Scene and switch to it.
        // Example: if ("menu".equalsIgnoreCase(name)) set(new MenuScene(context));
        if (name == null) return;

        // Coordination logic: mapping names to concrete classes
        switch (name.toLowerCase()) {
            case "menu":
                set(new MenuScene(context, null));
                break;
            case "settings":
                push(new SettingsScene(context));
                break;
            case "leaderboard":
                push(new LeadershipBoardScene(context));
                break;
            default:
                System.err.println("[SceneManager] Unknown scene name: " + name);
                break;
        }
    }

    public Scene peek() {
        return currentScene;
    }

    public void load() {
        if (currentScene != null) {
            currentScene.load();
        }
    }

    public void update() {
        if (currentScene != null) {
            currentScene.handleInput();
            currentScene.update();
        }
    }

    public void render() {
        if (currentScene != null) {
            currentScene.render();
        }
    }

    public void changeScene(Scene newScene) {
        set(newScene);
    }

    public String getActiveSceneName() {
        return currentScene == null ? "" : currentScene.getClass().getSimpleName();
    }

    public EngineContext getContext() {
        return context;
    }

    public void dispose() { // Dispose all scenes in the stack
        while (!scenes.isEmpty()) {
            pop();
        }
        System.out.println("[SceneManager] Disposed all scenes.");
    }
}

