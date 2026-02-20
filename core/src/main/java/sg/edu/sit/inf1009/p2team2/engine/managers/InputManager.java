package sg.edu.sit.inf1009.p2team2.engine.managers;

import sg.edu.sit.inf1009.p2team2.engine.input.InputMap;
import sg.edu.sit.inf1009.p2team2.engine.input.Keyboard;
import sg.edu.sit.inf1009.p2team2.engine.input.Mouse;
import com.badlogic.gdx.Gdx;
import java.util.HashMap;
import java.util.Map;

/**
 * INPUT MANAGER - Abstract Engine
 * Manages all input devices and provides input state to the application.
 * 
 * This is an ENGINE class - it has NO game-specific logic.
 * It only captures raw input and provides APIs.
 */
public class InputManager {
    
    // Input devices (owned by this manager)
    private Keyboard keyboard;
    private Mouse mouse;
    
    // Input mappings (support multiple configurations)
    private Map<String, InputMap> inputMaps;
    private String activeMapId;
    
    /**
     * Constructor - creates all input devices
     */
    public InputManager() {
        this.keyboard = new Keyboard();
        this.mouse = new Mouse();
        this.inputMaps = new HashMap<>();
        this.activeMapId = "default";
        
        // Create default input map
        InputMap defaultMap = new InputMap("default");
        defaultMap.loadDefaults();
        addInputMap("default", defaultMap);
    }
    
    /**
     * Update input state - call once per frame BEFORE processing input
     * 
     * @param dt Delta time (not used currently, but good for future)
     */
    public void update(float dt) {
        keyboard.update();
        mouse.update();
    }
    
    // ===== ACCESSORS =====
    
    /**
     * Get the keyboard device
     */
    public Keyboard getKeyboard() {
        return keyboard;
    }
    
    /**
     * Get the mouse device
     */
    public Mouse getMouse() {
        return mouse;
    }

    public void setMouseCoordinateTransformer(Mouse.CoordinateTransformer transformer) {
        mouse.setCoordinateTransformer(transformer);
    }
    
    // ===== INPUT MAP MANAGEMENT =====
    
    /**
     * Add a new input map configuration
     * 
     * @param id Unique identifier for this map
     * @param map The InputMap to add
     */
    public void addInputMap(String id, InputMap map) {
        inputMaps.put(id, map);
    }
    
    /**
     * Set which input map is currently active
     * 
     * @param id ID of the map to activate
     */
    public void setActiveMap(String id) {
        if (inputMaps.containsKey(id)) {
            this.activeMapId = id;
        } else {
            Gdx.app.error("InputManager", "InputMap not found: " + id);
        }
    }
    
    /**
     * Get the currently active input map
     */
    public InputMap getActiveMap() {
        return inputMaps.get(activeMapId);
    }

    public InputMap getInputMap() {
        return getActiveMap();
    }
    
    // ===== ACTION QUERIES (using active map) =====
    
    /**
     * Check if a named action is currently active
     * 
     * @param actionName Name of the action (e.g., "jump", "fire", "move_left")
     * @return true if the action's bound key is currently held down
     */
    public boolean isActionActive(String actionName) {
        InputMap map = getActiveMap();
        return map != null && map.isActionActive(actionName, keyboard);
    }
    
    /**
     * Check if a named action was just pressed this frame
     * 
     * @param actionName Name of the action
     * @return true if the action's bound key was just pressed
     */
    public boolean isActionPressed(String actionName) {
        InputMap map = getActiveMap();
        return map != null && map.isActionPressed(actionName, keyboard);
    }
    
    /**
     * Check if a named action was just released this frame
     * 
     * @param actionName Name of the action
     * @return true if the action's bound key was just released
     */
    public boolean isActionReleased(String actionName) {
        InputMap map = getActiveMap();
        return map != null && map.isActionReleased(actionName, keyboard);
    }
    
    /**
     * Clean up resources
     */
    public void dispose() {
        keyboard.reset();
        mouse.reset();
        inputMaps.clear();
    }
}
