package sg.edu.sit.inf1009.p2team2.demo;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.scene.RenderLayer;
import sg.edu.sit.inf1009.p2team2.engine.scene.SceneRenderer;


/**
 * MAINRENDERER
 * Concrete scene renderer for the main simulation scene.
 */
public class MainRenderer extends SceneRenderer {

    public MainRenderer(MainScene scene) {
        super(scene == null ? null : scene.getContext());
        addLayer(new RenderLayer() {
            @Override
            public void render() {
                if (scene != null) {
                    scene.renderMainScene();
                }
            }

            @Override
            public int getZOrder() {
                return 0;
            }

            @Override
            public boolean isVisible() {
                return true;
            }
        });
    }

    public MainRenderer(EngineContext context) {
        super(context);
    }
}
