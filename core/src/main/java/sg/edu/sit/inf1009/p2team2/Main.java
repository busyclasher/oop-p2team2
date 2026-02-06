//package sg.edu.sit.inf1009.p2team2;

//import com.badlogic.gdx.ApplicationAdapter;
//import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Entry point for the Part 1 (Abstract Engine) prototype.
 *
 * This will be wired to the engine managers (SceneManager, EntityManager, etc.)
 * as the team implements each subsystem.
 */
//public class Main extends ApplicationAdapter {
  //  @Override
   // public void render() {
   //     // Placeholder visual so the project can run while engine systems are being built.
    //    ScreenUtils.clear(0f, 0f, 0f, 1f);
   // }
  // }

package sg.edu.sit.inf1009.p2team2;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.managers.SceneManager;
import sg.edu.sit.inf1009.p2team2.engine.scenes.MenuScene;
import sg.edu.sit.inf1009.p2team2.engine.output.Display;

public class Main extends ApplicationAdapter {
    // These must be class-level variables so both create() and render() can see them
    private EngineContext context;
    private SceneManager sceneManager;

    @Override
    public void create() {
      context = new EngineContext();
    sceneManager = new SceneManager(null, context);
    
    // Check saved config
    boolean startFullscreen = context.getConfigManager().getBool("display.fullscreen");
    if (startFullscreen) {
        ((Display) context.getOutputManager().getDisplay()).toggleFullscreen();
    }
    
    sceneManager.set(new MenuScene(context, sceneManager));
    }

    @Override
    public void render() {
        // LibGDX clear screen (mandatory to prevent flickering)
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Ensure we don't call methods on null if initialization failed
        if (sceneManager != null) {
            // 1. Update logic (handles input and state)
            sceneManager.update();
        
            // 2. Coordinate rendering through the manager
            sceneManager.render();
        }
    }

    @Override
    public void dispose() {
        // Cleanup resources when the window is closed
        if (sceneManager != null) sceneManager.dispose();
        if (context != null) context.dispose();
    }
}