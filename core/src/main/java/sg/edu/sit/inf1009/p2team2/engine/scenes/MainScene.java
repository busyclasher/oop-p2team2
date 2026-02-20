package sg.edu.sit.inf1009.p2team2.engine.scenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import sg.edu.sit.inf1009.p2team2.engine.collision.Circle;
import sg.edu.sit.inf1009.p2team2.engine.collision.CollisionManager;
import sg.edu.sit.inf1009.p2team2.engine.config.ConfigKey;
import sg.edu.sit.inf1009.p2team2.engine.collision.Shape;
import sg.edu.sit.inf1009.p2team2.engine.config.ConfigManager;
import sg.edu.sit.inf1009.p2team2.engine.config.ConfigKeys;
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
import sg.edu.sit.inf1009.p2team2.engine.output.Audio;
import sg.edu.sit.inf1009.p2team2.engine.output.Renderer;

/**
 * Main simulation scene that demonstrates all engine managers in one runtime:
 * scene flow, entities, movement, collision, input/output and config usage.
 */
public class MainScene extends Scene {
    private enum DemoMode {
        INTERACTIVE,
        SHAPES,
        COLORS,
        TEXT,
        STRESS
    }

    private static final int[] ENTITY_PRESETS = {20, 100, 400};
    private static final float BORDER_PADDING = 8f;
    private static final float PLAYER_SIZE = 30f;
    private static final float NPC_SIZE = 22f;
    private static final float DEFAULT_PLAYER_SPEED = 240f;
    private static final String MUSIC_TRACK_ID = "simulation-theme";
    private static final String SPAWN_SOUND_ID = "spawn-marker";
    private static final float DEFAULT_FRICTION = 0.10f;

    private static final Color[] BACKGROUND_COLORS = {
        new Color(0.07f, 0.08f, 0.11f, 1f),
        new Color(0.08f, 0.11f, 0.08f, 1f),
        new Color(0.10f, 0.08f, 0.12f, 1f),
        new Color(0.10f, 0.10f, 0.10f, 1f),
        new Color(0.06f, 0.10f, 0.12f, 1f)
    };

    private static final Color[] TEST_COLORS = {
        Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN,
        Color.MAGENTA, Color.ORANGE, Color.PINK, Color.WHITE, Color.LIGHT_GRAY
    };

    private EntityManager entityManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;
    private Random random;

    private int playerEntityId;
    private int presetIndex;
    private int targetEntityCount;
    private int backgroundIndex;

    private static final String BACKGROUND_SPRITE = "mainscene.png";

    private boolean paused;
    private boolean collisionsEnabled;
    private boolean musicPlaying;
    private float playerSpeed;
    private float animationTime;
    private DemoMode currentMode;

    private float worldWidth;
    private float worldHeight;

    public MainScene(EngineContext context) {
        super(context);
        this.playerEntityId = -1;
        this.presetIndex = 0;
        this.targetEntityCount = ENTITY_PRESETS[0];
        this.backgroundIndex = 0;
        this.paused = false;
        this.collisionsEnabled = true;
        this.musicPlaying = false;
        this.playerSpeed = DEFAULT_PLAYER_SPEED;
        this.animationTime = 0f;
        this.currentMode = DemoMode.INTERACTIVE;
        this.worldWidth = 800f;
        this.worldHeight = 600f;
    }

    @Override
    public void onEnter() {
        paused = false;
    }

    @Override
    public void onExit() {
        stopSimulationMusic();
        persistSimulationConfig();
    }

    @Override
    public void load() {
        this.entityManager = new EntityManager();
        this.movementManager = new MovementManager(entityManager);
        this.collisionManager = new CollisionManager(entityManager);
        this.random = new Random(1009L);
        this.animationTime = 0f;
        this.currentMode = DemoMode.INTERACTIVE;

        refreshWorldBounds();
        prepareAudioResources();
        loadSimulationConfig();
        setupDemoEntities();
    }

    @Override
    public void unload() {
        if (entityManager != null) {
            entityManager.clear();
        }
        stopSimulationMusic();
        playerEntityId = -1;
    }

    @Override
    public void update(float dt) {
        if (entityManager == null || movementManager == null || collisionManager == null) {
            return;
        }

        refreshWorldBounds();
        animationTime += Math.max(0f, dt);
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
        renderer.setClearColor(BACKGROUND_COLORS[backgroundIndex]);
        renderer.clear();
        renderer.begin();

        // Draw background centered and scaled to the window
        renderer.drawBackground(BACKGROUND_SPRITE);

        renderEntities(renderer);
        renderModeOverlay(renderer);
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

        handleModeSwitchInput(keyboard);

        if (keyboard.isKeyPressed(Input.Keys.TAB)) {
            paused = !paused;
        }
        if (keyboard.isKeyPressed(Input.Keys.C)) {
            collisionsEnabled = !collisionsEnabled;
        }
        if (keyboard.isKeyPressed(Input.Keys.P)) {
            cycleEntityPreset();
        }
        if (keyboard.isKeyPressed(Input.Keys.F)) {
            toggleFullscreen();
        }
        if (keyboard.isKeyPressed(Input.Keys.M)) {
            toggleMusic();
        }
        if (keyboard.isKeyPressed(Input.Keys.PLUS) || keyboard.isKeyPressed(Input.Keys.EQUALS)) {
            adjustMasterVolume(0.05f);
        }
        if (keyboard.isKeyPressed(Input.Keys.MINUS)) {
            adjustMasterVolume(-0.05f);
        }
        if (keyboard.isKeyPressed(Input.Keys.SPACE)) {
            backgroundIndex = (backgroundIndex + 1) % BACKGROUND_COLORS.length;
            addNpcEntities(10);
        }
        if (keyboard.isKeyPressed(Input.Keys.BACKSPACE)) {
            removeNpcEntities(10);
        }
        if (keyboard.isKeyPressed(Input.Keys.ENTER)) {
            setupDemoEntities();
        }
        if (keyboard.isKeyPressed(Input.Keys.LEFT_BRACKET)) {
            movementManager.setFriction(movementManager.getFriction() - 0.05f);
        }
        if (keyboard.isKeyPressed(Input.Keys.RIGHT_BRACKET)) {
            movementManager.setFriction(movementManager.getFriction() + 0.05f);
        }

        if (mouse.isButtonPressed(0)) {
            spawnNpcAt(mouse.getPosition().x - NPC_SIZE * 0.5f, mouse.getPosition().y - NPC_SIZE * 0.5f);
            playSpawnSound();
        }
        if (mouse.isButtonPressed(1)) {
            removeNpcEntities(1);
        }
        float scroll = mouse.getScrollDelta();
        if (scroll != 0f) {
            playerSpeed = Math.max(60f, Math.min(500f, playerSpeed + scroll * 5f));
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

    private void setupDemoEntities() {
        buildSimulationWorld();
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
        collider.setShape(new sg.edu.sit.inf1009.p2team2.engine.collision.Rectangle(x, y, size, size));
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
            spawnEntity(randomX(NPC_SIZE), randomY(NPC_SIZE));
        }
    }

    private void spawnNpcAt(float x, float y) {
        spawnEntity(clampToWorldX(x, NPC_SIZE), clampToWorldY(y, NPC_SIZE));
    }

    private Entity spawnEntity(float x, float y) {
        return createEntity(x, y, NPC_SIZE, false);
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

    private void handleModeSwitchInput(Keyboard keyboard) {
        if (keyboard.isKeyPressed(Input.Keys.NUM_1)) {
            currentMode = DemoMode.INTERACTIVE;
        } else if (keyboard.isKeyPressed(Input.Keys.NUM_2)) {
            currentMode = DemoMode.SHAPES;
        } else if (keyboard.isKeyPressed(Input.Keys.NUM_3)) {
            currentMode = DemoMode.COLORS;
        } else if (keyboard.isKeyPressed(Input.Keys.NUM_4)) {
            currentMode = DemoMode.TEXT;
        } else if (keyboard.isKeyPressed(Input.Keys.NUM_5)) {
            currentMode = DemoMode.STRESS;
        }
    }

    private void toggleFullscreen() {
        if (context == null || context.getOutputManager() == null || context.getOutputManager().getDisplay() == null) {
            return;
        }
        context.getOutputManager().getDisplay().toggleFullscreen();
    }

    private void prepareAudioResources() {
        Audio audio = getAudio();
        if (audio == null) {
            return;
        }
        audio.loadMusic("audio/simulation_theme.ogg", MUSIC_TRACK_ID);
        audio.loadSound("audio/spawn_click.wav", SPAWN_SOUND_ID);
    }

    private void toggleMusic() {
        if (musicPlaying) {
            stopSimulationMusic();
        } else {
            startSimulationMusic();
        }
    }

    private void stopSimulationMusic() {
        Audio audio = getAudio();
        if (audio == null) {
            return;
        }
        audio.stopMusic();
        musicPlaying = false;
    }

    private void startSimulationMusic() {
        Audio audio = getAudio();
        if (audio == null) {
            return;
        }
        audio.playMusic(MUSIC_TRACK_ID, true);
        musicPlaying = true;
    }

    private void playSpawnSound() {
        Audio audio = getAudio();
        if (audio == null) {
            return;
        }
        audio.playSound(SPAWN_SOUND_ID, 1.0f, false);
    }

    private void adjustMasterVolume(float delta) {
        Audio audio = getAudio();
        if (audio == null) {
            return;
        }
        float next = Math.max(0f, Math.min(1f, audio.getMasterVolume() + delta));
        audio.setMasterVolume(next);
    }

    private Audio getAudio() {
        if (context == null || context.getOutputManager() == null) {
            return null;
        }
        return context.getOutputManager().getAudio();
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
            setColliderPosition(collider, transform.getPosition().x, transform.getPosition().y);
        }
    }

    private void keepEntitiesInsideWorld() {
        for (Entity entity : entityManager.getAllEntities()) {
            TransformComponent transform = entity.get(TransformComponent.class);
            VelocityComponent velocity = entity.get(VelocityComponent.class);
            ColliderComponent collider = entity.get(ColliderComponent.class);
            Rectangle bounds = getColliderBounds(collider);
            if (transform == null || velocity == null || collider == null
                || transform.getPosition() == null || velocity.getVelocity() == null || bounds == null) {
                continue;
            }

            float width = bounds.width;
            float height = bounds.height;
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
            setColliderPosition(collider, x, y);
        }
    }

    private void renderEntities(Renderer renderer) {
        if (entityManager == null) {
            return;
        }

        for (Entity entity : entityManager.getAllEntities()) {
            ColliderComponent collider = entity.get(ColliderComponent.class);
            RenderableComponent renderable = entity.get(RenderableComponent.class);
            Rectangle bounds = getColliderBounds(collider);
            if (collider == null || bounds == null || renderable == null || !renderable.isVisible()) {
                continue;
            }

            Color fill = renderable.getColor() == null ? Color.WHITE : renderable.getColor();
            renderer.drawRect(bounds, fill, true);
            renderer.drawRect(bounds, entity.getId() == playerEntityId ? Color.WHITE : Color.DARK_GRAY, false);
        }
    }

    private void renderModeOverlay(Renderer renderer) {
        Mouse mouse = context.getInputManager() == null ? null : context.getInputManager().getMouse();
        Vector2 mousePos = mouse == null ? new Vector2(worldWidth * 0.5f, worldHeight * 0.5f) : mouse.getPosition();
        Vector2 playerCenter = getPlayerCenter();

        renderer.drawCircle(mousePos, 20f, Color.RED, false);
        renderer.drawCircle(mousePos, 3f, Color.YELLOW, true);
        renderer.drawLine(playerCenter, mousePos, Color.ORANGE, 2f);

        switch (currentMode) {
            case SHAPES:
                renderShapesMode(renderer, mousePos);
                break;
            case COLORS:
                renderColorsMode(renderer);
                break;
            case TEXT:
                renderTextMode(renderer);
                break;
            case STRESS:
                renderStressMode(renderer);
                break;
            case INTERACTIVE:
            default:
                break;
        }
    }

    private void renderShapesMode(Renderer renderer, Vector2 mousePos) {
        renderer.drawRect(new Rectangle(40f, 120f, 120f, 80f), Color.GREEN, true);
        renderer.drawRect(new Rectangle(180f, 120f, 120f, 80f), Color.WHITE, false);
        renderer.drawCircle(new Vector2(380f, 160f), 40f, Color.CYAN, false);
        renderer.drawCircle(new Vector2(500f, 160f), 28f, Color.PINK, true);
        renderer.drawLine(new Vector2(560f, 120f), new Vector2(mousePos.x, mousePos.y), Color.LIME, 2f);
    }

    private void renderColorsMode(Renderer renderer) {
        float x = 40f;
        float y = 120f;
        for (int i = 0; i < TEST_COLORS.length; i++) {
            renderer.drawRect(new Rectangle(x, y, 40f, 28f), TEST_COLORS[i], true);
            renderer.drawRect(new Rectangle(x, y, 40f, 28f), Color.DARK_GRAY, false);
            x += 48f;
            if ((i + 1) % 5 == 0) {
                x = 40f;
                y += 36f;
            }
        }
    }

    private void renderTextMode(Renderer renderer) {
        renderer.drawText("TEXT MODE: renderer.drawText showcase", new Vector2(40f, 220f), "default", Color.WHITE);
        renderer.drawText("Scene: MainScene", new Vector2(40f, 196f), "default", Color.YELLOW);
        renderer.drawText("Managers wired through EngineContext", new Vector2(40f, 172f), "default", Color.CYAN);
        renderer.drawText("Mode switching: 1-5", new Vector2(40f, 148f), "default", Color.LIGHT_GRAY);
    }

    private void renderStressMode(Renderer renderer) {
        for (int i = 0; i < 120; i++) {
            float angle = animationTime * 0.7f + i * 0.15f;
            float cx = worldWidth * 0.5f + (float) Math.cos(angle) * (80f + (i % 5) * 16f);
            float cy = worldHeight * 0.5f + (float) Math.sin(angle) * (80f + (i % 7) * 14f);
            Color color = TEST_COLORS[i % TEST_COLORS.length];
            renderer.drawCircle(new Vector2(cx, cy), 3f + (i % 4), color, true);
        }
    }

    private void renderHud(Renderer renderer) {
        float top = worldHeight - 20f;
        Mouse mouse = context.getInputManager() == null ? null : context.getInputManager().getMouse();
        Vector2 mousePos = mouse == null ? new Vector2() : mouse.getPosition();
        Vector2 playerCenter = getPlayerCenter();
        Audio audio = getAudio();
        float masterVolume = audio == null ? 0f : audio.getMasterVolume();
        int activeCollisions = collisionManager == null ? 0 : collisionManager.getActiveCollisionCount();

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
                + " | ActiveCollisions: " + activeCollisions
                + " | State: " + (paused ? "PAUSED" : "RUNNING"),
            new Vector2(16f, top - 48f),
            "default",
            Color.CYAN
        );
        renderer.drawText(
            "GravityY: " + movementManager.getGravity().y + " | Friction: " + movementManager.getFriction()
                + " | PlayerSpeed: " + playerSpeed
                + " | Music: " + (musicPlaying ? "ON" : "OFF")
                + " | Volume: " + Math.round(masterVolume * 100f) + "%",
            new Vector2(16f, top - 72f),
            "default",
            Color.CYAN
        );
        renderer.drawText(
            "Mouse(" + Math.round(mousePos.x) + "," + Math.round(mousePos.y) + ")"
                + " | Player(" + Math.round(playerCenter.x) + "," + Math.round(playerCenter.y) + ")"
                + " | Mode: " + currentMode,
            new Vector2(16f, top - 96f),
            "default",
            Color.YELLOW
        );
        renderer.drawText(
            "Controls: WASD/Arrows move player | LeftClick +1 | RightClick -1 | Scroll speed",
            new Vector2(16f, 48f),
            "default",
            Color.LIGHT_GRAY
        );
        renderer.drawText(
            "1-5 mode | SPACE +10 + BG | BACKSPACE -10 | P preset | C collisions | F fullscreen",
            new Vector2(16f, 24f),
            "default",
            Color.LIGHT_GRAY
        );
        renderer.drawText(
            "M music | +/- volume | [ ] friction | TAB pause | ENTER reset | ESC menu",
            new Vector2(16f, 8f),
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
        ensureDefaultConfig(config, SimulationConfigKeys.SIMULATION_PRESET_INDEX);
        ensureDefaultConfig(config, SimulationConfigKeys.SIMULATION_GRAVITY_Y);
        ensureDefaultConfig(config, SimulationConfigKeys.SIMULATION_FRICTION);
        ensureDefaultConfig(config, SimulationConfigKeys.SIMULATION_PLAYER_SPEED);
        ensureDefaultConfig(config, SimulationConfigKeys.SIMULATION_COLLISIONS_ENABLED);
        ensureDefaultConfig(config, SimulationConfigKeys.SIMULATION_MUSIC_ENABLED);
        ensureDefaultConfig(config, ConfigKeys.AUDIO_VOLUME);

        presetIndex = clampPresetIndex(config.get(SimulationConfigKeys.SIMULATION_PRESET_INDEX));
        targetEntityCount = ENTITY_PRESETS[presetIndex];
        collisionsEnabled = config.get(SimulationConfigKeys.SIMULATION_COLLISIONS_ENABLED);
        musicPlaying = config.get(SimulationConfigKeys.SIMULATION_MUSIC_ENABLED);
        playerSpeed = Math.max(60f, config.get(SimulationConfigKeys.SIMULATION_PLAYER_SPEED));

        float gravityValue = config.get(SimulationConfigKeys.SIMULATION_GRAVITY_Y);
        float frictionValue = config.get(SimulationConfigKeys.SIMULATION_FRICTION);
        movementManager.setGravity(new Vector2(0f, gravityValue));
        movementManager.setFriction(frictionValue);

        Audio audio = getAudio();
        if (audio != null) {
            audio.setMasterVolume(config.get(ConfigKeys.AUDIO_VOLUME));
        }
        if (musicPlaying) {
            startSimulationMusic();
        }
    }

    private void persistSimulationConfig() {
        if (context == null || context.getConfigManager() == null || movementManager == null) {
            return;
        }

        ConfigManager config = context.getConfigManager();
        config.set(SimulationConfigKeys.SIMULATION_PRESET_INDEX, Integer.valueOf(presetIndex));
        config.set(SimulationConfigKeys.SIMULATION_GRAVITY_Y, Float.valueOf(movementManager.getGravity().y));
        config.set(SimulationConfigKeys.SIMULATION_FRICTION, Float.valueOf(movementManager.getFriction()));
        config.set(SimulationConfigKeys.SIMULATION_PLAYER_SPEED, Float.valueOf(playerSpeed));
        config.set(SimulationConfigKeys.SIMULATION_COLLISIONS_ENABLED, Boolean.valueOf(collisionsEnabled));
        config.set(SimulationConfigKeys.SIMULATION_MUSIC_ENABLED, Boolean.valueOf(musicPlaying));
        Audio audio = getAudio();
        if (audio != null) {
            config.set(ConfigKeys.AUDIO_VOLUME, Float.valueOf(audio.getMasterVolume()));
        }
        config.save(null);
    }

    private <T> void ensureDefaultConfig(ConfigManager config, ConfigKey<T> key) {
        if (config.has(key)) {
            return;
        }
        config.set(key, key.defaultValue());
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

    private Vector2 getPlayerCenter() {
        Entity player = entityManager == null ? null : entityManager.getEntity(playerEntityId);
        if (player == null) {
            return new Vector2(worldWidth * 0.5f, worldHeight * 0.5f);
        }
        TransformComponent transform = player.get(TransformComponent.class);
        ColliderComponent collider = player.get(ColliderComponent.class);
        Rectangle bounds = getColliderBounds(collider);
        if (transform == null || transform.getPosition() == null) {
            return new Vector2(worldWidth * 0.5f, worldHeight * 0.5f);
        }
        if (collider == null || bounds == null) {
            return transform.getPosition().cpy();
        }
        return new Vector2(
            transform.getPosition().x + bounds.width * 0.5f,
            transform.getPosition().y + bounds.height * 0.5f
        );
    }

    private Rectangle getColliderBounds(ColliderComponent collider) {
        if (collider == null) {
            return null;
        }

        Shape shape = collider.getShape();
        if (shape instanceof sg.edu.sit.inf1009.p2team2.engine.collision.Rectangle rect) {
            Vector2 position = rect.getPosition();
            return new Rectangle(position.x, position.y, rect.getWidth(), rect.getHeight());
        }
        if (shape instanceof Circle circle) {
            Vector2 center = circle.getCenter();
            float radius = circle.getRadius();
            return new Rectangle(center.x - radius, center.y - radius, radius * 2f, radius * 2f);
        }
        return null;
    }

    private void setColliderPosition(ColliderComponent collider, float x, float y) {
        if (collider == null) {
            return;
        }

        Shape shape = collider.getShape();
        if (shape instanceof sg.edu.sit.inf1009.p2team2.engine.collision.Rectangle rect) {
            rect.setPosition(new Vector2(x, y));
            return;
        }
        if (shape instanceof Circle circle) {
            float radius = circle.getRadius();
            circle.setCenter(new Vector2(x + radius, y + radius));
        }
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
