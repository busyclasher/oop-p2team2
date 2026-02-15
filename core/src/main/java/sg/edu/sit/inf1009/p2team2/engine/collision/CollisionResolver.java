package sg.edu.sit.inf1009.p2team2.engine.collision;

import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.VelocityComponent;

/**
 * Responsible for collision resolution (separation, impulses, velocity updates).
 */
public class CollisionResolver {
    private float epsilon = 0.0001f;

    public CollisionResolver() {
    }

    public void resolve(Collision c) {
        // TODO(Cody): implement resolution rules (trigger vs solid, impulses, events).
    }

    private void applyImpulse(Collider a, Collider b, Vector2 contactNormal) {
        // TODO(Cody): apply impulse to velocity components based on collision normal.
    }

    private void separatePositions(TransformComponent a, TransformComponent b, Contact contact) {
        // TODO(Cody): separate overlapping transforms using penetration depth.
    }

    private void stopVelocity(VelocityComponent v, Vector2 normal) {
        // TODO(Cody): remove velocity component along the collision normal.
    }

    public float getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(float epsilon) {
        this.epsilon = epsilon;
    }
}

