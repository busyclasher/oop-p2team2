package sg.edu.sit.inf1009.p2team2.engine.input;


import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import java.util.HashMap;
import java.util.Map;

/**
 * INPUT MAP - Abstract Engine
 * Maps action names to keyboard key codes.
 * 
 * Allows rebindable controls:
 * - "jump" -> Keys.SPACE
 * - "move_left" -> Keys.A
 * - "fire" -> Keys.MOUSE_LEFT
 * 
 * Different maps can be used for different contexts
 * (e.g., "menu", "gameplay", "player1", "player2")
 */
public class InputMap {
    
    // Unique identifier for this map
    private String mapId;
    
    // Action name -> key code mapping
    private Map<String, Integer> actionBindings;
    
    /**
     * Constructor with default ID
     */
    public InputMap() {
        this("default");
    }
    
    /**
     * Constructor with custom ID
     * 
     * @param mapId Unique identifier for this map
     */
    public InputMap(String mapId) {
        this.mapId = mapId;
        this.actionBindings = new HashMap<>();
    }
    
    /**
     * Bind an action to a key
     * 
     * @param actionName Name of the action (e.g., "jump")
     * @param keyCode libGDX key code (e.g., Input.Keys.SPACE)
     */
    public void bindAction(String actionName, int keyCode) {
        actionBindings.put(actionName, keyCode);
    }
    
    /**
     * Remove a binding
     * 
     * @param actionName Action to unbind
     */
    public void unbindAction(String actionName) {
        actionBindings.remove(actionName);
    }
    
    /**
     * Check if an action is currently active (key held down)
     * 
     * @param actionName Name of the action
     * @param keyboard Keyboard to query
     * @return true if action's bound key is down
     */
    public boolean isActionActive(String actionName, Keyboard keyboard) {
        Integer keyCode = actionBindings.get(actionName);
        return keyCode != null && keyboard.isKeyDown(keyCode);
    }

    public boolean isActionActive(String actionName) {
        Integer keyCode = actionBindings.get(actionName);
        return keyCode != null && Gdx.input.isKeyPressed(keyCode);
    }
    
    /**
     * Check if an action was just pressed this frame
     * 
     * @param actionName Name of the action
     * @param keyboard Keyboard to query
     * @return true if action's bound key was just pressed
     */
    public boolean isActionPressed(String actionName, Keyboard keyboard) {
        Integer keyCode = actionBindings.get(actionName);
        return keyCode != null && keyboard.isKeyPressed(keyCode);
    }

    public boolean isActionPressed(String actionName) {
        Integer keyCode = actionBindings.get(actionName);
        return keyCode != null && Gdx.input.isKeyJustPressed(keyCode);
    }
    
    /**
     * Check if an action was just released this frame
     * 
     * @param actionName Name of the action
     * @param keyboard Keyboard to query
     * @return true if action's bound key was just released
     */
    public boolean isActionReleased(String actionName, Keyboard keyboard) {
        Integer keyCode = actionBindings.get(actionName);
        return keyCode != null && keyboard.isKeyReleased(keyCode);
    }

    public boolean isActionReleased(String actionName) {
        // libGDX does not provide key-just-released directly;
        // manager-level keyboard tracking remains the authoritative path.
        return false;
    }
    
    /**
     * Get the key code bound to an action
     * 
     * @param actionName Action to look up
     * @return Key code, or -1 if not bound
     */
    public int getBoundKey(String actionName) {
        return actionBindings.getOrDefault(actionName, -1);
    }
    
    /**
     * Check if an action has a binding
     * 
     * @param actionName Action to check
     * @return true if action is bound to a key
     */
    public boolean hasAction(String actionName) {
        return actionBindings.containsKey(actionName);
    }
    
    /**
     * Clear all bindings
     */
    public void clearAll() {
        actionBindings.clear();
    }
    
    /**
     * Load default key bindings
     * Override this in subclasses for specific defaults
     */
    public void loadDefaults() {
        // Common default bindings
        bindAction("move_up", Input.Keys.W);
        bindAction("move_down", Input.Keys.S);
        bindAction("move_left", Input.Keys.A);
        bindAction("move_right", Input.Keys.D);
        bindAction("jump", Input.Keys.SPACE);
        bindAction("interact", Input.Keys.E);
        bindAction("pause", Input.Keys.ESCAPE);
    }
    
    /**
     * Get the map ID
     */
    public String getMapId() {
        return mapId;
    }
}
