package sg.edu.sit.inf1009.p2team2;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.scenes.CompleteIOTest;
import sg.edu.sit.inf1009.p2team2.engine.scenes.MenuScene;


/**
 * MAIN - Application Entry Point
 * Integrates libGDX ApplicationAdapter with our EngineContext
 * 
 * This is the bridge between libGDX and our Abstract Engine.
 */
public class Main extends ApplicationAdapter {
    
    private EngineContext engine;
    private float lastDeltaTime;
    
    /**
     * Called once when application starts
     * This is where we initialize the engine
     */
    @Override
    public void create() {
        System.out.println("[Main] Application starting...");
        
        // 1. Create the engine context
        engine = new EngineContext();
        
        // 2. Initialize the engine (after libGDX context is ready)
        engine.initialize();
        
        // 3. Start the engine
        engine.start();
        
        // 4. Load the first scene (MenuScene)
        //MenuScene menuScene = new MenuScene(engine);
        //engine.getSceneManager().push(menuScene);
        CompleteIOTest testScene = new CompleteIOTest(engine);
        engine.getSceneManager().push(testScene);
        
        System.out.println("[Main] Application started successfully!");
    }
    
    /**
     * Called every frame
     * This is the game loop
     */
    @Override
    public void render() {
        // Calculate delta time
        float dt = Gdx.graphics.getDeltaTime();
        lastDeltaTime = dt;
        
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
        System.out.println("[Main] Window resized: " + width + "x" + height);
        
        // Update display size
        if (engine != null) {
            engine.getOutputManager().getDisplay().resize(width, height);
        }
    }
    
    /**
     * Called when application is paused
     * (e.g., minimize window, switch to another app)
     */
    @Override
    public void pause() {
        System.out.println("[Main] Application paused");
        
        // Save config or game state here if needed
        if (engine != null) {
            engine.getConfigManager().saveConfig();
        }
    }
    
    /**
     * Called when application is resumed
     */
    @Override
    public void resume() {
        System.out.println("[Main] Application resumed");
    }
    
    /**
     * Called when application is closing
     * This is where we clean up
     */
    @Override
    public void dispose() {
        System.out.println("[Main] Application closing...");
        
        if (engine != null) {
            engine.dispose();
        }
        
        System.out.println("[Main] Application closed");
    }
}