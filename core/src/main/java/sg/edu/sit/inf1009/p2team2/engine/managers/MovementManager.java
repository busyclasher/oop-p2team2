package sg.edu.sit.inf1009.p2team2.engine.managers;

import sg.edu.sit.inf1009.p2team2.engine.systems.MovementSystem;

/**
 * Coordinates movement updates for non-player entities.
 */
public class MovementManager {
    private final MovementSystem movementSystem;

    public MovementManager() {
        this.movementSystem = new MovementSystem();
    }

    public MovementManager(MovementSystem movementSystem) {
        this.movementSystem = movementSystem;
    }

    public void update(float dt) {
        // TODO(Hasif): tie MovementSystem to the entity list / queries.
        movementSystem.update(dt);
    }
}

