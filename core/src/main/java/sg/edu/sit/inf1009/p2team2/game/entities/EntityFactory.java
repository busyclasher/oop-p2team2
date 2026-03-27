package sg.edu.sit.inf1009.p2team2.game.entities;

import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.entity.Entity;
import sg.edu.sit.inf1009.p2team2.engine.entity.EntityManager;
import sg.edu.sit.inf1009.p2team2.engine.entity.components.ColliderComponent;
import sg.edu.sit.inf1009.p2team2.engine.entity.components.InputComponent;
import sg.edu.sit.inf1009.p2team2.engine.entity.components.RenderableComponent;
import sg.edu.sit.inf1009.p2team2.engine.entity.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.entity.components.VelocityComponent;
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

        VelocityComponent velocity = new VelocityComponent();
        ColliderComponent collider = new ColliderComponent();
        collider.setBounds(new com.badlogic.gdx.math.Rectangle(
            startX - PLAYER_WIDTH / 2f, startY, PLAYER_WIDTH, PLAYER_HEIGHT));
        InputComponent input = new InputComponent();
        input.setActionMapId("player");

        player.add(transform);
        player.add(velocity);
        player.add(collider);
        player.add(input);
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

        VelocityComponent velocity = new VelocityComponent();
        velocity.getVelocity().set(0f, -speed);

        ColliderComponent collider = new ColliderComponent();
        collider.setBounds(new com.badlogic.gdx.math.Rectangle(
            x - size / 2f, y - size / 2f, size, size));

        RenderableComponent renderable = new RenderableComponent();
        renderable.setSpriteId(spriteFor(type));
        renderable.setColor(type.getColor().toGdxColor());

        entity.add(transform);
        entity.add(velocity);
        entity.add(collider);
        entity.add(renderable);
        entity.add(new FallingComponent(speed));
        entity.add(new GameEntityComponent(type));

        return entity;
    }

    private String spriteFor(EntityType type) {
        return switch (type) {
            case GOOD_BYTE -> "good_byte.png";
            case SAFE_EMAIL -> "safe_email.png";
            case GOLD_ENVELOPE -> "gold_envelope.png";
            case PHISHING_HOOK -> "phishing_hook.png";
            case RANSOMWARE_LOCK -> "ransomware_lock.png";
            case MALWARE_SWARM -> "malware_swarm.png";
            case ROOTKIT -> "rootkit.png";
            case SPYWARE -> "spyware.png";
            case FRENZY_ORB -> "frenzy_orb.png";
            case PLAYER -> "";
        };
    }
}
