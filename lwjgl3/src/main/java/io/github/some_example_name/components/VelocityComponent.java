// VELOCITY COMPONENT - How entity moves

package io.github.some_example_name.components;

import com.badlogic.gdx.math.Vector2;

public class VelocityComponent implements Component {
    public Vector2 velocity;
    public Vector2 acceleration;
    
    public VelocityComponent() {
        this.velocity = new Vector2(0, 0);
        this.acceleration = new Vector2(0, 0);
    }
    
    public VelocityComponent(float vx, float vy) {
        this.velocity = new Vector2(vx, vy);
        this.acceleration = new Vector2(0, 0);
    }
    
    public void stop() {
        velocity.set(0, 0);
        acceleration.set(0, 0);
    }
}