package sg.edu.sit.inf1009.p2team2.engine.collision;

import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;

/**
 * Collision participant attached to an {@link Entity}.
 */
public class Collider {
    private final Entity owner;
    private Shape shape;
    private boolean trigger;

    public Collider(Entity owner, Shape shape, boolean trigger) {
        this.owner = owner;
        this.shape = shape;
        this.trigger = trigger;
    }

    public Entity getOwner() {
        return owner;
    }

    public Shape getWorldShape() {
        return shape;
    }

    public void setWorldShape(Shape shape) {
        this.shape = shape;
    }

    public boolean isTrigger() {
        return trigger;
    }

    public void setTrigger(boolean trigger) {
        this.trigger = trigger;
    }
}

