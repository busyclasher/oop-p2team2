package sg.edu.sit.inf1009.p2team2.engine.managers;

import java.util.Stack;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.scenes.Scene;

/**
 * Manages scene transitions and a stack of scenes.
 */
public class SceneManager {
    private Scene currentScene;
    private final Stack<Scene> scenes = new Stack<>();
    private final EngineContext context;

    public SceneManager(EngineContext context) {
        this.context = context;
    }

    public void push(Scene scene) {
        // TODO(Ivan): call onExit/onEnter correctly when stacking scenes.
        if (scene == null) {
            return;
        }
        scenes.push(scene);
        currentScene = scene;
    }

    public void pop() {
        // TODO(Ivan): pop current scene and restore previous.
        if (scenes.isEmpty()) {
            currentScene = null;
            return;
        }
        scenes.pop();
        currentScene = scenes.isEmpty() ? null : scenes.peek();
    }

    public void set(Scene scene) {
        // TODO(Ivan): replace stack with a single active scene.
        scenes.clear();
        push(scene);
    }

    public Scene peek() {
        return scenes.isEmpty() ? null : scenes.peek();
    }

    public void changeScene(String name) {
        // TODO(Ivan): map scene name -> concrete Scene and switch to it.
        // Example: if ("menu".equalsIgnoreCase(name)) set(new MenuScene(context));
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
}

