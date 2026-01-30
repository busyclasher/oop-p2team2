package io.github.some_example_name.input;

import com.badlogic.gdx.Input.Keys;
import java.util.HashMap;
import java.util.Map;

public class InputMap {
    private Map<String, Integer> actionBindings;
    
    public InputMap() {
        actionBindings = new HashMap<>();
        loadDefaults();
    }
    
    public void loadDefaults() {
        // Default keybindings
        bindAction("move_left", Keys.A);
        bindAction("move_right", Keys.D);
        bindAction("move_up", Keys.W);
        bindAction("move_down", Keys.S);
        bindAction("grab", Keys.SPACE);
        bindAction("pause", Keys.ESCAPE);
    }
    
    public void bindAction(String actionName, int keyCode) {
        actionBindings.put(actionName, keyCode);
    }
    
    public void unbindAction(String actionName) {
        actionBindings.remove(actionName);
    }
    
    public int getBoundKey(String actionName) {
        return actionBindings.getOrDefault(actionName, -1);
    }
    
    public boolean hasAction(String actionName) {
        return actionBindings.containsKey(actionName);
    }
    
    public void clearAll() {
        actionBindings.clear();
    }
}