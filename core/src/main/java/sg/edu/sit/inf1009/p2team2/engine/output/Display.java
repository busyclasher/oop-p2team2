package sg.edu.sit.inf1009.p2team2.engine.output;

import com.badlogic.gdx.Gdx;

/**
 * Window/display abstraction.
 *
 * For libGDX, the platform layer already manages the window. Keep this class as a
 * façade so the engine remains decoupled from the underlying toolkit details.
 */
public class Display {
    private int width;
    private int height;
    private String title;
    private boolean fullscreen;
    private boolean shouldClose = false;

    public Display(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.fullscreen = false;
    }

    public void createWindow() {
        // TODO(HongYih): create/initialize the display/window (or map to libGDX setup).
    }

    public void setTitle(String title) {
        this.title = title;
        // TODO(HongYih): propagate to the platform window title.
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        // TODO(HongYih): propagate resize to the platform.
    }

    public void toggleFullscreen() {
        this.fullscreen = !this.fullscreen;
        // TODO(HongYih): apply fullscreen toggle to the platform.
    if (isFullscreen()) {
        // Get the current display mode of the monitor
        com.badlogic.gdx.Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
        // Set to full screen
        Gdx.graphics.setFullscreenMode(mode);
    } else {
        // Return to windowed mode (e.g., 800x600)
        Gdx.graphics.setWindowedMode(800, 600);
    }
    System.out.println("[Display] Fullscreen set to: " + fullscreen);
    }

    public void swapBuffers() {
        // TODO(HongYih): swap buffers/present frame if applicable.
    }

    public boolean shouldClose() {
        // TODO(HongYih): map to platform close-request state.
        return shouldClose;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void dispose() {
        // TODO(HongYih): dispose display resources if any.
    }
}

