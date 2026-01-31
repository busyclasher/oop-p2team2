package io.github.some_example_name.output;

public class OutputManager {
    private Display display;
    private Renderer renderer;
    private Audio audio;
    
    public OutputManager(int width, int height, String title) {
        this.display = new Display(width, height, title);
        this.renderer = new Renderer();
        this.audio = new Audio();
    }
    
    public void initialize() {
        display.createWindow();
        
        // Load assets here if needed
        // audio.loadSound("sounds/pickup.wav", "pickup");
    }
    
    public void beginFrame() {
        renderer.clear();
        renderer.begin();
    }
    
    public void endFrame() {
        renderer.end();
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
    
    public Audio getAudio() {
        return audio;
    }
    
    public Display getDisplay() {
        return display;
    }
    
    public void dispose() {
        renderer.dispose();
        audio.dispose();
        display.dispose();
    }
}
