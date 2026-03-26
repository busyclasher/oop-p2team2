package sg.edu.sit.inf1009.p2team2;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import sg.edu.sit.inf1009.p2team2.demo.MainScene;
import sg.edu.sit.inf1009.p2team2.demo.MenuScene;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.scene.Scene;
import sg.edu.sit.inf1009.p2team2.game.audio.GameAudio;
import sg.edu.sit.inf1009.p2team2.game.leaderboard.LeaderboardManager;
import sg.edu.sit.inf1009.p2team2.game.scenes.GameMenuScene;


/**
 * MAIN - Application Entry Point
 * Integrates libGDX ApplicationAdapter with our EngineContext
 * 
 * This is the bridge between libGDX and our Abstract Engine.
 */
public class Main extends ApplicationAdapter {
    private static final String START_SCENE_PROPERTY = "engine.scene";
    private static final String DEFAULT_START_SCENE = "menu";
    private static final String INPUT_OUTPUT_SCENE_CLASS =
        "sg.edu.sit.inf1009.p2team2.engine.scenes.tests.InputOutputTestScene";
    private static final String COMPLETE_IO_SCENE_CLASS =
        "sg.edu.sit.inf1009.p2team2.engine.scenes.tests.CompleteIOTest";
    
    private EngineContext    engine;
    private LeaderboardManager leaderboardManager;
    
    /**
     * Called once when application starts
     * This is where we initialize the engine
     */
    @Override
    public void create() {
        // 1. Create the engine context
        engine = new EngineContext();
        leaderboardManager = new LeaderboardManager();
        
        // 2. Initialize the engine (after libGDX context is ready)
        engine.initialize();
        engine.getOutputManager().getAudio().loadSettings();
        GameAudio.loadGameSounds(engine.getOutputManager().getAudio());
        engine.getOutputManager().getRenderer().resizeViewport(
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
        
        // 3. Start the engine
        engine.start();
        
        // 4. Load startup scene (menu by default, configurable for manual scene tests)
        Scene startupScene = resolveStartupScene();
        engine.getSceneManager().push(startupScene);
    }
    
    /**
     * Called every frame
     * This is the game loop
     */
    @Override
    public void render() {
        // Calculate delta time
        float dt = Gdx.graphics.getDeltaTime();
        
        // Cap delta time to prevent physics issues
        if (dt > 0.25f) {
            dt = 0.25f;  // Max 4 FPS minimum
        }
        
        // Only update if engine is initialized
        if (engine != null && engine.isRunning()) {
            // 1. Update game logic
            engine.update(dt);
            
            // 2. Render graphics
            engine.render();
        }
    }
    
    /**
     * Called when window is resized
     * 
     * @param width New width
     * @param height New height
     */
    @Override
    public void resize(int width, int height) {
        // Update display size
        if (engine != null) {
            engine.getOutputManager().getDisplay().syncFromSystemResize(width, height);
            engine.getOutputManager().getRenderer().resizeViewport(width, height);
        }
    }
    
    /**
     * Called when application is paused
     * (e.g., minimize window, switch to another app)
     */
    @Override
    public void pause() {
        // Save config or game state here if needed
        if (engine != null) {
            engine.getConfigManager().save(null);
        }
    }
    
    /**
     * Called when application is resumed
     */
    @Override
    public void resume() {
    }
    
    /**
     * Called when application is closing
     * This is where we clean up
     */
    @Override
    public void dispose() {
        if (engine != null) {
            engine.dispose();
        }
    }

    private Scene resolveStartupScene() {
        String sceneKey = System.getProperty(START_SCENE_PROPERTY, DEFAULT_START_SCENE)
            .trim()
            .toLowerCase();

        switch (sceneKey) {
            case "io":
            case "io-test":
            case "input-output":
                return createTestScene(INPUT_OUTPUT_SCENE_CLASS);
            case "complete":
            case "complete-io":
            case "complete-io-test":
                return createTestScene(COMPLETE_IO_SCENE_CLASS);
            case "main":
            case "main-scene":
                return new MainScene(engine);
            case "engine-menu":
                return new MenuScene(engine);
            case "menu":
            default:
                return new GameMenuScene(engine, leaderboardManager);
        }
    }

    private Scene createTestScene(String className) {
        try {
            Class<?> sceneType = Class.forName(className);
            if (!Scene.class.isAssignableFrom(sceneType)) {
                throw new IllegalStateException("Class is not a Scene: " + className);
            }
            return (Scene) sceneType.getConstructor(EngineContext.class).newInstance(engine);
        } catch (Exception e) {
            return new GameMenuScene(engine, leaderboardManager);
        }
    }
}
