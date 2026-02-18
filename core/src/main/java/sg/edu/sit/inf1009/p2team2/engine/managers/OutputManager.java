package sg.edu.sit.inf1009.p2team2.engine.managers;

import sg.edu.sit.inf1009.p2team2.engine.output.Audio;
import sg.edu.sit.inf1009.p2team2.engine.output.Display;
import sg.edu.sit.inf1009.p2team2.engine.output.Renderer;

/**
 * Centralized output manager (rendering + audio).
 */
public class OutputManager {
    // Output devices
    private final Display display;
    private final Renderer renderer;
    private final Audio audio;

    /**
     * Constructor
     * @param width Window width
     * @param height Window height
     */
    public OutputManager(int width, int height) {
        this(width, height, "Abstract Engine");
    }

    /**
     * Constructor overload kept for backward compatibility.
     *
     * @param width Window width
     * @param height Window height
     * @param title Window title
     */
    public OutputManager(int width, int height, String title) {
        this.display = new Display(width, height, title);
        this.renderer = new Renderer();
        this.audio = new Audio();
    }

    /**
     * Initialize output systems
     * Call this AFTER construction, before using
     */
    public void initialize() {
        display.createWindow();
    }

    /**
     * Update output systems - call once per frame
     * 
     * @param dt Delta time (not used currently, but good for future)
     */
    public void update(float dt) {
        // Swap display buffers (show what was rendered)
        display.swapBuffers();
    }

    // ===== ACCESSORS =====
    
    /**
     * Get the display device
     * 
     * @return Display instance for window management
     */
    public Display getDisplay() {
        return display;
    }
    
    /**
     * Get the renderer
     * 
     * @return Renderer instance for drawing
     */
    public Renderer getRenderer() {
        return renderer;
    }
    
    /**
     * Get the audio system
     * 
     * @return Audio instance for sound and music
     */
    public Audio getAudio() {
        return audio;
    }
    
    /**
     * Clean up resources
     * Call this when shutting down the engine
     */
    public void dispose() {
        display.dispose();
        renderer.dispose();
        audio.dispose();
    }
}
