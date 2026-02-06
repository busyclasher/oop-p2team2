package sg.edu.sit.inf1009.p2team2.engine.managers;

import sg.edu.sit.inf1009.p2team2.engine.output.Audio;
import sg.edu.sit.inf1009.p2team2.engine.output.Display;
import sg.edu.sit.inf1009.p2team2.engine.output.Renderer;
import sg.edu.sit.inf1009.p2team2.engine.world.World;

/**
 * Centralized output manager (rendering + audio).
 */
public class OutputManager {
    private final Display display;
    private final Renderer renderer;
    private final Audio audio;

    private World world;

    public OutputManager(int width, int height) {
        this.display = new Display(width, height, "Abstract Engine");
        this.renderer = new Renderer();
        this.audio = new Audio();
    }

    public void initialize() {
        // TODO(HongYih): initialize display/audio/render resources.
        display.createWindow();
    }

    public void beginFrame() {
        // TODO(HongYih): clear + begin render pass.
        renderer.begin();
    }

    public void endFrame() {
        // TODO(HongYih): end render pass + present frame.
        display.swapBuffers();
    }

    public void playSound(String name) {
        audio.playSound(name);
    }

    public void playSound(String name, float volume) {
        audio.playSound(name, volume);
    }

    public void playMusic(String name, boolean loop) {
        audio.playMusic(name, loop);
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public void dispose() {
        // TODO(HongYih): dispose output resources in the correct order.
        renderer.dispose();
        audio.dispose();
        display.dispose();
    }

    public Object getAudio() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAudio'");
    }

    public boolean shouldClose() {
        // TODO Auto-generated method stub
        return display != null && display.shouldClose();
    }

    public Object getDisplay() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDisplay'");
    }
}
