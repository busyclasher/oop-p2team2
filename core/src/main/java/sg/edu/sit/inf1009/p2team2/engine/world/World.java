package sg.edu.sit.inf1009.p2team2.engine.world;

import sg.edu.sit.inf1009.p2team2.engine.managers.EntityManager;

/**
 * Represents the simulated world state (entities + shared environment data).
 *
 * Keep this type generic so the abstract engine can be reused for many simulations.
 */
public class World {
    private final EntityManager entityManager;

    public World(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void update(float dt) {
        // TODO(Team): advance world simulation (systems/managers orchestration).
    }
}

