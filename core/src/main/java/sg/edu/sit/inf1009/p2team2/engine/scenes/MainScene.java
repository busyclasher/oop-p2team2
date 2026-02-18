package sg.edu.sit.inf1009.p2team2.engine.scenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import sg.edu.sit.inf1009.p2team2.engine.collision.CollisionManager;
import sg.edu.sit.inf1009.p2team2.engine.config.ConfigManager;
import sg.edu.sit.inf1009.p2team2.engine.config.ConfigVar;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.ColliderComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.InputComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.RenderableComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.VelocityComponent;
import sg.edu.sit.inf1009.p2team2.engine.input.Keyboard;
import sg.edu.sit.inf1009.p2team2.engine.input.Mouse;
import sg.edu.sit.inf1009.p2team2.engine.managers.EntityManager;
import sg.edu.sit.inf1009.p2team2.engine.managers.MovementManager;
import sg.edu.sit.inf1009.p2team2.engine.output.Renderer;

/**
 * Main simulation scene that demonstrates all engine managers in one runtime:
 * scene flow, entities, movement, collision, input/output and config usage.
 */
public class MainScene extends Scene {
    private static final int[] ENTITY_PRESETS = {20, 100, 400};
    private static final float BORDER_PADDING = 8f;
    private static final float PLAYER_SIZE = 30f;
    private static final float NPC_SIZE = 22f;
    private static final float DEFAULT_PLAYER_SPEED = 240f;

    private EntityManager entityManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;
    private Random random;

    private int playerEntityId;
    private int presetIndex;
    private int targetEntityCount;

    private boolean paused;
    private boolean collisionsEnabled;
    private float playerSpeed;

    private float worldWidth;
    private float worldHeight;

    public MainScene(EngineContext context) {
        super(context);
        this.playerEntityId = -1;
        this.presetIndex = 0;
        this.targetEntityCount = ENTITY_PRESETS[0];
        this.paused = false;
        this.collisionsEnabled = true;
        this.playerSpeed = DEFAULT_PLAYER_SPEED;
        this.worldWidth = 800f;
        this.worldHeight = 600f;
    }

    @Override
    public void onEnter() {
        paused = false;
    }

    @Override
    public void onExit() {
        persistSimulationConfig();
    }

    @Override
    public void load() {
        this.entityManager = new EntityManager();
        this.movementManager = new MovementManager(entityManager);
        this.collisionManager = new CollisionManager(entityManager);
        this.random = new Random(1009L);

        refreshWorldBounds();
        loadSimulationConfig();
        buildSimulationWorld();
    }

    @Override
    public void unload() {
        if (entityManager != null) {
            entityManager.clear();
        }
        playerEntityId = -1;
    }

    @Override
    public void update(float dt) {
        if (entityManager == null || movementManager == null || collisionManager == null) {
            return;
        }

        refreshWorldBounds();
        if (paused) {
            return;
        }

        movementManager.update(dt);
        syncAllColliderBounds();
        keepEntitiesInsideWorld();

        if (collisionsEnabled) {
            collisionManager.update(dt);
            syncAllColliderBounds();
            keepEntitiesInsideWorld();
        }
    }

    @Override
    public void render() {
        if (context == null || context.getOutputManager() == null) {
            return;
        }

        Renderer renderer = context.getOutputManager().getRenderer();
        renderer.clear();
        renderer.begin();

        renderEntities(renderer);
        renderHud(renderer);

        renderer.end();
    }

    @Override
    public void handleInput() {
        if (context == null || context.getInputManager() == null) {
            return;
        }

        Keyboard keyboard = context.getInputManager().getKeyboard();
        Mouse mouse = context.getInputManager().getMouse();
        if (keyboard == null || mouse == null) {
            return;
        }

        if (keyboard.isKeyPressed(Input.Keys.ESCAPE)) {
            context.getSceneManager().pop();
            return;
        }

        if (keyboard.isKeyPressed(Input.Keys.TAB)) {
            paused = !paused;
        }
        if (keyboard.isKeyPressed(Input.Keys.F)) {
            collisionsEnabled = !collisionsEnabled;
        }
        if (keyboard.isKeyPressed(Input.Keys.M)) {
            cycleEntityPreset();
        }
        if (keyboard.isKeyPressed(Input.Keys.SPACE)) {
            addNpcEntities(10);
        }
        if (keyboard.isKeyPressed(Input.Keys.BACKSPACE)) {
            removeNpcEntities(10);
        }
        if (keyboard.isKeyPressed(Input.Keys.ENTER)) {
            buildSimulationWorld();
        }
        if (keyboard.isKeyPressed(Input.Keys.PLUS) || keyboard.isKeyPressed(Input.Keys.EQUALS)) {
            movementManager.setFriction(movementManager.getFriction() + 0.05f);
        }
        if (keyboard.isKeyPressed(Input.Keys.MINUS)) {
            movementManager.setFriction(movementManager.getFriction() - 0.05f);
        }

        if (mouse.isButtonPressed(0)) {
            spawnNpcAt(mouse.getPosition().x - NPC_SIZE * 0.5f, mouse.getPosition().y - NPC_SIZE * 0.5f);
        }

        if (!paused) {
            updatePlayerInput(keyboard);
        }
    }

    private void buildSimulationWorld() {
        if (entityManager == null) {
            return;
        }

        entityManager.clear();
        float startX = Math.max(BORDER_PADDING, worldWidth * 0.5f - PLAYER_SIZE * 0.5f);
        float startY = Math.max(BORDER_PADDING, worldHeight * 0.5f - PLAYER_SIZE * 0.5f);
        Entity player = createEntity(startX, startY, PLAYER_SIZE, true);
        playerEntityId = player.getId();

        addNpcEntities(Math.max(0, targetEntityCount - 1));
    }

    private Entity createEntity(float x, float y, float size, boolean isPlayer) {
        Entity entity = entityManager.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.setPosition(new Vector2(x, y));
        transform.setRotation(0f);
        transform.setScale(new Vector2(1f, 1f));

        VelocityComponent velocity = new VelocityComponent();
        velocity.setAcceleration(new Vector2(0f, 0f));
        if (isPlayer) {
            velocity.setVelocity(new Vector2(0f, 0f));
        } else {
            velocity.setVelocity(randomVelocity());
        }

        ColliderComponent collider = new ColliderComponent();
        collider.setBounds(new Rectangle(x, y, size, size));
        collider.setLayer(isPlayer ? 1 : 0);
        collider.setMask(0xFFFF);
        collider.setTrigger(false);

        RenderableComponent renderable = new RenderableComponent();
        renderable.setColor(isPlayer ? Color.GOLD.cpy() : randomColor());

        entity.add(transform);
        entity.add(velocity);
        entity.add(collider);
        entity.add(renderable);

        if (isPlayer) {
            InputComponent input = new InputComponent();
            input.setActionMapId("default");
            input.enable();
            entity.add(input);
        }

        collisionManager.registerCollider(entity);
        return entity;
    }

    private void addNpcEntities(int count) {
        for (int i = 0; i < count; i++) {
            spawnNpcAt(randomX(NPC_SIZE), randomY(NPC_SIZE));
        }
    }

    private void spawnNpcAt(float x, float y) {
        createEntity(clampToWorldX(x, NPC_SIZE), clampToWorldY(y, NPC_SIZE), NPC_SIZE, false);
    }

    private void removeNpcEntities(int count) {
        if (count <= 0 || entityManager == null) {
            return;
        }

        List<Entity> snapshot = new ArrayList<>(entityManager.getAllEntities());
        int removed = 0;
        for (int i = snapshot.size() - 1; i >= 0 && removed < count; i--) {
            Entity entity = snapshot.get(i);
            if (entity.getId() == playerEntityId) {
                continue;
            }
            collisionManager.unregisterCollider(entity);
            entityManager.removeEntity(entity.getId());
            removed++;
        }
    }

    private void cycleEntityPreset() {
        presetIndex = (presetIndex + 1) % ENTITY_PRESETS.length;
        targetEntityCount = ENTITY_PRESETS[presetIndex];
        int delta = targetEntityCount - entityManager.size();
        if (delta > 0) {
            addNpcEntities(delta);
        } else if (delta < 0) {
            removeNpcEntities(-delta);
        }
    }

    private void updatePlayerInput(Keyboard keyboard) {
        Entity player = entityManager.getEntity(playerEntityId);
        if (player == null) {
            return;
        }

        VelocityComponent velocity = player.get(VelocityComponent.class);
        if (velocity == null || velocity.getVelocity() == null) {
            return;
        }

        Vector2 desiredVelocity = new Vector2();
        if (keyboard.isKeyDown(Input.Keys.W) || keyboard.isKeyDown(Input.Keys.UP)) {
            desiredVelocity.y += 1f;
        }
        if (keyboard.isKeyDown(Input.Keys.S) || keyboard.isKeyDown(Input.Keys.DOWN)) {
            desiredVelocity.y -= 1f;
        }
        if (keyboard.isKeyDown(Input.Keys.A) || keyboard.isKeyDown(Input.Keys.LEFT)) {
            desiredVelocity.x -= 1f;
        }
        if (keyboard.isKeyDown(Input.Keys.D) || keyboard.isKeyDown(Input.Keys.RIGHT)) {
            desiredVelocity.x += 1f;
        }

        if (desiredVelocity.isZero()) {
            velocity.getVelocity().scl(0.86f);
            if (velocity.getVelocity().len2() < 0.001f) {
                velocity.getVelocity().setZero();
            }
            return;
        }

        desiredVelocity.nor().scl(playerSpeed);
        velocity.setVelocity(desiredVelocity);
    }

    private void syncAllColliderBounds() {
        for (Entity entity : entityManager.getAllEntities()) {
            TransformComponent transform = entity.get(TransformComponent.class);
            ColliderComponent collider = entity.get(ColliderComponent.class);
            if (transform == null || collider == null || transform.getPosition() == null) {
                continue;
            }
            collider.updatePosition(transform.getPosition().x, transform.getPosition().y);
        }
    }

    private void keepEntitiesInsideWorld() {
        for (Entity entity : entityManager.getAllEntities()) {
            TransformComponent transform = entity.get(TransformComponent.class);
            VelocityComponent velocity = entity.get(VelocityComponent.class);
            ColliderComponent collider = entity.get(ColliderComponent.class);
            if (transform == null || velocity == null || collider == null
                || transform.getPosition() == null || velocity.getVelocity() == null || collider.getBounds() == null) {
                continue;
            }

            float width = collider.getBounds().width;
            float height = collider.getBounds().height;
            float x = transform.getPosition().x;
            float y = transform.getPosition().y;

            if (x < BORDER_PADDING) {
                x = BORDER_PADDING;
                velocity.getVelocity().x = Math.abs(velocity.getVelocity().x);
            } else if (x + width > worldWidth - BORDER_PADDING) {
                x = worldWidth - BORDER_PADDING - width;
                velocity.getVelocity().x = -Math.abs(velocity.getVelocity().x);
            }

            if (y < BORDER_PADDING) {
                y = BORDER_PADDING;
                velocity.getVelocity().y = Math.abs(velocity.getVelocity().y);
            } else if (y + height > worldHeight - BORDER_PADDING) {
                y = worldHeight - BORDER_PADDING - height;
                velocity.getVelocity().y = -Math.abs(velocity.getVelocity().y);
            }

            transform.getPosition().set(x, y);
            collider.updatePosition(x, y);
        }
    }

    private void renderEntities(Renderer renderer) {
        if (entityManager == null) {
            return;
        }

        for (Entity entity : entityManager.getAllEntities()) {
            ColliderComponent collider = entity.get(ColliderComponent.class);
            RenderableComponent renderable = entity.get(RenderableComponent.class);
            if (collider == null || collider.getBounds() == null || renderable == null || !renderable.isVisible()) {
                continue;
            }

            Rectangle bounds = collider.getBounds();
            Color fill = renderable.getColor() == null ? Color.WHITE : renderable.getColor();
            renderer.drawRect(bounds, fill, true);
            renderer.drawRect(bounds, entity.getId() == playerEntityId ? Color.WHITE : Color.DARK_GRAY, false);
        }
    }

    private void renderHud(Renderer renderer) {
        float top = worldHeight - 20f;
        renderer.drawText("ABSTRACT ENGINE SIMULATION", new Vector2(16f, top), "default", Color.WHITE);
        renderer.drawText(
            "Managers: Scene + Entity + Movement + Collision + Input/Output + Config",
            new Vector2(16f, top - 24f),
            "default",
            Color.LIGHT_GRAY
        );
        renderer.drawText(
            "Entities: " + entityManager.size() + " (preset " + ENTITY_PRESETS[presetIndex] + ")"
                + " | Collision: " + (collisionsEnabled ? "ON" : "OFF")
                + " | State: " + (paused ? "PAUSED" : "RUNNING"),
            new Vector2(16f, top - 48f),
            "default",
            Color.CYAN
        );
        renderer.drawText(
            "GravityY: " + movementManager.getGravity().y + " | Friction: " + movementManager.getFriction()
                + " | PlayerSpeed: " + playerSpeed,
            new Vector2(16f, top - 72f),
            "default",
            Color.CYAN
        );
        renderer.drawText(
            "Controls: WASD/Arrows move player | SPACE +10 | BACKSPACE -10 | M preset 20/100/400",
            new Vector2(16f, 48f),
            "default",
            Color.LIGHT_GRAY
        );
        renderer.drawText(
            "TAB pause | F collision toggle | +/- friction | ENTER reset | Left Click spawn | ESC menu",
            new Vector2(16f, 24f),
            "default",
            Color.LIGHT_GRAY
        );
    }

    private void refreshWorldBounds() {
        if (context == null || context.getOutputManager() == null || context.getOutputManager().getDisplay() == null) {
            return;
        }

        int width = context.getOutputManager().getDisplay().getWidth();
        int height = context.getOutputManager().getDisplay().getHeight();
        if (width > 100) {
            worldWidth = width;
        }
        if (height > 100) {
            worldHeight = height;
        }
    }

    private void loadSimulationConfig() {
        ConfigManager config = context.getConfigManager();
        ensureDefaultConfig(config, "simulation.presetIndex", Integer.valueOf(0));
        ensureDefaultConfig(config, "simulation.gravityY", Float.valueOf(0f));
        ensureDefaultConfig(config, "simulation.friction", Float.valueOf(0.10f));
        ensureDefaultConfig(config, "simulation.playerSpeed", Float.valueOf(DEFAULT_PLAYER_SPEED));
        ensureDefaultConfig(config, "simulation.collisionsEnabled", Boolean.TRUE);

        ConfigVar preset = config.get("simulation.presetIndex");
        ConfigVar gravityY = config.get("simulation.gravityY");
        ConfigVar friction = config.get("simulation.friction");
        ConfigVar speed = config.get("simulation.playerSpeed");
        ConfigVar collisionFlag = config.get("simulation.collisionsEnabled");
        ConfigVar volume = config.get("audio.volume");

        presetIndex = clampPresetIndex(preset == null ? 0 : preset.asInt());
        targetEntityCount = ENTITY_PRESETS[presetIndex];
        collisionsEnabled = collisionFlag == null || collisionFlag.asBool();
        playerSpeed = Math.max(60f, speed == null ? DEFAULT_PLAYER_SPEED : speed.asFloat());

        float gravityValue = gravityY == null ? 0f : gravityY.asFloat();
        float frictionValue = friction == null ? 0.10f : friction.asFloat();
        movementManager.setGravity(new Vector2(0f, gravityValue));
        movementManager.setFriction(frictionValue);

        if (volume != null && context.getOutputManager() != null && context.getOutputManager().getAudio() != null) {
            context.getOutputManager().getAudio().setMasterVolume(volume.asFloat());
        }
    }

    private void persistSimulationConfig() {
        if (context == null || context.getConfigManager() == null || movementManager == null) {
            return;
        }

        ConfigManager config = context.getConfigManager();
        config.set("simulation.presetIndex", new ConfigVar(Integer.valueOf(presetIndex), Integer.valueOf(0)));
        config.set("simulation.gravityY", new ConfigVar(Float.valueOf(movementManager.getGravity().y), Float.valueOf(0f)));
        config.set("simulation.friction", new ConfigVar(Float.valueOf(movementManager.getFriction()), Float.valueOf(0.10f)));
        config.set("simulation.playerSpeed", new ConfigVar(Float.valueOf(playerSpeed), Float.valueOf(DEFAULT_PLAYER_SPEED)));
        config.set("simulation.collisionsEnabled", new ConfigVar(Boolean.valueOf(collisionsEnabled), Boolean.TRUE));
        config.save(null);
    }

    private void ensureDefaultConfig(ConfigManager config, String key, Object defaultValue) {
        if (config.get(key) != null) {
            return;
        }
        config.set(key, new ConfigVar(defaultValue, defaultValue));
    }

    private int clampPresetIndex(int index) {
        if (index < 0) {
            return 0;
        }
        if (index >= ENTITY_PRESETS.length) {
            return ENTITY_PRESETS.length - 1;
        }
        return index;
    }

    private Vector2 randomVelocity() {
        float x = (random.nextFloat() * 2f - 1f) * 140f;
        float y = (random.nextFloat() * 2f - 1f) * 140f;
        return new Vector2(x, y);
    }

    private Color randomColor() {
        return new Color(
            0.25f + random.nextFloat() * 0.75f,
            0.25f + random.nextFloat() * 0.75f,
            0.25f + random.nextFloat() * 0.75f,
            1f
        );
    }

    private float randomX(float size) {
        float min = BORDER_PADDING;
        float max = Math.max(min, worldWidth - BORDER_PADDING - size);
        return min + random.nextFloat() * Math.max(0.001f, max - min);
    }

    private float randomY(float size) {
        float min = BORDER_PADDING;
        float max = Math.max(min, worldHeight - BORDER_PADDING - size);
        return min + random.nextFloat() * Math.max(0.001f, max - min);
    }

    private float clampToWorldX(float x, float size) {
        float min = BORDER_PADDING;
        float max = Math.max(min, worldWidth - BORDER_PADDING - size);
        return Math.max(min, Math.min(max, x));
    }

    private float clampToWorldY(float y, float size) {
        float min = BORDER_PADDING;
        float max = Math.max(min, worldHeight - BORDER_PADDING - size);
        return Math.max(min, Math.min(max, y));
    }
}
