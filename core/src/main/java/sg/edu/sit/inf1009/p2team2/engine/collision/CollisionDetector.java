package sg.edu.sit.inf1009.p2team2.engine.collision;

import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;

/**
 * Responsible for collision detection between colliders.
 *
 * Implementation can use broad-phase + narrow-phase depending on needs.
 */
public class CollisionDetector {
    private final Entity owner;
    private final Shape shape;
    private final boolean trigger;
    private final Vector2 offset = new Vector2();

    public CollisionDetector(Entity owner, Shape shape, boolean trigger) {
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

    public boolean isTrigger() {
        return trigger;
    }

    public Vector2 getOffset() {
        return offset;
    }
}

