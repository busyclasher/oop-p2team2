package sg.edu.sit.inf1009.p2team2.game.entities;

import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.entity.Entity;
import sg.edu.sit.inf1009.p2team2.engine.entity.EntityManager;
import sg.edu.sit.inf1009.p2team2.engine.entity.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.game.components.FallingComponent;
import sg.edu.sit.inf1009.p2team2.game.components.GameEntityComponent;
import sg.edu.sit.inf1009.p2team2.game.components.HealthComponent;

/**
 * Factory that assembles fully-configured {@link Entity} objects for the game.
 *
 * Design pattern: Factory Method - callers request an entity by type and
 * receive a ready-to-use object without knowing how components are assembled.
 */
public class EntityFactory {

    /** Default pixel size of falling items (square). */
    public static final float ENTITY_SIZE = 54f;
    /** Pixel width of the player rectangle. */
    public static final float PLAYER_WIDTH = 80f;
    /** Pixel height of the player rectangle. */
    public static final float PLAYER_HEIGHT = 100f;
    /** Starting number of player lives. */
    public static final int PLAYER_LIVES = 3;

    private final EntityManager entityManager;

    public EntityFactory(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Creates and registers the player entity.
     *
     * @param startX world-space X of the player's center
     * @param startY world-space Y of the player's bottom edge
     * @return the newly created player {@link Entity}
     */
    public Entity createPlayer(float startX, float startY) {
        return createPlayer(startX, startY, PLAYER_LIVES);
    }

    public Entity createPlayer(float startX, float startY, int lives) {
        Entity player = entityManager.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.setPosition(new Vector2(startX, startY));
        transform.setScale(new Vector2(PLAYER_WIDTH, PLAYER_HEIGHT));

        player.add(transform);
        player.add(new HealthComponent(lives));
        player.add(new GameEntityComponent(EntityType.PLAYER));

        return player;
    }

    /**
     * Creates and registers a falling entity of the given type.
     *
     * @param type entity type (must not be PLAYER)
     * @param x world-space X spawn position
     * @param y world-space Y spawn position
     * @param speed fall speed in pixels per second
     * @return the newly created falling {@link Entity}
     */
    public Entity createFallingEntity(EntityType type, float x, float y, float speed) {
        if (type == EntityType.PLAYER) {
            throw new IllegalArgumentException("Use createPlayer() for the player entity.");
        }

        Entity entity = entityManager.createEntity();

        float size = type.getDisplaySize() > 0f ? type.getDisplaySize() : ENTITY_SIZE;

        TransformComponent transform = new TransformComponent();
        transform.setPosition(new Vector2(x, y));
        transform.setScale(new Vector2(size, size));

        entity.add(transform);
        entity.add(new FallingComponent(speed));
        entity.add(new GameEntityComponent(type));

        return entity;
    }
}
