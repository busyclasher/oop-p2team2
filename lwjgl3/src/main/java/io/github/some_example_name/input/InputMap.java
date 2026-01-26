package io.github.some_example_name.input;
import com.badlogic.gdx.Input.Keys;
import java.util.Map;
import java.util.HashMap;

public class InputMap {
    private Map<String, Integer> actionBindings;
    
    public InputMap() {
        actionBindings = new HashMap<>();
        setDefaultBindings();
    }
    
    private void setDefaultBindings() {
        // Default keybindings for your recycling game
        actionBindings.put("move_left", Keys.A);
        actionBindings.put("move_right", Keys.D);
        actionBindings.put("move_up", Keys.W);
        actionBindings.put("move_down", Keys.S);
        actionBindings.put("grab", Keys.SPACE);
        actionBindings.put("pause", Keys.ESCAPE);
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
}
