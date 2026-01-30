// INPUTMANAGER - Main input facade

package io.github.some_example_name.input;

import com.badlogic.gdx.math.Vector2;
//import io.github.some_example_name.core.Entity;
//import io.github.some_example_name.core.World;
import io.github.some_example_name.components.InputComponent;
import io.github.some_example_name.components.VelocityComponent;
import io.github.some_example_name.components.TransformComponent;

public class InputManager {
    private Keyboard keyboard;
    private Mouse mouse;
    private InputMap mapper;
//    private World world;
    
/*     public InputManager(World world) {
        this.keyboard = new Keyboard();
        this.mouse = new Mouse();
        this.mapper = new InputMap();
        this.world = world;
    } */
    
    // Called once per frame to update hardware state
    public void pollEvents() {
        keyboard.update();
        mouse.update();
    }
    
    // Process input for all entities with InputComponent
/*     public void processInput(float dt) {
        for (Entity entity : world.getEntities()) {
            // Only process entities with InputComponent
            if (!entity.has(InputComponent.class)) continue;
            
            InputComponent input = entity.get(InputComponent.class);
            if (!input.enabled) continue;
            
            // Get velocity component (input affects movement)
            if (!entity.has(VelocityComponent.class)) continue;
            VelocityComponent velocity = entity.get(VelocityComponent.class);
            
            // Process movement based on action map
            float moveSpeed = 200f; // Could get from ConfigManager
            Vector2 inputVec = new Vector2(0, 0);
            
            if (isActionActive("move_left")) {
                inputVec.x -= 1;
            }
            if (isActionActive("move_right")) {
                inputVec.x += 1;
            }
            if (isActionActive("move_up")) {
                inputVec.y += 1;
            }
            if (isActionActive("move_down")) {
                inputVec.y -= 1;
            }
            
            // Normalize diagonal movement
            if (inputVec.len() > 0) {
                inputVec.nor().scl(moveSpeed);
            }
            
            velocity.velocity.set(inputVec);
        }
    } */
    
    // Check if an action is currently active
    public boolean isActionActive(String actionName) {
        int keyCode = mapper.getBoundKey(actionName);
        if (keyCode == -1) return false;
        return keyboard.isKeyHeld(keyCode);
    }
    
    // Check if an action was just pressed this frame
    public boolean isActionPressed(String actionName) {
        int keyCode = mapper.getBoundKey(actionName);
        if (keyCode == -1) return false;
        return keyboard.isKeyPressed(keyCode);
    }
    
    // Check if an action was just released this frame
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
    
    public void bindAction(String actionName, int keyCode) {
        mapper.bindAction(actionName, keyCode);
    }
    
    public void dispose() {
        // Clean up if needed
    }
}