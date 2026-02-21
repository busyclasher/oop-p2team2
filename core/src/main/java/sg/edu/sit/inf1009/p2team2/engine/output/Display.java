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
    private boolean applyingWindowedMode;
    
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
        this.applyingWindowedMode = false;
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
        if (width <= 0 || height <= 0) {
            return;
        }

        this.width = width;
        this.height = height;

        // Avoid recursive resize loops when this is called from the framework's resize callback.
        if (isFullscreen() || applyingWindowedMode) {
            return;
        }

        if (Gdx.graphics.getWidth() == width && Gdx.graphics.getHeight() == height) {
            return;
        }

        applyingWindowedMode = true;
        try {
            Gdx.graphics.setWindowedMode(width, height);
        } finally {
            applyingWindowedMode = false;
        }
    }

    /**
     * Sync stored windowed size from OS/libGDX resize callbacks.
     * This does not request another window mode change.
     *
     * @param width current framebuffer width
     * @param height current framebuffer height
     */
    public void syncFromSystemResize(int width, int height) {
        if (width <= 0 || height <= 0 || isFullscreen()) {
            return;
        }
        this.width = width;
        this.height = height;
    }
    
    /**
     * Toggle fullscreen mode
     */
    public void toggleFullscreen() {
        boolean currentlyFullscreen = isFullscreen();
        if (!currentlyFullscreen) {
            isFullscreen = true;
            Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
            Gdx.graphics.setFullscreenMode(displayMode);
        } else {
            isFullscreen = false;
            int targetWidth = width > 0 ? width : 800;
            int targetHeight = height > 0 ? height : 600;
            Gdx.graphics.setWindowedMode(targetWidth, targetHeight);
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
        if (Gdx.graphics != null) {
            isFullscreen = Gdx.graphics.isFullscreen();
        }
        return isFullscreen;
    }
    
    /**
     * Clean up resources
     */
    public void dispose() {
        // libGDX handles window cleanup
    }
}
