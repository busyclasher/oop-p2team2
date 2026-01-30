// INPUT COMPONENT - Marks entity as controllable

package io.github.some_example_name.components;

public class InputComponent implements Component {
    public String actionMapId;  // Which control scheme (e.g., "player1")
    public boolean enabled;     // Can receive input right now?
    
    public InputComponent() {
        this.actionMapId = "default";
        this.enabled = true;
    }
    
    public InputComponent(String actionMapId) {
        this.actionMapId = actionMapId;
        this.enabled = true;
    }
    
    public void enable() {
        enabled = true;
    }
    
    public void disable() {
        enabled = false;
    }
}


