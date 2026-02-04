package sg.edu.sit.inf1009.p2team2.engine.systems;

import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.VelocityComponent;

public class MovementSystem {
    private final Vector2 tmp = new Vector2();

    public void update(float dt) {
        // TODO(Hasif): iterate over entities with Transform + Velocity and integrate.
    }

    public void integrate(TransformComponent t, VelocityComponent v, float dt) {
        // TODO(Hasif): perform Euler/Verlet integration for position and velocity.
        // tmp can be used to avoid allocations.
    }
}

