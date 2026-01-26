package io.github.some_example_name.input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import java.util.HashMap;
import java.util.Map;

import io.github.some_example_name.components.InputComponent;
import io.github.some_example_name.components.VelocityComponent;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.world.World;

public class InputManager {
    private Keyboard keyboard;
    private Mouse mouse;
    private InputMap mapper;
    private World world; // Reference to get entities
    
    public InputManager(World world) {
        this.keyboard = new Keyboard();
        this.mouse = new Mouse();
        this.mapper = new InputMap();
        this.world = world;
    }
    
    public void pollEvents() {
        keyboard.update();
        mouse.update();
    }
    
    public void processInput(float dt) {
        // Get all entities and process their input
        for (Entity entity : world.getEntities()) {
            // Only process entities that have input component
            if (!entity.has(InputComponent.class)) continue;
            
            InputComponent input = entity.get(InputComponent.class);
            if (!input.enabled) continue;
            
            // Get velocity component (input affects movement)
            if (!entity.has(VelocityComponent.class)) continue;
            VelocityComponent velocity = entity.get(VelocityComponent.class);
            
            // Process movement based on action map
            float moveSpeed = 200f; // Or get from ConfigManager
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
    }
    
    public boolean isActionActive(String actionName) {
        int keyCode = mapper.getBoundKey(actionName);
        if (keyCode == -1) return false;
        return keyboard.isKeyHeld(keyCode);
    }
    
    public Vector2 getMousePosition() {
        return mouse.getPosition();
    }
    
    public boolean isMouseButtonDown() {
        return mouse.isButtonHeld(0); // Left button
    }
    
    public void dispose() {
        // Clean up if needed
    }
}