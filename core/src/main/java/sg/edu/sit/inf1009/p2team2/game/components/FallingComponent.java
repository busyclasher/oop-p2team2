package sg.edu.sit.inf1009.p2team2.game.components;

import sg.edu.sit.inf1009.p2team2.engine.entity.ComponentAdapter;

/**
 * ECS component that controls vertical fall speed for dropping entities.
 */
public class FallingComponent implements ComponentAdapter {

    private float speed;
    private boolean active;

    public FallingComponent(float speed) {
        this.speed  = speed;
        this.active = true;
    }

    public float   getSpeed()   { return speed; }
    public void    setSpeed(float speed) { this.speed = speed; }
    public boolean isActive()   { return active; }
    public void    deactivate() { this.active = false; }
}
