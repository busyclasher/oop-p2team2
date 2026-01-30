package io.github.some_example_name.output;

import com.badlogic.gdx.Gdx;

public class Display {
    private int width;
    private int height;
    private String title;
    private boolean isFullscreen;
    
    public Display(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.isFullscreen = false;
    }
    
    public void createWindow() {
        // LibGDX handles this automatically
        if (title != null && !title.isEmpty()) {
            Gdx.graphics.setTitle(title);
        }
    }
    
    public void setTitle(String title) {
        this.title = title;
        Gdx.graphics.setTitle(title);
    }
    
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public void toggleFullscreen() {
        isFullscreen = !isFullscreen;
        if (isFullscreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(width, height);
        }
    }
    
    public void swapBuffers() {
        // LibGDX handles this
    }
    
    public boolean shouldClose() {
        return false;
    }
    
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    public void dispose() {
        // Clean up
    }
}
