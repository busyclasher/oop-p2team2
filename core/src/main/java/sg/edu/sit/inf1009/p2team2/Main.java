package sg.edu.sit.inf1009.p2team2;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Entry point for the Part 1 (Abstract Engine) prototype.
 *
 * This will be wired to the engine managers (SceneManager, EntityManager, etc.)
 * as the team implements each subsystem.
 */
public class Main extends ApplicationAdapter {
    @Override
    public void render() {
        // Placeholder visual so the project can run while engine systems are being built.
        ScreenUtils.clear(0f, 0f, 0f, 1f);
    }
}

