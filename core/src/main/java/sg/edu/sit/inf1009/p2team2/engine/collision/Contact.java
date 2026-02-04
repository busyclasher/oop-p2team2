package sg.edu.sit.inf1009.p2team2.engine.collision;

import com.badlogic.gdx.math.Vector2;

/**
 * Contact information for a collision (normal, penetration, etc.).
 */
public class Contact {
    private final Vector2 normal = new Vector2();
    private float penetration;

    public Contact() {
    }

    public Vector2 getNormal() {
        return normal;
    }

    public float getPenetration() {
        return penetration;
    }

    public void setPenetration(float penetration) {
        this.penetration = penetration;
    }
}

