package sg.edu.sit.inf1009.p2team2.engine.managers;

import sg.edu.sit.inf1009.p2team2.engine.systems.MovementSystem;

/**
 * Coordinates movement updates for entities that have movement-related components.
 */
public class MovementManager {
    private final MovementSystem movementSystem;

    public MovementManager() {
        this.movementSystem = new MovementSystem();
    }

    public MovementManager(EntityManager entityManager) {
        this.movementSystem = new MovementSystem(entityManager);
    }

    public MovementManager(MovementSystem movementSystem) {
        this.movementSystem = movementSystem == null ? new MovementSystem() : movementSystem;
    }

    public void update(float dt) {
        movementSystem.update(dt);
    }
}
