package sg.edu.sit.inf1009.p2team2.engine.ecs.components;

import sg.edu.sit.inf1009.p2team2.engine.ecs.Component;

public class InputComponent implements Component {
    private String actionMapId;
    private boolean enabled;

    public InputComponent() {
        this.actionMapId = "";
        this.enabled = true;
    }

    public String getActionMapId() {
        return actionMapId;
    }

    public void setActionMapId(String actionMapId) {
        this.actionMapId = actionMapId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }
}

