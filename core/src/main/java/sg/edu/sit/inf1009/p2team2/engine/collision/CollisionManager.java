package sg.edu.sit.inf1009.p2team2.engine.collision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final List<CollisionListener> listeners = new ArrayList<>();

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
        return detector.detect(a, b) != null;
    }

    public void resolve(Collider a, Collider b) {
        Contact contact = detector.detect(a, b);
        if (contact == null) {
            return;
        }
        resolver.resolve(new Collision(a, b, contact));
    }

    public void register(Collider c) {
        if (c != null && !colliders.contains(c)) {
            colliders.add(c);
        }
    }

    public void unregister(Collider c) {
        colliders.remove(c);
    }

    public void update(float dt) {
        // TODO(Cody): detect + resolve collisions for this frame.
        List<Collision> detected = detectCollisions();
        resolveCollisions(detected);
        updateCollisionEvents(detected);
    }

    private List<Collision> detectCollisions() {
        collisions.clear();
        int count = colliders.size();
        for (int i = 0; i < count; i++) {
            Collider a = colliders.get(i);
            if (a == null) {
                continue;
            }
            for (int j = i + 1; j < count; j++) {
                Collider b = colliders.get(j);
                if (b == null) {
                    continue;
                }
                Contact contact = detector.detect(a, b);
                if (contact != null) {
                    collisions.add(new Collision(a, b, contact));
                }
            }
        }
        return collisions;
    }

    private void resolveCollisions(List<Collision> cs) {
        if (cs == null) {
            return;
        }
        for (Collision collision : cs) {
            resolver.resolve(collision);
        }
    }

    private void updateCollisionEvents(List<Collision> cs) {
        Map<CollisionKey, Collision> previous = toCollisionMap(currentCollisions);
        Map<CollisionKey, Collision> next = new HashMap<>();

        if (cs != null) {
            for (Collision collision : cs) {
                CollisionKey key = CollisionKey.of(collision);
                next.put(key, collision);
                if (previous.containsKey(key)) {
                    notifyEvent(collision, CollisionEventType.STAY);
                } else {
                    notifyEvent(collision, CollisionEventType.ENTER);
                }
            }
        }

        for (Map.Entry<CollisionKey, Collision> entry : previous.entrySet()) {
            if (!next.containsKey(entry.getKey())) {
                notifyEvent(entry.getValue(), CollisionEventType.EXIT);
            }
        }

        currentCollisions.clear();
        if (cs != null) {
            currentCollisions.addAll(cs);
        }
    }

    public void addListener(CollisionListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(CollisionListener listener) {
        listeners.remove(listener);
    }

    public List<Collision> getCurrentCollisions() {
        return Collections.unmodifiableList(currentCollisions);
    }

    private void notifyEvent(Collision collision, CollisionEventType type) {
        if (collision == null || type == null) {
            return;
        }

        for (CollisionListener listener : listeners) {
            dispatch(listener, collision, type);
        }

        Collider a = collision.getA();
        Collider b = collision.getB();
        if (a != null && a.getListener() != null) {
            dispatch(a.getListener(), collision, type);
        }
        if (b != null && b.getListener() != null) {
            dispatch(b.getListener(), collision, type);
        }
    }

    private void dispatch(CollisionListener listener, Collision collision, CollisionEventType type) {
        if (listener == null) {
            return;
        }
        switch (type) {
            case ENTER:
                listener.onCollisionEnter(collision);
                break;
            case STAY:
                listener.onCollisionStay(collision);
                break;
            case EXIT:
                listener.onCollisionExit(collision);
                break;
            default:
                break;
        }
    }

    private Map<CollisionKey, Collision> toCollisionMap(List<Collision> collisions) {
        Map<CollisionKey, Collision> map = new HashMap<>();
        if (collisions == null) {
            return map;
        }
        for (Collision collision : collisions) {
            map.put(CollisionKey.of(collision), collision);
        }
        return map;
    }

    private enum CollisionEventType {
        ENTER,
        STAY,
        EXIT
    }

    private static final class CollisionKey {
        private final Collider a;
        private final Collider b;
        private final int hash;

        private CollisionKey(Collider first, Collider second) {
            if (first == null || second == null) {
                this.a = first;
                this.b = second;
            } else {
                int ha = System.identityHashCode(first);
                int hb = System.identityHashCode(second);
                if (ha <= hb) {
                    this.a = first;
                    this.b = second;
                } else {
                    this.a = second;
                    this.b = first;
                }
            }
            int h1 = System.identityHashCode(a);
            int h2 = System.identityHashCode(b);
            this.hash = 31 * h1 + h2;
        }

        public static CollisionKey of(Collision collision) {
            if (collision == null) {
                return new CollisionKey(null, null);
            }
            return new CollisionKey(collision.getA(), collision.getB());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CollisionKey)) {
                return false;
            }
            CollisionKey other = (CollisionKey) o;
            return a == other.a && b == other.b;
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
