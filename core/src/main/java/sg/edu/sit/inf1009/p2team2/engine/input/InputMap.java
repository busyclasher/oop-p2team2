package sg.edu.sit.inf1009.p2team2.engine.input;

import java.util.HashMap;
import java.util.Map;

public class InputMap {
    private final Map<String, Integer> actionBindings = new HashMap<>();

    public InputMap() {
    }

    public void bindAction(String actionName, int keyCode) {
        actionBindings.put(actionName, keyCode);
    }

    public void unbindAction(String actionName) {
        actionBindings.remove(actionName);
    }

    public int getBoundKey(String actionName) {
        Integer key = actionBindings.get(actionName);
        return key != null ? key : -1;
    }

    public boolean hasAction(String actionName) {
        return actionBindings.containsKey(actionName);
    }

    public void clearAll() {
        actionBindings.clear();
    }

    public void loadDefaults() {
        // TODO(HongYih): define default bindings for engine demo scenes.
    }
}

