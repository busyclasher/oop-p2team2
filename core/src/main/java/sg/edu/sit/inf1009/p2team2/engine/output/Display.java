package sg.edu.sit.inf1009.p2team2.engine.output;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;

/**
 * DISPLAY - Abstract Engine
 * Manages the game window and display settings.
 * 
 * Uses libGDX for cross-platform window management.
 */
public class Display {
    
    private int width;
    private int height;
    private String title;
    private boolean isFullscreen;
    
    /**
     * Constructor
     * 
     * @param width Window width in pixels
     * @param height Window height in pixels
     * @param title Window title
     */
    public Display(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.isFullscreen = false;
    }
    
    /**
     * Create the window
     * Note: In libGDX, window is created automatically by the launcher.
     * This method can be used for post-creation setup.
     */
    public void createWindow() {
        // Window is created by libGDX launcher
        // This method can set initial window properties
        Gdx.graphics.setTitle(title);
    }
    
    /**
     * Set window title
     * 
     * @param title New window title
     */
    public void setTitle(String title) {
        this.title = title;
        Gdx.graphics.setTitle(title);
    }
    
    /**
     * Resize the window
     * 
     * @param width New width
     * @param height New height
     */
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        
        if (!isFullscreen) {
            Gdx.graphics.setWindowedMode(width, height);
        }
    }
    
    /**
     * Toggle fullscreen mode
     */
    public void toggleFullscreen() {
        isFullscreen = !isFullscreen;
        
        if (isFullscreen) {
            Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
            Gdx.graphics.setFullscreenMode(displayMode);
        } else {
            Gdx.graphics.setWindowedMode(width, height);
        }
    }
    
    /**
     * Swap display buffers (present rendered frame)
     * Note: In libGDX this is handled automatically, but we keep
     * this method for abstraction consistency
     */
    public void swapBuffers() {
        // libGDX handles buffer swapping automatically
        // This is a placeholder for consistency with the abstract engine pattern
    }
    
    /**
     * Check if window should close
     * 
     * @return true if user clicked close button
     */
    public boolean shouldClose() {
        // In libGDX, check if exit was requested
        return false; // libGDX handles this differently via ApplicationListener
    }
    
    /**
     * Get current window width
     * 
     * @return Width in pixels
     */
    public int getWidth() {
        return Gdx.graphics.getWidth();
    }
    
    /**
     * Get current window height
     * 
     * @return Height in pixels
     */
    public int getHeight() {
        return Gdx.graphics.getHeight();
    }
    
    /**
     * Check if currently in fullscreen mode
     * 
     * @return true if fullscreen
     */
    public boolean isFullscreen() {
        return isFullscreen;
    }
    
    /**
     * Clean up resources
     */
    public void dispose() {
        // libGDX handles window cleanup
    }
}