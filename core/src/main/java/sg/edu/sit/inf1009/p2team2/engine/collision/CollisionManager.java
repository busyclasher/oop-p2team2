package sg.edu.sit.inf1009.p2team2.engine.collision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;

/**
 * Manages collision detection/resolution for the world.
 */
public class CollisionManager {
    private Entity owner;
    private Shape shape;
    private boolean trigger;

    private final List<Collider> colliders = new ArrayList<>();
    private final CollisionDetector detector;
    private final CollisionResolver resolver;

    private final List<Collision> collisions = new ArrayList<>();
    private final List<Collision> currentCollisions = new ArrayList<>();

    public CollisionManager() {
        this.detector = new CollisionDetector(owner, shape, trigger);
        this.resolver = new CollisionResolver();
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

    public boolean checkCollision(Collider a, Collider b) {
        // TODO(Cody): implement collision test between a and b (using detector + shapes).
        return false;
    }

    public void resolve(Collider a, Collider b) {
        // TODO(Cody): resolve collision between a and b (impulses, separation, events).
    }

    public void register(Collider c) {
        // TODO(Cody): register collider into collision system.
        if (c != null) {
            colliders.add(c);
        }
    }

    public void unregister(Collider c) {
        // TODO(Cody): unregister collider from collision system.
        colliders.remove(c);
    }

    public void update(float dt) {
        // TODO(Cody): detect + resolve collisions for this frame.
        List<Collision> detected = detectCollisions();
        resolveCollisions(detected);
        updateCollisionEvents(detected);
    }

    private List<Collision> detectCollisions() {
        // TODO(Cody): populate collisions list using broad/narrow phase detection.
        return Collections.emptyList();
    }

    private void resolveCollisions(List<Collision> cs) {
        // TODO(Cody): resolve all detected collisions.
    }

    private void updateCollisionEvents(List<Collision> cs) {
        // TODO(Cody): maintain currentCollisions and fire enter/stay/exit events.
        notifyEvents(cs);
    }

    private void notifyEvents(List<Collision> collisions) {
        // TODO(Cody): notify interested systems/entities about collision events.
    }
}

