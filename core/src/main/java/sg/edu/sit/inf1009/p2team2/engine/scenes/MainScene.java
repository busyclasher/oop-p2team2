package sg.edu.sit.inf1009.p2team2.engine.scenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;

/**
 * Main simulation scene skeleton.
 *
 * Intentionally kept empty for now; simulation logic will be added later.
 */
public class MainScene extends Scene {

    public MainScene(EngineContext context) {
        super(context);
    }

    @Override
    public void onEnter() {
        // Reserved for future simulation setup.
    }

    @Override
    public void onExit() {
        // Reserved for future simulation teardown.
    }

    @Override
    public void load() {
        // Reserved for future resource loading.
    }

    @Override
    public void unload() {
        // Reserved for future resource cleanup.
    }

    @Override
    public void update(float dt) {
        // Simulation update will be implemented later.
    }

    @Override
    public void render() {
        if (context == null || context.getOutputManager() == null) {
            return;
        }

        var renderer = context.getOutputManager().getRenderer();
        renderer.clear();
        renderer.begin();
        renderer.drawText("MAIN SCENE (EMPTY SKELETON)", new Vector2(20f, 700f), "default", Color.WHITE);
        renderer.drawText("Simulation logic intentionally deferred", new Vector2(20f, 670f), "default", Color.LIGHT_GRAY);
        renderer.drawText("Press ESC to return to menu", new Vector2(20f, 640f), "default", Color.LIGHT_GRAY);
        renderer.end();
    }

    @Override
    public void handleInput() {
        if (context != null
            && context.getInputManager() != null
            && context.getInputManager().getKeyboard().isKeyPressed(Input.Keys.ESCAPE)) {
            context.getSceneManager().pop();
        }
    }
}
