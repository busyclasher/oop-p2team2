package io.github.some_example_name.input;

import com.badlogic.gdx.math.Vector2;

public class InputManager {
    private Keyboard keyboard;
    private Mouse mouse;
    private InputMap mapper;
    
    public InputManager() {
        this.keyboard = new Keyboard();
        this.mouse = new Mouse();
        this.mapper = new InputMap();
    }
    
    // Poll hardware state each frame
    public void pollEvents() {
        keyboard.update();
        mouse.update();
    }
    
    // Check if action is currently active
    public boolean isActionActive(String actionName) {
        int keyCode = mapper.getBoundKey(actionName);
        if (keyCode == -1) return false;
        return keyboard.isKeyHeld(keyCode);
    }
    
    // Check if action just pressed this frame
    public boolean isActionPressed(String actionName) {
        int keyCode = mapper.getBoundKey(actionName);
        if (keyCode == -1) return false;
        return keyboard.isKeyPressed(keyCode);
    }
    
    // Check if action just released this frame
    public boolean isActionReleased(String actionName) {
        int keyCode = mapper.getBoundKey(actionName);
        if (keyCode == -1) return false;
        return keyboard.isKeyReleased(keyCode);
    }
    
    public Vector2 getMousePosition() {
        return mouse.getPosition();
    }
    
    public boolean isMouseButtonDown() {
        return mouse.isButtonHeld(0);
    }
    
    public boolean isMouseButtonClicked() {
        return mouse.isButtonClicked(0);
    }
    
    public void bindAction(String actionName, int keyCode) {
        mapper.bindAction(actionName, keyCode);
    }
    
    public void dispose() {
        // Clean up if needed
    }
}
