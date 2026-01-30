package io.github.some_example_name.output;

//import io.github.some_example_name.core.Entity;
//import io.github.some_example_name.core.World;
import io.github.some_example_name.components.RenderableComponent;
import io.github.some_example_name.components.TransformComponent;
import java.util.ArrayList;
import java.util.List;

public class OutputManager {
    private Display display;
    private Renderer renderer;
    private Audio audio;
    //private World world;
    private Camera camera;
    
    /* public OutputManager(World world, int width, int height) {
        this.world = world;
        this.display = new Display(width, height, "Recycling Game");
        this.renderer = new Renderer();
        this.audio = new Audio();
        this.camera = new Camera(width, height);
        
        // Set up camera
        camera.setPosition(width / 2f, height / 2f);
        renderer.setCamera(camera);
    } */
    
    public void initialize() {
        display.createWindow();
        
        // Load assets (example)
        // audio.loadSound("sounds/pickup.wav", "pickup");
        // audio.loadMusic("music/background.mp3", "bgm");
    }
    
    public void beginFrame() {
        renderer.clear();
        renderer.begin();
    }
    
    /* public void renderWorld() {
        List<Entity> entities = world.getEntities();
        
        // Sort by zIndex for proper layering
        List<Entity> renderableEntities = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.has(RenderableComponent.class) && 
                entity.has(TransformComponent.class)) {
                renderableEntities.add(entity);
            }
        }
        
        renderableEntities.sort((a, b) -> {
            int zA = a.get(RenderableComponent.class).zIndex;
            int zB = b.get(RenderableComponent.class).zIndex;
            return Integer.compare(zA, zB);
        });
        
        // Render each entity
        for (Entity entity : renderableEntities) {
            RenderableComponent renderable = entity.get(RenderableComponent.class);
            if (!renderable.visible) continue;
            
            TransformComponent transform = entity.get(TransformComponent.class);
            
            renderer.drawSprite(
                renderable.spriteId,
                transform.position,
                transform.rotation,
                transform.scale,
                renderable.color
            );
        }
    } */
    
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
    
    public Camera getCamera() {
        return camera;
    }
    
    public void setCamera(Camera camera) {
        this.camera = camera;
        renderer.setCamera(camera);
    }
    
    public Renderer getRenderer() {
        return renderer;
    }
    
    public void dispose() {
        renderer.dispose();
        audio.dispose();
        display.dispose();
    }
}