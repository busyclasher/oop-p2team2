package sg.edu.sit.inf1009.p2team2.engine.scene;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;

/**
 * MENURENDERER
 * Concrete scene renderer for the menu scene.
 */
public class MenuRenderer extends SceneRenderer {

    public MenuRenderer(MenuScene scene) {
        super(scene == null ? null : scene.getContext());
        addLayer(new RenderLayer() {
            @Override
            public void render() {
                if (scene != null) {
                    scene.renderMenuScene();
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

    public MenuRenderer(EngineContext context) {
        super(context);
    }
}
