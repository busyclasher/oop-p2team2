package sg.edu.sit.inf1009.p2team2.demo;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.scene.RenderLayer;
import sg.edu.sit.inf1009.p2team2.engine.scene.SceneRenderer;


/**
 * SETTINGSRENDERER
 * Concrete scene renderer for the settings scene.
 */
public class SettingsRenderer extends SceneRenderer {

    public SettingsRenderer(SettingsScene scene) {
        super(scene == null ? null : scene.getContext());
        addLayer(new RenderLayer() {
            @Override
            public void render() {
                if (scene != null) {
                    scene.renderSettingsScene();
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

    public SettingsRenderer(EngineContext context) {
        super(context);
    }
}
