package io.github.some_example_name.components;

public class InputComponent implements Component {
    public String actionMapId;  // Which action map to use (e.g., "player1", "player2")
    public boolean enabled;     // Can this entity receive input right now?
    
    // Default constructor
    public InputComponent() {
        this.actionMapId = "default";
        this.enabled = true;
    }
    
    // Constructor with action map
    public InputComponent(String actionMapId) {
        this.actionMapId = actionMapId;
        this.enabled = true;
    }
    
    // Constructor with enabled state
    public InputComponent(String actionMapId, boolean enabled) {
        this.actionMapId = actionMapId;
        this.enabled = enabled;
    }
    
    // Helper methods
    public void enable() {
        enabled = true;
    }
    
    public void disable() {
        enabled = false;
    }
}

