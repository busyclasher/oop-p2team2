package sg.edu.sit.inf1009.p2team2.engine.scenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.input.Keyboard;
import sg.edu.sit.inf1009.p2team2.engine.input.Mouse;
import sg.edu.sit.inf1009.p2team2.engine.output.Audio;
import sg.edu.sit.inf1009.p2team2.engine.output.Renderer;
import sg.edu.sit.inf1009.p2team2.engine.output.Display;

/**
 * INPUT/OUTPUT TEST SCENE
 * 
 * This scene tests ALL Input/Output functionality:
 * 
 * KEYBOARD TESTS:
 * - Press WASD keys to move the green box
 * - Press SPACE to change background color
 * - Press ESC to return to menu
 * 
 * MOUSE TESTS:
 * - Move mouse to see red circle follow cursor
 * - Click LEFT mouse button to play sound
 * - Click RIGHT mouse button to draw a blue rectangle
 * - Scroll mouse wheel to change circle size
 * 
 * RENDERING TESTS:
 * - drawSprite() - Background image
 * - drawCircle() - Red circle at mouse
 * - drawRect() - Green box (WASD), blue rectangles (clicks)
 * - drawLine() - Lines from center to mouse
 * - drawText() - Instructions and mouse position
 * 
 * AUDIO TESTS:
 * - Left click plays sound effect
 * - Press M to toggle background music
 * - Press + to increase volume
 * - Press - to decrease volume
 * 
 * @author Test Suite
 */
public class InputOutputTestScene extends Scene {
    
    // Test state
    private Vector2 boxPosition;
    private float boxSpeed = 200f;
    private Color backgroundColor;
    private int colorIndex = 0;
    private Color[] colors = {
        Color.BLACK, Color.BLUE, Color.GREEN, Color.RED, Color.PURPLE
    };
    
    private float circleRadius = 30f;
    private java.util.List<Vector2> clickPoints = new java.util.ArrayList<>();
    
    private boolean musicPlaying = false;
    private float volume = 0.5f;
    
    // Test status
    private StringBuilder testLog = new StringBuilder();
    
    public InputOutputTestScene(EngineContext context) {
        super(context);
        this.boxPosition = new Vector2(400, 300);
        this.backgroundColor = Color.BLACK;
    }
    
    @Override
    public void onEnter() {
        log("=== INPUT/OUTPUT TEST STARTED ===");
        log("Scene entered successfully");
    }
    
    @Override
    public void onExit() {
        log("Scene exited");
    }
    
    @Override
    public void load() {
        log("Loading test scene resources...");
        
        // Try to load test audio (optional - won't crash if files don't exist)
        Audio audio = context.getOutputManager().getAudio();
        try {
            // Uncomment these if you have test audio files
            // audio.loadSound("assets/test_sound.wav", "test_beep");
            // audio.loadMusic("assets/test_music.mp3", "test_music");
            log("Audio system ready (no files loaded yet)");
        } catch (Exception e) {
            log("Audio files not found (this is OK for testing)");
        }
        
        log("Test scene loaded");
    }
    
    @Override
    public void unload() {
        log("Unloading test scene");
        clickPoints.clear();
    }
    
    @Override
    public void update(float dt) {
        handleInput();
        
        // Keep box on screen
        boxPosition.x = Math.max(50, Math.min(750, boxPosition.x));
        boxPosition.y = Math.max(50, Math.min(550, boxPosition.y));
    }
    
    @Override
    public void handleInput() {
        Keyboard keyboard = context.getInputManager().getKeyboard();
        Mouse mouse = context.getInputManager().getMouse();
        Display display = context.getOutputManager().getDisplay();
        Audio audio = context.getOutputManager().getAudio();
        
        float dt = context.getDeltaTime();
        
        // === KEYBOARD TESTS ===
        
        // Test: WASD movement
        if (keyboard.isKeyDown(Input.Keys.W)) {
            boxPosition.y += boxSpeed * dt;
            if (keyboard.isKeyPressed(Input.Keys.W)) {
                log("✓ W key pressed (isKeyPressed works)");
            }
        }
        if (keyboard.isKeyDown(Input.Keys.S)) {
            boxPosition.y -= boxSpeed * dt;
        }
        if (keyboard.isKeyDown(Input.Keys.A)) {
            boxPosition.x -= boxSpeed * dt;
        }
        if (keyboard.isKeyDown(Input.Keys.D)) {
            boxPosition.x += boxSpeed * dt;
        }
        
        // Test: Key press detection
        if (keyboard.isKeyPressed(Input.Keys.SPACE)) {
            colorIndex = (colorIndex + 1) % colors.length;
            backgroundColor = colors[colorIndex];
            log("✓ SPACE pressed - Background color changed");
        }
        
        // Test: ESC to return to menu
        if (keyboard.isKeyPressed(Input.Keys.ESCAPE)) {
            log("✓ ESC pressed - Returning to menu");
            context.getSceneManager().pop();
            return;
        }
        
        // Test: Volume controls
        if (keyboard.isKeyPressed(Input.Keys.PLUS) || keyboard.isKeyPressed(Input.Keys.EQUALS)) {
            volume = Math.min(1.0f, volume + 0.1f);
            audio.setMasterVolume(volume);
            log("✓ Volume increased: " + (int)(volume * 100) + "%");
        }
        if (keyboard.isKeyPressed(Input.Keys.MINUS)) {
            volume = Math.max(0.0f, volume - 0.1f);
            audio.setMasterVolume(volume);
            log("✓ Volume decreased: " + (int)(volume * 100) + "%");
        }
        
        // Test: Music toggle
        if (keyboard.isKeyPressed(Input.Keys.M)) {
            if (musicPlaying) {
                audio.stopMusic();
                musicPlaying = false;
                log("✓ Music stopped");
            } else {
                // Only play if music is loaded
                // audio.playMusic("test_music", true);
                musicPlaying = true;
                log("✓ Music play requested (no file loaded)");
            }
        }
        
        // Test: Fullscreen toggle
        if (keyboard.isKeyPressed(Input.Keys.F)) {
            display.toggleFullscreen();
            log("✓ Fullscreen toggled");
        }
        
        // === MOUSE TESTS ===
        
        Vector2 mousePos = mouse.getPosition();
        
        // Test: Left click - add click point and play sound
        if (mouse.isButtonPressed(0)) {
            clickPoints.add(mousePos.cpy());
            log("✓ Left click at (" + (int)mousePos.x + ", " + (int)mousePos.y + ")");
            
            // Try to play sound
            // audio.playSound("test_beep");
        }
        
        // Test: Right click - clear click points
        if (mouse.isButtonPressed(1)) {
            clickPoints.clear();
            log("✓ Right click - Cleared all click points");
        }
        
        // Test: Middle click
        if (mouse.isButtonPressed(2)) {
            log("✓ Middle click detected");
        }
        
        // Test: Scroll wheel
        float scroll = mouse.getScrollDelta();
        if (scroll != 0) {
            circleRadius += scroll * 5;
            circleRadius = Math.max(10, Math.min(100, circleRadius));
            log("✓ Scroll: radius = " + (int)circleRadius);
        }
        
        // Test: Mouse button held down
        if (mouse.isButtonDown(0)) {
            // Drawing continuously while held
        }
        
        // Test: Mouse button release
        if (mouse.isButtonReleased(0)) {
            log("✓ Left button released");
        }
    }
    
    @Override
    public void render() {
        Renderer renderer = context.getOutputManager().getRenderer();
        Display display = context.getOutputManager().getDisplay();
        Mouse mouse = context.getInputManager().getMouse();
        
        // Test: Clear screen with custom color
        renderer.setClearColor(backgroundColor);
        renderer.clear();
        
        // Test: Begin/End rendering
        renderer.begin();
        
        // === RENDERING TESTS ===
        
        // Test: Draw background sprite (if available)
        // Uncomment if you have a test sprite
        // renderer.drawSprite("test_background.png", new Vector2(400, 300), 0f, new Vector2(1, 1));
        
        // Test: Draw rectangles (filled and outline)
        Rectangle boxRect = new Rectangle(
            boxPosition.x - 25, 
            boxPosition.y - 25, 
            50, 
            50
        );
        renderer.drawRect(boxRect, Color.GREEN, true);  // Filled
        renderer.drawRect(boxRect, Color.WHITE, false); // Outline
        
        // Test: Draw all click points
        for (Vector2 point : clickPoints) {
            Rectangle clickRect = new Rectangle(point.x - 5, point.y - 5, 10, 10);
            renderer.drawRect(clickRect, Color.CYAN, true);
        }
        
        // Test: Draw circle at mouse position
        Vector2 mousePos = mouse.getPosition();
        renderer.drawCircle(mousePos, circleRadius, Color.RED, false);
        renderer.drawCircle(mousePos, 5f, Color.YELLOW, true);
        
        // Test: Draw line from center to mouse
        Vector2 center = new Vector2(display.getWidth() / 2f, display.getHeight() / 2f);
        renderer.drawLine(center, mousePos, Color.ORANGE, 2f);
        
        // Test: Draw border around screen
        Rectangle border = new Rectangle(5, 5, display.getWidth() - 10, display.getHeight() - 10);
        renderer.drawRect(border, Color.WHITE, false);
        
        // Test: Draw text
        renderer.drawText("INPUT/OUTPUT TEST SCENE", new Vector2(20, display.getHeight() - 30), "default", Color.WHITE);
        
        // Test: Draw instructions
        float y = display.getHeight() - 60;
        renderer.drawText("KEYBOARD: WASD=Move Box | SPACE=Change BG | ESC=Menu | F=Fullscreen", 
            new Vector2(20, y), "default", Color.LIGHT_GRAY);
        y -= 20;
        renderer.drawText("MOUSE: Move=Circle | Left Click=Mark | Right Click=Clear | Scroll=Resize", 
            new Vector2(20, y), "default", Color.LIGHT_GRAY);
        y -= 20;
        renderer.drawText("AUDIO: M=Music | +/- = Volume", 
            new Vector2(20, y), "default", Color.LIGHT_GRAY);
        
        // Test: Draw mouse position
        renderer.drawText(
            "Mouse: (" + (int)mousePos.x + ", " + (int)mousePos.y + ")", 
            new Vector2(20, 80), 
            "default", 
            Color.YELLOW
        );
        
        // Test: Draw box position
        renderer.drawText(
            "Box: (" + (int)boxPosition.x + ", " + (int)boxPosition.y + ")", 
            new Vector2(20, 60), 
            "default", 
            Color.GREEN
        );
        
        // Test: Draw status
        renderer.drawText(
            "Clicks: " + clickPoints.size() + " | Radius: " + (int)circleRadius + " | Vol: " + (int)(volume*100) + "%", 
            new Vector2(20, 40), 
            "default", 
            Color.CYAN
        );
        
        // Test: Draw recent log messages (last 3)
        String[] logs = testLog.toString().split("\n");
        int startIdx = Math.max(0, logs.length - 3);
        y = 20;
        for (int i = startIdx; i < logs.length; i++) {
            renderer.drawText(logs[i], new Vector2(20, y), "default", new Color(0.7f, 0.7f, 0.7f, 1f));
            y -= 15;
        }
        
        renderer.end();
    }
    
    /**
     * Log test events
     */
    private void log(String message) {
        System.out.println("[InputOutputTest] " + message);
        testLog.append(message).append("\n");
        
        // Keep log from getting too long
        String[] lines = testLog.toString().split("\n");
        if (lines.length > 20) {
            testLog = new StringBuilder();
            for (int i = lines.length - 20; i < lines.length; i++) {
                testLog.append(lines[i]).append("\n");
            }
        }
    }
}