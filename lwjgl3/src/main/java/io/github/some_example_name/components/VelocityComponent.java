package io.github.some_example_name.components;

import com.badlogic.gdx.math.Vector2;

public class VelocityComponent implements Component {
    public Vector2 velocity;      // Pixels per second
    public Vector2 acceleration;  // Pixels per second squared
    
    // Default constructor - no movement
    public VelocityComponent() {
        this.velocity = new Vector2(0, 0);
        this.acceleration = new Vector2(0, 0);
    }
    
    // Constructor with velocity only
    public VelocityComponent(float vx, float vy) {
        this.velocity = new Vector2(vx, vy);
        this.acceleration = new Vector2(0, 0);
    }
    
    // Constructor with velocity vector
    public VelocityComponent(Vector2 velocity) {
        this.velocity = velocity.cpy();
        this.acceleration = new Vector2(0, 0);
    }
    
    // Full constructor
    public VelocityComponent(Vector2 velocity, Vector2 acceleration) {
        this.velocity = velocity.cpy();
        this.acceleration = acceleration.cpy();
    }
    
    // Helper to stop movement
    public void stop() {
        velocity.set(0, 0);
        acceleration.set(0, 0);
    }
    
    // Helper to apply force/impulse
    public void addVelocity(float dx, float dy) {
        velocity.add(dx, dy);
    }
}