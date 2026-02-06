package sg.edu.sit.inf1009.p2team2.engine.collision;

/**
 * Listener for collision events emitted by {@link CollisionManager}.
 */
public interface CollisionListener {
    void onCollisionEnter(Collision collision);

    void onCollisionStay(Collision collision);

    void onCollisionExit(Collision collision);
}
