package sg.edu.sit.inf1009.p2team2.engine.core;

import sg.edu.sit.inf1009.p2team2.engine.config.ConfigManager;
import sg.edu.sit.inf1009.p2team2.engine.config.ConfigurationManager;
import sg.edu.sit.inf1009.p2team2.engine.managers.InputManager;
import sg.edu.sit.inf1009.p2team2.engine.managers.OutputManager;
import sg.edu.sit.inf1009.p2team2.engine.managers.SceneManager;

/**
 * ENGINE CONTEXT - Abstract Engine (Control)
 * Central dependency container for all engine managers
 * 
 * Following UML specification exactly:
 * - inputManager: InputManager
 * - outputManager: OutputManager
 * - configManager: ConfigManager
 * - sceneManager: SceneManager
 * - running: boolean
 * - deltaTime: float
 * 
 * This is the HEART of the engine - it creates and owns all managers.
 */
public class EngineContext {
    
    // Core managers
    private final InputManager inputManager;
    private final OutputManager outputManager;
    private final ConfigManager configManager;
    private final SceneManager sceneManager;
    
    // Engine state
    private boolean running;
    private float deltaTime;
    
    /**
     * Constructor
     * Creates all managers in the correct order
     */
    public EngineContext() {
        System.out.println("[EngineContext] Initializing engine...");
        
        // 1. Config first (other managers might read config)
        this.configManager = ConfigurationManager.getInstance();
        
        // 2. Input manager (no dependencies)
        this.inputManager = new InputManager();
        
        // 3. Output manager (reads config for window size, title)
        int width = configManager.getInt("display.width");
        int height = configManager.getInt("display.height");
        String title = configManager.getString("display.title");
        
        // Use defaults if config doesn't have values
        width = (width > 0) ? width : 800;
        height = (height > 0) ? height : 600;
        title = (title != null && !title.isEmpty()) ? title : "Abstract Engine";
        
        this.outputManager = new OutputManager(width, height, title);
        
        // 4. Scene manager (depends on context)
        this.sceneManager = new SceneManager(this);
        
        // Initialize state
        this.running = false;
        this.deltaTime = 0f;
        
        System.out.println("[EngineContext] Engine initialized successfully!");
    }
    
    /**
     * Initialize the engine
     * Call this after libGDX context is ready
     */
    public void initialize() {
        System.out.println("[EngineContext] Starting initialization...");
        
        outputManager.initialize();
        
        running = true;
        System.out.println("[EngineContext] Initialization complete!");
    }
    
    /**
     * Start the engine
     */
    public void start() {
        running = true;
        System.out.println("[EngineContext] Engine started");
    }
    
    /**
     * Stop the engine
     */
    public void stop() {
        running = false;
        System.out.println("[EngineContext] Engine stopped");
    }
    
    /**
     * Update the engine
     * Call this every frame
     * 
     * @param dt Delta time in seconds
     */
    public void update(float dt) {
        this.deltaTime = dt;
        
        // Update managers in order
        inputManager.update(dt);
        sceneManager.update(dt);
        outputManager.update(dt);
    }
    
    /**
     * Render the engine
     * Call this every frame after update
     */
    public void render() {
        sceneManager.render();
    }
    
    /**
     * Get input manager
     * 
     * @return InputManager instance
     */
    public InputManager getInputManager() {
        return inputManager;
    }
    
    /**
     * Get output manager
     * 
     * @return OutputManager instance
     */
    public OutputManager getOutputManager() {
        return outputManager;
    }
    
    /**
     * Get config manager
     * 
     * @return ConfigManager instance
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    /**
     * Get scene manager
     * 
     * @return SceneManager instance
     */
    public SceneManager getSceneManager() {
        return sceneManager;
    }
    
    /**
     * Check if engine is running
     * 
     * @return true if running
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Get current delta time
     * 
     * @return Delta time in seconds
     */
    public float getDeltaTime() {
        return deltaTime;
    }
    
    /**
     * Clean up all engine resources
     */
    public void dispose() {
        System.out.println("[EngineContext] Disposing engine...");
        
        sceneManager.dispose();
        outputManager.dispose();
        inputManager.dispose();
        
        running = false;
        
        System.out.println("[EngineContext] Engine disposed");
    }
}