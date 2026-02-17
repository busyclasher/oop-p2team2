package sg.edu.sit.inf1009.p2team2.engine.scenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.ecs.Entity;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.RenderableComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.ecs.components.VelocityComponent;
import sg.edu.sit.inf1009.p2team2.engine.managers.EntityManager;
import sg.edu.sit.inf1009.p2team2.engine.managers.MovementManager;

/**
 * Main simulation scene skeleton.
 */
public class MainScene extends Scene {
    private final EntityManager entityManager;
    private final MovementManager movementManager;

    public MainScene(EngineContext context) {
        super(context);
        this.entityManager = new EntityManager();
        this.movementManager = new MovementManager(entityManager);
    }

    @Override
    public void onEnter() {
        setupDemoEntities();
    }

    @Override
    public void onExit() {
        entityManager.clear();
    }

    @Override
    public void load() {
        // Reserved for scene resources.
    }

    @Override
    public void unload() {
        entityManager.clear();
    }

    @Override
    public void update(float dt) {
        movementManager.update(dt);
    }

    @Override
    public void render() {
        var renderer = context.getOutputManager().getRenderer();
        renderer.clear();
        renderer.begin();

        for (Entity entity : entityManager.getAllEntities()) {
            TransformComponent transform = entity.get(TransformComponent.class);
            RenderableComponent renderable = entity.get(RenderableComponent.class);
            if (transform == null || renderable == null || !renderable.isVisible()) {
                continue;
            }

            renderer.drawCircle(transform.getPosition(), 16f, Color.CYAN, true);
        }

        renderer.drawText("MAIN SCENE", new Vector2(20f, 700f), "default", Color.WHITE);
        renderer.drawText("Press ESC to return to menu", new Vector2(20f, 670f), "default", Color.LIGHT_GRAY);
        renderer.end();
    }

    @Override
    public void handleInput() {
        if (context.getInputManager().getKeyboard().isKeyPressed(Input.Keys.ESCAPE)) {
            context.getSceneManager().pop();
        }
    }

    private void setupDemoEntities() {
        if (!entityManager.getAllEntities().isEmpty()) {
            return;
        }
        spawnEntity(300f, 320f);
        spawnEntity(420f, 320f);
    }

    private Entity spawnEntity(float x, float y) {
        Entity entity = entityManager.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.setPosition(new Vector2(x, y));

        VelocityComponent velocity = new VelocityComponent();
        velocity.setVelocity(new Vector2(0f, 0f));

        RenderableComponent renderable = new RenderableComponent();
        renderable.setColor(Color.CYAN.cpy());

        entity.add(transform);
        entity.add(velocity);
        entity.add(renderable);
        return entity;
    }
}
