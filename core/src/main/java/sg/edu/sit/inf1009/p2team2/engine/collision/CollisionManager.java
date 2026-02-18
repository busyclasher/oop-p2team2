package sg.edu.sit.inf1009.p2team2.engine.collision;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;
import sg.edu.sit.inf1009.p2team2.engine.managers.EntityManager;

/**
 * Coordinates detection and resolution of collisions each frame.
 */
public class CollisionManager {
    private final EntityManager entityManager;
    private final CollisionDetector detector;
    private final CollisionResolver resolver;
    private final Map<String, Collision> activeCollisions;

    public CollisionManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.detector = new CollisionDetector();
        this.resolver = new CollisionResolver();
        this.activeCollisions = new LinkedHashMap<>();
    }

    public void update(float dt) {
        List<Collision> collisions = detector.detectCollisions(entityManager.getAllEntities());
        resolver.resolveCollisions(collisions);

        activeCollisions.clear();
        for (Collision collision : collisions) {
            activeCollisions.put(buildKey(collision.getEntityA(), collision.getEntityB()), collision);
        }
    }

    public void registerCollider(Entity entity) {
        // Collider registration is implicit through entity components in this model.
    }

    public void unregisterCollider(Entity entity) {
        if (entity == null) {
            return;
        }
        String prefix = entity.getId() + ":";
        activeCollisions.entrySet().removeIf(entry -> entry.getKey().startsWith(prefix)
            || entry.getKey().contains(":" + entity.getId()));
    }

    public int getActiveCollisionCount() {
        return activeCollisions.size();
    }

    private String buildKey(Entity entityA, Entity entityB) {
        if (entityA == null || entityB == null) {
            return "";
        }
        int a = Math.min(entityA.getId(), entityB.getId());
        int b = Math.max(entityA.getId(), entityB.getId());
        return a + ":" + b;
    }
}
